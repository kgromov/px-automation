package px.reports.buyers;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import px.reports.ReportTestData;

import java.util.Arrays;
import java.util.List;

import static config.Config.LOCALE;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;
import static px.reports.buyers.BuyerReportFiltersEnum.DEFAULT_BAYER_CATEGORY;

/**
 * Created by kgr on 11/17/2016.
 */
public class BuyerReportTestData extends ReportTestData {
    // filters data
    protected String buyerCategory = DEFAULT_BAYER_CATEGORY; // report types - 'BuyerCategoryEnum'
    protected ObjectIdentityData buyer;
    protected ObjectIdentityData salesManager;
    // table data
    protected JSONArray itemsByBuyerCategory;
    private JSONArray itemsByBuyerGUID;     // parentbuyerguid
    protected JSONArray itemsBySalesManager;
    // after category, buyer and sales manager filtering
    private JSONArray itemsByBuyerAndSalesManagerGUID;
    // rows count
    private int itemsByCategoryCount;
    static {
        // data mapping
        for (int i = 0; i <= 2; i++) {
            dataMap.put(String.format(LOCALE, "%." + i + "f", 200.0), "N/A");
            dataMap.put(String.format(LOCALE, "%." + i + "f", -200.0), "N/A");
        }
        dataMapping.add(new ValuesMapper("cpa", dataMap));
        dataMapping.add(new ValuesMapper("roi", dataMap));
    }
    // filters
    public static final String REPORT_TYPE_FILTER = "ReportType";
    public static final String BUYER_FILTER = "parentbuyerguid";
    public static final String SALES_MANAGERS_FILTER = "SalesManagerGuid";

    public BuyerReportTestData() {
        this(false);
    }

    BuyerReportTestData(int filters) {
        super();
        this.hasGraphics = true;
    }

    BuyerReportTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("buyerSummary/report");
        setSorting("dateTime", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
    }

    public BuyerReportTestData(String buyerCategory) {
        this(true);
        this.buyerCategory = buyerCategory;
        // headers in report overview table
        setHeaders();
        // choose any buyer from available list
        this.buyer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("buyers"));
        // items in table by buyer category
        setItemsByReportType(buyerCategory);
        // items in table by buyer category and buyer guid
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, buyerCategory)
                .filter(Arrays.asList(BUYER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(buyer.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBuyerGUID = dataProvider.getDataAsJSONArray(requestedURL);
        // items in sales managers drop down
        List<ObjectIdentityData> salesManagers = dataProvider.getCreatedInstancesData("buyerSalesManager");
        this.salesManager = getFilterObject(salesManagers, allRowsArray, "salesManagerUsername", "name");
        // items in table by sales manager
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, buyerCategory)
                .filter(Arrays.asList(SALES_MANAGERS_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(salesManager.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsBySalesManager = dataProvider.getDataAsJSONArray(requestedURL);
        // items in table by both buyer and sales manager
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, buyerCategory)
                .filter(Arrays.asList(BUYER_FILTER, SALES_MANAGERS_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(buyer.getGuid(), salesManager.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBuyerAndSalesManagerGUID = dataProvider.getDataAsJSONArray(requestedURL);
    }

    // filter data
    public ObjectIdentityData getBuyer() {
        return buyer;
    }

    public String getBuyerCategory() {
        return buyerCategory;
    }

    public String getSalesManagerName() {
        return salesManager.getName();
    }

    // table data
    public JSONArray getItemsByBuyerCategory() {
        return itemsByBuyerCategory;
    }

    public JSONArray getItemsByBuyerGUID() {
        return itemsByBuyerGUID;
    }

    public JSONArray getItemsBySalesManager() {
        return itemsBySalesManager;
    }

    public JSONArray getItemsByBuyerAndSalesManagerGUID() {
        return itemsByBuyerAndSalesManagerGUID;
    }

    public int getItemsByCategoryCount() {
        return itemsByCategoryCount;
    }

    public void setItemsByReportType(String reportType) {
        this.buyerCategory = reportType;
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, buyerCategory)
                .filter(Arrays.asList("FromPeriod", "ToPeriod"),
                        Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBuyerCategory = !buyerCategory.equals(DEFAULT_BAYER_CATEGORY) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        this.itemsByCategoryCount = buyerCategory.equals(DEFAULT_BAYER_CATEGORY) ? totalCount : dataProvider.getCurrentTotal();
        setHeaders();
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, buyerCategory)
                .filter(Arrays.asList("FromPeriod", "ToPeriod"),
                        Arrays.asList(fromPeriod, toPeriod))
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
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, buyerCategory)
                .build().getRequestedURL();
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances
                ? "buyer=" + (buyer != null ? buyer.toString() : "") +
                ", buyerCategory=" + buyerCategory +
                ", salesManager=" + (salesManager != null ? salesManager.toString() : "") +
                ", headersList=" + headersList : "";
        return super.toString() +
                "\nBuyerReportTestData{" +
                instanceDetails +
                '}';
    }
}