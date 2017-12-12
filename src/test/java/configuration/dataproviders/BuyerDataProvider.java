package configuration.dataproviders;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import dto.TestDataError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.buyers.BuyerTestData;
import px.objects.users.ContactTestData;
import px.objects.users.UserRoleTestData;
import px.objects.users.UserTestData;
import px.objects.users.UsersData;

import java.util.List;

import static configuration.helpers.DataHelper.getRandBoolean;
import static configuration.helpers.DataHelper.getRandomInt;

/**
 * Created by konstantin on 21.10.2016.
 */
public class BuyerDataProvider extends SuperDataProvider {
    @DataProvider
    public static Object[][] positiveBuyerData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            BuyerTestData testData = new BuyerTestData(dataMode);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] negativeBuyerData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            BuyerTestData testData = new BuyerTestData(dataMode);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] editBuyerData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            // cause buyer name is not updated
            BuyerTestData testData = new BuyerTestData(dataMode);
            testData.setPrevName(String.valueOf(jsonObject.get("buyerName")));
            testData.setGuid(String.valueOf(jsonObject.get("buyerGuid")));
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] editNegativeBuyerData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .negativeData()
                    .build();
            // cause buyer name is not updated
            BuyerTestData testData = new BuyerTestData(dataMode);
            testData.setPrevName(String.valueOf(jsonObject.get("buyerName")));
            testData.setGuid(String.valueOf(jsonObject.get("buyerGuid")));
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] deleteBuyerData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            BuyerTestData testData = new BuyerTestData(jsonObject);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    // -------------------------- Buyer user management --------------------------
    @DataProvider
    public static Object[][] createPositiveUserData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            ObjectIdentityData buyer = new ObjectIdentityData(jsonObject);
            UserTestData testData = new ContactTestData();
            return new Object[][]{
                    {testData, buyer}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] createNegativeUserData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            ObjectIdentityData buyer = new ObjectIdentityData(jsonObject);
            ContactTestData testData = new ContactTestData(false);
            if (getRandBoolean()) testData.setDuplicatedEmail();
            return new Object[][]{
                    {testData, buyer}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] deleteUserData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            ObjectIdentityData buyer = new ObjectIdentityData(jsonObject);
            String requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/buyerusers")
                    .withParams("buyerGuid", buyer.getGuid())
//                    .withEmptyFilter()
                    .filter("status", "Published")
                    .sort("description", "asc")
                    .build().getRequestedURL();
            List<ObjectIdentityData> users = ObjectIdentityData
                    .getObjectsByJSONArray(dataProvider.getDataAsJSONArray(requestURL));
            // select any existed user
            ObjectIdentityData user = !users.isEmpty() ? ObjectIdentityData.getAnyObjectFromList(users) : null;
            // create contact data if no users for certain buyer
            UserRoleTestData testData = user == null ? new UserRoleTestData() : new UserRoleTestData(user);
            return new Object[][]{
                    {testData, buyer}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] resetUserPasswordPositiveData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            ObjectIdentityData buyer = new ObjectIdentityData(jsonObject);
            String requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/buyerusers")
                    .withParams("buyerGuid", buyer.getGuid())
//                    .withEmptyFilter()
                    .filter("status", "Published")
                    .sort("description", "asc")
                    .build().getRequestedURL();
            JSONArray users = dataProvider.getDataAsJSONArray(requestURL);
            // select any existed user
            JSONObject object = users.length() > 0 ? users.getJSONObject(getRandomInt(users.length())) : null;
            // create contact data if no users for certain buyer
            UserRoleTestData testData = object == null
                    ? new UserRoleTestData().withStatus("Published")
                    : new BuyerUserRoleTestData(object);
            return new Object[][]{
                    {testData, buyer}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] resetUserPasswordNegativeData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            ObjectIdentityData buyer = new ObjectIdentityData(jsonObject);
            String requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/buyerusers")
                    .withParams("buyerGuid", buyer.getGuid())
//                    .withEmptyFilter()
                    .filter("status", "Published")
                    .sort("description", "asc")
                    .build().getRequestedURL();
            JSONArray users = dataProvider.getDataAsJSONArray(requestURL);
            // select any existed user
            JSONObject object = users.length() > 0 ? users.getJSONObject(getRandomInt(users.length())) : null;
            // create contact data if no users for certain buyer
            UserRoleTestData testData = object == null
                    ? new UserRoleTestData().withStatus("Published")
                    : new BuyerUserRoleTestData(object, false);
            return new Object[][]{
                    {testData, buyer}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] buyerUsersOverviewData() {
        try {
            JSONObject jsonObject = getInstanceDetails("buyers", "buyerName", "Buyer Name ");
            ObjectIdentityData buyer = new ObjectIdentityData(jsonObject);
            UsersData testData = new UsersData.BuyerUsersData(buyer);
            return new Object[][]{
                    {testData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    private static final class BuyerUserRoleTestData extends UserRoleTestData {

        BuyerUserRoleTestData(JSONObject user) {
            this(user, true);
        }

        BuyerUserRoleTestData(JSONObject user, boolean isPositive) {
            super(new ObjectIdentityData(user), isPositive);
            this.fullName = user.has("description") ? String.valueOf(user.get("description")) : null;
            this.status = user.has("status") ? String.valueOf(user.get("status")) : null;
        }
    }
}
