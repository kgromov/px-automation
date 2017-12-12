package px.objects.payouts;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.objects.DataMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kgr on 4/21/2017.
 */
public class PublishersPayoutTestData extends PayoutTestData {

    public PublishersPayoutTestData(DataMode dataMode) {
        super(dataMode);
        // all new payout data is already created in proper mode
        setInstanceGroup(PUBLISHER_PAYOUT_INSTANCE);
        setParents();
    }

    // get rid of fucking if - make like builder in the best way
    public PublishersPayoutTestData setCreateData() {
        this.publisherID = DataHelper.getRandomValueFromList(publisherIDs);
        ObjectIdentityData publisher = ObjectIdentityData.getObjectFromListByID(publishers, publisherID);
        this.publisherName = publisher.getName();
        this.publisherGUID = publisher.getGuid();
        // save already created target IDs in parent payouts
        setPayouts();
       /* // set payout fields as json
        this.payoutObject = asJSON();*/
        return this;
    }

    public PublishersPayoutTestData setDeleteData() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filterByKey("publisherId")
                .withEmptySorting()
                .build().getRequestedURL();
        this.existedPayouts = dataProvider.getDataAsJSONArray(requestedURL, true);
        Set<String> payoutsList = new HashSet<>(DataHelper.getListFromJSONArrayByKey(existedPayouts, "publisherId"));
        List<String> availablePublisherPayouts = DataHelper.getListByValue(new ArrayList<>(payoutsList), publisherIDs);
        if (availablePublisherPayouts.isEmpty()) return setCreateData();
        this.publisherID = DataHelper.getRandomValueFromList(availablePublisherPayouts);
        requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publishers")
                .filter("publisherId", publisherID)
                .sort("publisherId", "asc")
                .build().getRequestedURL();
        JSONObject publisherObject = dataProvider.getDataAsJSON(requestedURL);
        this.publisherName = String.valueOf(publisherObject.get("publisherName"));
        this.publisherGUID = String.valueOf(publisherObject.get("publisherGuid"));
        // save already created target IDs in parent payouts
        setPayouts();
        return this;
    }

    public PublishersPayoutTestData setUpdateData() {
        setHeaderObjects();
        setDeleteData();
//        setOfferObject();
        // set payout json
//        setPayoutObject();
        return this;
    }

    public PublishersPayoutTestData setUniquePayoutData() {
        // list to select publisherID
        List<String> availableOfferIDs = DataHelper.getListNotByValue(offerIDs, existedPayoutIDs);
        this.offerID = DataHelper.getRandomValueFromList(availableOfferIDs);
        ObjectIdentityData publisher = ObjectIdentityData.getObjectFromListByID(offers, offerID);
        this.offerName = publisher.getName();
        return this;
    }

    public PublishersPayoutTestData setNonUniquePayoutData() {
        // list to select publisherID
        List<String> availablePublisherIDs = existedPayoutIDs.isEmpty() ? offerIDs : new ArrayList<>(existedPayoutIDs);
        this.offerID = DataHelper.getRandomValueFromList(availablePublisherIDs);
        setOfferObject();
        this.offerName = String.valueOf(offerObject.get("offerName"));
        /*if (!ObjectIdentityData.getAllIDs(offers).contains(offerID)) {
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/offers")
                    .filter("offerId", offerID)
                    .sort("offerId", "asc")
                    .build().getRequestedURL();
            JSONObject offerObject = dataProvider.getDataAsJSON(requestedURL);
            this.offerName = String.valueOf(offerObject.get("offerName"));
        } else {
            ObjectIdentityData offer = ObjectIdentityData.getObjectFromListByID(offers, offerID);
            this.offerName = offer.getName();
        }*/
        return this;
    }

    @Override
    public void setPayoutObject() {
        // if payout has publisherID - update case
        this.payoutObject = existedPayoutIDs.contains(offerID) ?
                DataHelper.getJSONFromJSONArrayByCondition(existedPayouts, "offerId", offerID)
                : asJSON();
    }

    @Override
    protected void setPayouts() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter("publisherId", publisherID)
                .sort("offerId", "asc")
                .build().getRequestedURL();
        this.existedPayouts = dataProvider.getDataAsJSONArray(requestedURL);
        this.existedPayoutIDs = new HashSet<>(DataHelper.getListFromJSONArrayByKey(existedPayouts, "offerId"));
    }

    @Override
    public boolean isDuplicatedTargetID() {
        return existedPayoutIDs.contains(offerID);
    }
}
