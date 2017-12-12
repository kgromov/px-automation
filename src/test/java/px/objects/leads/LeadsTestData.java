package px.objects.leads;

import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.TestDataException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import px.objects.disposition.DispositionTestData;
import px.objects.leads.buyer.LeadsUnderBuyerTestData;
import px.reports.ReportTestData;
import px.reports.dto.DateRange;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.TransactionData;
import px.reports.leads.LeadsReportTestData;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.*;

/**
 * Created by kgr on 3/2/2017.
 */
public class LeadsTestData extends ReportTestData implements Leadable {
    {
        this.startMonthOffset = 1;
        this.durationDays = 1;
    }

    // lead report data navigate to preview
    protected LeadsReportTestData.ResponseObject lead;
    protected Set<LeadsReportTestData.ResponseObject> leads = new HashSet<>();
    // lead preview data
    protected JSONArray contactDataArray;         // api/leadpreview/contact?leadId=148652&toPeriod=2017-02-08
    protected JSONArray detailsDataArray;         // api/lead?leadId=148652&toPeriod=2017-02-08
    protected JSONArray tcpaDataArray;            // api/leadpreview/tcpa?leadId=148652&toPeriod=2017-02-08
    private JSONArray inboundDataArray;            // api/leadpreview/inbound?leadGuid=4B33D00E-DEAA-4BEC-9034-788A11613854
    protected JSONArray leadAttributesArray;      // api/leadpreview/answers?leadGuid=<GUID>
    private JSONArray buyerDetailsArray;        // api/leadpreview?count=10&filter={"leadId":"148652"}&page=1&sorting={"buyerName":"asc"}
    private JSONArray leadTransactionsArray;    // api/leadtransactions?count=10&filter={"leadId":"148652"}&page=1&sorting={"buyerName":"asc"}
    protected JSONArray dispositionHistory;      // api/leadtransactions?count=10&filter={"leadId":"148652"}&page=1&sorting={"buyerName":"asc"}
    private List<FieldFormatObject> buyerFields;
    private List<FieldFormatObject> transactionFields;
    private List<FieldFormatObject> dispositionFields;
    private int leadTransactionsCount;
    protected boolean isSoldLead;
    // aggregated map
    protected Map<String, String> leadDetailsMap;
    private List<LeadTransaction> leadTransactions;
    protected static final List<String> mismatchFormatList = Arrays.asList("qiqScore", "payout", "floorPayout", "directBid");
    protected static final List<String> digitsList = Arrays.asList("sessionLength", "phoneNumber", "homePhoneNumber");
    // for  reiteration
    private static final Predicate<String> LEAD_MORE_1_BUYER = buyer -> buyer.split(" - ").length > 1;
    // to preserve order
    private static final Comparator<LeadsReportTestData.ResponseObject> CREATION_DATE_COMPARATOR =
            (LeadsReportTestData.ResponseObject lead1, LeadsReportTestData.ResponseObject lead2) -> {
                long time1 = lead1.getDate().getTime();
                long time2 = lead2.getDate().getTime();
                return time1 > time2 ? -1 : (time1 == time2 ? 0 : 1);
            };


    static {
        // buyers
        missedHeadersMetricsMap.put("payout", "Currency");
        missedHeadersMetricsMap.put("floorPayout", "Currency");
        missedHeadersMetricsMap.put("directBid", "Currency");
        missedHeadersMetricsMap.put("creationDate", "Date");
        // transactions
        missedHeadersMetricsMap.put("postDate", "Date");
        // disposition
        missedHeadersMetricsMap.put("dispositionDate", "Date");
    }

    // mapping
    static {
        dataMap.put("0", "a few seconds");
        dataMap.put("1", "a minute");
    }

    protected LeadsTestData(boolean isDisposition) {
        // prerequisite
        setInstanceGroup("leaddetailsfastreport/data");
        setSorting("dateTime", "desc");
        setDateRanges();
        if (isDisposition) validateDateRangeBigData();
        setAllRowsByDateRange();
        setInstanceGroup("leadpreview");
        // buyers and transactions metadata
        if (!(this instanceof LeadsUnderBuyerTestData)) setHeaders();
    }

    public LeadsTestData() {
        this(false);
        // validation if there any available data
        setLead();
        this.isSoldLead = lead.isSoldLead();
        String requestedURL = null;
        try {
            // lead contact data
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup + "/contact")
                    .withParams(Arrays.asList("leadId", "toPeriod"), Arrays.asList(lead.getLeadId(), fromPeriod))
                    .build().getRequestedURL();
            this.contactDataArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL)).getJSONArray();
            // lead details
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup + "/lead")
                    .withParams(Arrays.asList("leadId", "toPeriod"), Arrays.asList(lead.getLeadId(), fromPeriod))
                    .build().getRequestedURL();
            this.detailsDataArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL)).getJSONArray();
            // tcpa and request data
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup + "/tcpa")
                    .withParams(Arrays.asList("leadId", "toPeriod"), Arrays.asList(lead.getLeadId(), fromPeriod))
                    .build().getRequestedURL();
            this.tcpaDataArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL)).getJSONArray();
            // lead attributes
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup + "/answers")
                    .withParams("leadGuid", lead.getLeadGuid())
                    .build().getRequestedURL();
            this.leadAttributesArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL)).getJSONArray();
            // inbound  transactions
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup + "/inbound")
                    .withParams("leadGuid", lead.getLeadGuid())
                    .build().getRequestedURL();
            this.inboundDataArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL)).getJSONArray();
            // buyer details
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup + "/buyer")
                    .filter(Arrays.asList("leadId", "toPeriod"), Arrays.asList(lead.getLeadId(), fromPeriod))
                    .withEmptySorting()
                    .build().getRequestedURL();
            this.buyerDetailsArray = dataProvider.getDataAsJSONArray(requestedURL);
            // lead transactions
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/leadtransactions")
                    .filter("leadId", lead.getLeadId())
                    .sort("buyerName", "asc")
                    .build().getRequestedURL();
            this.leadTransactionsArray = dataProvider.getDataAsJSONArray(requestedURL);
            this.leadTransactionsCount = dataProvider.getCurrentTotal();
        } catch (JSONException e) {
            throw new TestDataException(String.format("Unable to parse response by requested url = '%s'\tDetails:\t%s",
                    requestedURL, e.getMessage()), e);
        }
        // disposition history only if lead canUpdateDisposition
        if (lead.isCanChangeDisposition())
            setDispositionHistory();
        // aggregate like key-value
        leadDetailsMap = new HashMap<>();
        List<String> keys = getListFromJSONArrayByKey(contactDataArray, "field", false);
        List<String> values = getListFromJSONArrayByKey(contactDataArray, "default", false);
        // lead details
        keys.addAll(getListFromJSONArrayByKey(detailsDataArray, "field", false));
        values.addAll(getListFromJSONArrayByKey(detailsDataArray, "default", false));
        // tcpa/request data
        keys.addAll(getListFromJSONArrayByKey(tcpaDataArray, "field", false));
        values.addAll(getListFromJSONArrayByKey(tcpaDataArray, "default", false));
        // inbound data
        keys.addAll(getListFromJSONArrayByKey(inboundDataArray, "field", false));
        values.addAll(getListFromJSONArrayByKey(inboundDataArray, "default", false));
        // lead attributes - capitalized
        keys.addAll(getListFromJSONArrayByKey(leadAttributesArray, "field", false));
        values.addAll(getListFromJSONArrayByKey(leadAttributesArray, "value", false));
        // set buyer details
        for (int i = 0; buyerDetailsArray.length() == 1 && i < buyerDetailsArray.length(); i++) {
            JSONObject object = buyerDetailsArray.getJSONObject(i);
            // add buyer details to leadDetails map
            for (String key : object.keySet()) {
                keys.add(key);
                values.add(String.valueOf(object.get(key)));
            }
        }
        // set lead transactions
        this.leadTransactions = new ArrayList<>(leadTransactionsArray.length());
        for (int i = 0; i < leadTransactionsArray.length(); i++) {
            leadTransactions.add(new LeadTransaction(leadTransactionsArray.getJSONObject(i)));
        }
        // perform once
        keys.forEach(key -> leadDetailsMap.put(key, values.get(keys.indexOf(key)).replaceAll("\\s+", " ")));
        // delete some keys cause some are in lower case, some are capitalized
        Arrays.asList("firstName", "lastName", "zipcode", "birthDate", "gender",
                "publisherGuid", "publisherName", "publisherInstanceGuid",
                "leadGuid", "leadID", "buyerGuid", "buyerInstanceGuid", "campaignGuid").forEach(key -> leadDetailsMap.remove(key));
        // change some values to proper format
        leadDetailsMap.put("creationDate", lead.getCreationDate());
        leadDetailsMap.put("qiqScore", DataHelper.getSplittedByComma(leadDetailsMap.get("qiqScore"), 2));
        if (!isLeadSoldToMoreThan1Buyer()) {
            leadDetailsMap.put("floorPayout", DataHelper.getSplittedByComma(leadDetailsMap.get("floorPayout"), 2));
            leadDetailsMap.put("directBid", DataHelper.getSplittedByComma(leadDetailsMap.get("directBid"), 2));
            leadDetailsMap.put("payout", DataHelper.getSplittedByComma(leadDetailsMap.get("payout"), 2));
        }
        if (getLeadDetailsMap().containsKey("XmlBody"))
            leadDetailsMap.put("XmlBody", leadDetailsMap.get("XmlBody").replaceAll("\t|\n|\r", "").replaceAll("> <", "><"));
        if (leadDetailsMap.containsKey("theLeadId")) {
            String theLead = leadDetailsMap.get("theLeadId");
            if (Pattern.compile("0+|0{32}").matcher(DataHelper.remainDigits(theLead)).matches())
                leadDetailsMap.put("theLeadId", "N/A");
        }
    }

    public List<LeadTransaction> getLeadTransactions() {
        return leadTransactions;
    }

    public Map<String, String> getLeadDetailsMap() {
        return leadDetailsMap;
    }

    public int getLeadTransactionsCount() {
        return leadTransactionsCount;
    }

    public static boolean isRoundedFormatField(String fieldName) {
        return mismatchFormatList.contains(fieldName);
    }

    public static boolean compareDigitsField(String fieldName) {
        return digitsList.contains(fieldName);
    }

    public boolean isLeadSoldToMoreThan1Buyer() {
        return buyerDetailsArray != null && buyerDetailsArray.length() > 1;
    }

    // disposition history
    public void setDispositionHistory() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/leadDispositionHistory")
                .filter("leadGuid", lead.getLeadGuid())
                .sort("dispositionDate", "desc")
                .build().getRequestedURL();
        try {
            this.dispositionHistory = dataProvider.getDataAsJSONArray(requestedURL);
            // update according to mapping
            this.dispositionHistory = DispositionTestData.updateWithReachTime(dispositionHistory, "reachTime");
        } catch (JSONException e) {
            throw new TestDataException(String.format("Unable to parse response by requested url = '%s'\tDetails:\t%s", requestedURL, e.getMessage()), e);
        }
    }

    public boolean isSoldLead() {
        return isSoldLead;
    }

    public Set<String> getLeadBuyers() {
        List<String> list = new ArrayList<>(Collections.singletonList("-1"));
        if (isSoldLead) {
            list.addAll(Arrays.asList(lead.getBuyerId().split(" - ")));
            Collections.sort(list);
        }
        return new HashSet<>(list);
    }

    public Set<String> getDispositionHistoryBuyers() {
        List<String> list = getListFromJSONArrayByKey(dispositionHistory, "buyerId");
        Collections.sort(list);
        return new HashSet<>(list);
    }

    public List<FieldFormatObject> getBuyerFields() {
        return buyerFields;
    }

    public List<FieldFormatObject> getTransactionFields() {
        return transactionFields;
    }

    public List<FieldFormatObject> getDispositionFields() {
        return dispositionFields;
    }

    public JSONArray getBuyerDetailsArray() {
        return buyerDetailsArray;
    }

    public JSONArray getLeadTransactionsArray() {
        return leadTransactionsArray;
    }

    public JSONArray getDispositionHistory() {
        return dispositionHistory;
    }

    @Override
    protected void setHeaders() {
        // buyer fields metadata
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/leadpreview/buyer")
                .build().getRequestedURL();
        this.headers = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        this.headersList = getListFromJSONArrayByKey(headers, "field");
        this.buyerFields = new ArrayList<>(headersList.size());
        for (int i = 0; i < headersList.size(); i++) {
            buyerFields.add(new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i));
        }
        // lead transactions fields metadata
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/leadtransactions")
                .build().getRequestedURL();
        this.headers = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        this.headersList = getListFromJSONArrayByKey(headers, "field");
        this.transactionFields = new ArrayList<>(headersList.size());
        for (int i = 0; i < headersList.size(); i++) {
            transactionFields.add(new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i));
        }
        // disposition fields metadata
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/leadDispositionHistory")
                .build().getRequestedURL();
        this.headers = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS));
        this.headersList = getListFromJSONArrayByKey(headers, "field");
        this.dispositionFields = new ArrayList<>(headersList.size());
        for (int i = 0; i < headersList.size(); i++) {
            dispositionFields.add(new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i));
        }
    }

    // ------------------ Select proper lead with 5 attempts ------------------
    @Override
    public void setLead() {
        Set<DateRange> ranges = new HashSet<>();
        do {
            setLead(allRowsArray);
            if (lead != null) return;
            setInstanceGroup("leaddetailsfastreport/data");
            ranges.add(new DateRange(fromPeriod, toPeriod));
            setDateRanges();
            setAllRowsByDateRange();
        } while (ranges.size() < 5);
        throw new TestDataException(String.format("%s after 5 attempts in the following ranges %s",
                (this instanceof DispositionTestData) ? "No sold leads" : "There is no data", ranges));
    }

    @Override
    public void setLead(JSONArray allRowsArray) {
        if (allRowsArray.length() == 0) return;
        // choose any sold lead from array
        int leadIndex = DataHelper.getRandomInt(allRowsArray.length());
        log.info(String.format("Get '%d' lead from total '%d'", leadIndex, allRowsArray.length()));
        this.lead = LeadsReportTestData.getResponseObject(allRowsArray.getJSONObject(leadIndex));
        setInstanceGroup("leadpreview");
    }

    // to test on 2 buyers
    public void setLeadMoreThan1Buyer(JSONArray allRowsArray) {
        List<String> buyerIDs = getListFromJSONArrayByKey(allRowsArray, "buyerId");
        log.info("buyerIDs = " + buyerIDs);
        String buyerID = buyerIDs.stream().filter(LEAD_MORE_1_BUYER).findFirst().orElse(null);
        JSONObject lead = buyerID != null ? getJSONFromJSONArrayByCondition(allRowsArray, "buyerId", buyerID) : null;
        this.lead = lead != null ? LeadsReportTestData.getResponseObject(lead) : null;
    }

    @Override
    public LeadsReportTestData.ResponseObject getLead() {
        return lead;
    }

    // ----------------- Multiple (several via button) -----------------
    public void setLeads() {
        Set<DateRange> ranges = new HashSet<>();
        do {
            setLeads(allRowsArray);
            if (!leads.isEmpty()) return;
            setInstanceGroup("leaddetailsfastreport/data");
            ranges.add(new DateRange(fromPeriod, toPeriod));
            setDateRanges();
            setAllRowsByDateRange();
        } while (ranges.size() < 5);
        throw new TestDataException(String.format("%s after 5 attempts in the following ranges %s",
                (this instanceof DispositionTestData) ? "No sold leads" : "There is no data", ranges));
    }

    public void setLeads(JSONArray allRowsArray) {
        if (allRowsArray.length() == 0) return;
        int totalAvailable = getItemsTotalCount() > 100 ? 100 : getItemsTotalCount();
        int count = getRandomInt(1, totalAvailable > 10 ? 10 : totalAvailable);
        this.leads = new TreeSet<>(CREATION_DATE_COMPARATOR);
        for (int i = 0; i < count; i++) {
            setLead(allRowsArray);
            if (lead != null) leads.add(lead);
        }
    }

    public Set<LeadsReportTestData.ResponseObject> getLeads() {
        return leads;
    }

    public LeadsReportTestData.ResponseObject[] leadsAsArray() {
        return leads.toArray(new LeadsReportTestData.ResponseObject[leads.size()]);
    }

    @Override
    public String toString() {
        return "LeadsTestData{" +
                "fromPeriod=" + fromPeriod +
                ", toPeriod=" + toPeriod +
                ", transactions=" + leadTransactions.stream().map(LeadTransaction::toString).collect(Collectors.joining(", ")) +
                ", lead=" + lead +
                ", leadDetails=" + leadDetailsMap +
                '}';
    }

    static final class LeadTransaction implements TransactionData {
        private String leadId;
        private String postDate;
        private String transactionId;
        private String source;
        private String subId;
        private String payout;
        private String buyerName;
        private String postType;
        private String buyerPostResultCode;
        private String requestData;
        private String responseData;

        LeadTransaction(JSONObject jsonObject) {
            setData(jsonObject);
        }

        public String getLeadId() {
            return leadId;
        }

        public String getPostDate() {
            Date temp = DataHelper.getDateByFormatSimple(DataHelper.PX_INBOUND_REPORT_PATTERN, postDate.replace("T", " "));
            return DataHelper.getDateByFormatSimple(DataHelper.EXPIRATION_DATE_PATTERN, temp);
        }

        public String getTransactionId() {
            return transactionId;
        }

        public String getSource() {
            return source;
        }

        public String getSubId() {
            return subId;
        }

        public String getPayout() {
            /*DecimalFormat format = new DecimalFormat(String.format("#%s##", FRACTION_SPLITTER));
            return format.format(payout);*/
            try {
                return new BigDecimal(payout).toString();
            } catch (NumberFormatException e) {
                return payout;
            }
        }

        public String getBuyerName() {
            return buyerName;
        }

        public String getPostType() {
            return postType;
        }

        public String getBuyerPostResultCode() {
            return buyerPostResultCode;
        }

        public String getRequestData() {
            return StringEscapeUtils.unescapeJava(requestData);
        }

        public String getResponseData() {
            return StringEscapeUtils.unescapeJava(responseData);
        }

        @Override
        public void setData(JSONObject jsonObject) {
            this.leadId = String.valueOf(jsonObject.get("leadId"));
            this.postDate = String.valueOf(jsonObject.get("postDate"));
            this.transactionId = String.valueOf(jsonObject.get("transactionId"));
            this.source = String.valueOf(jsonObject.get("source"));
            this.subId = String.valueOf(jsonObject.get("subId"));
            this.payout = String.valueOf(jsonObject.get("payout"));
            this.buyerName = String.valueOf(jsonObject.get("buyerName"));
            this.postType = String.valueOf(jsonObject.get("postType"));
            this.buyerPostResultCode = String.valueOf(jsonObject.get("buyerPostResultCode"));
            this.requestData = jsonObject.has("requestData") ? String.valueOf(jsonObject.get("requestData")) : null;
            this.responseData = jsonObject.has("responseData") ? String.valueOf(jsonObject.get("responseData")) : null;
        }

        public boolean hasRequest() {
            return requestData != null && !requestData.isEmpty();
        }

        public boolean hasResponse() {
            return responseData != null && !responseData.isEmpty();
        }

        @Override
        public String toString() {
            return "LeadTransaction{" +
                    "leadId='" + leadId + '\'' +
                    ", postDate='" + postDate + '\'' +
                    ", transactionId='" + transactionId + '\'' +
                    ", source='" + source + '\'' +
                    ", subId='" + subId + '\'' +
                    ", payout='" + payout + '\'' +
                    ", buyerName='" + buyerName + '\'' +
                    ", postType='" + postType + '\'' +
                    ", buyerPostResultCode='" + buyerPostResultCode + '\'' +
                    ", requestData='" + requestData + '\'' +
                    ", responseData='" + responseData + '\'' +
                    '}';
        }
    }
}