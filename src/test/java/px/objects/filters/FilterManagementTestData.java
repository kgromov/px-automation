package px.objects.filters;

import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import dto.TestDataException;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import px.objects.filters.nodes.FilterNode;
import px.objects.filters.nodes.FilterNodeData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getListFromJSONArrayByKey;

/**
 * Created by kgr on 10/23/2017.
 */
public class FilterManagementTestData {
    private static final Logger log = Logger.getLogger(FilterManagementTestData.class);
    private final LxpDataProvider dataProvider;
    private final List<String> commands;
    private final List<String> functions;
    private List<FilterNodeData> fields;
    public static Map<String, String> FILTER_FIELDS_MAP = new HashMap<>();
    public static Map<String, String> FILTER_COMMANDS_MAP = new HashMap<>();
    // for range command only digits are available
    private static final List<String> RANGE_FIELDS = Arrays.asList(
            "ContactData.ZIPCode", "ContactData.DayPhoneNumber", "ContactData.PhoneNumber", "ContactData.IPAddress");
    public static final List<String> RANGE_COMMANDS = Arrays.asList("InRange", "NotInRange");
    public static final List<String> MULTI_SELECT_COMMANDS = Arrays.asList("In", "NotIn");
    // filter file
    public static final String FILTERS_FOLDER = "\\\\37.97.221.171\\g$\\demo_02\\virtualstore\\filters\\";

    static {
        // names (commandTag; after save replace . -> --)
        FILTER_FIELDS_MAP.put("ContactData.FirstName", "Demographic");
        FILTER_FIELDS_MAP.put("ContactData.LastName", "Demographic");
        FILTER_FIELDS_MAP.put("ContactData.EmailAddress", "Demographic");
        FILTER_FIELDS_MAP.put("ContactData.PhoneNumber", "Demographic");
        FILTER_FIELDS_MAP.put("ContactData.DayPhoneNumber", "Demographic");
        FILTER_FIELDS_MAP.put("ContactData.ResidenceType", "Demographic");
        FILTER_FIELDS_MAP.put("ContactData.YearsAtResidence", "Demographic");
        FILTER_FIELDS_MAP.put("ContactData.MonthsAtResidence", "Demographic");

        FILTER_FIELDS_MAP.put("ContactData.Address", "Geography");
        FILTER_FIELDS_MAP.put("ContactData.City", "Geography");
        FILTER_FIELDS_MAP.put("ContactData.State", "Geography");
        FILTER_FIELDS_MAP.put("ContactData.ZIPCode", "Geography");
        FILTER_FIELDS_MAP.put("ContactData.IPAddress", "Geography");
        FILTER_FIELDS_MAP.put("ContactData.County", "Geography");
        // commands (for select - first 4)
        // {InRange;  NotInRange} => 2 fields
        FILTER_COMMANDS_MAP.put("Equal", "Is");
        FILTER_COMMANDS_MAP.put("NotEqual", "Is not");
        FILTER_COMMANDS_MAP.put("In", "In a list");
        FILTER_COMMANDS_MAP.put("NotIn", "Not in a list");
        FILTER_COMMANDS_MAP.put("NotContain", "Not contain");
        FILTER_COMMANDS_MAP.put("InRange", "Between");
        FILTER_COMMANDS_MAP.put("NotInRange", "Not between");
        FILTER_COMMANDS_MAP.put("InPart", "In part");
        FILTER_COMMANDS_MAP.put("NotInPart", "Not in part");
        FILTER_COMMANDS_MAP.put("NotMask", "Not mask");
         /*FILTER_COMMANDS_MAP.put("Less", "Less");
        FILTER_COMMANDS_MAP.put("Greater", "Greater");
        FILTER_COMMANDS_MAP.put("Mask", "Mask");
        FILTER_COMMANDS_MAP.put("Contain", "Contain");*/
        // probably functions as well
    }

    public FilterManagementTestData(String country, String vertical) {
        dataProvider = new LxpDataProvider();
        // available commands
        this.commands = commands();
        // available filters
        this.functions = functions();
        // categories
        List<JSONObject> allFields = allFields(country, vertical);
        this.fields = contactData(allFields, commands);
    }

    // to get filter by campaign - api/buyercampaignfilters/filter?buyerInstanceGuid=GUID
    public List<FilterNode> getFilterTree(String guid) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyercampaignfilters/filter")
                .withParams("buyerInstanceGuid", guid)
                .build().getRequestedURL();
        try {
            JSONObject filter = new JSONObject(dataProvider.getDataAsString(requestedURL));
            if (filter.has("filterItems")) {
                JSONArray filterItems = filter.getJSONArray("filterItems");
                if (filterItems.length() > 0) {
                    JSONObject root = filterItems.getJSONObject(0).getJSONObject("params");
                    List<JSONObject> items = JSONWrapper.toList(root.getJSONArray("commands"));
                    return items.stream().map(FilterNode::new).collect(Collectors.toList());
                }
            }
        } catch (JSONException e) {
            throw new TestDataException("Unable to bypass filter tree", e);
        }
        return new ArrayList<>();
    }

    private List<FilterNodeData> allFields_(String country, String vertical) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyercampaignfilters/options/tags")
                .withParams(Arrays.asList("country", "vertical"),
                        Arrays.asList(country, vertical))
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.GET));
        return JSONWrapper.toList(jsonArray).stream().map(FilterNodeData::new).collect(Collectors.toList());
    }

    private List<JSONObject> allFields(String country, String vertical) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyercampaignfilters/options/tags")
                .withParams(Arrays.asList("country", "vertical"),
                        Arrays.asList(country, vertical))
                .build().getRequestedURL();
        JSONArray jsonArray = dataProvider.getDataAsJSONArray(requestedURL);
//        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.GET));
        return JSONWrapper.toList(jsonArray);
    }

    private List<FilterNodeData> contactData_(List<FilterNodeData> fields) {
        return fields.stream().filter(field ->
                field.getName().contains("ContactData.")).collect(Collectors.toList());
    }

    public List<FilterNodeData> contactData(List<JSONObject> fields) {
        return fields.stream().filter(field ->
                field.getString("field").contains("ContactData."))
                .map(FilterNodeData::new).collect(Collectors.toList());
    }

    public List<FilterNodeData> contactData(List<JSONObject> fields, List<String> commands) {
        return fields.stream().filter(field ->
                field.getString("field").contains("ContactData."))
                .map(field -> new FilterNodeData(field, commands)).collect(Collectors.toList());
    }

    private List<String> commands() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyercampaignfilters/options/commands")
                .build().getRequestedURL();
        JSONArray objects = dataProvider.getDataAsJSONArray(requestedURL);
        return getListFromJSONArrayByKey(objects, "name");
    }

    private List<String> functions() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyercampaignfilters/options/functions")
                .build().getRequestedURL();
        JSONArray objects = dataProvider.getDataAsJSONArray(requestedURL);
        return getListFromJSONArrayByKey(objects, "name");
    }

    public List<String> getCommands() {
        return commands;
    }

    public static List<String> getMultiCommands() {
        return Arrays.asList("Equal", "NotEqual", "In", "NotIn");
    }

    public static boolean isRangeCommand(String command) {
        return command.contains("In");
    }

    public static boolean isRangeAllowed(String name) {
        return RANGE_FIELDS.contains(name);
    }

    public List<String> getFunctions() {
        return functions;
    }

    public List<FilterNodeData> getFields() {
        return fields;
    }

    // ==================================== Check filter xml file =====================================
    public static void checkFilterFile(String campaignID) {
        log.info("Check that filter file was created for campaign with ID = " + campaignID);
        FileFilter CAMPAIGN_FILTER = log -> log.isFile() && log.getName().contains(campaignID) && log.getName().contains(".xml");
        File[] filters = new File(FILTERS_FOLDER).listFiles(CAMPAIGN_FILTER);
        if (filters == null || filters.length == 0)
            throw new TestDataException("No filter file by campaignId = " + campaignID);
        else if (filters.length > 1)
            throw new TestDataException(String.format("There are %d filter files by campaignId = %s", filters.length, campaignID));
        // deserialize filter
        File campaignFilter = filters[0];
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(campaignFilter);
            Element root = doc.getDocumentElement();
            log.info("filter content:\n" + root.toString());
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new TestDataException(String.format("Unable to deserialize campaign filter %s\tCause = %s", campaignFilter.getName(), e.getMessage()), e);
        }
    }

}
