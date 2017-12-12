package configuration.browser;

import config.Config;
import elements.HelperSingleton;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;
import utils.JsUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by okru on 2/20/2016.
 */
public class PXDriver {
    protected final Logger log = Logger.getLogger(this.getClass());
    private final JsUtils jsUtils;
    private WebDriver driver;
    private WebDriverWait wait;
    private List<String> logEntryList;

    public PXDriver(WebDriver driver) {
        this.driver = driver;
        this.jsUtils = new JsUtils(driver);
        this.wait = new WebDriverWait(this.driver, Config.DEFAULT_TIMEOUT);
        wait.ignoring(StaleElementReferenceException.class);
        // set ElementsHelper to be available in any place
        HelperSingleton.setHelper(driver);
    }

    public WebDriver getWrappedDriver() {
        return driver;
    }

    public WebDriverWait getWait() {
        return wait;
    }

    public void quitSession() {
        log.info("PXDriver: quitSession()");
        if (driver != null) {
            try {
                driver.close();
            } catch (NoSuchWindowException e) {
                log.error("Unable to close browser window\n" + e.getMessage());
            }
            driver.quit();
//            TimeUnit.SECONDS.sleep(5);
        }
    }

    // Navigation methods
    public void goToURL(String url) {
        log.info(String.format("Open page by url %s", url));
//        driver.get("data:,");
        driver.get(url);
        waitForPageLoaded();
        waitForAjaxComplete();
        setErrorsList();
    }

    public void goBackwards() {
        driver.navigate().back();
    }

    public void goForward() {
        driver.navigate().forward();
    }

    public String getCurrentUrl() {
        try {
            return driver.getCurrentUrl();
        } catch (NoSuchWindowException ignored) {
        } catch (UnhandledAlertException e2) {
            try {
                Alert alert = driver.switchTo().alert();
                String alertText = alert.getText();
                log.info("Alert appears after test method with text\n" + alertText);
            } catch (NoAlertPresentException ignored) {
            }
        }
        return null;
    }

    public boolean isMobileEmulatorUsed() {
        Capabilities capabilities = ((RemoteWebDriver) driver).getCapabilities();
        return Boolean.parseBoolean(String.valueOf(capabilities.getCapability(CapabilityType.HAS_TOUCHSCREEN))) ||
                Boolean.parseBoolean(String.valueOf(capabilities.getCapability("mobileEmulationEnabled")));
    }

    // windows methods
    public void maximizeBrowserWindow() {
        driver.manage().window().maximize();
    }

    public void resizeBrowserWindow(int width, int height) {
        Dimension dimension = new Dimension(width, height);
        driver.manage().window().setSize(dimension);
    }

    public void waitUntilWindow() {
        long begin = System.currentTimeMillis();
        log.info("waiting for Browser window");
        try {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return driver.getWindowHandles().size() > 0;
                }
            });
            log.info("Elapsed time milliseconds is '" + (System.currentTimeMillis() - begin) + "'");
        } catch (TimeoutException e) {
            throw new Error("There is no any browser window after timeout in " + Config.DEFAULT_TIMEOUT + " seconds");
        }
    }

    public void waitUntilWindowResize(Dimension dimension) {
        log.info("Dimension before resize\t" + dimension);
        long begin = System.currentTimeMillis();
        final int width = dimension.getWidth();
        final int height = dimension.getHeight();
        if (driver.manage().window().getSize().getWidth() == width && driver.manage().window().getSize().getHeight() == height)
            return;
        log.info("waiting for Browser changed size");
        try {
            wait.withTimeout(Config.DEFAULT_TIMEOUT / 2, TimeUnit.SECONDS);
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    Dimension dimension = driver.manage().window().getSize();
                    return dimension.getWidth() != width & dimension.getHeight() != height;
                }
            });
            log.info("Dimension after resize\t" + driver.manage().window().getSize());
            log.info("Elapsed time milliseconds is '" + (System.currentTimeMillis() - begin) + "'");
        } catch (TimeoutException e) {
            throw new Error("Browser window does not change size after timeout in " + Config.DEFAULT_TIMEOUT / 2 + " seconds");
        } finally {
            wait.withTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void moveToSecondTab() {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        if (tabs.size() == 0) return;
        driver.switchTo().window(tabs.get(tabs.size() - 1));
        driver.switchTo().activeElement();
    }

    public void moveToFirstTabAndCloseSecondTab() {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
//        if (tabs.size() != 2) return;
        if (tabs.size() > 1) {
            driver.switchTo().window(tabs.get(1));
            driver.close();
            driver.switchTo().window(tabs.get(0));
            driver.switchTo().activeElement();
        }
    }

    public void closePreviousTab() {
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
//        if (tabs.size() != 2) return;
        if (tabs.size() > 1) {
            driver.switchTo().window(tabs.get(0));
            driver.close();
            driver.switchTo().window(tabs.get(1));
            driver.switchTo().activeElement();
        }
    }

    public void deleteCookies() {
        driver.manage().deleteAllCookies();
    }

    // waiting and conditions
    public void setImplicitlyWait() {
        driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
    }

    public void setImplicitlyWait(long timeOutSeconds) {
        driver.manage().timeouts().implicitlyWait(timeOutSeconds, TimeUnit.SECONDS);
    }

    public void waitForAjaxComplete() {
        log.info("waiting for ajax completion");
        try {
            wait.withTimeout(Config.DEFAULT_SCRIPT_TIMEOUT, TimeUnit.SECONDS);
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return jsUtils.isAjaxComplete();
                }
            });
            log.info("All ajax calls are complete");
        } catch (TimeoutException e) {
            throw new Error("Ajax is not completed ($.active!=0) after timeout in " + Config.DEFAULT_SCRIPT_TIMEOUT + " seconds");
        } finally {
            wait.withTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void waitForPageLoaded() {
        long begin = new Date().getTime();
        log.info("waiting for Page is Loaded");
        try {
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return jsUtils.isPageLoaded();
                }
            });
            log.info("Elapsed time milliseconds is '" + (new Date().getTime() - begin) + "'");
        } catch (TimeoutException e) {
            throw new Error("Page is not loaded (document.readyState!='complete') after timeout in " + Config.DEFAULT_TIMEOUT + " seconds");
        }
    }

    // wait methods
    public void waitForUrlIs(String url) {
        wait.until(ExpectedConditions.urlToBe(url));
    }

    public void waitForUrlContains(String url) {
        wait.until(ExpectedConditions.urlContains(url));
    }

    public void printBrowserParameters() {
        Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
        log.info("----------- Browser Parameters -----------------------------------------------");
//        log.info("Tested URL:        " + this.url);
        log.info("Browser Name:      " + cap.getBrowserName());
        log.info("Browser version:   " + cap.getVersion());
        log.info("Platform:          " + cap.getPlatform());
        log.info("\nAll parameters:\n");
        for (Map.Entry<String, ?> entry : cap.asMap().entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
        }
        log.info("----------- Browser Parameters ------------------------------------------------");
    }

    // log console errors
    public void printBrowserErrors() {
//        if (settings.getBrowserName().equalsIgnoreCase("ie")) return;
        boolean errorsPresent = false;
        for (LogEntry logError : driver.manage().logs().get(LogType.BROWSER).filter(Level.SEVERE)) {
            if (!logEntryList.contains(logError.getMessage())) {
                Reporter.setEscapeHtml(false);
                Reporter.log("<br>" + logError.toString(), true);
                errorsPresent = true;
            }
        }
        assertThat("JavaScript errors are missed", errorsPresent, equalTo(false));
    }

    public void setErrorsList() {
//        if (settings.getBrowserName().equalsIgnoreCase("ie")) return;
        this.logEntryList = new ArrayList<>();
        for (LogEntry logError : driver.manage().logs().get("browser").filter(Level.SEVERE)) {
            logEntryList.add(logError.getMessage());
        }
    }

    public List<String> getLogEntryList() {
        return logEntryList;
    }
}