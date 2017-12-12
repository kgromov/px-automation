package px.reports.rerun;

import configuration.helpers.DataHelper;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.InstancesTestData;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.*;

/**
 * Created by konstantin on 05.10.2017.
 */
public class RerunTaskTestData extends InstancesTestData {
    public static final DataMode POSITIVE_MODE = new DataMode.Builder()
            .createData().positiveData().build();
    public static final DataMode NEGATIVE_MODE = new DataMode.Builder()
            .createData().negativeData().build();
    public static final DataMode DELETE_MODE = new DataMode.Builder()
            .deleteData().positiveData().build();
    private Map<String, String> verticalsMap;
    private ObjectIdentityData filter;
    private String repeatCycle;
    private String ageMin;
    private String ageMax;
    private String lastAgeMin;      // dependent allowMultiple = 'Yes'
    private String lastAgeMax;      // dependent allowMultiple = 'Yes'
    // yes/no
    private String allowMultiple;
    private String ignoreStatus;    // dependent allowMultiple = 'No'
    private String doPixel;
    private String allowBiding;
    // triple radio
    private String soldBefore;
    private String soldToCampaign;
    // multiple select
    private List<String> verticals;
    private List<ObjectIdentityData> campaigns;
    // date time
    private Date activeFromDate;
    private Date activeToDate;
    private Date startTimeDate;
    private String activeFrom;
    private String activeTo;
    private String startTime;
    private String creationDate;
    // auxiliary to determine filter data is filled out pr not
    private boolean withFilterData = true;
    // static data
    private static final List<String> SOLD_BEFORE = Arrays.asList("Both", "Sold", "Not Sold");
    private static final List<String> AVAILABLE_VERTICALS = Arrays.asList("AU-DebtManagement", "AU-Life", "AU-Solar",
            "CA-AutoFinance", "CA-General", "CO-Auto",
            "US-Auto", "US-AutoWarranty", "US-Education", "US-General",
            "US-Health", "US-Home", "US-HomeSecurity", "US-Hvac", "US-Life",
            "US-Mortgage", "US-PayDayLoans", "US-Roofing", "US-Solar", "US-Windows"
    );

    public RerunTaskTestData(JSONObject jsonObject) {
        super(DELETE_MODE);
        this.guid = String.valueOf(jsonObject.get("rerunTaskGuid"));
        this.filter = new ObjectIdentityData(null,
                String.valueOf(jsonObject.get("rerunFilterName")),
                String.valueOf(jsonObject.get("rerunFilterGuid")));
        this.name = filter.getName();
        this.startTime = String.valueOf(jsonObject.get("rerunTime"));
        this.repeatCycle = String.valueOf(jsonObject.get("runCycle"));
        this.activeFrom = String.valueOf(jsonObject.get("filterFromDate"));
        this.activeTo = String.valueOf(jsonObject.get("filterToDate"));
        this.ageMin = String.valueOf(jsonObject.get("leadAgeMin")) + " day(s)";
        this.ageMax = String.valueOf(jsonObject.get("leadAgeMax")) + " day(s)";
        this.allowMultiple = String.valueOf(jsonObject.get("allowReposted"));
        this.ignoreStatus = String.valueOf(jsonObject.get("ignoreRerunStatus"));
        this.creationDate = String.valueOf(jsonObject.get("creationDate"));
    }

    public RerunTaskTestData(DataMode dataMode) {
        super(dataMode);
        // yes/no
        this.allowMultiple = DataHelper.getRandomYesNo();
        this.ignoreStatus = DataHelper.getRandomYesNo();
        this.doPixel = DataHelper.getRandomYesNo();
        this.allowBiding = DataHelper.getRandomYesNo();
        // triple radio
        this.soldBefore = getRandomValueFromList(SOLD_BEFORE);
        this.soldToCampaign = getRandomValueFromList(SOLD_BEFORE);
        // digit
        this.ageMin = getQuantity(30);
        this.ageMax = getQuantity(Integer.parseInt(ageMin), 30);
        this.lastAgeMin = getQuantity(30);
        this.lastAgeMax = getQuantity(Integer.parseInt(lastAgeMin), 30);
        // map
        Map<String, String> billingPeriodsMap = dataProvider.getPossibleValueFromJSON("BillingPeriod");
        this.repeatCycle = getRandomValueFromList(new ArrayList<>(billingPeriodsMap.keySet()));
        this.verticalsMap = dataProvider.getPossibleValueFromJSON("VerticalAndCountry");
        this.verticals = getRandomListFromList(AVAILABLE_VERTICALS, getRandomInt(1, 10));
        // by requests -> http://development-ui.stagingpx.com/api/lightModel/buyerInstancesByVerticalAndCountry?verticalCountryList=US-AutoFinance|US-AutoMotive-New|US-AutoWarranty
       /* this.campaigns = dataProvider.getCreatedInstancesData("buyerinstancesbycategory",
                Collections.singletonList("category"), Collections.singletonList("1"));*/
        this.campaigns = dataProvider.getCreatedInstancesData("buyerInstancesByVerticalAndCountry",
                Collections.singletonList("verticalCountryList"),
                Collections.singletonList(StringUtils.join(verticals, "|")));
//                Collections.singletonList(verticals.stream().map(verticalsMap::get).collect(Collectors.joining("|"))));
        if (!campaigns.isEmpty()) {
            Set<ObjectIdentityData> temp = new HashSet<>();
            int count = DataHelper.getRandomInt(1, campaigns.size() > 10 ? 10 : campaigns.size());
            for (int i = 0; i < count; i++) {
                temp.add(ObjectIdentityData.getAnyObjectFromList(campaigns));
            }
            campaigns = new ArrayList<>(temp);
        }
        // filterCondition
        List<ObjectIdentityData> filters = dataProvider.getCreatedInstancesData("rerunTaskFilters");
        // remove 'create new' -> another flow
        Iterator<ObjectIdentityData> filtersIterator = filters.iterator();
        while (filtersIterator.hasNext()) {
            if (Pattern.compile("0+").matcher(remainDigits(filtersIterator.next().getGuid())).matches())
                filtersIterator.remove();
        }
        this.filter = ObjectIdentityData.getAnyObjectFromList(filters);
        // date time
        int days = getRandomInt(3);
        this.activeFromDate = getDateByDaysOffset(days);
        this.activeToDate = getDateByDaysOffset(activeFromDate, days);
        this.activeFrom = getDateByFormatSimple(PX_REPORT_DATE_PATTERN, activeFromDate);
        this.activeTo = getDateByFormatSimple(PX_REPORT_DATE_PATTERN, activeToDate);
        this.startTime = getDateByFormatSimple(CALENDAR_TABLE_TIME_PATTERN, new Date(new Date().getTime() + 100_000));
        // set unique name
        if(isPositive()) setPositiveData();
        else setNegativeData();
    }

    public String getName() {
        return name;
    }

    public ObjectIdentityData getFilter() {
        return filter;
    }

    public String getRepeatCycle() {
        return repeatCycle;
    }

    public String getAgeMin() {
        return ageMin;
    }

    public String getAgeMax() {
        return ageMax;
    }

    public String getLastAgeMin() {
        return lastAgeMin;
    }

    public String getLastAgeMax() {
        return lastAgeMax;
    }

    public String getAllowMultiple() {
        return allowMultiple;
    }

    public String getIgnoreStatus() {
        return ignoreStatus;
    }

    public String getDoPixel() {
        return doPixel;
    }

    public String getAllowBiding() {
        return allowBiding;
    }

    public String getSoldBefore() {
        return soldBefore;
    }

    public String getSoldToCampaign() {
        return soldToCampaign;
    }

    public List<String> getVerticals() {
        return verticals.stream().map(vertical -> getKeyByValue(verticalsMap, vertical)).collect(Collectors.toList());
    }

    public List<ObjectIdentityData> getCampaigns() {
        return campaigns;
    }

    public Date getActiveFromDate() {
        return activeFromDate;
    }

    public Date getActiveToDate() {
        return activeToDate;
    }

    public Date getStartTimeDate() {
        return startTimeDate;
    }

    public String getActiveFrom() {
        return activeFrom;
    }

    public String getActiveTo() {
        return activeTo;
    }

    public String getStartTime() {
        return startTime;
    }

    public boolean isAllowedMultipleRerun() {
        return allowMultiple != null && allowMultiple.equals("Yes");
    }

    public boolean isAnyCampaign() {
        return campaigns != null && !campaigns.isEmpty();
    }

    public boolean withNewFilter() {
        return withFilterData;
    }

    public RerunTaskTestData withNewFilter(boolean withFilterData) {
        this.withFilterData = withFilterData;
        return this;
    }

    public void setFilterByNewTask() {
        List<ObjectIdentityData> filters = dataProvider.getCreatedInstancesData("rerunTaskFilters");
        this.filter = ObjectIdentityData.getObjectFromListByName(filters, name);
    }

    public String getNegativeData() {
        Map<String, String> negativeData = new HashMap<>();
        negativeData.put("ageMin", ageMin);
        negativeData.put("ageMin", ageMin);
        if (isAllowedMultipleRerun()) {
            negativeData.put("lastAgeMin", lastAgeMin);
            negativeData.put("lastAgeMax", lastAgeMax);
        }
        if (withFilterData) negativeData.put("filterName", name);
        return negativeData.toString();
    }

    @Override
    public String toString() {
        return "RerunTaskTestData{" +
                "name='" + name + '\'' +
                ", filter='" + filter + '\'' +
                ", repeatCycle='" + repeatCycle + '\'' +
                ", ageMin='" + ageMin + '\'' +
                ", ageMax='" + ageMax + '\'' +
                ", lastAgeMin='" + lastAgeMin + '\'' +
                ", lastAgeMax='" + lastAgeMax + '\'' +
                ", allowMultiple='" + allowMultiple + '\'' +
                ", ignoreStatus='" + ignoreStatus + '\'' +
                ", doPixel='" + doPixel + '\'' +
                ", allowBiding='" + allowBiding + '\'' +
                ", soldBefore='" + soldBefore + '\'' +
                ", soldToCampaign='" + soldToCampaign + '\'' +
                ", verticals=" + verticals +
                ", campaigns=" + campaigns +
                ", activeFrom='" + activeFrom + '\'' +
                ", activeTo='" + activeTo + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
