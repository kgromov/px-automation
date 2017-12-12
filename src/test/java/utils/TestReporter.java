package utils;

import config.Config;
import configuration.helpers.DataHelper;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.testng.ITestResult;
import org.testng.Reporter;

import java.io.File;
import java.io.IOException;

/**
 * Created by kgr on 9/29/2016.
 */
public class TestReporter {
    private final static Logger log = Logger.getLogger(TestReporter.class);

    private static File takeScreenShot(WebDriver driver, String pathName) {
        try {
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            // take screenshot
            File screen = takesScreenshot.getScreenshotAs(OutputType.FILE);
            log.info(String.format("Created by driver screen details:\tpath = %s, length = %s", screen.getAbsolutePath(), screen.length()));
            File destinationFile = new File(Config.PATH_TO_SCREENS_WD + pathName + ".png");
            FileUtils.copyFile(screen, destinationFile);
            log.info(String.format("Copied to report folder screen details:\tpath = %s, length = %s", destinationFile.getAbsolutePath(), destinationFile.length()));
            return destinationFile;
        } catch (UnhandledAlertException e) {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            driver.close();
            driver.quit();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // handle cases when it's impossible to take screen, driver is null etc
    public static void reportErrorWithScreenshot(WebDriver driver) {
        reportErrorWithScreenshot(driver, "Browser screen by WebDriver:");
    }

    public static void reportErrorWithScreenshot(WebDriver driver, String screenDescription) {
        try {
            File path = takeScreenShot(driver, DataHelper.getCurrentDateFileFormat());
            if (path == null) return;
            log.info(String.format("%s\n%s", screenDescription, path.getAbsolutePath()));
            Reporter.setEscapeHtml(false);
            Reporter.log(getFormattedLinkWithPreview(path.getName(), screenDescription), true);
//            Reporter.setCurrentTestResult(null);
        } catch (Exception e) {
            log.error("Unable to take screenshot\n" + e.getMessage());
        }
    }

    private static String getFormattedLinkWithPreview(String imageName, String screenDescription) {
        String relativePath = Config.WD_SCREEN + imageName;
        return "<br><b>" + screenDescription + "</b><a href=\"" + relativePath + "\">browser screen" +
                "<img align=\"center\" height=\"10%\" width=\"10%\" src=\"" + relativePath + "\"></a>";
    }

    public static String getTestResult(ITestResult result) {
        int status = result.getStatus();
        String resultString;
        switch (status) {
            case 1:
                resultString = "Success";
                break;
            case 2:
                resultString = "Failure";
                break;
            case 3:
                resultString = "Skip";
                break;
            default:
                resultString = "Unknown";
        }
        return resultString;
    }
}