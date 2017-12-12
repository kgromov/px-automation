package tests.functional.crud;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.SecurityToken;
import configuration.helpers.SessionToken;
import dto.LxpDataProvider;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

/**
 * Created by kgr on 6/29/2017.
 */
public abstract class GrantsTest {
    private static final Logger log = Logger.getLogger(GrantsTest.class);
    private static SessionToken adminToken;
    private static SessionToken userToken;
    protected static LxpDataProvider adminDataProvider;
    protected static LxpDataProvider userDataProvider;

    @BeforeSuite(enabled = false)
    private void setTestBranch() {
        System.setProperty("test.branch", "http://rvmd-13057-ui.stagingpx.com");
    }

    @BeforeTest
    protected void setSessionToken() {
        // to be able execute requests under admin
        if (adminToken == null) {
            Config.setAdminUser();
            log.info("Set up admin session");
            adminToken = new SessionToken();
            adminDataProvider = new LxpDataProvider(adminToken);

        }
        Config.setTestURL(null);
        if (userToken == null) {
            log.info("Set up user session");
            SecurityToken.getToken();
//            userToken = new SessionToken();
            userDataProvider = new LxpDataProvider();
        }
    }

    protected abstract void setObjects();

    protected static JSONObject updateObjectWithParamId(JSONObject object, String paramName, String requestedURL) {
        JSONArray jsonArray = new JSONArray(adminDataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        JSONObject fieldIdObject = DataHelper.getJSONFromJSONArrayByCondition(jsonArray, "field", paramName);
        // append source object with mandatory field
        object.put(paramName, fieldIdObject.get("default"));
        return object;
    }
}
