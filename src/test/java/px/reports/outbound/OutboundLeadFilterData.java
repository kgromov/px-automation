package px.reports.outbound;

import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import px.funtional.lead.LeadDataUtils;
import px.objects.leads.LeadObject;
import px.objects.leads.Leadable;
import px.reports.ReportTestData;
import px.reports.dto.DateRange;
import px.reports.leads.LeadsReportTestData;
import utils.SoftAssertionHamcrest;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.PX_REPORT_DATE_PATTERN;
import static configuration.helpers.DataHelper.getDateByFormatSimple;
import static org.hamcrest.Matchers.equalTo;
import static px.reports.campaigns.CampaignsReportTestData.CAMPAIGNS_INSTANCE_NAME;
import static px.reports.outbound.OutboundTransactionTestData.BUYER_RESULT_CODE_FILTER;
import static px.reports.outbound.OutboundTransactionTestData.POST_TYPE_FILTER;

/**
 * Created by kgr on 11/10/2017.
 */
public class OutboundLeadFilterData extends ReportTestData implements Leadable {
    {
        this.startMonthOffset = 1;
        this.durationDays = 1;
    }

    // initial data
    private static Set<String> campaignIDs;
    // lead data
    private String xmlData;
    private JSONObject outboundTransaction;
    private JSONObject inboundTransaction;
    private JSONObject leadTransaction;
    private LeadsReportTestData.ResponseObject lead;
    private Set<LeadsReportTestData.ResponseObject> leads = new HashSet<>();
    // constants
    private Map<String, String> resultCodesMap = dataProvider.getPossibleValueFromJSON("BuyerPostResultCodes", "enumSequenceIndex");
    private static final String BUYER_RESULT_CODE_VALUE = "Success";
    private static final String POST_TYPE_VALUE = "POST|DIRECTPOST";
    // to set proper transaction
    private final static Predicate<JSONObject> TRANSACTION_CONDITION = item ->
            item.getBoolean("detailsAvailable") && campaignIDs.contains(String.valueOf(item.get("buyerId")));

    public OutboundLeadFilterData() {
        setInstanceGroup("outboundtransactions/report");
        setSorting("postDate", "desc");
//        setSorting("leadGuid", "desc");
        // prerequisite step
        // set available campaigns - automation created - see in CampaignDataProvider
        String period = getDateByFormatSimple(PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + CAMPAIGNS_INSTANCE_NAME)
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), // "buyerInstanceName"),
                        Arrays.asList(period, period))//, "Campaign Name "))
                .sort("totalSpend", "desc")
                .build().getRequestedURL();
        List<ObjectIdentityData> campaigns = ObjectIdentityData.getObjectsByJSONArray(dataProvider.getDataAsJSONArray(requestedURL));
        campaignIDs = new HashSet<>(ObjectIdentityData.getAllIDs(campaigns));
        setDateRanges();
        validateDateRangeBigData();
        setAllRowsByDateRange();
//        setHeaders();
        setLead();
    }

    @Override
    public void setLead() {
        Set<DateRange> ranges = new HashSet<>();
        do {
            setLead(allRowsArray);
            if (inboundTransaction != null) return;
            ranges.add(new DateRange(fromPeriod, toPeriod));
            setDateRanges();
            setAllRowsByDateRange();
        } while (ranges.size() < 5);
        throw new TestDataException(String.format("No success POST or DirectPost leads after 5 attempts in the following ranges %s", ranges));
    }

    @Override
    public void setLead(JSONArray allRowsArray) {
        if (allRowsArray.length() == 0) return;
        // filter by success and postType in outbound report
        List<JSONObject> transactions = JSONWrapper.toList(allRowsArray).stream()
                .filter(TRANSACTION_CONDITION).collect(Collectors.toList());
        Collections.shuffle(transactions);
        // search in inbound report (to prevent duplicates?)
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/inboundtransactions/report")
                .filter(Arrays.asList("LeadGuidFilter", "FromPeriod", "ToPeriod"),
                        Arrays.asList("%s", fromPeriod, toPeriod))
                .sort("date", "desc")
                .build().getRequestedURL();
        JSONArray itemsSearchBy = null;
        for (JSONObject transaction : transactions) {
            this.outboundTransaction = transaction;
            String leadGUID = String.valueOf(transaction.get("leadGuid"));
            // inbound  data
            String requestedURL2 = new RequestedURL.Builder()
                    .withRelativeURL("api/leadpreview/inbound")
                    .withParams("leadGuid", leadGUID)
                    .build().getRequestedURL();
            JSONArray jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL2)).getJSONArray();
            JSONObject xmlBody = JSONWrapper.toList(jsonArray).stream().filter(field ->
                    field.has("field") && field.getString("field").equals("XmlBody"))
                    .findFirst().orElse(null);
            this.xmlData = xmlBody != null ? xmlBody.getString("default") : null;
            //TODO: request by lookup if only xmlPost (cause unknown how to generate partner token)
            itemsSearchBy = xmlData != null && !LeadDataUtils.isHttpPost(xmlData)
                    ? dataProvider.getDataAsJSONArray(String.format(requestedURL, leadGUID)) : null;
            // till RVMD-14055 is done - add filter by vertical - remove from itemsSearchBy verticals {PAYDAYLOANS, PERSONALLOANS}
            List<Integer> indexes = new ArrayList<>();
            if (itemsSearchBy != null) {
                for (int i = 0; i < itemsSearchBy.length(); i++) {
                    JSONObject object = itemsSearchBy.getJSONObject(i);
                    String vertical = object.getString("vertical");
                    if (vertical.toUpperCase().equals("PAYDAYLOANS") || vertical.toUpperCase().equals("PERSONALLOANS"))
                        indexes.add(i);
                }
                indexes.forEach(itemsSearchBy::remove);
            }
            if (itemsSearchBy != null && itemsSearchBy.length() > 0) break;
        }
        // finally set lead Data if there are matches between outbound and inbound
        this.inboundTransaction = itemsSearchBy != null && itemsSearchBy.length() > 0 ? itemsSearchBy.getJSONObject(0) : null;
        log.info("Matched inbound transaction: " + inboundTransaction);
        // verify data accordance in outbound and inbound transactions
        if (inboundTransaction != null) checkTransactionsAccordance();
    }

    private void checkTransactionsAccordance() {
        // {publisherId, subId,  email, ?date, ?campaignId,}
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("PublisherId in both transactions is the same",
                outboundTransaction.get("publisherId"), equalTo(inboundTransaction.get("publisherId")));
        hamcrest.assertThat("SubId in both transactions is the same",
                outboundTransaction.get("subId"), equalTo(inboundTransaction.get("subId")));
        hamcrest.assertThat("Email in both transactions is the same",
                outboundTransaction.get("email"), equalTo(inboundTransaction.get("email")));
        hamcrest.assertAll();
    }

    public String getXmlData() {
        return xmlData;
    }

    public JSONObject getOutboundTransaction() {
        return outboundTransaction;
    }

    public JSONObject getInboundTransaction() {
        return inboundTransaction;
    }

    @Override
    public LeadObject getLead() {
        return null;
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList(BUYER_RESULT_CODE_FILTER, POST_TYPE_FILTER, "PostDate", "PostDate2"),
                        Arrays.asList(resultCodesMap.get(BUYER_RESULT_CODE_VALUE), POST_TYPE_VALUE, fromPeriod, toPeriod))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        this.totalCount = dataProvider.getCurrentTotal();
    }

    @Override
    public String toString() {
        return "OutboundLeadFilterData{" +
                "xmlData='" + xmlData + '\'' +
                ", outboundTransaction=" + outboundTransaction +
                ", inboundTransaction=" + inboundTransaction +
                '}';
    }
}
