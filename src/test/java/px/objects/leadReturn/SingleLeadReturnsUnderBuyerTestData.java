package px.objects.leadReturn;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import dto.TestData;
import org.json.JSONObject;
import px.reports.leadReturns.LeadReturnStatusEnum;
import px.reports.leadReturns.LeadReturnsReportTestData;

import java.util.*;

import static configuration.helpers.DataHelper.getRandomInt;
import static px.reports.campaigns.CampaignsReportTestData.CAMPAIGNS_INSTANCE_NAME;

/**
 * Created by kgr on 9/4/2017.
 */
public class SingleLeadReturnsUnderBuyerTestData extends SingleLeadReturnsTestData implements TestData {
    private List<ObjectIdentityData> campaigns;

    public SingleLeadReturnsUnderBuyerTestData() {
    }

    public SingleLeadReturnsUnderBuyerTestData(boolean b) {
        super(b);
        setCampaign("");
    }

    public SingleLeadReturnsUnderBuyerTestData(LeadReturnsReportTestData reportData, LeadReturnStatusEnum statusEnum) {
        super(false);
        this.isPositive = true;
        // prerequisite
        this.status = statusEnum.getValue();
        this.periodMonth = reportData.getPeriodMonthLabel();
        // choose any of existed leads return and set single lead return values
        JSONObject object = reportData.getItemsByPeriodMonth().getJSONObject(getRandomInt(reportData.getItemsByPeriodMonthCount()));
        this.campaign = new ObjectIdentityData(object.getString("buyerName"), String.valueOf(object.get("buyerId")), null);
        this.email = object.getString("emailAddress");
        this.reason = object.getString("standardReason");
        this.explanation = object.getString("theirReason");
    }

    public List<ObjectIdentityData> getCampaigns() {
        return campaigns;
    }

    public Set<String> getCampaignIDs() {
        return new HashSet<>(ObjectIdentityData.getAllIDs(campaigns));
    }

    @Override
    protected void setCampaign(String campaignID) {
        log.info("Select buyer campaign with id = '%s' from all buyer campaigns");
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + CAMPAIGNS_INSTANCE_NAME)
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(period, period))
                .sort("totalSpend", "desc")
                .build().getRequestedURL();
        this.campaigns = ObjectIdentityData.getObjectsByJSONArray(dataProvider.getDataAsJSONArray(requestedURL));
        this.campaign = ObjectIdentityData.getObjectFromListByID(campaigns, campaignID);
    }

}
