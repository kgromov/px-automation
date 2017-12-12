package configuration.dataproviders;

import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.DataProvider;
import dto.CredentialsData;

import static config.Config.password;
import static config.Config.user;

/**
 * Created by kgr on 10/3/2016.
 */
public class LoginDataProvider {
    @DataProvider
    public static Object[][] adminCredentials() {
        return new Object[][]{
                {new CredentialsData()}
        };
    }

    @DataProvider
    public static Object[][] wrongCredentials() {
        return new Object[][]{
                {new CredentialsData("User_"+ RandomStringUtils.random(5, true, true), "Password"+ RandomStringUtils.random(5, true, true))},
                {new CredentialsData("User_"+ RandomStringUtils.random(5, true, true), " ")},
                {new CredentialsData(" ", "Password"+ RandomStringUtils.random(5, true, true))},
                {new CredentialsData(user, "Password"+ RandomStringUtils.random(5, true, true))},
                {new CredentialsData("User_"+ RandomStringUtils.random(5, true, true), password)}
        };
    }

}
