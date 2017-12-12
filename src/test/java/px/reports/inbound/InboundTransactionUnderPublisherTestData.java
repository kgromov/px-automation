package px.reports.inbound;

import configuration.helpers.RequestedURL;
import org.json.JSONArray;
import px.reports.UserReports;
import px.reports.dto.AbstractFiltersResetData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import static configuration.helpers.DataHelper.getSetFromJSONArrayByKey;

/**
 * Created by kgr on 12/21/2016.
 */
public class InboundTransactionUnderPublisherTestData extends InboundTransactionTestData implements UserReports {
    {
        startMonthOffset = 2;
    }
    public InboundTransactionUnderPublisherTestData() {
        super(false);
        this.isInstances = true;
        setFiltersData();
        // rows in table by result code
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(RESULT_CODES_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(resultCode.replaceAll(" ", "+"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByResultCode = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by vertical
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(vertical, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by offer id
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(OFFER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(offer.getId(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByOfferID = dataProvider.getDataAsJSONArray(requestedURL);
        // all filters
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(RESULT_CODES_FILTER, VERTICAL_FILTER, OFFER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(resultCode.replaceAll(" ", "+"), vertical, offer.getId(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new InboundTransactionUnderPublisherTestData.FiltersResetData();
        resetData.setFilterValuesMap();
        // set inbound data not create each time
        TRANSACTIONS_SET = new HashSet<>();
        this.inboundData = new InboundTransactionData();
        checkSubIdsTo1Publisher(getSetFromJSONArrayByKey(allRowsArray, "subId"), "id");
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "offer=" + offer.toString() +
                        ", resultCode=" + resultCode +
                        ", vertical=" + vertical : "";
        return "InboundTransactionTestData{" +
                "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", totalCount='" + totalCount + '\'' +
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
            filterValuesMap.put(RESULT_CODES_FILTER, resultCode.replaceAll(" ", "+"));
            filterValuesMap.put(VERTICAL_FILTER, vertical);
            filterValuesMap.put(OFFER_FILTER, offer.getId());
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(RESULT_CODES_FILTER, resultCode);
            filterLabelValuesMap.put(VERTICAL_FILTER, vertical);
            filterLabelValuesMap.put(OFFER_FILTER, offer.getId() + " - " + offer.getName());
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
                case RESULT_CODES_FILTER:
                    return itemsByResultCode;
                case VERTICAL_FILTER:
                    return itemsByVertical;
                case OFFER_FILTER:
                    return itemsByOfferID;
            }
            return null;
        }
    }
}