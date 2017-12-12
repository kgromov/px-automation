package px.objects.pixels;

import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.objects.DataMode;

import java.util.ArrayList;
import java.util.Map;

import static configuration.helpers.DataHelper.getJSONFromJSONArrayByCondition;
import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 11/2/2017.
 */
public class UpdatePixelData extends CreatePixelTestData {
    // preview/test
    private String publisherLink;
    private String conversionLink;

    public UpdatePixelData(DataMode dataMode) {
        super(dataMode);
        setExistedPixels();
    }

    public UpdatePixelData(JSONObject object) {
        super(DataMode.getCreatedByResponse());
        this.publishers = dataProvider.getCreatedInstancesData("publishers");
        this.offers = dataProvider.getCreatedInstancesData("offers");
        this.publisher = ObjectIdentityData.getObjectFromListByID(publishers, String.valueOf(object.get("publisherId")));
        this.offer = ObjectIdentityData.getObjectFromListByID(offers, String.valueOf(object.get("offerId")));
        this.pixelObject = object;
        this.id = String.valueOf(pixelObject.get("offerPixelId"));
        setPixelObject();
    }

    @Override
    public UpdatePixelData setPixelData() {
        if (availablePublishers.isEmpty()) {
            super.setPixelData();
            super.setPixelObject();
        } else {
            this.publisher = ObjectIdentityData.getObjectFromListByID(publishers,
                    getRandomValueFromList(new ArrayList<>(availablePublishers)));
            setExistedPixels(publisher.getId());
            this.offer = ObjectIdentityData.getObjectFromListByID(offers, getRandomValueFromList(new ArrayList<>(existedOfferIDs)));
            if (offer == null) {
                super.setPixelData();
                super.setPixelObject();
            } else {
                setOffer(offer.getId());
                this.pixelObject = getJSONFromJSONArrayByCondition(existedPixels, "offerId", offer.getId());
                setPixelObject();
            }
        }
        // test section data
        this.publisherLink = getWebSiteUnique();
        this.conversionLink = isDirectLink
                ? offerURL + "&offer_id=" + offer.getId() + "&aff_id=" + publisher.getId() + "&test=1"
                : String.format("%s://lxptest.go2cloud.org/aff_l?offer_id=%s&aff_id=%s&test=1",
                offerProtocol.contains("https") ? "https" : "http", offer.getId(), publisher.getId());
        return this;
    }

    @Override
    public PixelTestData2 setPixelObject() {
        // update some values according to UI
        this.pixelType = pixelTypesMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(String.valueOf(pixelObject.get("type"))))
                .map(Map.Entry::getKey)
                .findFirst().orElse(pixelType);
        pixelObject.put("type", pixelType);
        pixelObject.put("publisherId", publisher.getId() + " - " + publisher.getName());
        pixelObject.put("offerId", offer.getId() + " - " + offer.getName());
        return this;
    }

    public String getPublisherLink() {
        return publisherLink;
    }

    public String getConversionLink() {
        return conversionLink;
    }

}
