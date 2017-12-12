package px.reports.buyerPerformance;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;

import java.util.*;

import static config.Config.LOCALE;

/**
 * Created by kgr on 3/30/2017.
 */
public class BuyerPerformanceTestData extends ReportTestData {
    // filters data
    private ObjectIdentityData publisher;
    private String buyerCategory;
    private List<String> verticalList;
    // initial data
    private Map<String, String> verticalsMap;
    // table data
    private JSONArray itemsByPublisherID;    // PublisherId
    private JSONArray itemsByBuyerCategory; // ?ReportType=<Category>
    private JSONArray itemsByVertical;      // Vertical
    private JSONArray itemsByAllFilters;
    private int buyerCategoryItemsCount;
    // filters reset
    private List<FieldFormatObject> fieldsNonCategory;
    protected AbstractFiltersResetData resetData;
    // constants
    public static final String BUYER_CATEGORY_FILTER = "ReportType";
    public static final String PUBLISHER_FILTER = "PublisherId";
    public static final String VERTICAL_FILTER = "Vertical";

    static {
        // data mapping
        /*int k = 200;
        dataMap.put(String.format("%d", k), "N/A");
        dataMap.put(String.format("-%d", k), "N/A");*/
        for (int i = 0; i <= 2; i++) {
           /* dataMap.put(String.format("%d,%0" + i + "d", k, 0), "N/A");
            dataMap.put(String.format("-%d,%0" + i + "d", k, 0), "N/A");
            dataMap.put(String.format("%d.%0" + i + "d", k, 0), "N/A");
            dataMap.put(String.format("-%d.%0" + i + "d", k, 0), "N/A");*/
            dataMap.put(String.format(LOCALE, "%." + i + "f", 200.0), "N/A");
            dataMap.put(String.format(LOCALE, "%." + i + "f", -200.0), "N/A");
        }
        dataMapping.add(new ValuesMapper("cpa", dataMap));
        dataMapping.add(new ValuesMapper("roi", dataMap));
    }

    public BuyerPerformanceTestData() {
        this(false);
    }

    private BuyerPerformanceTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("buyerPerformance/report");
        setSorting("spend", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
    }

    public BuyerPerformanceTestData(String buyerCategory) {
        this(true);
        this.buyerCategory = buyerCategory;
        super.setHeaders();
        this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
        this.verticalList = DataHelper.getRandomListFromList(new ArrayList<>(verticalsMap.keySet()),
                DataHelper.getRandomInt(1, verticalsMap.size() > 10 ? 10 : verticalsMap.size()));
        // choose any publisher from available list
        this.publisher = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publishers"));
        // items in table by buyer category
        setItemsByReportType(buyerCategory);
        // items in table by publisher id
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", buyerCategory)
                .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(publisher.getId(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByPublisherID = dataProvider.getDataAsJSONArray(requestedURL);
        // items in table by publisher id
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", buyerCategory)
                .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(verticalList, "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
        // all filters
        // items in table by publisher id
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", buyerCategory)
                .filter(Arrays.asList(PUBLISHER_FILTER, VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(publisher.getId(), StringUtils.join(verticalList, "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new BuyerPerformanceTestData.FiltersResetData();
        resetData.setFilterValuesMap();
    }

    // filter data
    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public String getBuyerCategory() {
        return buyerCategory;
    }

    public List<String> getVerticalList() {
        return verticalList;
    }

    // table data
    public JSONArray getItemsByPublisherID() {
        return itemsByPublisherID;
    }

    public JSONArray getItemsByBuyerCategory() {
        return itemsByBuyerCategory;
    }

    public JSONArray getItemsByVertical() {
        return itemsByVertical;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public int getBuyerCategoryItemsCount() {
        return buyerCategoryItemsCount;
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public void setItemsByReportType(String buyerCategory) {
        this.buyerCategory = buyerCategory;
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", buyerCategory)
                .filter(Arrays.asList("FromPeriod", "ToPeriod"),
                        Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBuyerCategory = dataProvider.getDataAsJSONArray(requestedURL);
        this.buyerCategoryItemsCount = dataProvider.getCurrentTotal();
    }

    public List<FieldFormatObject> getBuyerCategoriesFields() {
        return fields;
    }

    @Override
    public List<FieldFormatObject> getFields() {
        return isInstances && !((FiltersResetData) resetData).isChangeToDefaultFields() ? fields : fieldsNonCategory;
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = isInstances ? new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", buyerCategory)
                .build().getRequestedURL()
                : new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .build().getRequestedURL();
    }

    @Override
    protected void setHeaders() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .build().getRequestedURL();
        this.fieldsNonCategory = getFields(requestedURL);
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "buyerCategory=" + buyerCategory +
                        ", publisher=" + publisher.toString() +
                        ", verticalList=" + verticalList : "";
        return super.toString() +
                "BuyerPerformanceTestData{" + instanceDetails + '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {
        FiltersResetData() {
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL().replace("?", "&");
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
        }

        boolean isChangeToDefaultFields() {
            return !filterValuesMap.containsKey(BUYER_CATEGORY_FILTER);
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(PUBLISHER_FILTER, publisher.getId());
            filterValuesMap.put(VERTICAL_FILTER, StringUtils.join(verticalList, "|"));
            filterValuesMap.put(BUYER_CATEGORY_FILTER, buyerCategory);
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            filterLabelValuesMap.put(VERTICAL_FILTER, verticalList.toString());
            filterLabelValuesMap.put(BUYER_CATEGORY_FILTER, buyerCategory);
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            if (resetIterationMap.size() > 1) {
                resetIterationMap.remove(BUYER_CATEGORY_FILTER);
            }
            if (resetIterationMap.size() > 1)
                return itemsByFiltersCombination;
            else if (resetIterationMap.size() == 0)
                return allRowsArray;
            // last iteration
            switch (new ArrayList<>(resetIterationMap.keySet()).get(0)) {
                case PUBLISHER_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByPublisherID;
                case VERTICAL_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByVertical;
                case BUYER_CATEGORY_FILTER:
                    return itemsByBuyerCategory;
            }
            return null;
        }

        @Override
        public void resetFilter() {
            List<String> combinationFiltersList = new ArrayList<>(filterValuesMap.keySet());
            this.filterReset = DataHelper.getRandomValueFromList(combinationFiltersList);
            log.info(String.format("Reset '%s' filter", filterReset));
            // cause data in table supposed to be without reset filter
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            resetIterationMap.remove(filterReset);
            // if the last key in map 'BuyerCategory' return
            if (resetIterationMap.isEmpty() || resetIterationMap.containsKey(BUYER_CATEGORY_FILTER)) return;
            // remove cause it's not filter parameter
            resetIterationMap.remove(BUYER_CATEGORY_FILTER);
            // keys in filter json request
            List<String> keys = new ArrayList<>(resetIterationMap.keySet());
            keys.addAll(Arrays.asList("FromPeriod", "ToPeriod"));
            // values in filter json request
            List<String> values = new ArrayList<>(resetIterationMap.values());
            values.addAll(Arrays.asList(fromPeriod, toPeriod));
            // get rows in table after filter combination
            String requestedURL = new RequestedURL.Builder()
                    .withAbsoluteURL(_url)
                    .filter(keys, values)
                    .sort(_sortBy, _sortHow)
                    .build().getRequestedURL();
            this.itemsByFiltersCombination = dataProvider.getDataAsJSONArray(requestedURL);
        }
    }
}