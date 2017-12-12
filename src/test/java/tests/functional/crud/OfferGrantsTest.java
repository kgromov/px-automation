package tests.functional.crud;

import dto.ObjectIdentityData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import px.objects.DataMode;
import px.funtional.crud.GrantsVerification;
import px.funtional.crud.OfferRequestData;
import px.funtional.crud.RequestData;
import px.objects.offers.OfferTestData;

import java.util.List;

import static config.Config.isAdmin;

/**
 * Created by kgr on 6/29/2017.
 */
public class OfferGrantsTest extends GrantsTest {
    private List<ObjectIdentityData> offers;
    private OfferTestData testData;

    @BeforeClass
    protected void setObjects() {
        // set all available buyers by admin user
        this.offers = adminDataProvider.getCreatedInstancesData("offers");
        // select only automation created to prevent possible updates
        this.offers = ObjectIdentityData.getObjectsByName(offers, "Offer Name ");
    }

    @BeforeMethod
    private void setOfferTestData() {
        // cause admin grants required to execute some requests inside offers data
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new OfferTestData(dataMode);
    }

    @Test
    public void checkCreateOfferTest() {
        // create request data with campaign test data
        RequestData requestData = new OfferRequestData(testData);
        // check create post request
        GrantsVerification.checkCreateRequest(requestData, isAdmin());
    }

    @Test
    public void checkUpdateOfferTest() {
        // select any available buyer
        ObjectIdentityData offer = ObjectIdentityData.getAnyObjectFromList(offers);
        // create request data with campaign test data
        RequestData requestData = new OfferRequestData(testData, offer);
        // check create post request
        GrantsVerification.checkUpdateRequest(requestData, isAdmin());
    }
}
