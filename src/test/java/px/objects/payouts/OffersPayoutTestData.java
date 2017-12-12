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
public class OffersPayoutTestData extends PayoutTestData {

    public OffersPayoutTestData(DataMode dataMode) {
        super(dataMode);
        // all new payout data is already created in proper mode
        setInstanceGroup(OFFER_PAYOUT_INSTANCE);
        setParents();
    }

    // get rid of fucking if - make like builder in the best way
    public OffersPayoutTestData setCreateData() {
        this.offerID = DataHelper.getRandomValueFromList(offerIDs);
        this.offerName = ObjectIdentityData.getObjectFromListByID(offers, offerID).getName();
        // save already created target IDs in parent payouts
        setPayouts();
       /* // set payout fields as json
        this.payoutObject = asJSON();*/
        return this;
    }

    public OffersPayoutTestData setDeleteData() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filterByKey("offerId")
                .withEmptySorting()
                .build().getRequestedURL();
        this.existedPayouts = dataProvider.getDataAsJSONArray(requestedURL, true);
        Set<String> payoutsList = new HashSet<>(DataHelper.getListFromJSONArrayByKey(existedPayouts, "offerId"));
        List<String> availableOfferPayouts = DataHelper.getListByValue(new ArrayList<>(payoutsList), offerIDs);
        if (availableOfferPayouts.isEmpty()) return setCreateData();
        this.offerID = DataHelper.getRandomValueFromList(availableOfferPayouts);
        // save already created target IDs in parent payouts
        setPayouts();
        return this;
    }

    public OffersPayoutTestData setUpdateData() {
        setHeaderObjects();
        setDeleteData();
        setOfferObject();
        // set payout json
//        setPayoutObject();
        return this;
    }

    public OffersPayoutTestData setUniquePayoutData() {
        // list to select publisherID
        List<String> availablePublisherIDs = DataHelper.getListNotByValue(publisherIDs, existedPayoutIDs);
        this.publisherID = DataHelper.getRandomValueFromList(availablePublisherIDs);
        ObjectIdentityData publisher = ObjectIdentityData.getObjectFromListByID(publishers, publisherID);
        this.publisherName = publisher.getName();
        this.publisherGUID = publisher.getGuid();
        return this;
    }

    public OffersPayoutTestData setNonUniquePayoutData() {
        // list to select publisherID
        List<String> availablePublisherIDs = existedPayoutIDs.isEmpty() ? publisherIDs : new ArrayList<>(existedPayoutIDs);
        this.publisherID = DataHelper.getRandomValueFromList(availablePublisherIDs);
        if (!ObjectIdentityData.getAllIDs(publishers).contains(publisherID)) {
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publishers")
                    .filter("publisherId", publisherID)
                    .sort("publisherId", "asc")
                    .build().getRequestedURL();
            JSONObject publisherObject = dataProvider.getDataAsJSON(requestedURL);
            this.publisherName = String.valueOf(publisherObject.get("publisherName"));
            this.publisherGUID = String.valueOf(publisherObject.get("publisherGuid"));
        } else {
            ObjectIdentityData publisher = ObjectIdentityData.getObjectFromListByID(publishers, publisherID);
            this.publisherName = publisher.getName();
            this.publisherGUID = publisher.getGuid();
        }
        return this;
    }

    @Override
    public void setPayoutObject() {
        // if payout has publisherID - update case
        this.payoutObject = existedPayoutIDs.contains(publisherID) ?
                DataHelper.getJSONFromJSONArrayByCondition(existedPayouts, "publisherId", publisherID)
                : asJSON();
    }

    @Override
    protected void setPayouts() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter("offerId", offerID)
                .sort("publisherId", "asc")
                .build().getRequestedURL();
        this.existedPayouts = dataProvider.getDataAsJSONArray(requestedURL);
        this.existedPayoutIDs = new HashSet<>(DataHelper.getListFromJSONArrayByKey(existedPayouts, "publisherId"));
    }

    @Override
    public boolean isDuplicatedTargetID() {
        return existedPayoutIDs.contains(publisherID);
    }
}