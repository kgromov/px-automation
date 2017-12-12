package tests.objects.publishers;

import org.testng.annotations.Test;
import px.objects.publishers.pages.PublishersPage;
import tests.LoginTest;

/**
 * Created by kgr on 6/14/2017.
 */
public class ExportPublishersTest extends LoginTest {

    @Test()
    public void checkExport(){
        PublishersPage publishersPage = new PublishersPage(pxDriver);
        publishersPage.navigateToObjects();
        publishersPage.checkExport();
    }
}
