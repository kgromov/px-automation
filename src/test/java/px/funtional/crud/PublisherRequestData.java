package px.funtional.crud;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.publishers.PublisherTestData;
import px.objects.users.ContactTestData;

/**
 * Created by kgr on 6/30/2017.
 */
/* Extra post GET requests, create:
 * 1) api/publisher/trackingdata
 * 2) api/publisherinfo
 * 3) api/publisherinfo/address
 * 4) api/publisherinfo/contactitem
 * Extra post POST requests, update:
 * 1) api/publisher/trackingdata?publisherGuid=f534b5ad-edd9-4618-a8ea-b42613092896
 * 2) api/publisherinfo?parentObjectId=f534b5ad-edd9-4618-a8ea-b42613092896
 * 3) api/publisherinfo/address?parentObjectId=f534b5ad-edd9-4618-a8ea-b42613092896
 * 4) api/buyerinfo/contactitem?contactInfoId=&parentObjectId=4e6a61ee-f45f-4cfa-8f76-f221b5730e9c
 * advanced settings:
 * 1) api/publisherusers?count=10&filter=%7B%7D&page=1&publisherId=f534b5ad-edd9-4618-a8ea-b42613092896&sorting=%7B%7D
 * 2) api/publisherofferpixels?count=10&filter=%7B%22publisherId%22:405%7D&page=1&sorting=%7B%7D
 * 3) api/publisherpayouts?count=10&filter=%7B%22publisherId%22:405%7D&page=1&sorting=%7B%7D
 * 4) api/offerpublisheraccess?publisherId=405
 */
public final class PublisherRequestData implements RequestData {
    private PublisherTestData testData;
    private ObjectIdentityData identityData;

    public PublisherRequestData() {
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new PublisherTestData(dataMode);
    }

    public PublisherRequestData(PublisherTestData testData) {
        this.testData = testData;
    }

    public PublisherRequestData(PublisherTestData testData, ObjectIdentityData identityData) {
        this.testData = testData;
        this.identityData = identityData;
    }

    @Override
    public String createURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisher").build()
                .getRequestedURL();
    }

    @Override
    public String updateURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisher/update").build()
                .getRequestedURL();
    }

    // additional update post - 1 independent per section
    public String updatePublisherInfoURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisherinfo").build()
                .getRequestedURL();
    }

    public String updateTrackingDataURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisher/trackingdata").build()
                .getRequestedURL();
    }

    public String updateAddressURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisherinfo/address").build()
                .getRequestedURL();
    }

    @Override
    public String getURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisher/update")
                .withParams("publisherId", identityData.getGuid())
                .build().getRequestedURL();
    }

    // additional get requests - 1 independent per section
    public String getPublisherInfoURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisherinfo")
                .withParams("parentObjectId", identityData.getGuid())
                .build().getRequestedURL();
    }

    public String getTrackingDataURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisher/trackingdata")
                .withParams("publisherGuid", identityData.getGuid())
                .build().getRequestedURL();
    }

    public String getAddressURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/publisherinfo/address")
                .withParams("parentObjectId", identityData.getGuid())
                .build().getRequestedURL();
    }

    @Override
    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        ContactTestData contactData = testData.getContactTestData();
        object.put("publisherName", testData.getName());
        object.put("publisherTier", testData.getTier());
        object.put("type", testData.getType());
        object.put("accessMode", testData.getAccessMode());
        object.put("margin", testData.getMargin());
        object.put("minPrice", testData.getMinPrice());
        object.put("escoreCheckPercentage", testData.getEscorePercentage());
        object.put("spending", testData.getSpending());
        object.put("startBalance", testData.getStartBalance());
        object.put("maxNegativeBalance", testData.getMaxNegativeBalance());
        object.put("freeTests", testData.getFreeTests());
        object.put("fixedPricing", DataHelper.getRandBoolean());                                      // {true; false}
        object.put("addUpsellToBalance", DataHelper.getRandBoolean());                                // {true; false}
        object.put("emailAddress", contactData.getEmail());
        object.put("userPassword", contactData.getPassword());
        object.put("fullName", contactData.getFirstName() + " " + contactData.getLastName());
        object.put("zipCode", contactData.getZipCode());
        object.put("publisherManagerId", testData.getManagerID());                                    // could be get under admin only
        object.put("leadingBalance", testData.getLeadingBalance());                                   // could be null
        object.put("pingPostType", testData.getPingPostTypeMap().get(testData.getPingPostType()));    // enumSequenceNumber
        object.put("bidType", testData.getBidTypeMap().get(testData.getBidType()));                   // enumSequenceNumber
        object.put("outBidPerc", testData.getOutBidPerc());
        object.put("pingTimeout", testData.getPingPostTimeoutMap().get(testData.getPingTimeout()));   // enumSequenceNumber
        // update
        if (identityData != null) {
            object.put("publisherGuid", identityData.getGuid());
        }
        return object;
    }

    // additional json payload data - 1 independent per section
    public JSONObject asPublisherInfoJSON() {
        JSONObject object = new JSONObject();
        ContactTestData contactData = testData.getContactTestData();
//        object.put("contactInfoId", contactData.getFirstName());      // from getPublisherInfo
        object.put("firstName", contactData.getFirstName());
        object.put("middleName", contactData.getMiddleName());
        object.put("lastName", contactData.getLastName());
        object.put("initials", contactData.getInitials());
        object.put("birthDate", contactData.getBirthDate());
        object.put("email", contactData.getEmail());
        object.put("gender", contactData.getGender());
        object.put("companyName", testData.getCompanyName());
        object.put("maritalState", testData.getMaritalStatus());
        object.put("functionTitle", testData.getExtraInfo());
        return object;
    }

    public JSONObject asTrackingDataJSON() {
        JSONObject object = new JSONObject();
        object.put("publisherId", identityData.getId());
        object.put("publisherGuid", identityData.getGuid());
        object.put("accountManagerId", testData.getManagerID());
        object.put("accountManagerId", testData.getManagerID());        // from getTrackingData
        // offer or whatever data
//        object.put("paymentMethod", testData.getPaymentTerms());
        object.put("paymentTerms", testData.getPaymentTerms());
        return object;
    }

    public JSONObject asAddressJSON() {
        JSONObject object = new JSONObject();
        ContactTestData contactData = testData.getContactTestData();    // from getAddress
//        object.put("addressId", identityData.getGuid());
        object.put("publisherGuid", identityData.getGuid());
        object.put("street", contactData.getStreetAddress());
        object.put("streetNr", contactData.getStreetNR());
        object.put("zipCode", contactData.getZipCode());
        object.put("city", contactData.getCity());
        object.put("state", contactData.getState());
        object.put("country", testData.getCountry());
        object.put("extraInfo", testData.getExtraInfo());
//        object.put("type", identityData.getGuid());                   // what type?
        return object;
    }
}
