package pages;

import configuration.browser.PXDriver;
import dto.CredentialsData;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import px.objects.users.UserRoleTestData;
import px.objects.users.UserTestData;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.LoginPageLocators.*;

/**
 * Created by okru on 2/9/2016.
 */
public class LoginPage extends BasePage {

    public LoginPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public LoginPage login(CredentialsData data) {
        log.info("Type username or email");
        helper.waitUntilDisplayed(LOGIN_INPUT);
        helper.type(LOGIN_INPUT, data.getLogin());
        log.info("Type password");
        helper.waitUntilDisplayed(PASSWORD_INPUT);
        helper.type(PASSWORD_INPUT, data.getPassword());
        log.info("Login page: Clicking on 'Login button'");
        helper.click(LOGIN_BUTTON);
//        new ObjectPage(pxDriver){}.checkErrorMessage();
        return this;
    }

    public LoginPage loginAs(UserRoleTestData testData) {
        login(new CredentialsData(testData.getEmail(), testData.getPassword()));
        if (testData.isUserAbleToLogin()) {
            waitPageIsLoaded();
            checkLoggedUser(testData);
        } else {
            assertThat(String.format("User %s with status '%s' is not allowed to login",
                    testData.getEmail(), testData.getStatus()), !isLogged());
        }
        return this;
    }

    public LoginPage logout(){
        log.info("Logout");
        helper.scrollToElement(LOGOUT_BUTTON);
        helper.click(LOGOUT_BUTTON);
        // wait till progress finished
        waitPageIsLoaded();
        // wait till logout button become visible
        helper.waitUntilDisplayed(LOGIN_BUTTON);
        return this;
    }

    public boolean isLogged() {
        return helper.isElementPresent(LOGOUT_BUTTON, 5);
    }

    public DashboardPage waitLoggedIn() {
        long start = System.currentTimeMillis();
        try {
            // wait till progress finished
            waitPageIsLoaded(40);
            // wait till logout button become visible
            helper.waitUntilDisplayed(LOGOUT_BUTTON);
        } catch (TimeoutException | NoSuchElementException e) {
            throw new TimeoutException(String.format("page is not loaded after timeout in '%d' seconds",
                    TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
        } finally {
            log.info(String.format("Loading page duration = '%d' seconds",
                    TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
        }
        return new DashboardPage(pxDriver);
    }

    public LoginPage checkLoggedUser(UserRoleTestData testData){
        assertThat("Check logged user full name",
                helper.getElement(LOGGED_USER_NAME).getText(),
                containsString(testData.getFullName()));
        return this;
    }

    // move to consolidate validation class
    public LoginPage checkErrorMessage() {
        helper.waitUntilDisplayed(INVALID_CREDENTIALS_MSG);
        assertThat("Check Error message text",
                helper.getElement(INVALID_CREDENTIALS_MSG).getText(),
                equalTo("Invalid credentials"));
        return this;
    }

}