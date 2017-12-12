package tests.objects.roles.publisher;

import org.testng.annotations.Test;
import px.objects.offers.pages.OffersPage;
import tests.LoginTest;

/**
 * Created by kgr on 4/28/2017.
 */
public class OffersUnderPublisherTest extends LoginTest {

    @Test
    public void checkOffersReadOnly() {
        OffersPage offersPage = new OffersPage(pxDriver);
        offersPage.navigateToObjects();
        offersPage.checkReadOnly();
    }
}