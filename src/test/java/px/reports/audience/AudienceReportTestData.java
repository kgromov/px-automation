package px.reports.audience;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;

import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;

/**
 * Created by kgr on 4/24/2017.
 */
public class AudienceReportTestData extends ReportTestData {
    {
        super.startMonthOffset = 3;
        this.defaultReportTypesMap = getReportTypeMap(DEFAULT_VERTICAL);
    }

    // filters data
    protected Map<String, String> defaultReportTypesMap; // /api/lightModel/audienceReportTypes?filterParameter={"vertical":"all"}
    protected String vertical;
    protected ObjectIdentityData buyer;
    protected ObjectIdentityData publisher;
    // doubts
    protected ObjectIdentityData buyerCampaign;
    protected ObjectIdentityData subID;
    // table data
    protected JSONArray itemsByReportType;        // // ReportType=<value> vertical is DEFAULT, could be invoked while 'ReportType' filter verification
    protected JSONArray itemsByVertical;          // vertical
    protected JSONArray itemsByBuyerGUID;         // buyerguid
    protected JSONArray itemsByPublisherGUID;     // PublisherGuid
    protected JSONArray itemsByBuyerInstanceGUID;     // buyerinstanceguid
    protected JSONArray itemsByPublisherInstanceGUID; // PublisherInstanceGuid
    protected JSONArray itemsByAllFilters;
    protected int itemsByReportTypeCount;
    // auxiliary items by report type
    protected JSONArray itemsByDefaultReportType;         // ReportType=DEFAULT vertical is chosen, could be invoked while 'Vertical' filter verification
    protected JSONArray itemsByReportTypeNoVertical;         // ReportType=<ANY> vertical is not chosen, could be invoked while 'Vertical' filter verification
    // sub instances
    protected List<ObjectIdentityData> buyerCampaigns;  // /api/lightModel/buyerinstancesbyparent?parent=<buyer_guid>
    private List<ObjectIdentityData> subIDs;          // /api/lightModel/publisherinstances?publisherGuid=<publisher_guid>
    // initial data
    protected Map<String, String> reportTypesMap = defaultReportTypesMap;
    protected String reportType = DEFAULT_REPORT_TYPE;
    private Map<String, String> verticalsMap;
    // filters reset
    protected List<FieldFormatObject> fieldsByDefaultReportType;
    protected AbstractFiltersResetData resetData;
    // constants
    public static final String REPORT_TYPE_FILTER = "ReportType";
    public static final String BUYER_FILTER = "buyerguid";
    public static final String BUYER_INSTANCE_FILTER = "buyerinstanceguid";
    public static final String PUBLISHER_FILTER = "PublisherGuid";
    public static final String PUBLISHER_INSTANCE_FILTER = "PublisherInstanceGuid";
    public static final String VERTICAL_FILTER = "vertical";

    public AudienceReportTestData(int filters) {
        super(false);
        this.hasGraphics = true;
    }

    public AudienceReportTestData() {
        this(false);
        super.setHeaders();
    }

    public AudienceReportTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("audience/report");
        setSorting("conversions", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        if (isInstances) {
            // set prerequisite data
            this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
            // cause of bug - verticals (enumSequenceIndex=[23; 38]) have no report types
//            verticalsMap = AudienceReportTestData.getProperVerticals(verticalsMap);
            this.vertical = DataHelper.getRandomValueFromList(new ArrayList<>(verticalsMap.keySet()));
            this.reportTypesMap = getReportTypeMap(vertical);
            // get list of available buyers/publishers
            this.buyer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("buyers"));
            // buyer instances - filter items
            this.buyerCampaigns = dataProvider.getCreatedInstancesData("buyerinstancesbyparent",
                    Collections.singletonList("parent"), Collections.singletonList(buyer.getGuid()));
            this.buyerCampaign = buyerCampaigns.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(buyerCampaigns);
            if (Config.isAdmin()) {
                this.publisher = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publishers"));
                // publisher instances - filter items
                this.subIDs = dataProvider.getCreatedInstancesData("publisherinstances",
                        Collections.singletonList("publisherGuid"), Collections.singletonList(publisher.getGuid()));
                this.subID = subIDs.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(subIDs);
            }
            // rows in table by default report type and vertical
            log.info("Rows in table by default report type and vertical");
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", defaultReportTypesMap.get(DEFAULT_REPORT_TYPE))
                    .filter(Arrays.asList("vertical", "FromPeriod", "ToPeriod"), Arrays.asList(vertical, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByDefaultReportType = dataProvider.getDataAsJSONArray(requestedURL);
        }
    }

    // filter data
    public String getReportType() {
        return reportType;
    }

    public String getVertical() {
        return vertical;
    }

    public ObjectIdentityData getBuyer() {
        return buyer;
    }

    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getBuyerCampaign() {
        return buyerCampaign;
    }

    public ObjectIdentityData getSubID() {
        return subID;
    }

    public List<ObjectIdentityData> getBuyerCampaigns() {
        return buyerCampaigns;
    }

    public List<ObjectIdentityData> getSubIDs() {
        return subIDs;
    }

    public Set<String> getReportTypes() {
        return reportTypesMap.keySet();
    }

    public Set<String> getDefaultReportTypes() {
        return new TreeSet<>(defaultReportTypesMap.keySet());
    }

    // table data
    public JSONArray getItemsByDefaultReportType() {
        return itemsByDefaultReportType;
    }

    public JSONArray getItemsByReportTypeNoVertical() {
        return itemsByReportTypeNoVertical;
    }

    public JSONArray getItemsByReportType() {
        return itemsByReportType;
    }

    public JSONArray getItemsByVertical() {
        return itemsByVertical;
    }

    public JSONArray getItemsByBuyerGUID() {
        return itemsByBuyerGUID;
    }

    public JSONArray getItemsByPublisherGUID() {
        return itemsByPublisherGUID;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public JSONArray getItemsByBuyerInstanceGUID() {
        return itemsByBuyerInstanceGUID;
    }

    public JSONArray getItemsByPublisherInstanceGUID() {
        return itemsByPublisherInstanceGUID;
    }

    public int getItemsByReportTypeCount() {
        return itemsByReportTypeCount;
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public boolean hasBuyerInstances() {
        return buyerCampaigns != null && !buyerCampaigns.isEmpty();
    }

    public boolean hasPublisherInstances() {
        return subIDs != null && !subIDs.isEmpty();
    }

    public boolean hasChangedAfterVerticalReset() {
        return !reportTypesMap.containsKey(reportType);
    }

    public static Map<String, String> getReportTypeMap(String vertical) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/audienceReportTypes")
                .withParams("filterParameter", String.format("{\"vertical\":\"%s\"}", vertical))
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(new LxpDataProvider().getDataAsString(requestedURL));
        // validation for empty reportTypes - issue point in ticker
        try {
            assertThat(String.format("There are no report types by vertical '%s', request = %s",
                    vertical, requestedURL),
                    jsonArray.length(), greaterThan(0));
        } catch (AssertionError e) {
            throw new TestDataException(e.getMessage());
        }
        Map<String, String> map = new HashMap<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            map.put(String.valueOf(object.get("enumDescription")), String.valueOf(object.get("enumSequenceNumber")));
        }
        return map;
    }

    // workaround cause of bug - verticals (enumSequenceIndex=[23; 38]) have no report types
    public static Map<String, String> getProperVerticals(Map<String, String> verticalsMap) {
        Map<String, String> tempMap = new HashMap<>(verticalsMap);
        for (Map.Entry<String, String> vertical : verticalsMap.entrySet()) {
            int number = Integer.parseInt(vertical.getValue());
            if (number >= 24 & number <= 39) tempMap.remove(vertical.getKey());
        }
        return tempMap;
    }

    public void setItemsByReportType(String reportType) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", defaultReportTypesMap.get(reportType))
                .filter(Arrays.asList("vertical", "FromPeriod", "ToPeriod"), Arrays.asList(vertical, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByDefaultReportType = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        this.itemsByReportTypeCount = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getCurrentTotal() : totalCount;
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", reportTypesMap.get(reportType))
                .build().getRequestedURL();
    }

    @Override
    protected void setHeaders() {
        super.setHeaders();
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", defaultReportTypesMap.get(DEFAULT_REPORT_TYPE))
                .build().getRequestedURL();
        JSONArray headers = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        List<String> headersList = DataHelper.getListFromJSONArrayByKey(headers, "field");
        // common
        this.fieldsByDefaultReportType = new ArrayList<>(headersList.size());
        for (int i = 0; i < headersList.size(); i++) {
            FieldFormatObject field = new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i);
            // set popup title according to mapping
            if (field.hasPopup()) field.getPopupData().setTitle(popupTitleMap.get(field.getName()));
            fieldsByDefaultReportType.add(field);
        }
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", defaultReportTypesMap.get(reportType))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        // in case there are too many records
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

    @Override
    public List<String> filters() {
        return Arrays.asList(VERTICAL_FILTER, REPORT_TYPE_FILTER, BUYER_INSTANCE_FILTER);
    }

    @Override
    public String toString() {
        String buyerCampaignDetails = isInstances && !buyerCampaigns.isEmpty() ? buyerCampaign.toString() : "";
        String subIdDetails = isInstances && !subIDs.isEmpty() ? subID.toString() : "";
        String instanceDetails = isInstances ?
                "reportType=" + reportType +
                        ", vertical=" + vertical +
                        ", buyer=" + buyer +
                        ", publisher=" + publisher +
                        ", buyerCampaign=" + buyerCampaignDetails +
                        ", subID=" + subIdDetails +
                        ", buyerCampaigns=" + buyerCampaigns.stream().map(campaign -> campaign.getId() +
                        " - " + campaign.getName()).collect(Collectors.joining(", ")) +
                        ", subIDs=" + subIDs.stream().map(ObjectIdentityData::getId).collect(Collectors.joining(", "))
                : "";
        return super.toString() +
                "\nAudienceReportTestData{" +
                instanceDetails +
                '}';
    }
}