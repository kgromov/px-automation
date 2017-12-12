package tests.functional.crud;

import dto.ObjectIdentityData;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import px.objects.DataMode;
import px.funtional.crud.GrantsVerification;
import px.funtional.crud.PublisherRequestData;
import px.funtional.crud.RequestData;
import px.objects.publishers.PublisherTestData;

import java.util.List;

import static config.Config.isAdmin;

/**
 * Created by kgr on 6/29/2017.
 */
public class PublisherGrantsTest extends GrantsTest {
    private List<ObjectIdentityData> publishers;
    private List<ObjectIdentityData> publisherManagers;
    private PublisherTestData testData;

    @BeforeClass
    protected void setObjects() {
        // set all available publishers by admin user
        this.publishers = adminDataProvider.getCreatedInstancesData("publishers");
        // select only automation created to prevent possible updates
        this.publishers = ObjectIdentityData.getObjectsByName(publishers, "Publisher Name ");
        this.publisherManagers = adminDataProvider.getCreatedInstancesData("publisherManagers");
    }

    @BeforeMethod
    private void setPublisherTestData() {
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new PublisherTestData(dataMode);
        // select any publisher manager
        if (!publisherManagers.isEmpty()) {
            ObjectIdentityData publisherManager = ObjectIdentityData.getAnyObjectFromList(publisherManagers);
            testData.setManagerID(publisherManager.getId());
        }
    }

    @Test
    public void checkCreatePublisherTest() {
        // create request data with campaign test data
        RequestData requestData = new PublisherRequestData(testData);
        // check create post request
        GrantsVerification.checkCreateRequest(requestData, isAdmin());
    }

    @Test
    public void checkUpdatePublisherTest() {
        // select any available buyer
        ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
        // update request data with campaign test data
        RequestData requestData = new PublisherRequestData(testData, publisher);
        // check update post request
        GrantsVerification.checkUpdateRequest(requestData, isAdmin());
    }

    @Test
    public void checkUpdatePublisherInfoTest() {
        // select any available buyer
        ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
        // update request data with campaign test data
        PublisherRequestData requestData = new PublisherRequestData(testData, publisher);
        // append with contactInfoId (currently workaround till Session pool is implemented)
        JSONObject publisherInfoJSON = updateObjectWithParamId(requestData.asPublisherInfoJSON(),
                "contactInfoId", requestData.getPublisherInfoURL());
        // check update post request
        GrantsVerification.checkUpdateRequest(requestData, publisherInfoJSON,
                requestData.updatePublisherInfoURL(), requestData.getPublisherInfoURL(), isAdmin());
    }

    @Test
    public void checkUpdateTrackingDataTest() {
        // select any available buyer
        ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
        // update request data with campaign test data
        PublisherRequestData requestData = new PublisherRequestData(testData, publisher);
        // check update post request
        GrantsVerification.checkUpdateRequest(requestData, requestData.asTrackingDataJSON(),
                requestData.updateTrackingDataURL(), requestData.getTrackingDataURL(), isAdmin());
    }

    @Test
    public void checkUpdateAddressTest() {
        // select any available buyer
        ObjectIdentityData publisher = ObjectIdentityData.getAnyObjectFromList(publishers);
        // update request data with campaign test data
        PublisherRequestData requestData = new PublisherRequestData(testData, publisher);
        // append with addressId (currently workaround till Session pool is implemented)
        JSONObject addressJSON = updateObjectWithParamId(requestData.asAddressJSON(),
                "addressId", requestData.getAddressURL());
        // check update post request
        GrantsVerification.checkUpdateRequest(requestData, addressJSON,
                requestData.updateAddressURL(), requestData.getAddressURL(), isAdmin());
    }

}