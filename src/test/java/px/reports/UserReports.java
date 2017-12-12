package px.reports;

import config.Config;
import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.apache.log4j.Logger;
import utils.CustomMatcher;
import utils.SoftAssertionHamcrest;

import java.util.*;
import java.util.stream.Collectors;

import static config.Config.user;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static px.reports.campaigns.CampaignsReportTestData.CAMPAIGNS_INSTANCE_NAME;

/**
 * Created by kgr on 7/28/2017.
 */
public interface UserReports {
    Logger LOGGER = Logger.getLogger(UserReports.class);

    default void checkCampaignsTo1Buyer(List<ObjectIdentityData> buyerCampaigns) {
        if (!Config.isBuyer()) return;
        LOGGER.info("Check buyer campaigns belong to 1 buyer");
        LxpDataProvider dataProvider = new LxpDataProvider();
        List<ObjectIdentityData> buyers = dataProvider.getCreatedInstancesData("buyers");
        try {
            assertThat(String.format("There is 1 buyer under buyer\tuser: '%s'", user), buyers.size(), equalTo(1));
        } catch (AssertionError e) {
            throw new TestDataException(String.format("There are %d buyers: %s under buyer user\tuser: '%s'", buyers.size(), buyers, user));
        }
        // "Clemens Agneza Conti" - buyer name on staging
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // buyer instances - filter items
 /*       this.campaigns = dataProvider.getCreatedInstancesData("buyerinstancesbyparent",
                Collections.singletonList("parent"), Collections.singletonList(buyer.getGuid()));*/
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + CAMPAIGNS_INSTANCE_NAME)
                .filter(Arrays.asList("ParentBuyerGuid", "FromPeriod", "ToPeriod"),
                        Arrays.asList(buyer.getGuid(), period, period))
                .sort("totalSpend", "asc")
                .build().getRequestedURL();
        List<ObjectIdentityData> campaigns = ObjectIdentityData.getObjectsByJSONArray(dataProvider.getDataAsJSONArray(requestedURL));
        // verification itself
        List<String> campaignGUIDs = ObjectIdentityData.getAllGUIDs(campaigns);
        // register of GUIDs is not always the same
        campaignGUIDs = campaignGUIDs.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<String> filterCampaignGUIDs = ObjectIdentityData.getAllGUIDs(buyerCampaigns);
        filterCampaignGUIDs.removeAll(campaignGUIDs);
        List<ObjectIdentityData> extraCampaigns = filterCampaignGUIDs.stream().map(guid ->
                ObjectIdentityData.getObjectFromListByGUID(buyerCampaigns, guid)).collect(Collectors.toList());
        if (extraCampaigns.size() > 0)
            throw new TestDataException(String.format("The following list of campaigns '%s' " +
                    "does not belong to list of buyer '%s' campaigns", extraCampaigns, buyer));
    }

    default void checkSubIdsTo1Publisher(List<ObjectIdentityData> publisherSubIDs) {
        if (!Config.isPublisher()) return;
        LOGGER.info("Check publisher subIDs belong to 1 publisher");
        LxpDataProvider dataProvider = new LxpDataProvider();
        List<ObjectIdentityData> publishers = dataProvider.getCreatedInstancesData("publishers");
        try {
            assertThat(String.format("There is 1 publisher under publisher\tuser: '%s'", user), publishers.size(), equalTo(1));
        } catch (AssertionError e) {
            throw new TestDataException(String.format("There are %d publishers: %s under publisher user\tuser: '%s'", publishers.size(), publishers, user));
        }
        ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
        // publisher instances - filter items
        List<ObjectIdentityData> subIDs = dataProvider.getCreatedInstancesData("publisherinstances",
                Collections.singletonList("publisherGuid"), Collections.singletonList(publisher.getGuid()));
        // verification itself
        List<String> subIdNames = ObjectIdentityData.getAllNames(subIDs);
        // register of GUIDs (Names) is not always the same
        subIdNames = subIdNames.stream().map(String::toLowerCase).collect(Collectors.toList());
        List<String> filterSubIdNames = ObjectIdentityData.getAllNames(publisherSubIDs);
        filterSubIdNames.removeAll(subIdNames);
        List<ObjectIdentityData> extraSubIDs = filterSubIdNames.stream().map(name ->
                ObjectIdentityData.getObjectFromListByName(publisherSubIDs, name)).collect(Collectors.toList());
        if (extraSubIDs.size() > 0)
            throw new TestDataException(String.format("The following list of subIDs '%s' " +
                    "does not belong to list of publisher '%s' subIDs", extraSubIDs, publisher));
    }

    default void checkSubIdsTo1Publisher(Set<String> publisherSubIDs, String by) {
        if (!Config.isPublisher()) return;
        LOGGER.info("Check publisher subIDs belong to 1 publisher");
        LxpDataProvider dataProvider = new LxpDataProvider();
        List<ObjectIdentityData> publishers = dataProvider.getCreatedInstancesData("publishers");
        try {
            assertThat(String.format("There is 1 publisher under publisher\tuser: '%s'", user), publishers.size(), equalTo(1));
        } catch (AssertionError e) {
            throw new TestDataException(String.format("There are %d publishers: %s under publisher user\tuser: '%s'", publishers.size(), publishers, user));
        }
        ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
        // publisher instances - filter items
        List<ObjectIdentityData> subIDs = dataProvider.getCreatedInstancesData("publisherinstances",
                Collections.singletonList("publisherGuid"), Collections.singletonList(publisher.getGuid()));
        // verification itself
        List<String> subIdBy = new ArrayList<>();
        switch (by) {
            case "name":
                subIdBy = ObjectIdentityData.getAllNames(subIDs);
                break;
            case "id":
                subIdBy = ObjectIdentityData.getAllIDs(subIDs);
                break;
            case "guid":
                subIdBy = ObjectIdentityData.getAllGUIDs(subIDs);
                break;
        }
        // register of GUIDs (Names) is not always the same
        subIdBy = subIdBy.stream().map(String::toLowerCase).collect(Collectors.toList());
        publisherSubIDs.removeAll(subIdBy);
        if (publisherSubIDs.size() > 0)
            throw new TestDataException(String.format("The following list of subIDs '%s' " +
                    "does not belong to list of publisher '%s' subIDs", publisherSubIDs, publisher));
    }

    default void checkPublisherHiddenNames(List<ObjectIdentityData> publishers) {
        if (Config.isAdmin()) return;
        LOGGER.info("Check that publisher names are hidden with ids");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ObjectIdentityData.getAllIDs(publishers).forEach(name ->
                hamcrest.assertThat(String.format("Publisher name '%s'is hidden with its id", name), name, CustomMatcher.matchesPattern("\\d+"))
        );
        hamcrest.assertAll();
    }
}