package dto;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import px.objects.InstancesTestData;
import px.objects.brokers.BrokerTestData;
import px.objects.campaigns.CampaignTestData;
import px.objects.offers.OfferTestData;
import px.objects.publishers.PublisherTestData;
import utils.SoftAssertionHamcrest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kgr on 11/7/2016.
 */
public class CheckTestData {
    private static final Logger log = Logger.getLogger(CheckTestData.class);
    private static final LxpDataProvider dataProvider = new LxpDataProvider();

    // updated instance specific verification
    public static void checkEditedBroker(BrokerTestData testData) {
        log.info(String.format("Verify edited '%s' broker with updated details on lxp backend", testData.getName()));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + testData.getInstanceGroup())
                .filter("brokerName", testData.getName())
                .sort("brokerName", "asc")
                .build().getRequestedURL();
        JSONObject jsonObject = dataProvider.getDataAsJSON(requestedURL);
        hamcrest.assertThat("Description is updated to", String.valueOf(jsonObject.get("description")), equalTo(testData.getDescription()));
        hamcrest.assertThat("Daily capacity is updated to ", String.valueOf(jsonObject.get("dailyCap")), equalTo(testData.getDailyCapacity()));
        hamcrest.assertAll();
    }

    public static void checkEditedPublisher(PublisherTestData testData) {
        log.info(String.format("Verify edited '%s' publisher with updated details on lxp backend", testData.getName()));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + testData.getInstanceGroup())
                .filter("publisherName", testData.getName())
                .sort("publisherName", "asc")
                .build().getRequestedURL();
        JSONObject jsonObject = dataProvider.getDataAsJSON(requestedURL);
        hamcrest.assertThat("Name is updated to", String.valueOf(jsonObject.get("publisherName")), equalTo(testData.getName()));
        hamcrest.assertThat("PublisherTier is updated to", String.valueOf(jsonObject.get("publisherTier")), equalTo(testData.getTier()));
        hamcrest.assertThat("Type is updated to", String.valueOf(jsonObject.get("type")), equalTo(testData.getType()));
        hamcrest.assertThat("AccessMode is updated to", String.valueOf(jsonObject.get("accessMode")), equalTo(testData.getAccessMode()));
        hamcrest.assertThat("Margin is updated to", String.valueOf(jsonObject.get("margin")), equalTo(testData.getMargin()));
        hamcrest.assertThat("AddUpsellToBalance is updated to", DataHelper.getYesNo(String.valueOf(jsonObject.get("addUpsellToBalance"))), equalTo(testData.getUpsellBalance()));
        hamcrest.assertThat("EscoreCheckPercentage is updated to", String.valueOf(jsonObject.get("escoreCheckPercentage")), equalTo(testData.getEscorePercentage()));
        hamcrest.assertThat("FixedPricing is updated to", DataHelper.getYesNo(String.valueOf(jsonObject.get("fixedPricing"))), equalTo(testData.getFixedPricing()));
        hamcrest.assertAll();
    }

    public static void checkEditedCampaign(CampaignTestData testData) {
        log.info(String.format("Verify edited '%s' campaign with updated details on lxp backend", testData.getName()));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstances/report")
                .filter(Arrays.asList("buyerInstanceName", "FromPeriod", "ToPeriod"), Arrays.asList( testData.getName(), period, period))
                .sort("buyerInstanceName", "asc")
                .build().getRequestedURL();
        JSONObject jsonObject = dataProvider.getDataAsJSON(requestedURL);
        hamcrest.assertThat("DeliveryType is updated to", String.valueOf(jsonObject.get("buyerInstanceDeliveryType")), equalTo(testData.getDeliveryType()));
        hamcrest.assertThat("Vertical is updated to", String.valueOf(jsonObject.get("vertical")), equalTo(testData.getVertical()));
        // required get value by key -> specific getter or two and common method
        hamcrest.assertThat("BuyerCategory is updated to",
                testData.getCategoryKeyByValue(String.valueOf(jsonObject.get("buyerCategory"))), equalTo(testData.getBuyerCategory()));
        hamcrest.assertAll();
    }

    public static void checkEditedOffer(OfferTestData testData) {
        log.info(String.format("Verify edited '%s' campaign with updated details on lxp backend", testData.getName()));
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + testData.getInstanceGroup())
                .filter("offerName", testData.getName())
                .sort("offerName", "asc")
                .build().getRequestedURL();
        if (!testData.isVisibleByStatus()) {
            assertThat(String.format("Offer updated with status '%s' became invisible in table`", testData.getStatus()),
                    dataProvider.getDataAsJSONArray(requestedURL).length(), equalTo(0));
        } else {
            SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
            JSONObject jsonObject = dataProvider.getDataAsJSON(requestedURL);
            hamcrest.assertThat("Name is updated to", String.valueOf(jsonObject.get("offerName")), equalTo(testData.getName()));
            hamcrest.assertThat("PreviewURL is updated to", String.valueOf(jsonObject.get("previewUrl")), equalTo(testData.getPreviewURL()));
            hamcrest.assertThat("OfferCategories is updated to", String.valueOf(jsonObject.get("categoryName")), equalTo(testData.getOfferCategories()));
            hamcrest.assertAll();
        }
    }

    public static void checkInstanceCreated(String group, InstancesTestData instancesTestData) {
        log.info(String.format("Instance '%s' created - present in response.", instancesTestData.getName()));
        List<ObjectIdentityData> instancesList = dataProvider.getCreatedInstancesData(group);
        List<String> namesList = ObjectIdentityData.getAllNames(instancesList);
        assertThat(String.format("Instance '%s' created - present in response. All %s:\n%s",
                instancesTestData.getName(), group, namesList), namesList.contains(instancesTestData.getName()));
    }

    public static void checkInstanceDeleted(String group, InstancesTestData instancesTestData) {
        log.info(String.format("Instance '%s' deleted - absent in response.", instancesTestData.getName()));
        List<ObjectIdentityData> instancesList = dataProvider.getCreatedInstancesData(group);
        List<String> namesList = ObjectIdentityData.getAllNames(instancesList);
        assertThat(String.format("Instance '%s' deleted - absent in response. All %s:\n%s",
                instancesTestData.getName(), group, namesList), !namesList.contains(instancesTestData.getName()));
    }

}
