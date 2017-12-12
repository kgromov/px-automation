package px.reports.audience;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import px.reports.UserReports;
import px.reports.dto.FieldFormatObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static px.reports.audience.AudienceFiltersEnum.DEFAULT_REPORT_TYPE;
import static px.reports.audience.AudienceFiltersEnum.DEFAULT_VERTICAL;

/**
 * Created by kgr on 4/26/2017.
 */
public class AudienceNonVerticalUnderBuyerReportTestData extends AudienceReportTestData implements UserReports {

    public AudienceNonVerticalUnderBuyerReportTestData() {
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
        // all filters
        List<String> paramsKeys = new ArrayList<>(Arrays.asList("vertical", "buyerguid", "FromPeriod", "ToPeriod"));
        if (hasBuyerInstances()) paramsKeys.add(BUYER_INSTANCE_FILTER);
        List<String> paramsValues = new ArrayList<>(Arrays.asList(vertical, buyer.getGuid(), fromPeriod, toPeriod));
        if (hasBuyerInstances()) paramsValues.add(buyerCampaign.getGuid());
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
//                    .withParams("ReportType", reportTypesMap.get(reportType))
                .withParams("ReportType", hasChangedAfterVerticalReset()
                        ? defaultReportTypesMap.get(DEFAULT_REPORT_TYPE) : defaultReportTypesMap.get(reportType))
                .filter(paramsKeys, paramsValues)
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
        checkCampaignsTo1Buyer(buyerCampaigns);
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
}