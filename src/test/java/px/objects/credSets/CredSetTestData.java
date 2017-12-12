package px.objects.credSets;

import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.InstancesTestData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kgr on 10/30/2017.
 */
public class CredSetTestData extends InstancesTestData {
    private Map<String, String> details;
    private Map<String, Boolean> editableFieldsMap;

    public CredSetTestData(DataMode dataMode) {
        super(dataMode);
    }

    public CredSetTestData(JSONObject object) {
        super(DataMode.getCreatedByResponse());
        this.guid = String.valueOf(object.get("credSetGuid"));
        this.name = String.valueOf(object.get("credSetName"));
        this.description = String.valueOf(object.get("description"));
        this.prevName = name;
        setDetails(guid);
    }

    public CredSetTestData(CredSetsOverviewData overviewData) {
        super(DataMode.getCreatedByResponse());
        if (overviewData.getItemsTotalCount() > 0) {
            int index = DataHelper.getRandomInt(overviewData.getItemsTotalCount());
            JSONObject object = overviewData.getAllRowsArray().getJSONObject(index);
            this.name = String.valueOf(object.get("credSetName"));
            this.guid = String.valueOf(object.get("credSetGuid"));
            this.description = String.valueOf(object.get("description"));
        }
    }

    public CredSetTestData setDetails(String guid) {
        // credSet details data
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/credset")
                .withParams("guid", guid)
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        this.details = new HashMap<>();
        this.editableFieldsMap = new HashMap<>();
        JSONWrapper.toList(jsonArray).stream().filter(credSet -> credSet.has("field")).forEach(credSet -> {
            String key = String.valueOf(credSet.get("field"));
            String value = String.valueOf(credSet.get("default"));
//            boolean isEditable = credSet.has("editable") && Boolean.parseBoolean(String.valueOf(credSet.get("editable")));
            details.put(key, value);
            editableFieldsMap.put(key, key.equalsIgnoreCase("description"));
        });
        return this;
    }

    public Map<String, String> asMap() {
        this.details = new HashMap<>();
        details.put("credSetName", prevName != null ? prevName : name);
        details.put("credSetGuid", guid);
        details.put("description", description);
        return details;
    }

    public Map<String, String> asEditableMap() {
        this.editableFieldsMap = new HashMap<>();
        editableFieldsMap.put("credSetName", false);
        editableFieldsMap.put("credSetGuid", false);
        editableFieldsMap.put("description", true);
        return details;
    }

    public Map<String, String> getDetails() {
        return details;
    }

    public Map<String, Boolean> getEditableFieldsMap() {
        return editableFieldsMap;
    }

    @Override
    public CredSetTestData setPositiveData() {
        super.setPositiveData();
        this.name = name.replaceAll("\\s*", "");
        return this;
    }

}
