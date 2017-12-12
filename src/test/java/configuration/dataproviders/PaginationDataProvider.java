package configuration.dataproviders;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import dto.TestDataError;
import org.json.JSONArray;
import org.testng.annotations.DataProvider;

import java.util.List;

/**
 * Created by kgr on 4/18/2017.
 */
public class PaginationDataProvider extends SuperDataProvider {

    @DataProvider
    public static Object[][] buyerUsersPaginationData() {
        try {
            // select automation created buyer
            List<ObjectIdentityData> buyers = ObjectIdentityData.getObjectsByName(
                    dataProvider.getCreatedInstancesData("buyers"), "Buyer Name ");
            ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
            // get chosen buyer users
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/buyerusers")
                    .withParams("buyerGuid", buyer.getGuid())
                    .withEmptyFilter()
                    .sort("description", "asc")
                    .build().getRequestedURL();
            JSONArray buyerUsers = dataProvider.getDataAsJSONArray(requestedURL);
            return new Object[][]{
                    {buyer, buyerUsers}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] publisherUsersPaginationData() {
        try {
            // select automation created publisher
            List<ObjectIdentityData> publishers = ObjectIdentityData.getObjectsByName(
                    dataProvider.getCreatedInstancesData("publishers"), "Publisher Name ");
            ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
            // get chosen buyer users
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publisherusers")
                    .withParams("publisherId", publisher.getGuid())
                    .withEmptyFilter()
                    .withEmptySorting()
                    .build().getRequestedURL();
            JSONArray publisherUsers = dataProvider.getDataAsJSONArray(requestedURL);
            return new Object[][]{
                    {publisher, publisherUsers}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] publisherPixelsPaginationData() {
        try {
            // select automation created publisher
            List<ObjectIdentityData> publishers = ObjectIdentityData.getObjectsByName(
                    dataProvider.getCreatedInstancesData("publishers"), "Publisher Name ");
            ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
            // get chosen buyer users
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publisherofferpixels")
                    .filter("publisherId", publisher.getId())
                    .withEmptySorting()
                    .build().getRequestedURL();
            JSONArray pixels = dataProvider.getDataAsJSONArray(requestedURL);
            return new Object[][]{
                    {publisher, pixels}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] publisherPayoutsPaginationData() {
        try {
            // select automation created publisher
            List<ObjectIdentityData> publishers = ObjectIdentityData.getObjectsByName(
                    dataProvider.getCreatedInstancesData("publishers"), "Publisher Name ");
            ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
            // get chosen buyer users
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publisherpayouts")
                    .filter("publisherId", publisher.getId())
                    .withEmptySorting()
                    .build().getRequestedURL();
            JSONArray payouts = dataProvider.getDataAsJSONArray(requestedURL);
            return new Object[][]{
                    {publisher, payouts}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] offerPayoutsPaginationData() {
        try {
            // select automation created publisher
            List<ObjectIdentityData> offers = ObjectIdentityData.getObjectsByName(
                    dataProvider.getCreatedInstancesData("offers"), "Offer Name ");
            ObjectIdentityData offer = ObjectIdentityData.getAnyObjectFromList(offers);
            // get chosen buyer users
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/offerpayouts")
                    .filter("offerId", offer.getId())
                    .sort("offerId", "asc")
                    .build().getRequestedURL();
            JSONArray payouts = dataProvider.getDataAsJSONArray(requestedURL);
            return new Object[][]{
                    {offer, payouts}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }

    @DataProvider
    public static Object[][] offerURLsPaginationData() {
        try {
            // select automation created publisher
            List<ObjectIdentityData> offers = ObjectIdentityData.getObjectsByName(
                    dataProvider.getCreatedInstancesData("offers"), "Offer Name ");
            ObjectIdentityData offer = ObjectIdentityData.getAnyObjectFromList(offers);
            // get chosen buyer users
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/offerurls")
                    .filter("offerId", offer.getId())
                    .sort("offerUrlName", "asc")
                    .build().getRequestedURL();
            JSONArray offerURLs = dataProvider.getDataAsJSONArray(requestedURL);
            return new Object[][]{
                    {offer, offerURLs}
            };
        } catch (Exception e) {
            TestDataError.collect(e);
            return new Object[][]{
                    {null}
            };
        }
    }
}