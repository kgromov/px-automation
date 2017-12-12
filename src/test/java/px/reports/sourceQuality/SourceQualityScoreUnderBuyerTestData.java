package px.reports.sourceQuality;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import px.reports.UserReports;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kgr on 11/17/2016.
 */
public class SourceQualityScoreUnderBuyerTestData extends SourceQualityScoreTestData implements UserReports {

    public SourceQualityScoreUnderBuyerTestData(int filters) {
        super(filters);
    }

    public SourceQualityScoreUnderBuyerTestData(boolean isInstances) {
        super(false);
        this.isInstances = true;
        setInstanceGroup("sourceqiqscore/report");
        setSorting("qiqScore", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        setHeaders();
        // any available buyer
        this.buyer = ObjectIdentityData.getAnyObjectFromList(dataProvider.getCreatedInstancesData("buyers"));
        // rows in table by buyes GUID
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(BUYER_FILTER, "FromPeriod", "ToPeriod"),
                        Arrays.asList(buyer.getGuid(), fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.itemsByBuyerGUID = dataProvider.getDataAsJSONArray(requestedURL);
        // buyer instances - filter items
        this.buyerCampaigns = dataProvider.getCreatedInstancesData("buyerinstancesbyparent",
                Collections.singletonList("parent"), Collections.singletonList(buyer.getGuid()));
        this.buyerCampaign = buyerCampaigns.isEmpty() ? null : ObjectIdentityData.getAnyObjectFromList(buyerCampaigns);
        this.itemsByBuyerInstanceGUID = new JSONArray();
        if (hasBuyerInstances()) {
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_FILTER, BUYER_INSTANCE_FILTER, "FromPeriod", "ToPeriod"),
                            Arrays.asList(buyer.getGuid(), buyerCampaign.getGuid(), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerInstanceGUID = dataProvider.getDataAsJSONArray(requestedURL);
        }
        checkCampaignsTo1Buyer(buyerCampaigns);
    }

    @Override
    public List<String> filters() {
        return Collections.singletonList(BUYER_INSTANCE_FILTER);
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "buyer=" + buyer +
                        ", buyerCampaign=" + (!buyerCampaigns.isEmpty() ? buyerCampaign.toString() : "") +
                        ", buyerCampaigns=" + (!buyerCampaigns.isEmpty() ? buyerCampaigns.stream().map(campaign -> campaign.getId() +
                        " - " + campaign.getName()).collect(Collectors.joining(", ")) : "")
                : "";
        return "\nSourceQualityScoreTestData{" +
                "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", totalCount='" + totalCount + '\'' +
                instanceDetails +
                '}';
    }
}