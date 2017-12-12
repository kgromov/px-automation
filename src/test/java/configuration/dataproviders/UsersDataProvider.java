package configuration.dataproviders;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import dto.TestDataError;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import px.objects.users.UserRoleTestData;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by konstantin on 21.10.2016.
 */
public class UsersDataProvider extends SuperDataProvider {
    public static final Pattern AUTOMATION_USER_PATTERN = Pattern.compile("(staging)|(px)|(test)");

    @DataProvider
    public static Object[][] createUserPositiveData() {
        try {
            UserRoleTestData testData = new UserRoleTestData().withUserRoles();
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
    public static Object[][] createUserNegativeData() {
        try {
            UserRoleTestData testData = new UserRoleTestData(false).withUserRoles();
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
    public static Object[][] userPreviewData() {
        try {
            // as there is no strict criteria like in other  px objects (Buyer Name, Offer Name etc)
            ObjectIdentityData user = ObjectIdentityData.getAnyObjectFromList(automationCreatedUsersNotDeleted());
            UserRoleTestData testData = new UserRoleTestData(user);
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
    public static Object[][] editUserPositiveData() {
        try {
            // as there is no strict criteria like in other  px objects (Buyer Name, Offer Name etc)
            ObjectIdentityData user = ObjectIdentityData.getAnyObjectFromList(automationCreatedUsersNotDeleted());
            UserRoleTestData testData = new UserRoleTestData(user)
                    .withUserRoles().withCustomRights().withStatus();
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
    public static Object[][] editUserNegativeData() {
        try {
            // as there is no strict criteria like in other  px objects
            ObjectIdentityData user = ObjectIdentityData.getAnyObjectFromList(automationCreatedUsersNotDeleted());
            UserRoleTestData testData = new UserRoleTestData(user, false)
                    .withUserRoles().withCustomRights().withStatus();
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
    public static Object[][] deleteUserData() {
        try {
            // as there is no strict criteria like in other  px objects
            ObjectIdentityData user = ObjectIdentityData.getAnyObjectFromList(automationCreatedUsersNotDeleted());
            return new Object[][]{
                    {user}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] resetUserPasswordPositiveData() {
        try {
            ObjectIdentityData user = ObjectIdentityData.getAnyObjectFromList(automationCreatedUsersNotDeleted());
            UserRoleTestData testData = new UserRoleTestData(user).mapToNames();
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
    public static Object[][] resetUserPasswordNegativeData() {
        try {
            ObjectIdentityData user = ObjectIdentityData.getAnyObjectFromList(automationCreatedUsersNotDeleted());
            UserRoleTestData testData = new UserRoleTestData(user, false).mapToNames();
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

    private static List<ObjectIdentityData> automationCreatedUsers(List<ObjectIdentityData> users) {
        return users.stream()
                .filter(user -> user.getName().contains("@") && !AUTOMATION_USER_PATTERN.matcher(user.getName()).find())
                .collect(Collectors.toList());
    }

    private static List<ObjectIdentityData> automationCreatedUsersNotDeleted() {
        String requestURL = new RequestedURL.Builder()
                .withRelativeURL("api/users")
                .withEmptyFilter()
                .sort("description", "asc")
                .build().getRequestedURL();
        JSONArray users = dataProvider.getDataAsJSONArray(requestURL);
        List<ObjectIdentityData> filteredUsers = new ArrayList<>();
        for (int i = 0; i < users.length(); i++) {
            JSONObject user = users.getJSONObject(i);
            String name = user.getString("userName");
            String status = user.getString("status");
            if (!status.equals("Deleted") && name.contains("@") && !AUTOMATION_USER_PATTERN.matcher(name).find())
                filteredUsers.add(new ObjectIdentityData(user));
        }
        return filteredUsers;
    }
}