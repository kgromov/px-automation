package tests.objects;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import dto.ObjectIdentityData;
import org.testng.annotations.Test;
import pages.groups.PaginationStrategy;
import px.objects.brokers.pages.BrokersPage;
import px.objects.buyers.pages.BuyersPage;
import px.objects.campaigns.pages.CampaignsPage;
import px.objects.credSets.pages.CredSetsOverviewPage;
import px.objects.customRights.pages.CustomRightsOverviewPage;
import px.objects.offers.pages.OffersPage;
import px.objects.publishers.pages.PublishersPage;
import px.objects.users.pages.UsersPage;
import tests.LoginTest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by konstantin on 21.10.2016.
 */
public class PxObjectsPaginationTest extends LoginTest {
    private final LxpDataProvider dataProvider = new LxpDataProvider();
    private boolean allowDefaultLogin = super.allowDefaultLogin = false;
    private PaginationStrategy defaultStrategy = rows -> rows;

    @Test()
    public void checkBrokersPagination() {
        List<ObjectIdentityData> brokers = dataProvider.getCreatedInstancesData("brokers");
        BrokersPage brokersPage = new BrokersPage(pxDriver);
        pxDriver.goToURL(url);
        login();
        brokersPage.navigateToObjects();
        brokersPage.checkPagination(defaultStrategy, brokers.size());
    }

    @Test()
    public void checkBuyersPagination() {
        List<ObjectIdentityData> buyers = dataProvider.getCreatedInstancesData("buyers");
        BuyersPage buyersPage = new BuyersPage(pxDriver);
        pxDriver.goToURL(url);
        login();
        buyersPage.navigateToObjects();
        buyersPage.checkPagination(defaultStrategy, buyers.size());
    }

    @Test()
    public void checkCampaignsPagination() {
//        List<ObjectIdentityData> campaigns = new LxpDataProvider().getCreatedInstancesData("buyerInstances");
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/buyerInstances/report")
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(period, period))
                .sort("totalSpend", "asc")
                .build().getRequestedURL();
        dataProvider.getDataAsJSONArray(requestedURL);
        CampaignsPage campaignsPage = new CampaignsPage(pxDriver);
        pxDriver.goToURL(url);
        login();
        campaignsPage.navigateToObjects();
        campaignsPage.checkPagination(defaultStrategy, dataProvider.getCurrentTotal());
    }

    @Test()
    public void checkOffersPagination() {
//        List<ObjectIdentityData> offers = new LxpDataProvider().getCreatedInstancesData("offers");
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/offers")
                .filter(Arrays.asList("FromPeriod", "ToPeriod"), Arrays.asList(period, period))
                .sort("totalSpend", "asc")
                .build().getRequestedURL();
        dataProvider.getDataAsJSONArray(requestedURL);
        OffersPage offersPage = new OffersPage(pxDriver);
        pxDriver.goToURL(url);
        login();
        offersPage.navigateToObjects();
        offersPage.checkPagination(defaultStrategy, dataProvider.getCurrentTotal());
    }

    @Test()
    public void checkPublishersPagination() {
//        List<ObjectIdentityData> publishers = new LxpDataProvider().getCreatedInstancesData("publishers");
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/publishers")
                .withEmptyFilter()
                .sort("publisherName", "asc")
                .build().getRequestedURL();
        List<String> publishers1 = DataHelper.getListFromJSONArrayByKey(dataProvider.getDataAsJSONArray(requestedURL, true), "publisherName");
        List<String> publishers2 = ObjectIdentityData.getAllNames(dataProvider.getCreatedInstancesData("publishers"));
        publishers2.removeAll(publishers1);
        dataProvider.getDataAsJSONArray(requestedURL);
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        pxDriver.goToURL(url);
        login();
        publishersPage.navigateToObjects();
//        publishersPage.checkPagination(defaultStrategy, publishers.size());
        publishersPage.checkPagination(defaultStrategy, dataProvider.getCurrentTotal());
    }

    @Test()
    public void checkUsersPagination() {
        String requestURL = new RequestedURL.Builder()
                .withRelativeURL("api/users")
                .withEmptyFilter()
                .sort("description", "asc")
                .build().getRequestedURL();
        dataProvider.getDataAsJSONArray(requestURL);
        UsersPage usersPage = new UsersPage(pxDriver);
        pxDriver.goToURL(url);
        login();
        usersPage.navigateToObjects();
        usersPage.checkPagination(defaultStrategy, dataProvider.getCurrentTotal());
    }

    @Test
    public void checkCredSetsPagination() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/credsets")
                .withEmptyFilter()
                .sort("credSetName", "asc")
                .build().getRequestedURL();
        dataProvider.getDataAsJSONArray(requestedURL);
        CredSetsOverviewPage credSetsPage = new CredSetsOverviewPage(pxDriver);
        pxDriver.goToURL(url);
        login();
        credSetsPage.navigateToObjects();
        credSetsPage.checkPagination(defaultStrategy, dataProvider.getCurrentTotal());
    }

    @Test
    public void checkCustomRightsPagination() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/customrights")
                .withEmptyFilter()
                .sort("rightName", "asc")
                .build().getRequestedURL();
        dataProvider.getDataAsJSONArray(requestedURL);
        CustomRightsOverviewPage overviewPage = new CustomRightsOverviewPage(pxDriver);
        pxDriver.goToURL(url);
        login();
        overviewPage.navigateToObjects();
        overviewPage.checkPagination(defaultStrategy, dataProvider.getCurrentTotal());
    }
}