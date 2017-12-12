package px.objects.pixels;

import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.InstancesTestData;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getListByValue;
import static configuration.helpers.DataHelper.getListFromJSONArrayByKey;

/**
 * Created by kgr on 1/26/2017.
 */
public abstract class PixelTestData2 extends InstancesTestData {
    public static final List<String> EXPECTED_ERRORS_DESCRIPTION = Arrays.asList("Could not create pixel.", "Could not update pixel", "Code is invalid based on type");
    public static final String SCRIPT_PIXEL_TYPE = "code";
    public static final String IMAGE_PIXEL_TYPE = "image";
    public static final String POSTBACK_PIXEL_TYPE = "url";
    public static final String HTTP_OFFER_PROTOCOL = "http";
    public static final String IMAGE_OFFER_PROTOCOL = "img";
    public static final String POSTBACK_OFFER_PROTOCOL = "server";
    // parent objects
    protected ObjectIdentityData publisher;
    protected ObjectIdentityData offer;
    // available parent objects
    protected List<ObjectIdentityData> publishers;
    protected List<ObjectIdentityData> offers;
    protected Set<String> offerIDs;
    protected Set<String> publisherIDs;
    // offer data
    protected String offerProtocol;
    protected String offerURL;
    protected boolean isDirectLink;
    // auxiliary data
    protected JSONArray existedPixels;
    protected JSONObject pixelObject;
    protected Set<String> existedOfferIDs;
    protected Set<String> availablePublishers;

    public PixelTestData2(DataMode dataMode) {
        super(dataMode);
        setInstanceGroup("publisherofferpixels");
//        this.publishers = ObjectIdentityData.getObjectsByName(dataProvider.getCreatedInstancesData("publishers"), "Publisher Name ");
        this.publishers = dataProvider.getCreatedInstancesData("publishers");
        this.publisherIDs = new HashSet<>(ObjectIdentityData.getAllIDs(publishers));
//        this.offers = ObjectIdentityData.getObjectsByName(dataProvider.getCreatedInstancesData("offers"), "Offer Name ");
        this.offers = dataProvider.getCreatedInstancesData("offers");
        this.offerIDs = new HashSet<>(ObjectIdentityData.getAllIDs(offers));
    }

    /**
     * USAGE: preview, edit, delete;
     *
     * @return set all already created pixels
     */
    public PixelTestData2 setExistedPixels() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherofferpixels")
                .filter("offerPixelStatus", "active")
                .withEmptySorting()
                .build().getRequestedURL();
        List<JSONObject> allPixels = JSONWrapper.toList(dataProvider.getDataAsJSONArray(requestedURL, true));
        Set<String> allPixelPublishers = allPixels.stream().map(pixel ->
                JSONWrapper.getString(pixel, "publisherId")).collect(Collectors.toSet());
        this.availablePublishers = allPixelPublishers.stream().filter(publisher ->
                publisherIDs.contains(publisher)).collect(Collectors.toSet());
        return this;
    }

    /**
     * USAGE: create mode
     *
     * @param publishedID - id of chosen publisher
     * @return existed pixels for certain publisher by publisherID
     */
    public PixelTestData2 setExistedPixels(String publishedID) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherofferpixels")
                .filter(Arrays.asList("publisherId", "offerPixelStatus"),
                        Arrays.asList(publishedID, "active"))
                .withEmptySorting()
                .build().getRequestedURL();
        this.existedPixels = dataProvider.getDataAsJSONArray(requestedURL);
        this.existedOfferIDs = new HashSet<>(getListFromJSONArrayByKey(existedPixels, "offerId"));
        // if some deleted offer
        return this;
    }

    /**
     * Set offer details {offerProtocol, offerURL, isDirectLink}
     * Based on offer details pixel data is set
     *
     * @param offerID - id of chosen offer
     * @return offer details by offerID
     */
    public PixelTestData2 setOffer(String offerID) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/offer")
                .withParams("offerID", offerID)
                .build().getRequestedURL();
        JSONObject jsonObject = new JSONObject(dataProvider.getDataAsString(requestedURL));
        String offerName = String.valueOf(jsonObject.get("offerName"));
        this.offer = new ObjectIdentityData(offerID, offerName, null);
        this.offerProtocol = String.valueOf(jsonObject.get("protocol"));
        this.offerURL = String.valueOf(jsonObject.get("offerUrl"));
        this.isDirectLink = jsonObject.get("allowDirectLinks").equals(1);
        return this;
    }

    public PixelTestData2 setPixelObject(String publisherID, String offerID) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherofferpixels")
                .filter(Arrays.asList("publisherId", "offerId"),
                        Arrays.asList(publisherID, offerID))
                .withEmptySorting()
                .build().getRequestedURL();
        this.pixelObject = dataProvider.getDataAsJSON(requestedURL);
        this.id = String.valueOf(pixelObject.get("offerPixelId"));
        return this;
    }

    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getOffer() {
        return offer;
    }

    // details/update
    public JSONObject getPixelObject() {
        return pixelObject;
    }

    public int getExistedPixelsCount() {
        return existedPixels.length();
    }

    public boolean isHttpProtocol() {
        return offerProtocol.contains(HTTP_OFFER_PROTOCOL) && !offerProtocol.contains(IMAGE_OFFER_PROTOCOL);
    }

    public boolean isImageProtocol() {
        return offerProtocol.contains(IMAGE_OFFER_PROTOCOL);
    }

    public boolean isPostBackProtocol() {
        return offerProtocol.contains(POSTBACK_OFFER_PROTOCOL);
    }

    public boolean isAnyAvailablePixel() {
//        return !existedOfferIDs.isEmpty();
        return !getListByValue(offerIDs, existedOfferIDs).isEmpty();
    }

    public boolean isDuplicatedOffer() {
        return existedOfferIDs.contains(offer.getId());
    }

    public boolean wasCreated() {
        return pixelObject != null && pixelObject.has("pixelObject");
    }

    // ------- to be implemented -------
    public abstract PixelTestData2 setPixelObject();

    public abstract PixelTestData2 setPixelData();
}