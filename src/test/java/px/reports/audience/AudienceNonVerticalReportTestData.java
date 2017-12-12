package px.reports.audience;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import org.json.JSONArray;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;

import java.util.*;

import static px.reports.audience.AudienceFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;

/**
 * Created by kgr on 4/26/2017.
 */
public class AudienceNonVerticalReportTestData extends AudienceReportTestData {

    public AudienceNonVerticalReportTestData() {
        super(true);
        // set proper reportType, in this data class from defaultReportTypesMap
        this.reportType = DataHelper.getRandomValueFromList(new ArrayList<>(defaultReportTypesMap.keySet()));
        setHeaders();
        // rows in table by report type
        log.info("Rows in table by report type. No vertical");
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", defaultReportTypesMap.get(reportType))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByReportType = !vertical.equals(DEFAULT_VERTICAL) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        // items in table by vertical
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
//                    .withParams("ReportType", reportTypesMap.get(reportType))
                .withParams("ReportType", defaultReportTypesMap.get(reportType))
                .filter(Arrays.asList("vertical", "FromPeriod", "ToPeriod"), Arrays.asList(vertical, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByVertical = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getDataAsJSONArray(requestedURL) : itemsByDefaultReportType;
        // items in table by buyer guid
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
//                    .withParams("ReportType", reportTypesMap.get(reportType))
                .withParams("ReportType", defaultReportTypesMap.get(reportType))
                .filter(Arrays.asList(BUYER_FILTER, "FromPeriod", "ToPeriod"), Arrays.asList(buyer.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBuyerGUID = dataProvider.getDataAsJSONArray(requestedURL);
        // items in table by publisher guid
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
//                    .withParams("ReportType", reportTypesMap.get(reportType))
                .withParams("ReportType", defaultReportTypesMap.get(reportType))
                .filter(Arrays.asList(PUBLISHER_FILTER, "FromPeriod", "ToPeriod"), Arrays.asList(publisher.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByPublisherGUID = dataProvider.getDataAsJSONArray(requestedURL);
        // items by buyer instance guid
        if (hasBuyerInstances()) {
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
//                        .withParams("ReportType", reportTypesMap.get(reportType))
                    .withParams("ReportType", defaultReportTypesMap.get(reportType))
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
//                        .withParams("ReportType", reportTypesMap.get(reportType))
                    .withParams("ReportType", defaultReportTypesMap.get(reportType))
                    .filter(Arrays.asList(PUBLISHER_FILTER, PUBLISHER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(publisher.getGuid(), subID.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPublisherInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
        }
        // all filters
        List<String> paramsKeys = new ArrayList<>(Arrays.asList("vertical", "buyerguid", "PublisherGuid", "FromPeriod", "ToPeriod"));
        if (hasBuyerInstances()) paramsKeys.add(BUYER_INSTANCE_FILTER);
        if (hasPublisherInstances()) paramsKeys.add(PUBLISHER_INSTANCE_FILTER);
        List<String> paramsValues = new ArrayList<>(Arrays.asList(vertical, buyer.getGuid(), publisher.getGuid(), fromPeriod, toPeriod));
        if (hasBuyerInstances()) paramsValues.add(buyerCampaign.getGuid());
        if (hasPublisherInstances()) paramsValues.add(subID.getGuid());
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
//                    .withParams("ReportType", reportTypesMap.get(reportType))
                .withParams("ReportType", hasChangedAfterVerticalReset()
                        ? defaultReportTypesMap.get(DEFAULT_REPORT_TYPE) : defaultReportTypesMap.get(reportType))
                .filter(paramsKeys, paramsValues)
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new FiltersResetData();
        resetData.setFilterValuesMap();
    }

    @Override
    public List<FieldFormatObject> getFields() {
        return hasChangedAfterVerticalReset() ? fieldsByDefaultReportType : fields;
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", defaultReportTypesMap.get(reportType))
                .build().getRequestedURL();
    }

    private class FiltersResetData extends AbstractFiltersResetData {
        FiltersResetData() {
            this._instanceGroup = instanceGroup;
//            this._url = headersURL.replace("?", "&");
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", hasChangedAfterVerticalReset()
                            ? defaultReportTypesMap.get(DEFAULT_REPORT_TYPE) : defaultReportTypesMap.get(reportType))
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
            filterValuesMap.put(VERTICAL_FILTER, vertical);
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(BUYER_FILTER, buyer.getName());
            if (hasBuyerInstances())
                filterLabelValuesMap.put(BUYER_INSTANCE_FILTER, buyerCampaign.getId() + " - " + buyerCampaign.getName());
            filterLabelValuesMap.put(PUBLISHER_FILTER, publisher.getId() + " - " + publisher.getName());
            if (hasPublisherInstances())
                filterLabelValuesMap.put(PUBLISHER_INSTANCE_FILTER, subID.getId());
            filterLabelValuesMap.put(VERTICAL_FILTER, vertical);
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            if (filterValuesMap.size() > 1)
                return itemsByFiltersCombination;
            else if (filterValuesMap.size() == 0)
                return hasChangedAfterVerticalReset() ? allRowsArray : itemsByReportType;
            // last iteration
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case BUYER_FILTER:
                    return hasChangedAfterVerticalReset() ? itemsByFiltersCombination : itemsByBuyerGUID;
                case BUYER_INSTANCE_FILTER:
                    return hasChangedAfterVerticalReset() ? itemsByFiltersCombination : itemsByBuyerInstanceGUID;
                case PUBLISHER_FILTER:
                    return hasChangedAfterVerticalReset() ? itemsByFiltersCombination : itemsByPublisherGUID;
                case PUBLISHER_INSTANCE_FILTER:
                    return hasChangedAfterVerticalReset() ? itemsByFiltersCombination : itemsByPublisherInstanceGUID;
                case VERTICAL_FILTER:
                    return hasChangedAfterVerticalReset() ? itemsByFiltersCombination : itemsByVertical;
            }
            return null;
        }

        // override, including ReportType filter and its dependency with Vertical filter
        @Override
        public void resetFilter() {
            List<String> combinationFiltersList = new ArrayList<>(filterValuesMap.keySet());
            this.filterReset = DataHelper.getRandomValueFromList(combinationFiltersList);
            log.info(String.format("Reset '%s' filter", filterReset));
            // remove dependent child filters
            if (filterReset.equals(BUYER_FILTER)) filterValuesMap.remove(BUYER_INSTANCE_FILTER);
            if (filterReset.equals(PUBLISHER_FILTER)) filterValuesMap.remove(PUBLISHER_INSTANCE_FILTER);
            // to prevent redundant request, data already exists
            if (filterValuesMap.size() == 1 || (!hasChangedAfterVerticalReset() & combinationFiltersList.size() <= 2))
                return;
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