package px.objects;

import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.LocaleData;
import dto.LxpDataProvider;
import dto.TestData;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import pages.groups.PaginationStrategy;
import px.reports.dto.FieldFormatObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static configuration.helpers.DataHelper.*;

/**
 * Created by kgr on 10/18/2016.
 */
// possibly create enum for test data state {CREATE, EDIT, DELETE and pass to constructor
// or even object with mode and positive or negative
public abstract class InstancesTestData implements TestData, PaginationStrategy {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected String name;
    protected String prevName;
    protected String description;
    protected LxpDataProvider dataProvider;
    // test data mode
    private DataMode dataMode;
    // edited
    protected String id;
    protected String guid;
    protected String instanceGroup;
    // i18n support
    protected LocaleData localeData;
    // headers
    protected String headersURL;
    protected List<FieldFormatObject> fields;
    // metricType unfortunately is used only for graphic parameters
    protected static Map<String, String> missedHeadersMetricsMap = new HashMap<>();
    // generic data mapping
    protected static List<ValuesMapper> dataMapping = new ArrayList<>();
    protected static Map<String, String> dataMap = new HashMap<>();

    /**
     * @param dataMode Data mode could be {CREATE, UPDATE, DELETE}
     *                 positive or negative
     *                 Default: CREATE and positive
     */
    public InstancesTestData(DataMode dataMode) {
        this.dataMode = dataMode;
        this.dataProvider = new LxpDataProvider();
    }

    public InstancesTestData setPositiveData() {
        this.name = this.getClass().getSimpleName().replace("TestData", "")
                + " Name " + RandomStringUtils.random(6, true, true);
        this.description = "Create " + this.getClass().getSimpleName().replace("TestData", " ") + name;
        return this;
    }

    public InstancesTestData setNegativeData() {
        this.localeData = new LocaleData();
        this.name = this.getClass().getSimpleName().replace("TestData", "")
                + " Name " + RandomStringUtils.random(6, true, true)
                + getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        this.description = "Create " + this.getClass().getSimpleName().replace("TestData", " ") + name;
        return this;
    }

    public boolean isPositive() {
        return dataMode.isPositive();
    }

    public boolean isCreateMode() {
        return dataMode.getMode().equals(DataModeEnum.CREATE.name());
    }

    public boolean isEditMode() {
        return dataMode.getMode().equals(DataModeEnum.UPDATE.name());
    }

    public boolean isDeleteMode() {
        return dataMode.getMode().equals(DataModeEnum.DELETE.name());
    }

    public boolean isCreatedByResponse() {
        return dataMode.isInitByJSON();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrevName() {
        return prevName;
    }

    public void setPrevName(String prevName) {
        this.prevName = prevName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstanceGroup() {
        return instanceGroup;
    }

    public void setInstanceGroup(String instanceGroup) {
        this.instanceGroup = instanceGroup;
    }

    // headers in table
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .build().getRequestedURL();
    }

    protected void setHeaderObjects() {
        setHeadersURL();
        JSONArray headers = new JSONArray(dataProvider.getDataAsString(headersURL, HttpMethodsEnum.OPTIONS));
        this.fields = new ArrayList<>(headers.length());
        for (int i = 0; i < headers.length(); i++) {
            FieldFormatObject field = new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i);
         /*   // set popup title according to mapping
            if (field.hasPopup()) field.getPopupData().setTitle(popupTitleMap.get(field.getName()));*/
            // set mapping
            if (ValuesMapper.hasMappedValues(dataMapping, field.getName()))
                field.setValuesMap(ValuesMapper.getMappedValues(dataMapping, field.getName()).getMap());
            fields.add(field);
        }
    }

    public List<FieldFormatObject> getFields() {
        return fields;
    }

    // reusable methods while to generate unique name
    protected String getWebSiteUnique() {
        return isPositive() ? "http://www." + RandomStringUtils.randomAlphabetic(8).toLowerCase() + "." + RandomStringUtils.randomAlphabetic(3)
                : "http://www." + getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 8)) + "." + RandomStringUtils.randomAlphabetic(3);
    }

    protected String getPercentage() {
        return getQuantity(100);
    }

    protected String getQuantity(int maxValue) {
       return getQuantity(1, maxValue);
    }

    protected String getQuantity(int minValue, int maxValue) {
        int value = getRandomInt(minValue, maxValue);
        value = isPositive() ? value : -value;
        return String.valueOf(value);
    }

    protected String getRandomCharSequence() {
        return getRandomCharSequence(6);
    }

    protected String getRandomCharSequence(int length) {
        return isPositive() ? RandomStringUtils.random(length, true, true)
                : getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), length));
    }

    @Override
    public int getRowsPerPage(int rows) {
        return rows;
    }

    @Override
    public String toString() {
        return "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
