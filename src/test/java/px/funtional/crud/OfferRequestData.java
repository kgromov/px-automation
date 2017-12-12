package px.funtional.crud;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.objects.offers.OfferTestData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kgr on 6/30/2017.
 */
public final class OfferRequestData implements RequestData {
    private OfferTestData testData;
    private ObjectIdentityData identityData;
    // do not fidget with fields some are missed after test data initialization
    private final static List<String> EXCLUDED_FIELDS = Arrays.asList("hostnameId", "redirectOfferId",
            "isPrivate", "showCustomVariables", "allowDirectLinks", "allowWebsiteLinks",
            "allowMultipleConversions", "enforceEncryptTrackingPixels", "enableOfferWhitelist",
            "setSessionOnImpression", "sessionImpressionHours", "sessionHours", "customSessionHours",
            "convertedOfferType", "convertedOfferUrl", "convertedOfferUrl", "convertedOfferId"
    );

    public OfferRequestData(OfferTestData testData) {
        this.testData = testData;
    }

    public OfferRequestData(OfferTestData testData, ObjectIdentityData identityData) {
        this.testData = testData;
        this.identityData = identityData;
    }

    @Override
    public String createURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/offer").build()
                .getRequestedURL();
    }

    @Override
    public String updateURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/offer/offerUpdate").build()
                .getRequestedURL();
    }

    @Override
    public String getURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/offer") // simple key-value, asMap is required to Override
//                .withRelativeURL("api/offer/getOfferForEdit") - field, default
                .withParams("offerId", identityData.getId())
                .build().getRequestedURL();
    }

    @Override
    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        // mandatory
        object.put("offerName", testData.getName());
        object.put("description", testData.getDescription());
        object.put("previewUrl", testData.getPreviewURL());
        object.put("offerUrl", testData.getOfferURL());
        object.put("protocol", testData.getProtocolsMap().get(testData.getProtocol()));         // enumSequenceNumber
        object.put("offerStatus", testData.getStatus());
        object.put("payoutType", testData.getPayoutTypeMap().get(testData.getPayoutType()));    // enumSequenceNumber
        object.put("revenueType", testData.getRevenueTypeMap().get(testData.getRevenueType())); // enumSequenceNumber
        object.put("conversionCap", testData.getConversionCap());
        // not mandatory
        object.put("expirationDate", testData.getExpirationFormattedDate());
        object.put("note", testData.getNote());
        object.put("refId", testData.getRefID());
        object.put("offerCategories", testData.getOfferCategories());                           // null or categoryId1|categoryId12...
        object.put("currency", testData.getCurrencyMap().get(testData.getCurrency()));          // enumSequenceNumber
//        object.put("hostnameId", testData.getTrackingDomain());                               // is not filled in
//        object.put("redirectOfferId", testData.getRedirectOffer());                           // null or offerId
        // payout method + payout type dependencies
        object.put("tieredPayout", testData.isDefaultPayoutMethod() ? 0 : 1);                   // payoutMethod {0, 1 - tiered}
        if (testData.isConversionPayout())
            object.put("defaultPayout", testData.getDefaultPayout());                           // seems to be mandatory
        if (testData.isPercentagePayout())
            object.put("percentPayout", testData.getDefaultPayout());
        // revenue method + revenue type
        object.put("tieredRevenue", testData.isDefaultRevenueMethod() ? 0 : 1);                 // revenueMethod {0, 1 - tiered}
        if (testData.isConversionRevenue())
            object.put("maxPayout", testData.getMaxPayout());                                   // seems to be mandatory
        if (testData.isPercentagePayout())
            object.put("maxPercentPayout", testData.getMaxPayout());
        // set randomly in multicheckbox
//        object.put("isPrivate", testData.isP());
//        object.put("showCustomVariables", testData.isP());
//        object.put("allowDirectLinks", testData.isP());
//        object.put("allowWebsiteLinks", testData.isP());
//        object.put("allowMultipleConversions", testData.isP());
//        object.put("enforceEncryptTrackingPixels", testData.isP());
//        object.put("enableOfferWhitelist", testData.isP());
  /*      // dependent fields
        object.put("setSessionOnImpression", testData.getSessionTracking());                    // by indexes
        object.put("sessionImpressionHours", testData.getSessionImpressionHours());
        object.put("sessionHours", testData.getSessionHours());
        object.put("customSessionHours", testData.getCustomSessionHours());
        // secondary offer
        object.put("convertedOfferType", testData.getSecondaryOffer());                         // {network, url}
        // depend fields if fields above is specified
        object.put("convertedOfferUrl", testData.getOfferByName());
        object.put("convertedOfferUrl", testData.isSecondaryOfferByName()                       // network
                ? testData.getOfferByName() : testData.getOfferByURL());                        // url
        object.put("convertedOfferId", testData.getOfferByURL());                               // network*/
        // update
        if (identityData != null) {
            object.put("OfferId", identityData.getId());
        }
        return object;
    }

    @Override
    public Map<String, String> asMap(String requestedURL) {
        JSONObject updateDetails = new JSONObject(dataProvider.getDataAsString(requestedURL));
        Map<String, String> objectDetails = new HashMap<>(updateDetails.keySet().size());
        updateDetails.keySet().forEach(key -> objectDetails.put(key, String.valueOf(updateDetails.get(key))));
        // remove some not mandatory fields
        EXCLUDED_FIELDS.forEach(objectDetails::remove);
        return objectDetails;
    }
}
