package px.reports.audience;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import org.json.JSONArray;
import px.reports.UserReports;
import px.reports.dto.AbstractFiltersResetData;
import px.reports.dto.FieldFormatObject;

import java.util.*;
import java.util.stream.Collectors;

import static px.reports.audience.AudienceFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;

/**
 * Created by kgr on 4/26/2017.
 */
public class AudienceVerticalUnderBuyerReportTestData extends AudienceReportTestData implements UserReports {
    // add items for vertical with default reportType

    public AudienceVerticalUnderBuyerReportTestData() {
        super(true);
        // set proper reportType, in this data class by in reportTypesMap by vertical
        this.reportType = DataHelper.getRandomValueFromList(new ArrayList<>(reportTypesMap.keySet()));
        setHeaders();
        // rows in table by report type
        log.info("Rows in table by report type. No vertical");
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", reportTypesMap.get(reportType))
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByReportTypeNoVertical = !vertical.equals(DEFAULT_VERTICAL) ? dataProvider.getDataAsJSONArray(requestedURL) : allRowsArray;
        // rows in table by default report type and vertical
        log.info("Rows in table by vertical");
        // items in table by vertical, itemsByVertical = itemsByReportType
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", reportTypesMap.get(reportType))
                .filter(Arrays.asList(VERTICAL_FILTER, "FromPeriod", "ToPeriod"), Arrays.asList(vertical, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByVertical = !reportType.equals(DEFAULT_REPORT_TYPE) ? dataProvider.getDataAsJSONArray(requestedURL) : itemsByDefaultReportType;
        this.itemsByReportType = itemsByVertical;
        // items in table by buyer guid
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", reportTypesMap.get(reportType))
                .filter(Arrays.asList(VERTICAL_FILTER, BUYER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(vertical, buyer.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBuyerGUID = dataProvider.getDataAsJSONArray(requestedURL);
        // items by buyer instance guid
        if (hasBuyerInstances()) {
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", reportTypesMap.get(reportType))
                    .filter(Arrays.asList(VERTICAL_FILTER, BUYER_FILTER, BUYER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(vertical, buyer.getGuid(), buyerCampaign.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
        }
        // all filters
        List<String> paramsKeys = new ArrayList<>(Arrays.asList("vertical", "buyerguid", "FromPeriod", "ToPeriod"));
        if (hasBuyerInstances()) paramsKeys.add(BUYER_INSTANCE_FILTER);
        List<String> paramsValues = new ArrayList<>(Arrays.asList(vertical, buyer.getGuid(), fromPeriod, toPeriod));
        if (hasBuyerInstances()) paramsValues.add(buyerCampaign.getGuid());
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams("ReportType", reportTypesMap.get(reportType))
                .filter(paramsKeys, paramsValues)
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        // set filter values map
        this.resetData = new AudienceVerticalUnderBuyerReportTestData.FiltersResetData();
        resetData.setFilterValuesMap();
        checkCampaignsTo1Buyer(buyerCampaigns);
    }

    @Override
    public List<FieldFormatObject> getFields() {
        return ((FiltersResetData) resetData).isChangeToDefaultFields() ? fieldsByDefaultReportType : fields;
    }

    @Override
    public String toString() {
        String buyerCampaignDetails = isInstances && !buyerCampaigns.isEmpty() ? buyerCampaign.toString() : "";
        String instanceDetails = isInstances ?
                "reportType=" + reportType +
                        ", vertical=" + vertical +
                        ", buyer=" + buyer +
                        ", publisher=" + publisher +
                        ", buyerCampaign=" + buyerCampaignDetails +
                        ", buyerCampaigns=" + buyerCampaigns.stream().map(campaign -> campaign.getId() +
                        " - " + campaign.getName()).collect(Collectors.joining(", "))
                : "";
        return "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", totalCount='" + totalCount + '\'' +
                "\nAudienceReportTestData{" +
                instanceDetails +
                '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {
        private String defaultReportTypeURL;

        FiltersResetData() {
            this._instanceGroup = instanceGroup;
            this._url = headersURL.replace("?", "&");
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
            // required when ReportType filter to be reset
            this.defaultReportTypeURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .withParams("ReportType", defaultReportTypesMap.get(DEFAULT_REPORT_TYPE))
                    .build().getRequestedURL().replace("?", "&");
        }

        boolean isChangeToDefaultFields() {
            return !filterValuesMap.containsKey(REPORT_TYPE_FILTER);
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            if (hasBuyerInstances())
                filterValuesMap.put(BUYER_INSTANCE_FILTER, buyerCampaign.getGuid());
            filterValuesMap.put(REPORT_TYPE_FILTER, reportTypesMap.get(reportType));
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            if (hasBuyerInstances())
                filterLabelValuesMap.put(BUYER_INSTANCE_FILTER, buyerCampaign.getId() + " - " + buyerCampaign.getName());
            filterLabelValuesMap.put(REPORT_TYPE_FILTER, reportType);
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
            if (resetIterationMap.size() > 1) {
                resetIterationMap.remove(REPORT_TYPE_FILTER);
            }
            if (resetIterationMap.size() > 1)
                return itemsByFiltersCombination;
            else if (resetIterationMap.size() == 0)
                return itemsByDefaultReportType;
            // last iteration
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case BUYER_INSTANCE_FILTER:
                    return isChangeToDefaultFields() ? itemsByFiltersCombination : itemsByBuyerInstanceGUID;
                case REPORT_TYPE_FILTER:
                    return itemsByDefaultReportType;
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
            if (resetIterationMap.isEmpty() || (resetIterationMap.size() == 1 && resetIterationMap.containsKey(REPORT_TYPE_FILTER)))
                return;
            // add vertical cause other filters are set when it is already set
            resetIterationMap.put(VERTICAL_FILTER, vertical);
            // remove 'ReportType' cause it's url not filter parameter
            resetIterationMap.remove(REPORT_TYPE_FILTER);
            // keys in filter json request
            List<String> keys = new ArrayList<>(resetIterationMap.keySet());
            keys.addAll(Arrays.asList(_fromPeriodKey, _toPeriodKey));
            // values in filter json request
            List<String> values = new ArrayList<>(resetIterationMap.values());
            values.addAll(Arrays.asList(fromPeriod, toPeriod));
            // get rows in table after filter combination
            String requestedURL = new RequestedURL.Builder()
                    .withAbsoluteURL(isChangeToDefaultFields() || filterReset.equals(REPORT_TYPE_FILTER) ? defaultReportTypeURL : _url)
                    .filter(keys, values)
                    .sort(_sortBy, _sortHow)
                    .build().getRequestedURL();
            this.itemsByFiltersCombination = dataProvider.getDataAsJSONArray(requestedURL);
        }
    }
}