package configuration.browser;

import org.apache.log4j.Logger;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.logging.Level;

/**
 * Created by kgr on 9/29/2016.
 */
public class CapabilitiesFactory {
    private static final Logger log = Logger.getLogger(CapabilitiesFactory.class);

    public static DesiredCapabilities createCapabilities(WebDriverSettings settings, Proxy seleniumProxy) {
        DesiredCapabilities capabilities = createCapabilities(settings.getBrowserName());
        if (settings.isUseProxy()) {
            // in case windows server 2008 or other situations
//            System.setProperty("bmp.allowNativeDnsFallback", "true");
            capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
        }
        return capabilities;
    }

    public static DesiredCapabilities createCapabilities(String browserName) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        switch (browserName) {
            case "chrome": {
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, "/usr/bin/chromedriver");
                    capabilities.setCapability("chrome.binary", "/usr/bin/chromedriver");
                }
                capabilities.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());
                break;
            }
            case "ie": {
                capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                capabilities.setCapability("trustAllSSLCertificates", true);
                capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
                capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
                capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
                if (!System.getProperty("os.arch").contains("64")) {
                    System.setProperty(InternetExplorerDriverService.IE_DRIVER_EXE_PROPERTY,
                            "./src/test/resources/drivers/IEDriverServer.exe");
                }
                break;
            }
            case "safari": {
//                capabilities = DesiredCapabilities.safari();
                System.setProperty("webdriver.safari.noinstall", "true");
                break;
            }
        }
        // turn on log preferences
        LoggingPreferences loggingPreferences = new LoggingPreferences();
        loggingPreferences.enable(LogType.BROWSER, Level.ALL);
        capabilities.setCapability(CapabilityType.LOGGING_PREFS, loggingPreferences);
        return capabilities;
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-breakpad");
        options.addArguments("--disable-infobars");
//        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
//        options.addArguments("--disable-application-cache");
//        options.addArguments("--ignore-certifcate-errors");
        return options;
    }
}
