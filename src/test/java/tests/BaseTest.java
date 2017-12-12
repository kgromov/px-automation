package tests;

import config.Config;
import configuration.browser.PXDriver;
import configuration.browser.WebDriverFactory;
import configuration.browser.WebDriverSettings;
import dto.TestDataError;
import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import utils.FailListener;
import utils.TestReporter;

import java.lang.reflect.Method;

@Listeners({FailListener.class})
public class BaseTest {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected PXDriver pxDriver;
    protected WebDriverSettings settings;
    protected String url;
    private String methodName;

    @BeforeClass
    @Parameters(value = {"browser", "version", "url"})
    public void setUp(@Optional("chrome") String browser,
                      @Optional("any") String version,
                      @Optional("") String url) throws Exception {
        TestDataError.clearErrors();
        // override from xml
        if (this.url == null) this.url = url;
        Config.setTestURL(this.url);
        this.url = Config.testUrl;
        this.pxDriver = WebDriverFactory.createDriver(browser);
        pxDriver.printBrowserParameters();
    }

    // alerts handling
    @BeforeMethod(alwaysRun = true)
    public void beforeTestMethod(Method method, Object[] parameters) throws Exception {
        // set current method
        String previousMethodName = methodName;
        FailListener.CURRENT_METHOD_NAME = method.getAnnotation(Test.class).testName();
        if (this.methodName == null) this.methodName = FailListener.CURRENT_METHOD_NAME;
        if (!FailListener.CURRENT_METHOD_NAME.isEmpty()) {
            if (this.methodName.equals(FailListener.CURRENT_METHOD_NAME)) {
                if (previousMethodName != null) ++FailListener.OWN_INVOCATION_COUNT;
            } else {
                this.methodName = FailListener.CURRENT_METHOD_NAME;
//                FailListener.OWN_INVOCATION_COUNT = 0;
            }
        }
        // check for test data exception
        if (TestDataError.isAnyTestDataExceptions()) return;
        log.info("------------------------------------------------------------------------------------------------------");
        log.info(getMethodFullName(method) + " Test Started.");
        log.info("------------------------------------------------------------------------------------------------------");
        // Close opened tabs in browser except first tab
        if (pxDriver != null) {
            pxDriver.moveToFirstTabAndCloseSecondTab();
            pxDriver.deleteCookies();
        }
        log.info("************************ TEST DATA BEGIN ***********************************");
        for (Object parameter : parameters)
            log.info(parameter.toString());
        log.info("************************ TEST DATA END *************************************\n");
    }

    // Add screen/reporting/alerts/skipped tests handling
    @AfterMethod(alwaysRun = true)
    public void afterTestMethod(Method method, ITestResult result) throws Exception {
        log.warn("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        log.warn(getMethodFullName(method) + " - Test Finished.");
        log.warn("Test result: " + TestReporter.getTestResult(result));
        log.warn("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (!result.isSuccess()) {
            Reporter.setCurrentTestResult(result);
            // to prevent skip tests
            if (result.getStatus() != ITestResult.SKIP && !TestDataError.isSkippedTest() && pxDriver != null)
                TestReporter.reportErrorWithScreenshot(pxDriver.getWrappedDriver());
        }
        TestDataError.clearErrors();
    }

    @AfterClass(alwaysRun = true)
    public void afterAllTests() {
        log.info("===============================================");
        log.info("Test methods have been executed:\n" + FailListener.METHODS_SET);
        if (pxDriver != null)
            pxDriver.quitSession();
        log.info("WebDriver instance closed");
        FailListener.METHOD_ERROR_MAP = null;
        FailListener.METHODS_SET = null;
        FailListener.OWN_INVOCATION_COUNT = 0;
        log.info("===============================================");
    }

    private String getMethodFullName(Method method) {
        return this.getClass().getSimpleName() + "_" + method.getName();
    }
}