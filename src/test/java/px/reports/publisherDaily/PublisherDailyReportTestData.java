package px.reports.publisherDaily;

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
import java.util.stream.Collectors;

import static pages.groups.Pagination.MAX_ROWS_LIMIT;
import static px.reports.publisherDaily.PublisherDailyReportFiltersEnum.DEFAULT_REPORT_TYPE;

/**
 * Created by konstantin on 10.04.2017.
 */
public class PublisherDailyReportTestData extends ReportTestData {
    // filters data
    private List<ObjectIdentityData> publishers;
    private List<ObjectIdentityData> publisherManagers; // ' ' => '+'
    protected List<ObjectIdentityData> offers;
    protected List<String> browsersList;
    protected String reportType = DEFAULT_REPORT_TYPE; // TODO: validation for hourly, move to another class (+ buyerCampaign)
    // grouping
    protected List<FieldFormatObject> groupingFields;
    protected List<FieldFormatObject> nonGroupingFields;
    protected List<FieldFormatObject> groupingToSetFields;
    private final Comparator<FieldFormatObject> fieldsComparator = (FieldFormatObject f1, FieldFormatObject f2) -> f1.getIndex() - f2.getIndex();
    // initial data
    protected Map<String, String> browsersMap;
    // table data
    private JSONArray itemsByPublisherIDs;      // AffiliateIdFilter
    private JSONArray itemsByPublisherManagers; // AffiliateManagerFilter,
    protected JSONArray itemsByOfferIDs;          // OfferIdFilter
    protected JSONArray itemsByBrowsers;          // BrowserFilter
    protected JSONArray itemsByReportType;        // ReportType=<type>
    protected JSONArray itemsByGrouping;          // Grouping=<grouping_field>
    //    private JSONArray itemsByFiltersCombination;
    protected JSONArray itemsByAllFilters;
    protected int itemsByGroupingCount;
    // filters reset
    protected AbstractFiltersResetData resetData;
    // constants
    public static final String REPORT_TYPE_FILTER = "ReportType";
    public static final String GROUPING_FILTER = "Grouping";
    public static final String PUBLISHER_FILTER = "AffiliateIdFilter";
    public static final String PUBLISHER_MANAGER_FILTER = "AffiliateManagerFilter";
    public static final String OFFER_FILTER = "OfferIdFilter";
    public static final String BROWSER_FILTER = "BrowserFilter";
    // static data
    public static final Map<String, String> REPORT_TYPE_MAP = new HashMap<>();

    static {
        REPORT_TYPE_MAP.put("Hour", "hourly");
        REPORT_TYPE_MAP.put("Day", "daily");
        REPORT_TYPE_MAP.put("Week", "weekly");
        REPORT_TYPE_MAP.put("Month", "monthly");
        // data mapping
        dataMap.put("", "Default");
        dataMapping.add(new ValuesMapper("offerUrl", dataMap));
    }

    public PublisherDailyReportTestData() {
        this(false);
        // extra handling for 'hourly' report type
        this.durationDays = reportType.equals("Hour") ? 2 : durationDays;
        setDateRanges();
        setAllRowsByDateRange();
        super.setHeaders();
        // grouping
        setGroupingFields();
    }

    protected PublisherDailyReportTestData(boolean isInstances) {
        super(isInstances);
        setInstanceGroup("publisherstats/report");
        setSorting("dateTime", "desc");
    }

    public PublisherDailyReportTestData(String reportType, boolean hasGrouping) {
        this(true);
        this.reportType = reportType;
        // extra handling for 'hourly' report type
        this.durationDays = reportType.equals("Hour") ? 2 : durationDays;
        setDateRanges();
        setAllRowsByDateRange();
        super.setHeaders();
        // grouping
        setGroupingFields();
        // grouping to set
        setGroupingFieldsToSet(hasGrouping);
        List<String> groupingsList = getGroupingsList();
        // common filters
        this.browsersMap = dataProvider.getPossibleValueFromJSON("HasOffersBrowsers");
        this.browsersList = DataHelper.getRandomListFromList(new ArrayList<>(browsersMap.keySet()), DataHelper.getRandomInt(1, 10));
        /*// get list of available publishers
        this.publishers = dataProvider.getCreatedInstancesData("publishers");
        int count = DataHelper.getRandomInt(1, 10);
        Set<ObjectIdentityData> temp = new HashSet<>();
        for (int i = 0; i < count; i++) {
            temp.add(publishers.get(DataHelper.getRandomInt(publishers.size())));
        }
        publishers = new ArrayList<>(temp);
        Comparator<ObjectIdentityData> comparator = (ObjectIdentityData p1, ObjectIdentityData p2)
                -> Integer.parseInt(p1.getId()) - Integer.parseInt(p2.getId());
        Collections.sort(publishers, comparator);
        // get list of available publisher managers
        this.publisherManagers = dataProvider.getCreatedInstancesData("publisherManagers");
        count = DataHelper.getRandomInt(1, publisherManagers.size() > 10 ? 10 : publisherManagers.size());
        temp = new HashSet<>();
        for (int i = 0; i < count; i++) {
            temp.add(publisherManagers.get(DataHelper.getRandomInt(publisherManagers.size())));
        }
        publisherManagers = new ArrayList<>(temp);
        // get list of available offers
        this.offers = dataProvider.getCreatedInstancesData("offers");
        count = DataHelper.getRandomInt(1, 10);
        temp = new HashSet<>();
        for (int i = 0; i < count; i++) {
            temp.add(offers.get(DataHelper.getRandomInt(offers.size())));
        }
        offers = new ArrayList<>(temp);*/
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
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER),
                        Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByGrouping = dataProvider.getDataAsJSONArray(requestedURL);
        // get list of available publishers  => get any publisher from 1st 100 rows
        this.publishers = dataProvider.getCreatedInstancesData("publishers");
        publishers = getFilterObjects(publishers, itemsByGrouping, "affiliateID", "id");
        // get list of available publisher managers => get any offer from 1st 100 rows
        this.publisherManagers = dataProvider.getCreatedInstancesData("publisherManagers");
        publisherManagers = getFilterObjects(publisherManagers, itemsByGrouping, "affiliateManager", "name");
        // get list of available offers => get any offer from 1st 100 rows
        this.offers = dataProvider.getCreatedInstancesData("offers");
        // get any publisher from 1st 100 rows
        offers = getFilterObjects(offers, itemsByGrouping, "offerID", "id");
        // rows in table by publisher IDs
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER),
                        Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
//                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(publishers), "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByPublisherIDs = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by publisher manager IDs
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER),
                        Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
//                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(PUBLISHER_MANAGER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(ObjectIdentityData.getAllNames(publisherManagers), "|").replaceAll(" ", "+"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByPublisherManagers = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by offer IDs
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER),
                        Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
//                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(OFFER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(offers), "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByOfferIDs = dataProvider.getDataAsJSONArray(requestedURL);
        // rows in table by browsers
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER),
                        Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
//                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(BROWSER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(browsersList, "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBrowsers = dataProvider.getDataAsJSONArray(requestedURL);
        // all filters
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER),
                        Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
//                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .filter(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_MANAGER_FILTER, OFFER_FILTER, BROWSER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(publishers), "|"),
                                StringUtils.join(ObjectIdentityData.getAllNames(publisherManagers), "|").replaceAll(" ", "+"),
                                StringUtils.join(ObjectIdentityData.getAllIDs(offers), "|"),
                                StringUtils.join(browsersList, "|"), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new FiltersResetData();
        resetData.setFilterValuesMap();
    }

    // filter data
    public List<ObjectIdentityData> getPublishers() {
        return publishers;
    }

    public List<ObjectIdentityData> getPublisherManagers() {
        return publisherManagers;
    }

    public List<ObjectIdentityData> getOffers() {
        return offers;
    }

    public List<String> getBrowsersList() {
        return browsersList;
    }

    public String getReportType() {
        return reportType;
    }

    // table data
    public JSONArray getItemsByPublisherIDs() {
        return itemsByPublisherIDs;
    }

    public JSONArray getItemsByPublisherManagers() {
        return itemsByPublisherManagers;
    }

    public JSONArray getItemsByOfferIDs() {
        return itemsByOfferIDs;
    }

    public JSONArray getItemsByBrowsers() {
        return itemsByBrowsers;
    }

    public JSONArray getItemsByReportType() {
        return itemsByReportType;
    }

    public JSONArray getItemsByGrouping() {
        return itemsByGrouping;
    }

    public JSONArray getItemsByAllFilters() {
        return itemsByAllFilters;
    }

    public int getItemsByGroupingCount() {
        return itemsByGroupingCount;
    }

    public AbstractFiltersResetData getResetData() {
        return resetData;
    }

    // grouping
    public List<FieldFormatObject> getGroupingToSetFields() {
        return groupingToSetFields;
    }

    public List<String> getGroupingsList() {
        return FieldFormatObject.getFieldNames(groupingToSetFields);
    }

    public boolean hasGrouping() {
        return !groupingToSetFields.isEmpty();
    }

    public List<FieldFormatObject> getNonGroupingFields() {
        return nonGroupingFields;
    }

    public List<FieldFormatObject> getGroupingFields() {
        return groupingFields;
    }

    public void setGroupingFields() {
        // to save indexes
//        List<FieldFormatObject> fields = new ArrayList<>(this.fields);
        // reset indexes
        for (int i = 0; i < fields.size(); i++) {
            fields.get(i).setIndex(i);
        }
        List<String> groupingFieldNames = FieldFormatObject.getGroupingFieldNames(fields);
        this.groupingFields = groupingFieldNames.stream().map(name ->
                FieldFormatObject.getFieldObjectFromListByName(fields, name)).collect(Collectors.toList());
        List<String> nonGroupingFieldNames = FieldFormatObject.getFieldNames(fields).stream()
                .filter(name -> !groupingFieldNames.contains(name)).collect(Collectors.toList());
        this.nonGroupingFields = nonGroupingFieldNames.stream().map(name ->
                FieldFormatObject.getFieldObjectFromListByName(fields, name)).collect(Collectors.toList());
    }

    // for export test
    public void setGroupingFieldsToSet(boolean hasGrouping) {
        List<String> groupingsList = hasGrouping ? DataHelper.getRandomListFromList(FieldFormatObject.getFieldNames(groupingFields), DataHelper.getRandomInt(1, 2)) : new ArrayList<>();
        // change fields indexes to be able to check table afterwards
        changeFieldsIndexes();
        // verification - do not include publisher and offer together
        boolean isPublisherGroping = false;
        boolean isOfferGrouping = false;
        for (String name : groupingsList) {
            isPublisherGroping |= name.contains("affiliate");
            isOfferGrouping |= name.contains("offer");
        }
        groupingsList = isPublisherGroping & isOfferGrouping ? groupingsList.subList(0, 1) : groupingsList;
        this.groupingToSetFields = groupingsList.stream().map(name ->
                FieldFormatObject.getFieldObjectFromListByName(groupingFields, name)).collect(Collectors.toList());
        groupingToSetFields.sort(fieldsComparator);
    }

    // if check with grouping field(s) it is required to remove from groupingFieldsNames, add to nonGroupingFields
    private void changeFieldsIndexes() {
        int startGroupingIndex = groupingFields.isEmpty() ? 0 : groupingFields.get(0).getIndex();
        nonGroupingFields.stream().filter(field -> !field.isGrouping() && field.getIndex() > startGroupingIndex)
                .forEach(field -> field.setIndex(field.getIndex() - groupingFields.size()));
    }

    void setGrouping() {
        // just to update with original indexes
        setGroupingFields();
        // exclude groupingList items from groupingFieldsNames, include to nonGroupingFields
        groupingFields.removeAll(groupingToSetFields);
        nonGroupingFields.addAll(groupingToSetFields);
        // sort by indexes
        nonGroupingFields.sort(fieldsComparator);
        // change according to table
        for (int i = 0; i < nonGroupingFields.size(); i++) {
            nonGroupingFields.get(i).setIndex(i);
        }
    }

    void resetGrouping() {
        // just to update with original indexes
        setGroupingFields();
        // include groupingList items to groupingFieldsNames, exclude from nonGroupingFields
        groupingFields.addAll(groupingToSetFields);
        nonGroupingFields.removeAll(groupingToSetFields);
        // sort by indexes
        nonGroupingFields.sort(fieldsComparator);
        // change according to table
        for (int i = 0; i < nonGroupingFields.size(); i++) {
            nonGroupingFields.get(i).setIndex(i);
        }
    }

    public void setReportTypeItems(String reportType) {
        this.reportType = reportType;
        // extra handling for 'hourly' report type
        this.durationDays = reportType.equals("Hour") ? 2 : durationDays;
        setDateRanges();
        setAllRowsByDateRange();
        // grouping
        setGroupingFields();
        // grouping to set
        setGroupingFieldsToSet(true);
        List<String> groupingsList = getGroupingsList();
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
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER),
                        Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(groupingsList, "|")))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByGrouping = dataProvider.getDataAsJSONArray(requestedURL);
        this.itemsByGroupingCount = dataProvider.getCurrentTotal();
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(reportType))
                .build().getRequestedURL();
    }
/*
    @Override
    protected void setHeaders() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER), Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(getGroupingsList(), "|")))
                .build().getRequestedURL();
        this.headers = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        this.headersList = DataHelper.getListFromJSONArrayByKey(headers, "field");
        setFields();
    }*/

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(REPORT_TYPE_FILTER, REPORT_TYPE_MAP.get(DEFAULT_REPORT_TYPE))
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
    public String toString() {
        String instanceDetails = isInstances ?
                "reportType=" + reportType +
                        ", groupingsList=" + getGroupingsList() +
                        ", publishers=" + publishers.stream().map(ObjectIdentityData::toString).collect(Collectors.joining(", ")) +
                        ", publisherManagers=" + publisherManagers.stream().map(ObjectIdentityData::getName).collect(Collectors.joining(", ")) +
                        ", offers=" + offers.stream().map(ObjectIdentityData::toString).collect(Collectors.joining(", ")) +
                        ", browsersList=" + browsersList : "";
        return super.toString() +
                "\nPublisherDailyTestData{" +
                instanceDetails +
                '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {

        FiltersResetData() {
            this._instanceGroup = instanceGroup;
//            this._url = headersURL.replace("?", "&");
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams(Arrays.asList(REPORT_TYPE_FILTER, GROUPING_FILTER),
                            Arrays.asList(REPORT_TYPE_MAP.get(reportType), StringUtils.join(getGroupingsList(), "|")))
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
            filterValuesMap.put(PUBLISHER_FILTER, StringUtils.join(ObjectIdentityData.getAllIDs(publishers), "|"));
            filterValuesMap.put(PUBLISHER_MANAGER_FILTER, StringUtils.join(ObjectIdentityData.getAllNames(publisherManagers), "|").replaceAll(" ", "+"));
            filterValuesMap.put(OFFER_FILTER, StringUtils.join(ObjectIdentityData.getAllIDs(offers), "|"));
            filterValuesMap.put(BROWSER_FILTER, StringUtils.join(browsersList, "|"));
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(PUBLISHER_FILTER, publishers.stream().map(p -> p.getId() +
                    "-" + p.getName()).collect(Collectors.joining(", ")));
            filterLabelValuesMap.put(PUBLISHER_MANAGER_FILTER, publisherManagers.stream()
                    .map(ObjectIdentityData::getName).collect(Collectors.joining(", ")));
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
                case PUBLISHER_FILTER:
                    return itemsByPublisherIDs;
                case PUBLISHER_MANAGER_FILTER:
                    return itemsByPublisherManagers;
                case OFFER_FILTER:
                    return itemsByOfferIDs;
                case BROWSER_FILTER:
                    return itemsByBrowsers;
            }
            return null;
        }
    }
}