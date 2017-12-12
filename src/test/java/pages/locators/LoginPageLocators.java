package pages.locators;

import org.openqa.selenium.By;

/**
 * Created by kgr on 9/30/2016.
 */
public class LoginPageLocators {
    // frequently used fields
    public static final By LOGIN_INPUT = By.name("UserName"); //input[@name='UserName']
    public static final By PASSWORD_INPUT = By.name("Password"); //input[@name='Password']
    public static final By LOGIN_BUTTON = By.xpath(".//input[@type='submit']");
    public static final By LOGOUT_BUTTON = By.cssSelector(".px-header-greeting .px-header-logout");
    public static final By LOGGED_USER_NAME = By.cssSelector(".px-header-person");      // full user name + of + {buyerName, publisherName, PXDemo_02=IntAdministrators}
    public static final By PX_LOGO = By.xpath(".//div[@class='px-header-logo']");
    public static final By INVALID_CREDENTIALS_MSG = By.xpath(".//div[@class='px-error-message ng-binding ng-scope']");

    public static final By WELCOME_TEXT = By.xpath(".//div[@class='px-log-white']/div[@class='px-login-title']");
    public static final By LOGIN_PIC = By.xpath(".//i[@class='px-login-icon']");
    public static final By PASSWORD_PIC = By.xpath(".//i[@class='px-password-icon']");
    public static final By KEEP_ME_IN_CHECKBOX = By.xpath(".//form[@name='login']/span/input[@type='checkbox']");
    public static final By LOST_PASSWORD_LINK = By.xpath(".//a[text()='Lost Password?']");
    public static final By SING_UP_AS_PUBLISHER_LINK = By.xpath(".//a[text()='Sign up as Publisher']");
    public static final By SING_UP_AS_LEAD_BUYER_LINK = By.xpath(".//a[text()='Sign up as Lead Buyer']");
    public static final By COPYRIGHT_TEXT = By.xpath(".//div[@class='px-login-footer']/p");
}