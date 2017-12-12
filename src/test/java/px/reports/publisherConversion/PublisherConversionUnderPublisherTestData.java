package px.reports.publisherConversion;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import px.reports.dto.AbstractFiltersResetData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kgr on 4/4/2017.
 */
public class PublisherConversionUnderPublisherTestData extends PublisherConversionTestData {
    // TODO: check startMonthOffset, durationDays

    public PublisherConversionUnderPublisherTestData() {
        super(false);
        this.isInstances = true;
        this.browsersMap = dataProvider.getPossibleValueFromJSON("HasOffersBrowsers");
        this.conversionStatusMap = dataProvider.getPossibleValueFromJSON("ConvStatuses");
        this.browser = DataHelper.getRandomValueFromList(new ArrayList<>(browsersMap.keySet()));
        // get any conversion status from 1st 100 rows
//            this.conversionStatus = DataHelper.getRandomValueFromList(new ArrayList<>(conversionStatusMap.keySet()));
        conversionStatus = getFilterValue(allRowsArray, "convStatus");
        // get any adjustment from 1st 100 rows
//            this.adjustment = String.valueOf(DataHelper.getRandomInt(0, 1));
        adjustment = DataHelper.getListFromJSONArrayByKey(allRowsArray, "adjustment").isEmpty() ? String.valueOf(0) : getFilterValue(allRowsArray, "adjustment");
        // get any of available offers => get any offer from 1st 100 rows
//            this.offer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("offers"));
        List<ObjectIdentityData> offers = dataProvider.getCreatedInstancesData("offers");
        this.offer = getFilterObject(offers, allRowsArray, "offerID", "id");
        // rows in table by offer id
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(OFFER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(offer.getId(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByOfferID = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by adjustment
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(ADJUSTMENT_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(adjustment, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAdjustment = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by browser
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(BROWSER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(browser, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBrowser = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by conversion status
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(CONVERSION_STATUS_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(conversionStatus, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByConversionStatus = dataProvider.getDataAsJSONArray(requestedURL);
        // all filters
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(ADJUSTMENT_FILTER, OFFER_FILTER, BROWSER_FILTER, CONVERSION_STATUS_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(adjustment, offer.getId(), browser, conversionStatus, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new PublisherConversionUnderPublisherTestData.FiltersResetData();
        resetData.setFilterValuesMap();
    }

    @Override
    public List<String> filters() {
        return Arrays.asList(ADJUSTMENT_FILTER, OFFER_FILTER, BROWSER_FILTER, CONVERSION_STATUS_FILTER);
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "offer=" + offer.toString() +
                        ", adjustment=" + DataHelper.getYesNo(adjustment) +
                        ", browser=" + browser +
                        ", conversionStatus=" + conversionStatus : "";
        return "PublisherConversionTestData{" +
                "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", totalCount='" + totalCount + '\'' + instanceDetails + '}';
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
            filterValuesMap.put(ADJUSTMENT_FILTER, DataHelper.getYesNo(adjustment));
            filterValuesMap.put(OFFER_FILTER, offer.getId());
            filterValuesMap.put(BROWSER_FILTER, browser);
            filterValuesMap.put(CONVERSION_STATUS_FILTER, conversionStatus);
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(ADJUSTMENT_FILTER, adjustment);
            filterLabelValuesMap.put(OFFER_FILTER, offer.getId() + " - " + offer.getName());
            filterLabelValuesMap.put(BROWSER_FILTER, browser);
            filterLabelValuesMap.put(CONVERSION_STATUS_FILTER, conversionStatus);
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
                case ADJUSTMENT_FILTER:
                    return itemsByAdjustment;
                case OFFER_FILTER:
                    return itemsByOfferID;
                case BROWSER_FILTER:
                    return itemsByBrowser;
                case CONVERSION_STATUS_FILTER:
                    return itemsByConversionStatus;
            }
            return null;
        }
    }
}
