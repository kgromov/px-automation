package px.objects.leads.buyer;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.TestDataException;
import org.json.JSONException;
import px.objects.leads.LeadsTestData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static configuration.helpers.DataHelper.getListFromJSONArrayByKey;

/**
 * Created by kgr on 3/2/2017.
 */
public class LeadsUnderBuyerTestData extends LeadsTestData {
    {
        this.startMonthOffset = 0;
    }
    // under buyer user
    public static final List<String> ADMIN_MENU_ITEMS = Arrays.asList("Buyer Details",
            "Disposition History", "Inbound Request", "Transactions");

    public LeadsUnderBuyerTestData() {
        super(false);
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
        } catch (JSONException e) {
            throw new TestDataException(String.format("Unable to parse response by requested url = '%s'\tDetails:\t%s",
                    requestedURL, e.getMessage()), e);
        }
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
        // lead attributes - capitalized
        keys.addAll(getListFromJSONArrayByKey(leadAttributesArray, "field", false));
        values.addAll(getListFromJSONArrayByKey(leadAttributesArray, "value", false));
        // perform once
        keys.forEach(key -> leadDetailsMap.put(key, values.get(keys.indexOf(key)).replaceAll("\\s+", " ")));
        // delete some keys cause some are in lower case, some are capitalized
        Arrays.asList("firstName", "lastName", "zipcode", "birthDate", "gender",
                "publisherGuid", "publisherName", "publisherInstanceGuid",
                "leadGuid", "leadID", "buyerGuid", "buyerInstanceGuid", "campaignGuid").forEach(key -> leadDetailsMap.remove(key));
        // change some values to proper format
        leadDetailsMap.put("creationDate", lead.getCreationDate());
        leadDetailsMap.put("qiqScore", DataHelper.getSplittedByComma(leadDetailsMap.get("qiqScore"), 2));
        if (leadDetailsMap.containsKey("theLeadId")) {
            String theLead = leadDetailsMap.get("theLeadId");
            if (Pattern.compile("0+|0{32}").matcher(DataHelper.remainDigits(theLead)).matches())
                leadDetailsMap.put("theLeadId", "N/A");
        }
    }

    @Override
    public String toString() {
        return "LeadsUnderBuyerTestData{" +
                "fromPeriod=" + fromPeriod +
                ", toPeriod=" + toPeriod +
                ", leadDetails=" + leadDetailsMap +
                '}';
    }
}