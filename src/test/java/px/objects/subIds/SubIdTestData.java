package px.objects.subIds;

import configuration.helpers.*;
import dto.LxpDataProvider;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.publishers.PublisherTestData;
import px.objects.users.ContactTestData;
import px.reports.dto.FieldFormatObject;

import java.util.*;

import static pages.groups.MetaData.PERCENTAGE_FORMAT;

/**
 * Created by kgr on 7/14/2017.
 */
public class SubIdTestData extends PublisherTestData {
    private ObjectIdentityData publisher;
    // general info
    private String noParentInherit;
    // details
    private Map<String, String> detailsMap;
    // static list for contact items
    public static final List<String> CONTACT_ITEMS = Arrays.asList("TEL/Mobile", "TEL/Business",
            "INT/email", "INT/web", "PRV/BillingCycle", "PRV/PaymentTerms");

    static {
        // missed formats
        missedHeadersMetricsMap.put("leadReturnPercentage", PERCENTAGE_FORMAT);
        // data mapping
        dataMap.put("false", "No");
        dataMap.put("true", "Yes");
        dataMapping.add(new ValuesMapper("addUpsellToBalance", dataMap));
        dataMapping.add(new ValuesMapper("fixedPricing", dataMap));
        dataMapping.add(new ValuesMapper("noParentInherit", dataMap));
        dataMap = new HashMap<>();
        new LxpDataProvider().getPossibleValueFromJSON("PingPostBidType").entrySet().
                forEach(entry -> dataMap.put(entry.getValue(), entry.getKey()));
        dataMap.put("0", "None");
        dataMapping.add(new ValuesMapper("bidType", dataMap));
        dataMap = new HashMap<>();
        new LxpDataProvider().getPossibleValueFromJSON("PingTimeoutLimit").entrySet().
                forEach(entry -> dataMap.put(entry.getValue(), entry.getKey()));
        dataMapping.add(new ValuesMapper("pingTimeout", dataMap));
    }

    public SubIdTestData(DataMode dataMode) {
        super(dataMode);
        setInstanceGroup("publisherInstances");
        this.noParentInherit = DataHelper.getRandomYesNo();
    }

    public void setPublisher(ObjectIdentityData publisher) {
        this.publisher = publisher;
    }

    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public String getNoParentInherit() {
        return noParentInherit;
    }

    private void setDetailsMap(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            if (object.has("field")) {
                try {
                    // set fields
                    FieldFormatObject field = new FieldFormatObject(object, missedHeadersMetricsMap, i);
                    // set mapping
                    if (ValuesMapper.hasMappedValues(dataMapping, field.getName()))
                        field.setValuesMap(ValuesMapper.getMappedValues(dataMapping, field.getName()).getMap());
                    fields.add(field);
                    // set data map
                    String key = String.valueOf(object.get("field"));
                    String value = object.has("default") ? String.valueOf(object.get("default")) : "";
                    detailsMap.put(key, value);
                } catch (NullPointerException | JSONException e) {
                    log.error(String.format("Unable to handle field: '%s'\tDetails=\n%s", object, e.getMessage()));
                }
            }
        }
    }

    public Map<String, String> getDetailsMap() {
        return detailsMap;
    }

    public Map<String, String> asMap() {
        this.detailsMap = new HashMap<>();
        // general info
        detailsMap.put("publisherInstanceName", name);
//        detailsMap.put("subID", guid);
        detailsMap.put("noParentInherit", noParentInherit);
        detailsMap.put("margin", getMargin());
        detailsMap.put("minPrice", getMinPrice());
        detailsMap.put("escoreCheckPercentage", getEscorePercentage());
        detailsMap.put("spending", getSpending());
        detailsMap.put("startBalance", getStartBalance());
        detailsMap.put("maxNegativeBalance", getMaxNegativeBalance());
        detailsMap.put("freeTests", getFreeTests());
        detailsMap.put("qiqScore", getQualityScore());
        detailsMap.put("leadReturnPercentage", getLeadPercentage());
        detailsMap.put("fraudCheckPercentage", getFraudPercentage());
        detailsMap.put("type", getType());
        detailsMap.put("accessMode", getAccessMode());
        detailsMap.put("leadingBalance", getLeadingBalance());
        detailsMap.put("addUpsellToBalance", getUpsellBalance());
        detailsMap.put("fixedPricing", getFixedPricing());
        detailsMap.put("status", getStatus());
        // contact info
        ContactTestData contactData = getContactTestData();
        detailsMap.put("firstName", contactData.getFirstName());
        detailsMap.put("lastName", contactData.getLastName());
        detailsMap.put("middleName", contactData.getMiddleName());
        detailsMap.put("initials", contactData.getInitials());
        detailsMap.put("birthDate", contactData.getBirthDate());
        detailsMap.put("gender", contactData.getGender());
        detailsMap.put("email", contactData.getEmail());
        detailsMap.put("companyName", getCompanyName());
        detailsMap.put("maritalState", getMaritalStatus());
        // address info
        detailsMap.put("street", contactData.getStreetAddress());
        detailsMap.put("streetNr", contactData.getStreetNR());
        detailsMap.put("zipCode", contactData.getZipCode());
        detailsMap.put("city", contactData.getCity());
        detailsMap.put("state", contactData.getState());
        detailsMap.put("country", getCountry());
        detailsMap.put("extraInfo", getExtraInfo());
        return detailsMap;
    }

    public Map<String, String> generalInfoAsMap() {
        Map<String, String> detailsMap = new HashMap<>();
        detailsMap.put("publisherInstanceName", name);
//        detailsMap.put("subID", guid);
        detailsMap.put("noParentInherit", noParentInherit);
        detailsMap.put("margin", getMargin());
        detailsMap.put("minPrice", getMinPrice());
        detailsMap.put("escoreCheckPercentage", getEscorePercentage());
        detailsMap.put("spending", getSpending());
        detailsMap.put("startBalance", getStartBalance());
        detailsMap.put("maxNegativeBalance", getMaxNegativeBalance());
        detailsMap.put("freeTests", getFreeTests());
        detailsMap.put("qiqScore", getQualityScore());
        detailsMap.put("leadReturnPercentage", getLeadPercentage());
        detailsMap.put("fraudCheckPercentage", getFraudPercentage());
        detailsMap.put("type", getType());
        detailsMap.put("accessMode", getAccessMode());
        detailsMap.put("leadingBalance", getLeadingBalance());
        detailsMap.put("addUpsellToBalance", getUpsellBalance());
        detailsMap.put("fixedPricing", getFixedPricing());
        detailsMap.put("status", getStatus());
        // add ping post fields
        detailsMap.put("pingPostType", getPingPostType());
        detailsMap.put("bidType", getBidType());
        detailsMap.put("bidFloorPerc", getBidFloor());
        detailsMap.put("outBidPerc", getOutBidPerc());
        detailsMap.put("outBidPercPerc", getOutBidPercPerc());
        detailsMap.put("pingTimeout", getPingTimeout());
        return detailsMap;
    }

    public Map<String, String> contactInfoAsMap() {
        Map<String, String> detailsMap = new HashMap<>();
        ContactTestData contactData = getContactTestData();
        detailsMap.put("firstName", contactData.getFirstName());
        detailsMap.put("lastName", contactData.getLastName());
        detailsMap.put("middleName", contactData.getMiddleName());
        detailsMap.put("initials", contactData.getInitials());
        detailsMap.put("birthDate", contactData.getBirthDate());
        detailsMap.put("gender", contactData.getGender());
        detailsMap.put("email", contactData.getEmail());
        detailsMap.put("companyName", getCompanyName());
        detailsMap.put("maritalState", getMaritalStatus());
        return detailsMap;
    }

    public Map<String, String> addressInfoAsMap() {
        Map<String, String> detailsMap = new HashMap<>();
        ContactTestData contactData = getContactTestData();
        detailsMap.put("street", contactData.getStreetAddress());
        detailsMap.put("streetNr", contactData.getStreetNR());
        detailsMap.put("zipCode", contactData.getZipCode());
        detailsMap.put("city", contactData.getCity());
        detailsMap.put("state", contactData.getState());
        detailsMap.put("country", getCountry());
        detailsMap.put("extraInfo", getExtraInfo());
        return detailsMap;
    }

    @Override
    public void setHeaderObjects() {
        this.fields = new ArrayList<>();
        // aggregate like key-value
        this.detailsMap = new HashMap<>();
        // contact info
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherinfo")
                .withParams("parentObjectId", guid)
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS)).getJSONArray();
        setDetailsMap(jsonArray);
        // address info
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherinfo/address")
                .withParams("parentObjectId", guid)
                .build().getRequestedURL();
        jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS)).getJSONArray();
        setDetailsMap(jsonArray);
        // general data
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherinstancecrud")
                .withParams("publisherInstanceGuid", guid)
                .build().getRequestedURL();
        jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS)).getJSONArray();
        setDetailsMap(jsonArray);
      /*  // get contact item as well
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherinfo/contactitem")
                .withParams(Arrays.asList("contactInfoId", "parentObjectId"),
                        Arrays.asList(detailsMap.get("contactInfoId"), guid))
                .build().getRequestedURL();
        jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.OPTIONS)).getJSONArray();
        setDetailsMap(jsonArray);*/
//        CONTACT_ITEMS.forEach(item -> detailsMap.put(item, ""));
        // remove items
        detailsMap.remove("contactInfoId");
        detailsMap.remove("addressId");
        detailsMap.remove("subID");
//        detailsMap.remove("functionTitle");
    }

    @Override
    public String toString() {
        return super.toString() +
                "\nSubIdTestData{" +
                "publisher=" + publisher +
                ", noParentInherit='" + noParentInherit + '\'' +
                '}';
    }
}