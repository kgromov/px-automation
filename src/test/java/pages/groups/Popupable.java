package pages.groups;

import elements.ElementsHelper;
import elements.HelperSingleton;
import org.apache.log4j.Logger;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.util.concurrent.TimeUnit;

import static pages.locators.ElementLocators.*;

/**
 * Created by kgr on 11/7/2017.
 */
public interface Popupable {
    Logger logger = Logger.getLogger(Popupable.class);

    default void closePopupWithAttempts() {
        logger.info("Close popup with attempts");
        ElementsHelper helper = HelperSingleton.getHelper();
        helper.getWait().withTimeout(0, TimeUnit.SECONDS);
        try {
            logger.info("1st attempt");
            helper.getElement(POPUP_CLOSE).click();
            helper.waitUntilToBeInvisible(POPUP, 2);
        } catch (WebDriverException e) {
            logger.error("DEBUG:\tUnable to close popup\tDetails = " + e.getMessage());
            try {
                logger.info("2nd attempt");
                helper.getElement(POPUP_CLOSE).click();
                helper.waitUntilToBeInvisible(POPUP, 1);
            } catch (WebDriverException e2) {
                String message = (e instanceof NoSuchElementException) ? "No popup appears after clicking on cell"
                        : (e instanceof TimeoutException) ? "Popup verification failed due attempt to close"
                        : ("Another WebDriver exception" + e.getMessage());
                logger.info("DEBUG\tPopup verification failed due to\t" + e.getMessage());
                throw new WebDriverException(message, e);
            }
        }
    }

    default void waitPopupTextLoaded() {
        logger.info("Start wait till text in popup loaded");
        ElementsHelper helper = HelperSingleton.getHelper();
        helper.getWait().withTimeout(2, TimeUnit.SECONDS).until(
                new ExpectedCondition<Boolean>() {
                    @Override
                    public Boolean apply(WebDriver driver) {
                        String value = helper.getElement(POPUP_TEXT).getText();
                        return !value.isEmpty() && !value.contains("data is being loaded");
                    }
                }
        );
        logger.info("Text in popup is loaded");
    }
}
