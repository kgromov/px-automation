package tests;

import configuration.dataproviders.LoginDataProvider;
import dto.CredentialsData;
import dto.TestDataError;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.LoginPage;

import static config.Config.setAdminUser;
import static config.Config.setTestURL;


/**
 * Created by okru on 2/9/2016.
 */
public class LoginTest extends BaseTest {
    private LoginPage loginPage;
    protected boolean allowDefaultLogin = true;

    @BeforeMethod()
    protected void loginToPX() {
        if (!TestDataError.isAnyTestDataExceptions() && allowDefaultLogin) {
            pxDriver.goToURL(url);
            login();
        }
    }

    @Test(dataProvider = "adminCredentials", dataProviderClass = LoginDataProvider.class, enabled = false)
    public void loginTest(CredentialsData credentialsData) {
        new LoginPage(pxDriver)
                .login(credentialsData)
                .waitLoggedIn()
                // check dashboard page
                .checkPage();
    }

    // possibly logout
    @Test(dataProvider = "wrongCredentials", dataProviderClass = LoginDataProvider.class, enabled = false)
    public void negativeLoginTest(CredentialsData credentialsData) {
        new LoginPage(pxDriver)
                .login(credentialsData)
                .checkErrorMessage();
    }

    private void login(CredentialsData credentialsData) {
        long start = System.currentTimeMillis();
        loginPage = new LoginPage(pxDriver);
        if (!loginPage.isLogged()) {
            loginPage.login(credentialsData)
                    .waitLoggedIn();
        }
        log.info(String.format("Logged in with credentials %s\ttime to login = %d seconds",
                credentialsData, (System.currentTimeMillis() - start)));
    }

    protected void login() {
        login(new CredentialsData());
    }

    protected void logout() {
        if (loginPage.isLogged()) {
            loginPage.logout();
        }
    }

    protected void reLoginAsAdmin(){
        logout();
        setAdminUser();
        login();
    }

    protected void reLoginAsUser(){
        logout();
        setTestURL(null);
        login();
    }
}
