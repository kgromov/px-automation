package px.reports.rerun;

import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.LxpDataProvider;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONException;
import org.json.JSONObject;
import px.reports.ReportTestData;

import java.util.*;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getKeyByValueIgnoreCase;

/**
 * Created by kgr on 10/9/2017.
 */
public class FilterDetailsTestData extends ReportTestData {
    private ObjectIdentityData filter;
    // aggregated map
    private Map<String, String> filterDetails;
    // constants
    public static final String VERTICALS_KEY = "vertical";
    public static final String CAMPAIGNS_KEY = "buyerList";

    static {
        // mapping
        dataMap.put("0", "No");
        dataMap.put("1", "Yes");
        dataMap.put("false", "No");
        dataMap.put("true", "Yes");
        dataMapping.add(new ValuesMapper("doPixel", dataMap));
        dataMapping.add(new ValuesMapper("allowBidding", dataMap));
    }

    public FilterDetailsTestData(ObjectIdentityData filter) {
        this.dataProvider = new LxpDataProvider();
        this.filter = filter;
        setHeaders();
        // all available campaigns - to get their names
        List<ObjectIdentityData> campaigns = dataProvider.getCreatedInstancesData("buyerinstancesbycategory",
                Collections.singletonList("category"), Collections.singletonList("1"));
        // vertical and country map
        Map<String, String> verticalsMap = dataProvider.getPossibleValueFromJSON("VerticalAndCountry");
        this.filterDetails = new LinkedHashMap<>();
        for (int i = 0; i < headers.length(); i++) {
            JSONObject object = headers.getJSONObject(i);
            if (object.has("field")) {
                try {
                    String key = String.valueOf(object.get("field"));
                    String value = String.valueOf(object.get("default"));
                    // vertical from | to , join
                    if (key.equals(VERTICALS_KEY)) {
                        value = Arrays.stream(value.split("\\|")).map(item ->
                                getKeyByValueIgnoreCase(verticalsMap, item)).collect(Collectors.joining(", "));
                    }
                    // campaigns: id -> id - name
                    if (key.equals(CAMPAIGNS_KEY) && value != null && !value.isEmpty()) {
                        value = Arrays.stream(value.split("\\|")).map(id ->
                                id + " - " + ObjectIdentityData.getObjectFromListByID(campaigns, id).getName()
                        ).collect(Collectors.joining(", "));
                    }
                    filterDetails.put(key, value);
                } catch (JSONException e) {
                    throw new TestDataException(String.format("No 'default' value for " +
                            "'%s' field, json = '%s'", object.get("field"), object));
                }
            }
        }
        filterDetails.remove("filters");
//        filterDetails.remove("buyerListMode");
        filterDetails.remove("rerunFilterGuid");
    }

    public ObjectIdentityData getFilter() {
        return filter;
    }

    public Map<String, String> getFilterDetails() {
        return filterDetails;
    }

    @Override
    protected void setHeaders() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/rerunplanningtask/filter")
                .withParams("filterGuid", filter.getGuid())
                .build().getRequestedURL();
        this.fields = getFields(requestedURL);
    }

    @Override
    public String toString() {
        return "FilterDetailsTestData{" +
                "filter=" + filter +
                ", filterDetails=" + filterDetails +
                '}';
    }
}
