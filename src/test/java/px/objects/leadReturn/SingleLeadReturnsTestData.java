package px.objects.leadReturn;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.LocaleData;
import dto.ObjectIdentityData;
import dto.TestData;
import dto.TestDataException;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.leads.Leadable;
import px.reports.ReportTestData;
import px.reports.dto.DateRange;
import px.reports.leadReturns.LeadReturnStatusEnum;
import px.reports.leadReturns.LeadReturnsReportTestData;
import px.reports.leads.LeadsReportTestData;

import java.util.*;
import java.util.function.Predicate;

import static configuration.helpers.DataHelper.*;
import static px.reports.campaigns.CampaignsReportTestData.CAMPAIGNS_INSTANCE_NAME;
import static px.reports.leadReturns.LeadReturnStatusEnum.PENDING;

/**
 * Created by kgr on 9/4/2017.
 */
public class SingleLeadReturnsTestData extends ReportTestData implements TestData, Leadable {
    {
        this.startMonthOffset = 3;
        this.durationDays = 1;
    }

    // lead report data navigate to return
    protected LeadsReportTestData.ResponseObject leadResponse;
    // parent data
    protected ObjectIdentityData campaign;
    // lead rerun data
    protected String email;
    protected String reason;
    protected String explanation;
    protected String declineExplanation;
    // report data
    protected String status;
    protected String periodMonth;
    // test mode
    protected boolean isPositive = true;
    // for reiteration
    private static final Predicate<JSONObject> SOLD_LEAD_CONDITION = lead ->
            !String.valueOf(lead.get("buyerId")).equals("43")
                    && String.valueOf(lead.get("buyerId")).split(" - ").length == 1 // leave for now multiple buyers lead
                    && lead.has("canReturn") && lead.getBoolean("canReturn");

    public SingleLeadReturnsTestData(boolean fake) {
    }

    public SingleLeadReturnsTestData() {
        // prerequisite
        setInstanceGroup("leaddetailsfastreport/data");
        setSorting("dateTime", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        // set lead that is sold and carReturn
        setLead();
        String campaignID = leadResponse.getBuyerId();
        setCampaign(campaignID);
        // reason
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/reasons")
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL));
        List<String> dispositionStatuses = DataHelper.getListFromJSONArrayByKey(jsonArray, "ourReason");
        this.reason = DataHelper.getRandomValueFromList(dispositionStatuses);
        // set period month
        this.periodMonth = getDateByFormatSimple(PERIOD_MONTH_PATTERN, leadResponse.getDate());
    }

    /**
     * This constructor is required to choose one from existed by status
     *
     * @param statusEnum - status enum
     */
    public SingleLeadReturnsTestData(LeadReturnStatusEnum statusEnum) {
        this.isPositive = true;
        // prerequisite
        LeadReturnsReportTestData reportData = new LeadReturnsReportTestData(PENDING.getValue()).withAttempts();
        if (!reportData.isAnyLeadReturnByStatus())
            throw new TestDataException(String.format("No returned leads in month %s with status %s",
                    reportData.getPeriodMonthLabel(), PENDING.getValue()));
        this.status = statusEnum.getValue();
        this.periodMonth = reportData.getPeriodMonthLabel();
        // choose any of existed leads return and set single lead return values
        JSONObject object = reportData.getItemsByPeriodMonth().getJSONObject(getRandomInt(reportData.getItemsByPeriodMonthCount()));
        this.campaign = new ObjectIdentityData(object.getString("buyerName"), String.valueOf(object.get("buyerId")), null);
        this.email = object.getString("emailAddress");
        this.reason = object.getString("standardReason");
        this.explanation = object.getString("theirReason");
    }

    public SingleLeadReturnsTestData(LeadReturnsReportTestData reportData, JSONObject returnObject) {
        this.isPositive = true;
        this.status = reportData.getReportType();
        this.periodMonth = reportData.getPeriodMonthLabel();
        // already chosen return lead object
        this.campaign = new ObjectIdentityData(returnObject.getString("buyerName"), String.valueOf(returnObject.get("buyerId")), null);
        this.email = returnObject.getString("emailAddress");
        this.reason = returnObject.getString("standardReason");
        this.explanation = returnObject.getString("theirReason");
    }

    public SingleLeadReturnsTestData withDeclineExplanation(boolean isPositive) {
        this.isPositive = isPositive;
        if (isPositive) {
            this.declineExplanation = "Decline return cause " + RandomStringUtils.random(6, true, true);
        } else {
            LocaleData localeData = new LocaleData();
            this.declineExplanation = "Decline return cause " +
                    getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        }
        return this;
    }

    public SingleLeadReturnsTestData withStatus(LeadReturnStatusEnum statusEnum) {
        this.status = statusEnum.getValue();
        return this;
    }

    public SingleLeadReturnsTestData withPeriodMonth(String periodMonth) {
        this.periodMonth = periodMonth;
        return this;
    }

    protected void setCampaign(String campaignID) {
        log.info("Select buyer campaign with id = '%s' from report response");
        // init return data
        if (leadResponse.getBuyerInstanceName() != null)
            this.campaign = new ObjectIdentityData(campaignID, leadResponse.getBuyerInstanceName(), null);
        else {
            String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + CAMPAIGNS_INSTANCE_NAME)
                    .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(period, period))
                    .sort("totalSpend", "desc")
                    .build().getRequestedURL();
            List<JSONObject> campaigns = JSONWrapper.toList(dataProvider.getDataAsJSONArray(requestedURL));
            JSONObject object = campaigns.stream().filter(campaign ->
                    String.valueOf(campaign.get("buyerInstanceID")).equals(campaignID)).findFirst().orElse(null);
            this.campaign = object != null ? new ObjectIdentityData(campaignID, String.valueOf(object.get("buyerInstanceName")),
                    String.valueOf(object.get("buyerInstanceGuid")))
                    : null;
        }
        if (campaign == null)
            throw new TestDataException(String.format("No matched campaign name to campaignID '%s'\tDetails: %s", campaignID, leadResponse.toJSON()));
    }

    // ------------------ Select proper lead with 5 attempts ------------------
    @Override
    public void setLead() {
        Set<DateRange> ranges = new HashSet<>();
        do {
            setLead(allRowsArray);
            if (leadResponse != null) return;
            setInstanceGroup("leaddetailsfastreport/data");
            ranges.add(new DateRange(fromPeriod, toPeriod));
            setDateRanges();
            setAllRowsByDateRange();
        } while (ranges.size() < 5);
        throw new TestDataException(String.format("No leads satisfied condition <soldLead and canReturn> " +
                "after 5 attempts in the following ranges %s]", ranges));

    }

    @Override
    public void setLead(JSONArray allRowsArray) {
        List<JSONObject> leads = JSONWrapper.toList(allRowsArray);
        Collections.shuffle(leads);
        JSONObject lead = leads.stream().filter(SOLD_LEAD_CONDITION).findFirst().orElse(null);
        this.leadResponse = lead != null ? LeadsReportTestData.getResponseObject(lead) : null;
        log.info("DEBUG:\t" + leadResponse);
        setInstanceGroup("leadpreview");
    }

    @Override
    public LeadsReportTestData.ResponseObject getLead() {
        return leadResponse;
    }

    public ObjectIdentityData getCampaign() {
        return campaign;
    }

    public String getEmail() {
        return email;
    }

    public String getReason() {
        return reason;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getDeclineExplanation() {
        return declineExplanation;
    }

    // not to init all report data
    public String getStatus() {
        return status;
    }

    public String getPeriodMonth() {
        return periodMonth;
    }

    public JSONObject toJSONWithStatus(String status) {
        JSONObject object = leadResponse.toJSON();
        // cause lead return and lead have differences in keys
        object.put("buyerName", leadResponse.getBuyerInstanceName());
        object.put("leadCreationDate", leadResponse.getCreationDate());
        object.put("emailAddress", email);
        // add lead return form fields
        object.put("standardReason", reason);
        object.put("theirReason", explanation);
        object.put("reviewStatus", status);
        return object;
    }

    @Override
    public TestData setPositiveData() {
        this.isPositive = true;
        this.email = leadResponse.getEmail(); // possibly change for negative test
        this.explanation = "Return cause " + RandomStringUtils.random(6, true, true);
        this.declineExplanation = "Decline return cause " + RandomStringUtils.random(6, true, true);
        return this;
    }

    @Override
    public TestData setNegativeData() {
        this.isPositive = false;
        LocaleData localeData = new LocaleData();
        this.email = leadResponse.getEmail(); // possibly change for negative test
        this.explanation = "Return cause " +
                getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        this.declineExplanation = "Decline return cause " +
                getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        return this;
    }

    @Override
    public boolean isPositive() {
        return isPositive;
    }

    @Override
    public String toString() {
        return "SingleLeadReturnsTestData{" +
                "lead=" + leadResponse +
                ", campaign=" + campaign +
                ", email='" + email + '\'' +
                ", reason='" + reason + '\'' +
                ", explanation='" + explanation + '\'' +
                ", declineExplanation='" + declineExplanation + '\'' +
                ", fromPeriod=" + fromPeriod +
                ", toPeriod=" + toPeriod +
                '}';
    }
}