package px.reports.campaigns;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import px.reports.ReportTestData;
import px.reports.UserReports;
import px.reports.dto.AbstractFiltersResetData;

import java.util.*;

/**
 * Created by kgr on 4/28/2017.
 */
public class CampaignsReportTestData extends ReportTestData implements UserReports {
    // filters data
    private List<String> verticalList;
    private ObjectIdentityData buyer;
    // initial data
    private Map<String, String> verticalsMap;
    // table data
    private JSONArray itemsByVertical;
    private JSONArray itemsByBuyerGUID;
    private JSONArray itemsByAllFilters;
    private int itemsByVerticalCount;
    private int itemsByBuyerGUIDCount;
    // filters reset
    protected AbstractFiltersResetData resetData;
    // constants
    public static final String VERTICAL_FILTER = "Vertical";
    public static final String BUYER_FILTER = "parentbuyerguid";
    public static final String CAMPAIGNS_INSTANCE_NAME = "buyerInstances/report";

    public CampaignsReportTestData() {
        this(false);
        checkCampaignsTo1Buyer(ObjectIdentityData.getObjectsByJSONArray(allRowsArray));
    }

    public CampaignsReportTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup(CAMPAIGNS_INSTANCE_NAME);
        setSorting("totalSpend", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
        if (isInstances) {
            // initial data
            this.verticalsMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
            this.verticalList = DataHelper.getRandomListFromList(new ArrayList<>(verticalsMap.keySet()),
                    DataHelper.getRandomInt(1, verticalsMap.size() > 10 ? 10 : verticalsMap.size()));
            // rows in table by vertical
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(StringUtils.join(verticalList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
            this.itemsByVerticalCount = dataProvider.getCurrentTotal();
            if(Config.isAdmin()){
                this.buyer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("buyers"));
                // rows in table by vertical
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .filter(Arrays.asList(BUYER_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(buyer.getGuid(), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByBuyerGUID = dataProvider.getDataAsJSONArray(requestedURL);
                this.itemsByBuyerGUIDCount = dataProvider.getCurrentTotal();
                // rows in table by all filters
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .filter(Arrays.asList(VERTICAL_FILTER, BUYER_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(StringUtils.join(verticalList, "|"), buyer.getGuid(), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
                // set filter values map
                this.resetData = new CampaignsReportTestData.FiltersResetData();
                resetData.setFilterValuesMap();
            }
        }
    }

    // filter data
    public List<String> getVerticalList() {
        return verticalList;
    }

    public ObjectIdentityData getBuyer() {
        return buyer;
    }

    // table data
    public JSONArray getItemsByVertical() {
        return itemsByVertical;
    }

    public JSONArray getItemsByBuyerGUID() {
        return itemsByBuyerGUID;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public int getItemsByVerticalCount() {
        return itemsByVerticalCount;
    }

    public int getItemsByBuyerGUIDCount() {
        return itemsByBuyerGUIDCount;
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nCampaignsReportTestData{" +
                (isInstances ? "verticalList=" + verticalList : "") +
                (isInstances ? "buyer=" + buyer : "") +
                '}';
    }

    // filters reset
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

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(BUYER_FILTER, buyer.getGuid());
            filterValuesMap.put(VERTICAL_FILTER, StringUtils.join(verticalList, "|"));
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(BUYER_FILTER, buyer.getName());
            filterLabelValuesMap.put(VERTICAL_FILTER, verticalList.toString());
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
                case BUYER_FILTER:
                    return itemsByBuyerGUID;
                case VERTICAL_FILTER:
                    return itemsByVertical;
            }
            return null;
        }
    }
}