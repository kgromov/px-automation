package px.reports.sourceQuality;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.ReportTestData;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by kgr on 11/17/2016.
 */
public class SourceQualityScoreTestData extends ReportTestData {
    // filter data
    protected ObjectIdentityData buyer;
    private ObjectIdentityData publisher;
    protected ObjectIdentityData buyerCampaign;
    private ObjectIdentityData subID;
    // table data
    protected JSONArray itemsByBuyerGUID;
    private JSONArray itemsByPublisherGUID;
    protected JSONArray itemsByBuyerInstanceGUID;     // buyerinstanceguid
    private JSONArray itemsByPublisherInstanceGUID; // PublisherInstanceGu
    private JSONArray itemsByAllFilters; //http://beta.px.com/api/sourceqiqscore/report?count=50&filter={"PublisherGuid":<guid>, "buyerGuid":<guid>}, table
    // sub instances
    protected List<ObjectIdentityData> buyerCampaigns;  // /api/lightModel/buyerinstancesbyparent?parent=<buyer_guid>
    private List<ObjectIdentityData> subIDs;          // /api/lightModel/publisherinstances?publisherGuid=<publisher_guid>
    // filters reset
    protected AbstractFiltersResetData resetData;
    // constants
    public static final String BUYER_FILTER = "buyerguid";
    public static final String BUYER_INSTANCE_FILTER = "buyerinstanceguid";
    public static final String PUBLISHER_FILTER = "PublisherGuid";
    public static final String PUBLISHER_INSTANCE_FILTER = "PublisherInstanceGuid";

    static {
        popupTitleMap.put("qiqScore", "Publisher quality score");
    }

    protected SourceQualityScoreTestData(int filters) {
        super(false);
        this.hasGraphics = true;
    }

    public SourceQualityScoreTestData() {
        this(false);
    }

    public SourceQualityScoreTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("sourceqiqscore/report");
        setSorting("qiqScore", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
        if (isInstances) {
            // any available buyer
            this.buyer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("buyers"));
            // any available publisher
//            this.publisher = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("publishers"));
            List<ObjectIdentityData> publishers = dataProvider.getCreatedInstancesData("publishers");
            this.publisher = getFilterObject(publishers, allRowsArray, "publisherID", "id");
            // buyer instances - filter items
            this.buyerCampaigns = dataProvider.getCreatedInstancesData("buyerinstancesbyparent",
                    Collections.singletonList("parent"), Collections.singletonList(buyer.getGuid()));
            this.buyerCampaign = buyerCampaigns.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(buyerCampaigns);
            // publisher instances - filter items
            this.subIDs = dataProvider.getCreatedInstancesData("publisherinstances",
                    Collections.singletonList("publisherGuid"), Collections.singletonList(publisher.getGuid()));
            this.subID = subIDs.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(subIDs);
            // rows in table by buyes GUID
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(buyer.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerGUID = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by publisher guid
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublisherGUID = dataProvider.getDataAsJSONArray(requestedURL);
            // items by buyer instance guid
            if (hasBuyerInstances()) {
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .filter(Arrays.asList(BUYER_FILTER, BUYER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(buyer.getGuid(), buyerCampaign.getGuid(), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByBuyerInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
            }
            // items by publisher instance guid
            if (hasPublisherInstances()) {
                requestedURL = new RequestedURL.Builder()
                        .withRelativeURL("api/" + instanceGroup)
                        .filter(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                                Arrays.asList(publisher.getGuid(), subID.getGuid(), fromPeriod, toPeriod))
                        .sort(sortBy, sortHow)
                        .build().getRequestedURL();
                this.itemsByPublisherInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
            }
            //  rows in table after all filters
            List<String> paramsKeys = new ArrayList<>(Arrays.asList(PUBLISHER_FILTER, BUYER_FILTER, "FromPeriod", "ToPeriod"));
            if (hasBuyerInstances()) paramsKeys.add(BUYER_INSTANCE_FILTER);
            if (hasPublisherInstances()) paramsKeys.add(PUBLISHER_INSTANCE_FILTER);
            List<String> paramsValues = new ArrayList<>(Arrays.asList(publisher.getGuid(), buyer.getGuid(), fromPeriod, toPeriod));
            if (hasBuyerInstances()) paramsValues.add(buyerCampaign.getGuid());
            if (hasPublisherInstances()) paramsValues.add(subID.getGuid());
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(paramsKeys, paramsValues)
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
            // set filter values map
            this.resetData = new SourceQualityScoreTestData.FiltersResetData();
            resetData.setFilterValuesMap();
        }
    }

    // filter data
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

    // table data
    public JSONArray getItemsByBuyerGUID() {
        return itemsByBuyerGUID;
    }

    public JSONArray getItemsByPublisherGUID() {
        return itemsByPublisherGUID;
    }

    public JSONArray getItemsByBuyerInstanceGUID() {
        return itemsByBuyerInstanceGUID;
    }

    public JSONArray getItemsByPublisherInstanceGUID() {
        return itemsByPublisherInstanceGUID;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public boolean hasBuyerInstances() {
        return buyerCampaigns != null && !buyerCampaigns.isEmpty();
    }

    public boolean hasPublisherInstances() {
        return subIDs != null && !subIDs.isEmpty();
    }

    // filter reset data
    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    @Override
    public String toString() {
        String buyerCampaignDetails = isInstances && !buyerCampaigns.isEmpty() ? buyerCampaign.toString() : "";
        String subIdDetails = isInstances && !subIDs.isEmpty() ? subID.toString() : "";
        String instanceDetails = isInstances ?
                "buyer=" + buyer +
                        ", publisher=" + publisher +
                        ", buyerCampaign=" + buyerCampaignDetails +
                        ", subID=" + subIdDetails +
                        ", buyerCampaigns=" + buyerCampaigns.stream().map(campaign -> campaign.getId() +
                        " - " + campaign.getName()).collect(Collectors.joining(", ")) +
                        ", subIDs=" + subIDs.stream().map(ObjectIdentityData::getId).collect(Collectors.joining(", "))
                : "";
        return super.toString() +
                "\nSourceQualityScoreTestData{" +
                instanceDetails +
                '}';
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

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(BUYER_FILTER, buyer.getGuid());
            if (hasBuyerInstances())
                filterValuesMap.put(BUYER_INSTANCE_FILTER, buyerCampaign.getGuid());
            filterValuesMap.put(PUBLISHER_FILTER, publisher.getGuid());
            if (hasPublisherInstances())
                filterValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getGuid());
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(BUYER_FILTER, buyer.getName());
            if (hasBuyerInstances())
                filterLabelValuesMap.put(BUYER_INSTANCE_FILTER, buyerCampaign.getId() + " - " + buyerCampaign.getName());
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            if (hasPublisherInstances())
                filterLabelValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getId());
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
                case BUYER_INSTANCE_FILTER:
                    return itemsByBuyerInstanceGUID;
                case PUBLISHER_FILTER:
                    return itemsByPublisherGUID;
                case PUBLISHER_INSTANCE_FILTER:
                    return itemsByPublisherInstanceGUID;
            }
            return null;
        }

        @Override
        public void resetFilter() {
            List<String> combinationFiltersList = new ArrayList<>(filterValuesMap.keySet());
            this.filterReset = DataHelper.getRandomValueFromList(combinationFiltersList);
            log.info(String.format("Reset '%s' filter", filterReset));
            // remove dependent child filters
            if (filterReset.equals(BUYER_FILTER)) filterValuesMap.remove(BUYER_INSTANCE_FILTER);
            if (filterReset.equals(PUBLISHER_FILTER)) filterValuesMap.remove(PUBLISHER_INSTANCE_FILTER);
            // to prevent redundant request, data already exists
            if (combinationFiltersList.size() <= 2) return;
            // cause data in table supposed to be without reset filter
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            resetIterationMap.remove(filterReset);
            // keys in filter json request
            List<String> keys = new ArrayList<>(resetIterationMap.keySet());
            keys.addAll(Arrays.asList(_fromPeriodKey, _toPeriodKey));
            // values in filter json request
            List<String> values = new ArrayList<>(resetIterationMap.values());
            values.addAll(Arrays.asList(_fromPeriod, _toPeriod));
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