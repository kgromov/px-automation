package px.objects.pixels;

import configuration.helpers.DataHelper;
import dto.LocaleData;
import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.objects.DataMode;

import java.util.*;

import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 11/2/2017.
 */
public class CreatePixelTestData extends PixelTestData2 {
    // initial data
    protected Map<String, String> pixelTypesMap;
    protected Map<String, String> pixelStatusesMap;
    // pixel fields
    protected String pixelType;       // depends on offer protocol
    protected String pixelCode;       // depends on pixel type
    protected String pixelStatus;     // edit mode

    public CreatePixelTestData(DataMode dataMode) {
        super(dataMode);
        this.pixelTypesMap = dataProvider.getPossibleValueFromJSON("offerpixeltypes", "enumSequenceNumber");
        this.pixelStatusesMap = dataProvider.getPossibleValueFromJSON("OfferPixelStatuses"); // edit mode
        this.localeData = isPositive() ? null : new LocaleData();
    }

    @Override
    public CreatePixelTestData setPixelData() {
        this.publishers = ObjectIdentityData.getObjectsByName(dataProvider.getCreatedInstancesData("publishers"), "Publisher Name ");
        this.publisherIDs = new HashSet<>(ObjectIdentityData.getAllIDs(publishers));
        this.offers = ObjectIdentityData.getObjectsByName(dataProvider.getCreatedInstancesData("offers"), "Offer Name ");
        this.offerIDs = new HashSet<>(ObjectIdentityData.getAllIDs(offers));
        this.publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
        setExistedPixels(publisher.getId());
        offerIDs.removeAll(existedOfferIDs);
        List<String> offerIds = new ArrayList<>(!isPositive() && isAnyAvailablePixel() ? existedOfferIDs : offerIDs);
        this.offer = ObjectIdentityData.getObjectFromListByID(offers, getRandomValueFromList(offerIds));
        setOffer(offer.getId());
        return this;
    }

    @Override
    public PixelTestData2 setPixelObject() {
        this.pixelObject = asJSON();
        return this;
    }

    @Override
    public PixelTestData2 setPositiveData() {
        Map<String, String> tempTypesMap = new HashMap<>();
        if (isHttpProtocol()) tempTypesMap = DataHelper.getMapContainsInValue(pixelTypesMap, SCRIPT_PIXEL_TYPE);
        else if (isImageProtocol()) tempTypesMap = DataHelper.getMapContainsInValue(pixelTypesMap, IMAGE_PIXEL_TYPE);
        else if (isPostBackProtocol())
            tempTypesMap = DataHelper.getMapContainsInValue(pixelTypesMap, POSTBACK_PIXEL_TYPE);
        this.pixelType = getRandomValueFromList(new ArrayList<>(tempTypesMap.keySet()));
        // code format depends on
        this.pixelCode = String.format("%s://development.stagingrevi.com/aff_l?offer_id=%s",
                offerProtocol.contains("https") ? "https" : "http", offer.getId());
        if (isHttpProtocol()) this.pixelCode = String.format("<script type='text/javascript'>//<![CDATA[" +
                " document.write('<iframe src='%s'</iframe>');" +
                "//]]></script>", pixelCode);
        else if (isImageProtocol()) this.pixelCode = String.format("<img src='%s'/>", pixelCode);
        return this;
    }

    @Override
    public PixelTestData2 setNegativeData() {
        this.pixelType = getRandomValueFromList(new ArrayList<>(pixelTypesMap.keySet()));
        this.pixelCode = String.format("%s/offer_id=%s", getWebSiteUnique(), offer.getId());
        if (isImageProtocol()) this.pixelCode = String.format("<script type='text/javascript'>//<![CDATA[" +
                " document.write('<iframe src='%s'</iframe>');" +
                "//]]></script>", pixelCode);
        else if (isHttpProtocol()) this.pixelCode = String.format("<img src='%s'/>", pixelCode);
        return this;
    }

    private JSONObject asJSON() {
        log.info("pixelType = " + pixelType);
        JSONObject object = new JSONObject();
        object.put("publisherId", publisher.getId() + " - " + publisher.getName());
        object.put("offerId", offer.getId() + " - " + offer.getName());
        object.put("type", pixelType);
        object.put("code", pixelCode);
        object.put("offerPixelStatus", pixelStatus);
        // doubts, probably invoke after creation
        object.put("offerPixelStatus", "active");
//        if (isCreateMode()) object.put("offerPixelStatus", "active");
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


}
