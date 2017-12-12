package px.objects.pixels;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.LocaleData;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.InstancesTestData;

import java.util.*;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomInt;
import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 1/26/2017.
 */
public class PixelTestData extends InstancesTestData {
    public static final List<String> EXPECTED_ERRORS_DESCRIPTION = Arrays.asList("Could not create pixel.", "Could not update pixel", "Code is invalid based on type");
    private static final String SCRIPT_PIXEL_TYPE = "code";
    private static final String IMAGE_PIXEL_TYPE = "image";
    private static final String POSTBACK_PIXEL_TYPE = "url";
    private static final String HTTP_OFFER_PROTOCOL = "http";
    private static final String IMAGE_OFFER_PROTOCOL = "img";
    private static final String POSTBACK_OFFER_PROTOCOL = "server";
    // initial data
    private Map<String, String> pixelTypesMap;
    // pixel data
    private String pixelType;
    private String pixelCode;
    private String pixelStatus; // while update only ~ offerStatus
    private String publisherID;
    // offer data
    private String offerID;
    private String offerName;
    private String offerProtocol;
    private String offerStatus;
    private String offerURL;
    private boolean isDirectLink;
    // parent
    private ObjectIdentityData publisher;
    private ObjectIdentityData offer;
    // details/edit data
    private Map<String, String> pixelDetailsMap;
    private String publisherLink;
    private String conversionLink;
    // auxiliary data
    private JSONArray existedPixels;
    private JSONObject pixelObject;
    private Set<String> existedOfferIDs;
    // parent data
    private JSONArray offers;
    private List<ObjectIdentityData> publishers;
    //    private JSONArray publishers;
    private Set<String> offerIDs;
    private Set<String> publisherIDs;

    // publisherofferpixels&filter={"publisherId":1006}&page=1&sorting={}
    // use for negative/details test
    public PixelTestData(DataMode dataMode) {
        super(dataMode);
        setInstanceGroup("publisherofferpixels");
        this.pixelTypesMap = dataProvider.getPossibleValueFromJSON("offerpixeltypes", "enumSequenceNumber");
        // set parent data to be able to select available pixel data
        setParents();
    }

    // publisherId = 0 means pixel works for all publishers
    public PixelTestData() {
        this(DataMode.getCreatedByResponse());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherofferpixels")
                .filter("offerPixelStatus", "active")
                .withEmptySorting()
                .build().getRequestedURL();
        List<JSONObject> allPixels = JSONWrapper.toList(dataProvider.getDataAsJSONArray(requestedURL, true));
        allPixels = allPixels.stream().filter(pixel ->
                publisherIDs.contains(String.valueOf(pixel.get("publisherId")))).collect(Collectors.toList());
        JSONObject object = allPixels.get(getRandomInt(allPixels.size()));
        this.pixelObject = object;
        this.id = String.valueOf(pixelObject.get("offerPixelId"));
        this.publisherID = String.valueOf(object.get("publisherId"));
        this.offerID = String.valueOf(object.get("offerId"));
//        this.publisher = ObjectIdentityData.getObjectFromListByID(dataProvider.getCreatedInstancesData("publishers"), publisherID);
        this.publisher = ObjectIdentityData.getObjectFromListByID(publishers, publisherID);
        if (publisher == null)
            throw new TestDataException(String.format("Unable to find publisher by publisherId = '%s\tPixel object = %s'", publisherID, pixelObject));
        setOfferData();
        // update some values according to UI
        this.pixelType = pixelTypesMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(String.valueOf(pixelObject.get("type"))))
                .map(Map.Entry::getKey)
                .findFirst().orElse(pixelType);
        // test section data
        this.publisherLink = getWebSiteUnique();
        this.conversionLink = isDirectLink
                ? offerURL + "&offer_id=" + offerID + "&aff_id=" + publisherID + "&test=1"
                : String.format("%s://lxptest.go2cloud.org/aff_l?offer_id=%s&aff_id=%s&test=1",
                (offerProtocol.contains("https") ? "https" : "http"), offerID, publisherID);
        pixelObject.put("type", pixelType);
        pixelObject.put("publisherId", publisher.getId() + " - " + publisher.getName());
        pixelObject.put("offerId", offer.getId() + " - " + offer.getName());
    }

    /**
     * @param testData - PixelTestData to be cloned
     * @return - current PixelTestData with cloned fields {publisher, offer data}
     */
    public PixelTestData clone(PixelTestData testData) {
        this.id = testData.getId();
        generatePixelDataByOfferProtocol();
        return this;
    }

    public PixelTestData setExistedPixels() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherofferpixels")
                .filter("offerPixelStatus", "active")
                .withEmptySorting()
                .build().getRequestedURL();
        JSONArray allPixels = dataProvider.getDataAsJSONArray(requestedURL, true);
        Set<String> allPixelPublishers = new HashSet<>(DataHelper.getListFromJSONArrayByKey(allPixels, "publisherId"));
        List<String> availablePixelPublishers = DataHelper.getListByValue(allPixelPublishers, publisherIDs);
        log.info("availablePixelPublishers = " + availablePixelPublishers);
        if (availablePixelPublishers.isEmpty()) {
            log.info("No automation created offer pixels");
            setCreateData();
            setUniquePixelData();
            generatePixelDataByOfferProtocol();
            return this;
        }
        this.publisherID = getRandomValueFromList(availablePixelPublishers);
//        this.publisher = new ObjectIdentityData(DataHelper.getJSONFromJSONArrayByCondition(publishers, "publisherId", publisherID));
        this.publisher = ObjectIdentityData.getObjectFromListByID(publishers, publisherID);
        // set offer data - protocol as the most important one
        setPixels();
        return this;
    }

    public PixelTestData setCreateData() {
        // choose any automation created publisher
     /*   int index = DataHelper.getRandomInt(publishers.length());
        this.publisher = new ObjectIdentityData(publishers.getJSONObject(index));*/
        this.publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
        // to prevent duplicated offerID
        setPixels();
    /*    // pixel type depends on offer protocol
        generatePixelDataByOfferProtocol();*/
        return this;
    }

    // update data
    /* 1) api/offerpixel/edit?offerPixelId=139  - pixel details
       2) api/offerURL/generatetracking         - pixel conversion URL (offer URL + some pizel info)
     */
    public PixelTestData setUpdateData() {
        // publisher and offer are already defined
        setPixelObject();
        // set id
        // currently without creating
     /*   if (!pixelObject.has("offerPixelId"))
            throw new TestDataException("No available pixels for publisher " + publisher);*/
        this.id = pixelObject.has("offerPixelId") ? String.valueOf(pixelObject.get("offerPixelId")) : null;
        // update some values according to UI
        this.pixelType = pixelTypesMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(String.valueOf(pixelObject.get("type"))))
                .map(Map.Entry::getKey)
                .findFirst().orElse(pixelType);
        // test section data
        this.publisherLink = getWebSiteUnique();
        this.conversionLink = isDirectLink
                ? offerURL + "&offer_id=" + offerID + "&aff_id=" + publisherID + "&test=1"
                : String.format("%s://lxptest.go2cloud.org/aff_l?offer_id=%s&aff_id=%s&test=1",
                offerProtocol.contains("https") ? "https" : "http", offerID, publisherID);
        pixelObject.put("type", pixelType);
        pixelObject.put("publisherId", publisher.getId() + " - " + publisher.getName());
        pixelObject.put("offerId", offer.getId() + " - " + offer.getName());
        return this;
    }

    @Override
    public PixelTestData setPositiveData() {
        Map<String, String> tempTypesMap = new HashMap<>();
        if (isHttpProtocol()) tempTypesMap = DataHelper.getMapContainsInValue(pixelTypesMap, SCRIPT_PIXEL_TYPE);
        else if (isImageProtocol()) tempTypesMap = DataHelper.getMapContainsInValue(pixelTypesMap, IMAGE_PIXEL_TYPE);
        else if (isPostBackProtocol())
            tempTypesMap = DataHelper.getMapContainsInValue(pixelTypesMap, POSTBACK_PIXEL_TYPE);
        this.pixelType = getRandomValueFromList(new ArrayList<>(tempTypesMap.keySet()));
        // code format depends on
        this.pixelCode = String.format("%s://development.stagingrevi.com/aff_l?offer_id=%s",
                offerProtocol.contains("https") ? "https" : "http", offerID);
        if (isHttpProtocol()) this.pixelCode = String.format("<script type='text/javascript'>//<![CDATA[" +
                " document.write('<iframe src='%s'</iframe>');" +
                "//]]></script>", pixelCode);
        else if (isImageProtocol()) this.pixelCode = String.format("<img src='%s'/>", pixelCode);
        return this;
    }

    @Override
    public PixelTestData setNegativeData() {
        this.localeData = new LocaleData();
        this.pixelType = getRandomValueFromList(new ArrayList<>(pixelTypesMap.keySet()));
        this.pixelCode = String.format("%s/offer_id=%s", getWebSiteUnique(), offerID);
        if (isImageProtocol()) this.pixelCode = String.format("<script type='text/javascript'>//<![CDATA[" +
                " document.write('<iframe src='%s'</iframe>');" +
                "//]]></script>", pixelCode);
        else if (isHttpProtocol()) this.pixelCode = String.format("<img src='%s'/>", pixelCode);
        return this;
    }

    /**
     * used to generate pixel fields data;
     * depends on pixel type/offer protocol
     */
    // TODO: change to OfferPixelStatuses whan 12660 be merged
    public void generatePixelDataByOfferProtocol() {
        Map<String, String> statuses = dataProvider.getPossibleValueFromJSON("HasOffersStatuses");
        statuses.remove("paused");
        statuses.remove("expired");
        this.pixelStatus = getRandomValueFromList(new ArrayList<>(statuses.keySet()));
        if (isPositive()) setPositiveData();
        else setNegativeData();
    }

    /**
     * get all pixels by certain publisher
     */
    private void setPixels() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherofferpixels")
                .filter(Arrays.asList("publisherId", "offerPixelStatus"),
                        Arrays.asList(publisher.getId(), "active"))
                .withEmptySorting()
                .build().getRequestedURL();
        this.existedPixels = dataProvider.getDataAsJSONArray(requestedURL);
        this.existedOfferIDs = new HashSet<>(DataHelper.getListFromJSONArrayByKey(existedPixels, "offerId"));
    }

    public void setPixelObject() {
        this.pixelObject = existedOfferIDs.contains(offerID)
                ? DataHelper.getJSONFromJSONArrayByCondition(existedPixels, "offerId", offerID)
                : asJSON();
        log.info("pixelObject = " + pixelObject);
    }

    /**
     * Get offer data by offerId to save offerProtocol.
     * 1) if created data - to map pixel type with offer protocol;
     * 2) if update/delete data - djes not matter, could not be invoked
     */
    public PixelTestData setOfferData() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/offer")
                .withParams("offerID", offerID)
                .build().getRequestedURL();
        JSONObject jsonObject = new JSONObject(dataProvider.getDataAsString(requestedURL));
        this.offerName = String.valueOf(jsonObject.get("offerName"));
        this.offer = new ObjectIdentityData(offerID, offerName, null);
        this.offerProtocol = String.valueOf(jsonObject.get("protocol"));
        this.offerStatus = String.valueOf(jsonObject.get("offerStatus"));
        this.offerURL = String.valueOf(jsonObject.get("offerUrl"));
        this.isDirectLink = jsonObject.get("allowDirectLinks").equals(1);
        return this;
    }

    /**
     * Predefined step - save all publishers/offers in JSONArray to:
     * 1) keep IDs of available/automation created offers and publishers;
     * 2) keep JSONArray to select and set later on pixelObject
     */
    private void setParents() {
        // choose any automation created publisher
       /* String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publishers")
                .filter("publisherName", "Publisher Name ")
                .sort("publisherName", "asc")
                .build().getRequestedURL();*/
        //        this.publisherIDs = new HashSet<>(DataHelper.getListFromJSONArrayByKey(publishers, "publisherId"));
        this.publishers = ObjectIdentityData.getObjectsByName(dataProvider.getCreatedInstancesData("publishers"), "Publisher Name ");
        this.publisherIDs = new HashSet<>(ObjectIdentityData.getAllIDs(publishers));
        // choose any automation created offer
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/offers")
                .filter("offerName", "Offer Name ")
                .sort("offerName", "asc")
                .build().getRequestedURL();
        this.offers = dataProvider.getDataAsJSONArray(requestedURL);
        this.offerIDs = new HashSet<>(DataHelper.getListFromJSONArrayByKey(offers, "offerId"));
    }

    /**
     * Select pixelID. offerID from already created pixels
     *
     * @return this object - current PixelTestData
     */
    public PixelTestData setUniquePixelData() {
        // choose any available automation created offer
        List<String> availablePublisherIDs = DataHelper.getListNotByValue(offerIDs, existedOfferIDs);
        this.offerID = getRandomValueFromList(availablePublisherIDs);
        setOfferData();
        return this;
    }

    /**
     * Select unique offerID - not contains in created pixels by certain publisher
     * check for empty available offers list
     *
     * @return this object - current PixelTestData
     */
    public PixelTestData setNonUniquePixelData() {
        // choose any available automation created offer
        List<String> availableOfferIDs = DataHelper.getListByValue(offerIDs, existedOfferIDs);
        log.info("availableOfferIDs = " + availableOfferIDs);
        if (availableOfferIDs.isEmpty()) {
            log.info("No automation created offer pixels");
            setUniquePixelData();
            generatePixelDataByOfferProtocol();
            return this;
        }
        this.offerID = getRandomValueFromList(availableOfferIDs);
        // set offer data - protocol as the most important one
        setOfferData();
        return this;
    }

    public PixelTestData setPixelObject(String publisherID, String offerID) {
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

    public JSONObject asJSON() {
        log.info("pixelType = " + pixelType);
        JSONObject object = new JSONObject();
        object.put("publisherId", publisher.getId() + " - " + publisher.getName());
        object.put("offerId", offer.getId() + " - " + offer.getName());
        object.put("type", pixelType);
        object.put("code", pixelCode);
        object.put("offerPixelStatus", pixelStatus);
        // doubts, probably invoke after creation
        if (isCreateMode()) object.put("offerPixelStatus", "active");
        return object;
    }

    public String getPixelType() {
        return pixelType;
    }

    public String getPixelCode() {
        return pixelCode;
    }

    public String getPixelStatus() {
        return pixelStatus;
    }

    public String getOfferID() {
        return offerID;
    }

    public String getOfferName() {
        return offerName;
    }

    public String getOfferProtocol() {
        return offerProtocol;
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

    public Map<String, String> getPixelDetailsMap() {
        return pixelDetailsMap;
    }

    public String getPublisherLink() {
        return publisherLink;
    }

    public String getConversionLink() {
        return conversionLink;
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
        return !DataHelper.getListByValue(offerIDs, existedOfferIDs).isEmpty();
    }

    public boolean hasConversionLinkByOfferStatus() {
        return offerStatus != null && (offerStatus.equals("active") || offerStatus.equals("pending"));
    }

    @Override
    public String toString() {
        String updateDetails = isCreatedByResponse() || isEditMode()
                ? ", pixelId='" + id + '\'' +
                ", conversionLink='" + conversionLink + '\'' +
                ", publisherLink='" + publisherLink + '\'' +
                ", isDirectLink='" + isDirectLink + '\''
                : "";
        return "PixelTestData{" +
                "pixelType='" + pixelType + '\'' +
                ", pixelCode='" + pixelCode + '\'' +
                ", pixelStatus='" + pixelStatus + '\'' +
                ", offer='" + offer + '\'' +
                ", publisher='" + publisher + '\'' +
                ", offerProtocol='" + offerProtocol + '\'' +
                ", offerURL='" + offerURL + '\'' +
                ", offerStatus='" + offerStatus + '\'' +
                updateDetails +
                '}';
    }
}
