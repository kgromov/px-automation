package px.objects.users;

import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 8/15/2017.
 */
public class UserRoleTestData extends UserTestData {
    private final LxpDataProvider dataProvider = new LxpDataProvider();
    protected ObjectIdentityData user;
    private List<ObjectIdentityData> userRoles;     // api/lightModel/credsetrights
    private ObjectIdentityData userRole;
    private List<ObjectIdentityData> customRights;  //api/customrights/user?userGuid=0349e33b-0617-407c-b91d-ec0240c8d3cc
    private ObjectIdentityData customRight;
    protected String fullName;
    protected String status;
    // for preview
    private Map<String, String> detailsMap;

    public UserRoleTestData() {
        this(true);
    }

    public UserRoleTestData(boolean isPositive) {
        super(isPositive);
        this.fullName = String.join(" ", firstName, lastName);
    }

    public UserRoleTestData(ObjectIdentityData user) {
        this(user, true);
    }

    public UserRoleTestData(ObjectIdentityData user, boolean isPositive) {
        this(isPositive);
        this.user = user;
        this.email = user.getName();
        this.fullName = String.join(" ", firstName, lastName);
        setDetailsMap();
    }

    public UserRoleTestData withUserRoles() {
        this.userRoles = dataProvider.getCreatedInstancesData("credsetrights");
        this.userRoles = ObjectIdentityData.getRandomSubList(userRoles);
        this.userRole = ObjectIdentityData.getAnyObjectFromList(userRoles);
        return this;
    }

    public UserRoleTestData withCustomRights() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/customrights/user")
                .withParams("userGuid", user.getGuid())
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL)).getJSONArray();
        this.customRights = ObjectIdentityData.getObjectsByJSONArray(jsonArray);
        this.customRights = ObjectIdentityData.getRandomSubList(customRights);
        this.customRight = ObjectIdentityData.getAnyObjectFromList(customRights);
        return this;
    }

    public UserRoleTestData withStatus() {
        Map<String, String> statuses = dataProvider.getPossibleValueFromJSON("status");
        this.status = getRandomValueFromList(new ArrayList<>(statuses.keySet()));
        return this;
    }

    public UserRoleTestData withStatus(String status) {
        this.status = status;
        return this;
    }

    public UserRoleTestData withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserRoleTestData withDataMode(boolean isPositive) {
        this.isPositive = isPositive;
        return this;
    }

    private UserRoleTestData setDetailsMap() {
        // aggregate like key-value
        this.detailsMap = new HashMap<>();
        // contact info
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/users/crud")
                .withParams("userGuid", user.getGuid())
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS)).getJSONArray();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (object.has("field")) {
                try {
                    // set data map
                    String key = String.valueOf(object.get("field"));
                    String value = object.has("default") ? String.valueOf(object.get("default")) : "";
                    // multi select values
                    if (key.equals("userRole") || key.equals("customRights")) {
                        value = value.replaceAll("(\\\\Set\\\\)|(\\\\Custom\\\\)", "").replaceAll("\\\\", ".");
                        String[] values = value.split("\\|");
                        value = StringUtils.join(values, ", ");
                    }
                    detailsMap.put(key, value);
                } catch (NullPointerException | JSONException e) {
                    System.out.println(String.format("Unable to handle field: '%s'\tDetails=\n%s", object, e.getMessage()));
                }
            }
        }
        return this;
    }

    public Map<String, String> getDetailsMap() {
        return detailsMap;
    }

    public Map<String, String> asMap() {
        Map<String, String> map = new HashMap<>();
        map.put("userName", email);
        map.put("description", fullName);
        map.put("status", status);
        // differences between create and edit mode
        Set<String> roles = new TreeSet<>(ObjectIdentityData.getAllNames(userRoles));
        Set<String> rights = new TreeSet<>(ObjectIdentityData.getAllNames(customRights));
        // if edit mode it's required to update with previous values
        if (detailsMap != null) {
            List<String> prevRoles = new ArrayList<>(Arrays.asList(detailsMap.get("userRole").split(", ")));
            List<String> temp = new ArrayList<>(prevRoles);
            // remove intersection
            prevRoles.removeAll(roles);
            // remain only unique (new) in roles to edit
            roles.removeAll(temp);
            // add unique
            roles.addAll(prevRoles);
            List<String> prevRights = new ArrayList<>(Arrays.asList(detailsMap.get("customRights").split(", ")));
            temp = new ArrayList<>(prevRights);
            // remove intersection
            prevRights.removeAll(rights);
            // remain only unique (new) in roles to edit
            rights.removeAll(temp);
            // add unique
            rights.addAll(prevRights);
            // remove empty items
            roles.remove("");
            rights.remove("");
        }
        map.put("userRole", roles.stream().collect(Collectors.joining(", ")));
        map.put("customRights", rights.stream().collect(Collectors.joining(", ")));
        return map;
    }

    public UserRoleTestData mapToNames() {
        if (detailsMap == null) return this;
        this.email = detailsMap.get("userName");
        this.fullName = detailsMap.get("description");
        this.status = detailsMap.get("status");
        return this;
    }

    public List<ObjectIdentityData> getUserRoles() {
        return userRoles;
    }

    public ObjectIdentityData getUserRole() {
        return userRole;
    }

    public List<ObjectIdentityData> getCustomRights() {
        return customRights;
    }

    public ObjectIdentityData getCustomRight() {
        return customRight;
    }

    public String getStatus() {
        return status;
    }

    public String getFullName() {
        return fullName;
    }

    public ObjectIdentityData getUser() {
        return user;
    }

    // only published users could login
    public boolean isUserAbleToLogin() {
        return status != null && status.equals("Published");
    }

    public boolean isUserDeleted() {
        return detailsMap != null && detailsMap.get("status").equals("Deleted");
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", password='" + password + '\'' +
                ", status='" + status + '\'' +
                (userRoles != null ? "uerRoles=" + userRoles.stream().map(ObjectIdentityData::getName).collect(Collectors.joining(", ")) : "") +
                ", userRole=" + userRole +
                (customRights != null ? ", customRights=" + customRights.stream().map(ObjectIdentityData::getName).collect(Collectors.joining(", ")) : "") +
                ", customRight=" + customRight +
                '}';
    }
}