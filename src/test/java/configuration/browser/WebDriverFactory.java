package configuration.browser;

import config.Config;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;

public enum WebDriverFactory {
    CHROME("chrome") {
        @Override
        public WebDriver getDriverWithCapabilities(DesiredCapabilities capabilities) {
//            for debug
//            System.setProperty("webdriver.chrome.logfile", "D:\\chromedriver.log");
            return capabilities == null ? new ChromeDriver() : new ChromeDriver(capabilities);
        }
    },
    FIREFOX("firefox") {
        @Override
        public WebDriver getDriverWithCapabilities(DesiredCapabilities capabilities) {
            return capabilities == null ? new FirefoxDriver() : new FirefoxDriver(capabilities);
        }
    },
    IE("ie") {
        @Override
        public WebDriver getDriverWithCapabilities(DesiredCapabilities capabilities) {
            return capabilities == null ? new InternetExplorerDriver() : new InternetExplorerDriver(capabilities);
        }
    },

    PHANTOMJS("phantomjs") {
        @Override
        public WebDriver getDriverWithCapabilities(DesiredCapabilities capabilities) {
            return capabilities == null ? new PhantomJSDriver() :  new PhantomJSDriver(capabilities);
        }
    },

    SAFARI("safari") {
        @Override
        public WebDriver getDriverWithCapabilities(DesiredCapabilities capabilities) {
            return capabilities == null ? new SafariDriver() : new SafariDriver(capabilities);
        }
    };

    private String name;

    private WebDriverFactory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private static WebDriverFactory getByName(String name) {
        for (WebDriverFactory browser : WebDriverFactory.class.getEnumConstants()) {
            if (name.equals(browser.getName())) return browser;
        }
        throw new Error("There is no such browser: " + name);
    }

    public abstract WebDriver getDriverWithCapabilities(DesiredCapabilities capabilities);

    public static PXDriver createDriver(Proxy seleniumProxy, WebDriverSettings settings) throws InterruptedException {
        WebDriver driver = getByName(settings.getBrowserName()).getDriverWithCapabilities(CapabilitiesFactory.createCapabilities(settings, seleniumProxy));
        PXDriver pxDriver = new PXDriver(driver);
        pxDriver.waitUntilWindow();
        Dimension dimension = driver.manage().window().getSize();
        driver.manage().window().setPosition(new Point(20, 20));
        if (settings.isUseMobileEmulator()) {
            driver.manage().window().setSize(new Dimension(Config.MOBILE_WIDTH, Config.MOBILE_HEIGHT));
        } else {
            driver.manage().window().setSize(new Dimension(Config.DESKTOP_WIDTH, Config.DESKTOP_HEIGHT));
            driver.manage().window().maximize();
        }
        pxDriver.waitUntilWindowResize(dimension);
        pxDriver.setImplicitlyWait();
        return pxDriver;
    }

    public static PXDriver createDriver(String browserName) {
        WebDriver driver = getByName(browserName).getDriverWithCapabilities(CapabilitiesFactory.createCapabilities(browserName));
        PXDriver pxDriver = new PXDriver(driver);
        pxDriver.waitUntilWindow();
        Dimension dimension = driver.manage().window().getSize();
        pxDriver.maximizeBrowserWindow();
        pxDriver.waitUntilWindowResize(dimension);
        pxDriver.setImplicitlyWait();
        return pxDriver;
    }
}