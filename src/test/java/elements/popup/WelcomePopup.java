package elements.popup;

import elements.SuperElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static pages.locators.DashboardPageLocators.WELCOME_POPUP;

/**
 * Created by kgr on 11/22/2016.
 */
@FindBy(xpath = WELCOME_POPUP)
public class WelcomePopup extends SuperElement {
    @FindBy(xpath = ".//div[contains(@class, 'wm-close-button')]")
    private WebElement closeButton;

    public void closePopup(){
        closeButton.click();
    }
}
