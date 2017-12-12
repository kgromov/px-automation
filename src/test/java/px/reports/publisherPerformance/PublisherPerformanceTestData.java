package px.reports.publisherPerformance;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import px.reports.ReportTestData;
import px.reports.dto.AbstractFiltersResetData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kgr on 3/13/2017.
 */
public class PublisherPerformanceTestData extends ReportTestData {
    private int startMonthOffset = super.startMonthOffset = 3;
    // filters data
    private ObjectIdentityData publisherManager; // ' ' => '+'
    private List<String> buyerCategoriesList;
    private List<String> verticalList;
    // initial data
    private Map<String, String> buyerCategoriesMap;
    private Map<String, String> verticalsMap;
    // table data
    private JSONArray itemsByPublisherManager;
    private JSONArray itemsByVertical;
    private JSONArray itemsByBuyerCategories;
    private JSONArray itemsByAllFilters;
    // items counts
    private int buyerCategoriesItemsCount;
    private int verticalItemsCount;
    private int itemsByPublisherManagersCount;
    // filters reset
    private AbstractFiltersResetData resetData;
    // constants
    public static final String PUBLISHER_MANAGER_FILTER = "PublisherManagerFilter";
    public static final String VERTICAL_FILTER = "Vertical";
    public static final String BUYER_CATEGORY_FILTER = "BuyerCategory";

    static {
        missedHeadersMetricsMap.put("cpl", "Currency");
        missedHeadersMetricsMap.put("marginPerc", "Percentage");
    }

    public PublisherPerformanceTestData() {
        this(false);
    }

    public PublisherPerformanceTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("publisherperformance/report");
        setSorting("spending", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        // set headers objects - later on in parent setHeaders() when became stable
        setHeaders();
        if (isInstances) {
            // initial data
            this.buyerCategoriesMap = dataProvider.getPossibleValueFromJSON("BuyerCategories");
            this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
            // filter data
            // get list of available publisher managers
            this.publisherManager = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publisherManagers"));
            this.buyerCategoriesList = DataHelper.getRandomListFromList(new ArrayList<>(buyerCategoriesMap.keySet()),
                    DataHelper.getRandomInt(1, buyerCategoriesMap.size()));
            this.verticalList = DataHelper.getRandomListFromList(new ArrayList<>(verticalsMap.keySet()),
                    DataHelper.getRandomInt(1, verticalsMap.size() > 10 ? 10 : verticalsMap.size()));
            // rows in table by buyer categories
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_CATEGORY_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(StringUtils.join(buyerCategoriesList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerCategories = dataProvider.getDataAsJSONArray(requestedURL);
            this.buyerCategoriesItemsCount = dataProvider.getCurrentTotal();
            // rows in table by vertical
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList("Vertical", "FromPeriod", "ToPeriod"),
                            Arrays.asList(StringUtils.join(verticalList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
            this.verticalItemsCount = dataProvider.getCurrentTotal();
            // rows in table by publisher manager
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_MANAGER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisherManager.getName().replaceAll(" ", "+"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublisherManager = dataProvider.getDataAsJSONArray(requestedURL);
            this.itemsByPublisherManagersCount = dataProvider.getCurrentTotal();
            // all filters
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList("BuyerCategory", "Vertical", "FromPeriod", "ToPeriod"),
                            Arrays.asList(StringUtils.join(buyerCategoriesList, "|"),
                                    StringUtils.join(verticalList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
            // set filter values map
            this.resetData = new FiltersResetData();
            resetData.setFilterValuesMap();
        }
    }

    // filter data
    public List<String> getBuyerCategoriesList() {
        return buyerCategoriesList;
    }

    public List<String> getTableBuyerCategories() {
        return buyerCategoriesList.stream().map(category ->
                category.equals("Data") | category.equals("Other") ? category : category + "s"
        ).collect(Collectors.toList());
    }

    public List<String> getAllTableBuyerCategories() {
        return buyerCategoriesMap.keySet().stream().map(category ->
                category.equals("Data") | category.equals("Other") ? category : category + "s"
        ).collect(Collectors.toList());
    }

    public List<String> getVerticalList() {
        return verticalList;
    }

    public ObjectIdentityData getPublisherManager() {
        return publisherManager;
    }

    // table data
    public JSONArray getItemsByVertical() {
        return itemsByVertical;
    }

    public JSONArray getItemsByBuyerCategories() {
        return itemsByBuyerCategories;
    }

    public JSONArray getItemsByPublisherManager() {
        return itemsByPublisherManager;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public int getBuyerCategoriesItemsCount() {
        return buyerCategoriesItemsCount;
    }

    public int getVerticalItemsCount() {
        return verticalItemsCount;
    }

    public int getItemsByPublisherManagersCount() {
        return itemsByPublisherManagersCount;
    }

    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    public boolean hasBuyerCategories() {
        return resetData != null && ((FiltersResetData) resetData).hasBuyerCategories();
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "buyerCategoriesList=" + buyerCategoriesList +
                        ", publisherManager=" + publisherManager +
                        ", verticalList=" + verticalList : "";
        return super.toString() +
                "PublisherPerformanceTestData{" + instanceDetails + '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {
        FiltersResetData() {
            this._instanceGroup = instanceGroup;
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .build().getRequestedURL().replace("?", "&");
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
        }

        public boolean hasBuyerCategories() {
            return filterValuesMap.containsKey(BUYER_CATEGORY_FILTER);
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(PUBLISHER_MANAGER_FILTER, publisherManager.getName().replaceAll(" ", "+"));
            filterValuesMap.put(VERTICAL_FILTER, StringUtils.join(verticalList, "|"));
            filterValuesMap.put(BUYER_CATEGORY_FILTER, StringUtils.join(buyerCategoriesList, "|"));
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_MANAGER_FILTER, publisherManager.getName());
            filterLabelValuesMap.put(VERTICAL_FILTER, verticalList.toString());
            filterLabelValuesMap.put(BUYER_CATEGORY_FILTER, buyerCategoriesList.toString());
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            if (filterValuesMap.size() > 1)
                return itemsByFiltersCombination;
            else if (filterValuesMap.size() == 0)
                return allRowsArray;
            // last iteration
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case PUBLISHER_MANAGER_FILTER:
                    return itemsByPublisherManager;
                case VERTICAL_FILTER:
                    return itemsByVertical;
                case BUYER_CATEGORY_FILTER:
                    return itemsByBuyerCategories;
            }
            return null;
        }
    }
}
