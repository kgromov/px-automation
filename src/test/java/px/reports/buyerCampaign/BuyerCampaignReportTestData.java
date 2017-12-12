package px.reports.buyerCampaign;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import px.reports.ReportTestData;
import px.reports.UserReports;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;

import java.util.*;
import java.util.stream.Collectors;

import static config.Config.LOCALE;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;
import static px.reports.buyerCampaign.BuyerCampaignReportFiltersEnum.DEFAULT_BAYER_CATEGORY;
import static px.reports.buyerCampaign.BuyerCampaignReportFiltersEnum.DEFAULT_REPORT_TYPE;

/**
 * Created by kgr on 3/16/2017.
 */
public class BuyerCampaignReportTestData extends ReportTestData implements UserReports {
    // filters data
    // report types - 'BuyerCategoryEnum'
    private String reportType = DEFAULT_REPORT_TYPE;
    private String buyerCategory = DEFAULT_BAYER_CATEGORY;
    private String buyerCategoryIndex;
    private List<String> transactionTypeList;
    private List<String> transactionTypeTextList;
    protected List<ObjectIdentityData> buyerCampaigns;
    private ObjectIdentityData buyerCampaign;
    private ObjectIdentityData defaultBuyerCampaign;
    private ObjectIdentityData defaultBuyerCampaignByCategory;
    // table data
    private JSONArray itemsByReportType;
    private JSONArray itemsByBuyerCategory;
    private JSONArray itemsByBuyerCampaign;
    private JSONArray itemsByTransactionType;
    private JSONArray itemsByAllFilters;
    private boolean hasCampaignsByCategory;
    private List<FieldFormatObject> fieldsNonCategory;
    // table rows count
    private int itemsByReportTypeCount;
    private int itemsByBuyerCategoryCount;
    // initial data
    private Map<String, String> buyerCategoriesMap;
    // filters reset
    protected AbstractFiltersResetData resetData;
    // filters
    public static final String REPORT_TYPE_FILTER = "ReportType";
    public static final String BUYER_CATEGORY_FILTER = "BuyerCategory";
    public static final String BUYER_CAMPAIGN_FILTER = "buyerinstanceguid";
    public static final String TRANSACTION_TYPE_FILTER = "TransactionType";

    static {
        popupTitleMap.put("filteredPings", "Filtered leads");
        popupTitleMap.put("pings", "Ping responses");
        popupTitleMap.put("averagePingBid", "Bid price spread");
        popupTitleMap.put("bidsWon", "Post responses");
        popupTitleMap.put("cpl", "Payout spread");
        // data mapping
        for (int i = 0; i <= 2; i++) {
            dataMap.put(String.format(LOCALE, "%." + i + "f", 200.0), "N/A");
            dataMap.put(String.format(LOCALE, "%." + i + "f", -200.0), "N/A");
        }
        dataMapping.add(new ValuesMapper("cpa", dataMap));
        dataMapping.add(new ValuesMapper("roi", dataMap));
        // transaction type
        dataMap = new HashMap<>();
        dataMap.put("Published", "Realtime");
        dataMap.put("FollowUp", "Rerun");
    }

    public BuyerCampaignReportTestData() {
        this.hasGraphics = true;
    }

    private BuyerCampaignReportTestData(boolean isInstances) {
        super(isInstances);
        this.hasGraphics = true;
        setInstanceGroup("buyerCampaign");
        setSorting("dateTime", "desc");
        setDateRanges();
        this.buyerCategoriesMap = dataProvider.getPossibleValueFromJSON("BuyerCategories");
        this.buyerCategoryIndex = buyerCategoriesMap.get(buyerCategory);
        // select all campaigns by category
        this.buyerCampaigns = dataProvider.getCreatedInstancesData("buyerinstancesbycategory",
                Collections.singletonList("category"), Collections.singletonList(buyerCategoryIndex));
        // default campaign - 1st in created list
        this.defaultBuyerCampaign = buyerCampaigns.get(0);
        this.defaultBuyerCampaignByCategory = defaultBuyerCampaign;
        this.buyerCampaign = defaultBuyerCampaign;
        // allRowsArray - all defaults data
        setAllRowsByDateRange();
       /* setHeaders();
        // default column fields
        this.fieldsNonCategory = fields;*/
        checkCampaignsTo1Buyer(buyerCampaigns);
    }

    public BuyerCampaignReportTestData(String reportType, String buyerCategory, boolean isInstances) {
        this(reportType, buyerCategory);
        this.isInstances = isInstances;
    }

    public BuyerCampaignReportTestData(String reportType, String buyerCategory) {
        this(true);
        this.reportType = reportType;
        this.buyerCategory = buyerCategory;
        // for report type
        setHeaders();
        // default column fields (default buyerCategory, non-default reportType)
        this.fieldsNonCategory = fields;
        // for buyerCategory and reportType
        this.buyerCategoryIndex = buyerCategoriesMap.get(buyerCategory);
        // they are always different cause of different category
        this.buyerCampaigns = !buyerCategory.equals(DEFAULT_BAYER_CATEGORY) ? dataProvider.getCreatedInstancesData("buyerinstancesbycategory",
                Collections.singletonList("category"), Collections.singletonList(buyerCategoryIndex)) : buyerCampaigns;
        this.hasCampaignsByCategory = !buyerCampaigns.isEmpty();
        // transaction type
        List<String> temp = new ArrayList<>(dataMap.keySet());
        this.transactionTypeList = DataHelper.getRandBoolean() ? temp : DataHelper.getRandomListFromList(temp, 1);
        this.transactionTypeTextList = transactionTypeList.stream()
                .map(type -> dataMap.get(type)).collect(Collectors.toList());
        // headers in report overview table
        setHeaders();
        // items by default buyerCampaign, default buyerCategory and non-default reportType
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList("BuyerCategory", REPORT_TYPE_FILTER),
                        Arrays.asList(buyerCategoriesMap.get(DEFAULT_BAYER_CATEGORY), REPORT_TYPE_MAP.get(reportType)))
                .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, "FromPeriod", "ToPeriod"), Arrays.asList(defaultBuyerCampaign.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByReportType = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        this.itemsByReportTypeCount = dataProvider.getCurrentTotal();
        // another case
        if (!hasCampaignsByCategory) {
            this.itemsByBuyerCategory = new JSONArray();
            this.itemsByBuyerCampaign = new JSONArray();
        } else {
            // default campaign - 1st in created list
            this.defaultBuyerCampaignByCategory = buyerCampaigns.get(0);
            // select any buyer campaign from created
            this.buyerCampaign = ObjectIdentityData.getAnyObjectFromList(buyerCampaigns);
            // items by default buyerCampaign  by category, non-default buyerCategory and non-default reportType
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams(Arrays.asList("BuyerCategory", REPORT_TYPE_FILTER),
                            Arrays.asList(buyerCategoryIndex, REPORT_TYPE_MAP.get(reportType)))
                    .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(defaultBuyerCampaignByCategory.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerCategory = !buyerCategory.equals(DEFAULT_BAYER_CATEGORY) ? dataProvider.getDataAsJSONArray(requestedURL) : itemsByReportType;
            this.itemsByBuyerCategoryCount = !buyerCategory.equals(DEFAULT_BAYER_CATEGORY) ? getItemsCurrentTotalCount() : itemsByReportTypeCount;
            if (!isInstances) return;
            // items by buyerCampaign - all non-default values
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams(Arrays.asList("BuyerCategory", REPORT_TYPE_FILTER),
                            Arrays.asList(buyerCategoryIndex, REPORT_TYPE_MAP.get(reportType)))
                    .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(buyerCampaign.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerCampaign = !buyerCampaign.getGuid().equals(defaultBuyerCampaignByCategory.getGuid()) ?
                    dataProvider.getDataAsJSONArray(requestedURL) : itemsByBuyerCategory;
            if (allowTransactionType()) {
                // rows in table by transaction type
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .withParams(Arrays.asList("BuyerCategory", REPORT_TYPE_FILTER),
                                Arrays.asList(buyerCategoryIndex, REPORT_TYPE_MAP.get(reportType)))
                        .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, TRANSACTION_TYPE_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(defaultBuyerCampaignByCategory.getGuid(), StringUtils.join(transactionTypeList, "|"), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByTransactionType = dataProvider.getDataAsJSONArray(requestedURL);
                // items by all filters
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .withParams(Arrays.asList("BuyerCategory", REPORT_TYPE_FILTER),
                                Arrays.asList(buyerCategoryIndex, REPORT_TYPE_MAP.get(reportType)))
                        .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, TRANSACTION_TYPE_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(buyerCampaign.getGuid(), StringUtils.join(transactionTypeList, "|"), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByAllFilters = buyerCampaign.equals(defaultBuyerCampaign) ? itemsByTransactionType : dataProvider.getDataAsJSONArray(requestedURL);
                // set filter values map
                this.resetData = new BuyerCampaignReportTestData.FiltersResetData();
                resetData.setFilterValuesMap();
            }
        }
    }

    // filter data
    public String getReportType() {
        return reportType;
    }

    public String getBuyerCategory() {
        return buyerCategory;
    }

    public List<String> getTransactionTypeList() {
        return transactionTypeTextList;
    }

    public ObjectIdentityData getBuyerCampaign() {
        return buyerCampaign;
    }

    public ObjectIdentityData getDefaultBuyerCampaign() {
        return defaultBuyerCampaign;
    }

    public ObjectIdentityData getDefaultBuyerCampaignByCategory() {
        return defaultBuyerCampaignByCategory;
    }

    public List<ObjectIdentityData> getBuyerCampaigns() {
        return buyerCampaigns;
    }

    // table data
    public JSONArray getItemsByReportType() {
        return itemsByReportType;
    }

    public JSONArray getItemsByBuyerCategory() {
        return itemsByBuyerCategory;
    }

    public JSONArray getItemsByBuyerCampaign() {
        return itemsByBuyerCampaign;
    }

    public JSONArray getItemsByTransactionType() {
        return itemsByTransactionType;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public int getItemsByReportTypeCount() {
        return itemsByReportTypeCount;
    }

    public int getItemsByBuyerCategoryCount() {
        return itemsByBuyerCategoryCount;
    }

    public boolean hasCampaignsByCategory() {
        return hasCampaignsByCategory;
    }

    public boolean allowTransactionType() {
        return hasCampaignsByCategory && buyerCategory.equals(DEFAULT_BAYER_CATEGORY) && Config.isAdmin();
    }

    public List<FieldFormatObject> getFieldsNonCategory() {
        return fieldsNonCategory;
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(BUYER_CATEGORY_FILTER, REPORT_TYPE_FILTER),
                        Arrays.asList(buyerCategoriesMap.get(DEFAULT_BAYER_CATEGORY), REPORT_TYPE_MAP.get(DEFAULT_REPORT_TYPE)))
                .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(defaultBuyerCampaign.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        // in case there are too many records
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup + "/metadata")
                .withParams(Arrays.asList(BUYER_CATEGORY_FILTER, REPORT_TYPE_FILTER),
                        Arrays.asList(this.buyerCategoryIndex, REPORT_TYPE_MAP.get(reportType)))
                .build().getRequestedURL();
    }

    @Override
    public List<String> filters() {
        return Arrays.asList(REPORT_TYPE_FILTER, BUYER_CATEGORY_FILTER, BUYER_CAMPAIGN_FILTER);
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "reportType='" + reportType + '\'' +
                        ", buyerCategory='" + buyerCategory + '\'' +
                        ", buyerCampaign=" + buyerCampaign.toString() +
                        ", transactionTypeList=" + transactionTypeList +
                        ", defaultBuyerCampaign=" + defaultBuyerCampaign.toString() +
                        ", defaultBuyerCampaignByCategory=" + defaultBuyerCampaignByCategory.toString() : "";
        return super.toString() +
                "\nBuyerReportTestData{" +
                instanceDetails +
                '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {

        public FiltersResetData() {
            this._instanceGroup = instanceGroup;
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams(Arrays.asList(BUYER_CATEGORY_FILTER, REPORT_TYPE_FILTER),
                            Arrays.asList(buyerCategoryIndex, REPORT_TYPE_MAP.get(reportType)))
                    .build().getRequestedURL().replace("?", "&");
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(BUYER_CAMPAIGN_FILTER, buyerCampaign.getGuid());
            filterValuesMap.put(TRANSACTION_TYPE_FILTER, StringUtils.join(transactionTypeList, "|"));
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(BUYER_CAMPAIGN_FILTER, buyerCampaign.getId() + " - " + buyerCampaign.getName());
            filterLabelValuesMap.put(TRANSACTION_TYPE_FILTER, transactionTypeList.toString());
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            if (filterValuesMap.size() > 1)
                return itemsByFiltersCombination;
            else if (filterValuesMap.size() == 0)
                return itemsByBuyerCategory;
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case BUYER_CAMPAIGN_FILTER:
                    return itemsByBuyerCampaign;
                case TRANSACTION_TYPE_FILTER:
                    return itemsByTransactionType;
            }
            return null;
        }

        @Override
        public void resetFilter() {
            List<String> combinationFiltersList = new ArrayList<>(filterValuesMap.keySet());
            this.filterReset = DataHelper.getRandomValueFromList(combinationFiltersList);
            log.info(String.format("Reset '%s' filter", filterReset));
            // to prevent redundant request, data already exists
            if (combinationFiltersList.size() <= 2) return;
            // cause data in table supposed to be without reset filter
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            resetIterationMap.remove(filterReset);
            // update to default campaign
            if (filterReset.equals(BUYER_CAMPAIGN_FILTER)) {
                resetIterationMap.put(BUYER_CAMPAIGN_FILTER, defaultBuyerCampaign.getGuid());
                filterLabelValuesMap.put(BUYER_CAMPAIGN_FILTER,
                        defaultBuyerCampaign.getId() + " - " + defaultBuyerCampaign.getName());
            }
            // keys in filter json request
            List<String> keys = new ArrayList<>(resetIterationMap.keySet());
            keys.addAll(Arrays.asList(_fromPeriodKey, _toPeriodKey));
            // values in filter json request
            List<String> values = new ArrayList<>(resetIterationMap.values());
            values.addAll(Arrays.asList(_fromPeriod, _toPeriod));
            // get rows in table after filter combination
            String requestedURL = new RequestedURL.Builder()
//                .withRelativeURL("api/" + _instanceGroup)
                    .withAbsoluteURL(_url)
                    .filter(keys, values)
                    .sort(_sortBy, _sortHow)
                    .build().getRequestedURL();
            this.itemsByFiltersCombination = dataProvider.getDataAsJSONArray(requestedURL);
        }
    }
}