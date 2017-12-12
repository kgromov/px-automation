package configuration.dataproviders;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import dto.TestDataError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.DataMode;
import px.objects.access.AccessTestData;
import px.objects.publishers.PublisherTestData;
import px.objects.users.ContactTestData;
import px.objects.users.UserRoleTestData;
import px.objects.users.UserTestData;
import px.objects.users.UsersData;

import java.util.List;

import static configuration.helpers.DataHelper.getRandBoolean;
import static configuration.helpers.DataHelper.getRandomInt;
import static px.objects.access.AccessTestData.PUBLISHERS;

/**
 * Created by konstantin on 21.10.2016.
 */
public class PublisherDataProvider extends SuperDataProvider {
    @DataProvider
    public static Object[][] positivePublisherData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            PublisherTestData testData = new PublisherTestData(dataMode);
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
    public static Object[][] negativePublisherData() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .negativeData()
                    .build();
            PublisherTestData testData = new PublisherTestData(dataMode);
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
    public static Object[][] editPublisherData() {
        try {
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .positiveData()
                    .build();
            // cause publisher name is not updated, id is not unique
            PublisherTestData newData = new PublisherTestData(dataMode);
            newData.setPrevName(String.valueOf(jsonObject.get("publisherName")));
            newData.setId(String.valueOf(jsonObject.get("publisherId")));
            newData.setGuid(String.valueOf(jsonObject.get("publisherGuid")));
            PublisherTestData oldData = new PublisherTestData(jsonObject);
            return new Object[][]{
                    {oldData, newData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] editNegativePublisherData() {
        try {
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            DataMode dataMode = new DataMode.Builder()
                    .updateData()
                    .negativeData()
                    .build();
            // cause publisher name is not updated, id is not unique
            PublisherTestData newData = new PublisherTestData(dataMode);
            newData.setPrevName(String.valueOf(jsonObject.get("publisherName")));
            newData.setId(String.valueOf(jsonObject.get("publisherId")));
            newData.setGuid(String.valueOf(jsonObject.get("publisherGuid")));
            PublisherTestData oldData = new PublisherTestData(jsonObject);
            return new Object[][]{
                    {oldData, newData}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] deletePublisherData() {
        try {
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            PublisherTestData testData = new PublisherTestData(jsonObject);
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

    // -------------------------- Edit Advanced settings --------------------------
    @DataProvider
    public static Object[][] checkOffersAccess() {
        try {
            DataMode dataMode = new DataMode.Builder()
                    .createData()
                    .positiveData()
                    .build();
            AccessTestData testData = new AccessTestData(dataMode, PUBLISHERS);
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

    // -------------------------- Publisher user management --------------------------
    @DataProvider
    public static Object[][] createPositiveUserData() {
        try {
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            ObjectIdentityData publisher = new ObjectIdentityData(jsonObject);
            UserTestData testData = new ContactTestData();
            return new Object[][]{
                    {testData, publisher}
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
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            ObjectIdentityData publisher = new ObjectIdentityData(jsonObject);
            UserTestData testData = new ContactTestData(false);
            if (getRandBoolean()) testData.setDuplicatedEmail();
            return new Object[][]{
                    {testData, publisher}
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
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            ObjectIdentityData publisher = new ObjectIdentityData(jsonObject);
            String  requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publisherusers")
                    .withParams("publisherId", publisher.getGuid())
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
                    {testData, publisher}
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
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            ObjectIdentityData publisher = new ObjectIdentityData(jsonObject);
            String  requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publisherusers")
                    .withParams("publisherId", publisher.getGuid())
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
                    : new PublisherUserRoleTestData(object);
            return new Object[][]{
                    {testData, publisher}
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
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            ObjectIdentityData publisher = new ObjectIdentityData(jsonObject);
            String  requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publisherusers")
                    .withParams("publisherId", publisher.getGuid())
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
                    : new PublisherUserRoleTestData(object, false);
            return new Object[][]{
                    {testData, publisher}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null, null}
            };
        }
    }

    @DataProvider
    public static Object[][] publisherUsersOverviewData() {
        try {
            JSONObject jsonObject = getInstanceDetails("publishers", "publisherName", "Publisher Name ");
            ObjectIdentityData publisher = new ObjectIdentityData(jsonObject);
            UsersData testData = new UsersData.PublisherUsersData(publisher);
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

    private static final class PublisherUserRoleTestData extends UserRoleTestData {

        PublisherUserRoleTestData(JSONObject user) {
            this(user, true);
        }

        PublisherUserRoleTestData(JSONObject user, boolean isPositive) {
            super(new ObjectIdentityData(user), isPositive);
            this.fullName = user.has("description") ? String.valueOf(user.get("description")) : null;
            this.status = user.has("status") ? String.valueOf(user.get("status")) : null;
        }
    }
}
