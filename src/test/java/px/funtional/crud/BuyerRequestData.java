package px.funtional.crud;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.buyers.BuyerTestData;
import px.objects.users.ContactTestData;

import java.util.HashSet;
import java.util.Set;

import static config.Config.isAdmin;

/**
 * Created by kgr on 6/30/2017.
 */
/* Buyer crud post instead of other objects is separated:
 * Extra post POST requests, create:
 * 1) /api/buyerinfo - does not return anything (204, no content )
 * 2) /api/buyerinfo/contactitem - 204, no content
 * 3) /api/buyerinfo/address - 204, no content
 * Update post returns data that intend to be updated
 *
 * Extra post GET requests, update:
 * 1) /api/buyerinfo?parentObjectId=4e6a61ee-f45f-4cfa-8f76-f221b5730e9c - return all buyerInfo fields
 * 2) /api/buyerinfo/contactitem?contactInfoId=519bec43-d9bb-4555-8b25-ed51a85bcb51&parentObjectId= (contactInfoId from buyerInfo get)
 * 3) /api/buyerinfo/address?contactInfoId=519bec43-d9bb-4555-8b25-ed51a85bcb51 (contactInfoId from buyerInfo get)
 * User:
 * 1) create - /api/buyeruser
 * 2) get - /api/buyerusers?buyerGuid=4e6a61ee-f45f-4cfa-8f76-f221b5730e9c&count=10&filter={}&page=1&sorting={"description":"asc"}
 * 3) /api/buyeruser/resetpassword - post
 */
public final class BuyerRequestData implements RequestData {
    private BuyerTestData testData;
    private ObjectIdentityData identityData;

    public BuyerRequestData() {
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new BuyerTestData(dataMode);
    }

    public BuyerRequestData(ObjectIdentityData identityData) {
        this.identityData = identityData;
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new BuyerTestData(dataMode);
    }

    public BuyerRequestData(BuyerTestData testData, ObjectIdentityData identityData) {
        this.testData = testData;
        this.identityData = identityData;
    }

    @Override
    public String createURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyer/create").build()
                .getRequestedURL();
    }

    @Override
    public String updateURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyer/update").build()
                .getRequestedURL();
    }

    // additional update post - 1 independent per section
    public String updateAddressURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyerinfo/address").build()
                .getRequestedURL();
    }

    public String updateBuyerInfoURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyerinfo").build()
                .getRequestedURL();
    }

    @Override
    public String getURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyer")
                .withParams("buyerGuid", identityData.getGuid())
                .build().getRequestedURL();
    }

    // additional get requests - 1 independent per section
    public String getAddressURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyerinfo/address")
                .withParams("parentObjectId", identityData.getGuid())
                .build().getRequestedURL();
    }

    public String getBuyerInfoURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/buyerinfo")
                .withParams("parentObjectId", identityData.getGuid())
                .build().getRequestedURL();
    }

    @Override
    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        object.put("buyerName", testData.getName());
        // update
        if (identityData != null) {
            object.put("buyerGuid", identityData.getGuid());
            object.put("monthlyCap", testData.getMonthlyCap());
            object.put("status", testData.getStatus());
        }
        return object;
    }

    @Override
    public Set<String> allowedFieldsToUpdate() {
        return isAdmin() ? asJSON().keySet() : new HashSet<>();
    }

    @Override
    public Set<String> allowedFieldsToUpdate(JSONObject fields) {
        fields.remove("buyerName");
        return isAdmin() ? fields.keySet() : new HashSet<>();
    }

    // additional json payload data - 1 independent per section
    public JSONObject asBuyerInfoJSON() {
        JSONObject object = new JSONObject();
        ContactTestData contactData = testData.getContactTestData();
//        object.put("contactInfoId", contactData.getFirstName());      // from getBuyerInfo
        object.put("firstName", contactData.getFirstName());
        object.put("middleName", contactData.getMiddleName());
        object.put("lastName", contactData.getLastName());
        object.put("initials", contactData.getInitials());
        object.put("birthDate", contactData.getBirthDate());
        object.put("email", contactData.getEmail());
        object.put("gender", contactData.getGender());
        object.put("functionTitle", testData.getFunctionTitle());
        object.put("PRV/PaymentTerms", testData.getPaymentTerms());
        object.put("Buyer/CreditLimit", testData.getCreditLimits());
        return object;
    }

    public JSONObject asAddressJSON() {
        JSONObject object = new JSONObject();
        ContactTestData contactData = testData.getContactTestData();
//        object.put("addressId", contactData.getStreetAddress());      // from getBuyerInfoAddress
        object.put("street", contactData.getStreetAddress());
        object.put("streetNr", contactData.getStreetNR());
        object.put("zipCode", contactData.getZipCode());
        object.put("city", contactData.getCity());
        object.put("state", contactData.getState());
        object.put("country", testData.getCountry());
        return object;
    }
}
