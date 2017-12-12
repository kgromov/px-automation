package tests.functional.crud;

import dto.ObjectIdentityData;
import org.json.JSONObject;
import org.testng.annotations.*;
import px.objects.DataMode;
import px.objects.buyers.BuyerTestData;
import px.funtional.crud.BuyerRequestData;
import px.funtional.crud.GrantsVerification;
import px.funtional.crud.RequestData;

import java.util.List;

import static config.Config.isAdmin;

/**
 * Created by kgr on 6/29/2017.
 */
public class BuyerGrantsTest extends GrantsTest {
    private List<ObjectIdentityData> buyers;
    private List<ObjectIdentityData> users;
    private BuyerTestData testData;

    @BeforeClass
    protected void setObjects() {
        // set all available buyers by admin user
        this.buyers = adminDataProvider.getCreatedInstancesData("buyers");
        // select only automation created to prevent possible updates
        this.buyers = ObjectIdentityData.getObjectsByName(buyers, "Buyer Name ");
        // set all available publisher managers by admin user
        this.users = adminDataProvider.getCreatedInstancesData("users");
    }

    @BeforeMethod
    private void setPublisherTestData() {
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new BuyerTestData(dataMode);
    }

    @Test
    public void checkCreateBuyerTest() {
        // create request data with campaign test data
        RequestData requestData = new BuyerRequestData();
        // check create post request
        GrantsVerification.checkCreateRequest(requestData, isAdmin());
    }

    @Test
    public void checkUpdateBuyerTest() {
        // select any available buyer
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // create request data with campaign test data
        RequestData requestData = new BuyerRequestData(testData, buyer);
        // check create post request
        GrantsVerification.checkUpdateRequest(requestData, isAdmin());
    }

    @Test
    public void checkUpdateBuyerInfoTest() {
        // select any available buyer
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // update request data with campaign test data
        BuyerRequestData requestData = new BuyerRequestData(testData, buyer);
        // append with contactInfoId (currently workaround till Session pool is implemented)
        // check update post request
        JSONObject buyerInfoJSON = updateObjectWithParamId(requestData.asBuyerInfoJSON(),
                "contactInfoId", requestData.getBuyerInfoURL());
        GrantsVerification.checkUpdateRequest(requestData, buyerInfoJSON,
                requestData.updateBuyerInfoURL(), requestData.getBuyerInfoURL(), isAdmin());
    }

    @Test
    public void checkUpdateAddressTest() {
        // select any available buyer
        ObjectIdentityData buyer = ObjectIdentityData.getAnyObjectFromList(buyers);
        // update request data with campaign test data
        BuyerRequestData requestData = new BuyerRequestData(testData, buyer);
        // append with addressId (currently workaround till Session pool is implemented)
        JSONObject addressJSON = updateObjectWithParamId(requestData.asAddressJSON(),
                "addressId", requestData.getAddressURL());
        // check update post request
        GrantsVerification.checkUpdateRequest(requestData, addressJSON,
                requestData.updateAddressURL(), requestData.getAddressURL(), isAdmin());
    }
}