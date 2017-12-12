package px.reports.publisherDaily;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import px.reports.dto.AbstractFiltersResetData;

import java.util.*;
import java.util.stream.Collectors;

import static px.reports.publisherDaily.PublisherDailyReportFiltersEnum.DEFAULT_REPORT_TYPE;

/**
 * Created by konstantin on 10.04.2017.
 */
public class PublisherDailyReportUnderPublisherTestData extends PublisherDailyReportTestData {

    public PublisherDailyReportUnderPublisherTestData() {
        super(false);
        this.hasGraphics = true;
    }

    public PublisherDailyReportUnderPublisherTestData(String reportType, boolean hasGrouping) {
        super(false);
        this.isInstances = true;
        this.reportType = reportType;
        // extra handling for 'hourly' report type
        this.durationDays = reportType.equals("Hour") ? 2 : durationDays;
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
        // grouping
        setGroupingFields();
        // grouping to set
        setGroupingFieldsToSet(hasGrouping);
        List<String> groupingsList = getGroupingsList();
        // common filters
        this.browsersMap = dataProvider.getPossibleValueFromJSON("HasOffersBrowsers");
        this.browsersList = DataHelper.getRandomListFromList(new ArrayList<>(browsersMap.keySet()), DataHelper.getRandomInt(1, 10));
        // rows in table by report type
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByReportType = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        // rows in table by grouping
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER), Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByGrouping = dataProvider.getDataAsJSONArray(requestedURL);
        // get list of available offers => get any offer from 1st 100 rows
        this.offers = dataProvider.getCreatedInstancesData("offers");
        // old approach cause there is no column 'offerId' for publisher
        int count = DataHelper.getRandomInt(1, 10);
        Set<ObjectIdentityData> temp = new HashSet<>();
        for (int i = 0; i < count; i++) {
            temp.add(offers.get(DataHelper.getRandomInt(offers.size())));
        }
        offers = new ArrayList<>(temp);
        // rows in table by offer IDs
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER), Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
//                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(OFFER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(offers), "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByOfferIDs = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by browsers
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER), Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
//                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(BROWSER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(browsersList, "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBrowsers = dataProvider.getDataAsJSONArray(requestedURL);
        // all filters
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER), Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
//                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(OFFER_FILTER, BROWSER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(offers), "|"),
                                StringUtils.join(browsersList, "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new FiltersResetData();
        resetData.setFilterValuesMap();
    }

    @Override
    public List<String> filters() {
        return Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER, OFFER_FILTER, BROWSER_FILTER);
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "reportType=" + reportType +
                        ", groupingsList=" + getGroupingsList() +
                        ", offers=" + offers.stream().map(ObjectIdentityData::toString).collect(Collectors.joining(", ")) +
                        ", browsersList=" + browsersList : "";
        return "\nPublisherDailyTestData{" +
                "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", totalCount='" + totalCount + '\'' +
                instanceDetails +
                '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {

        FiltersResetData() {
            this._instanceGroup = instanceGroup;
//            this._url = headersURL.replace("?", "&");
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER), Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(getGroupingsList(), "|")))
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
            filterValuesMap.put(OFFER_FILTER, StringUtils.join(ObjectIdentityData.getAllIDs(offers), "|"));
            filterValuesMap.put(BROWSER_FILTER, StringUtils.join(browsersList, "|"));
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(OFFER_FILTER, offers.stream().map(p -> p.getId() +
                    "-" + p.getName()).collect(Collectors.joining(", ")));
            filterLabelValuesMap.put(BROWSER_FILTER, browsersList.toString());
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            if (filterValuesMap.size() > 1)
                return itemsByFiltersCombination;
            else if (filterValuesMap.size() == 0)
                return itemsByGrouping;
            // last iteration
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case OFFER_FILTER:
                    return itemsByOfferIDs;
                case BROWSER_FILTER:
                    return itemsByBrowsers;
            }
            return null;
        }
    }
}