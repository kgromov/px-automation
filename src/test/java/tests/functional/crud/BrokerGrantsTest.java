package tests.functional.crud;

import dto.ObjectIdentityData;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import px.funtional.crud.BrokerRequestData;
import px.funtional.crud.GrantsVerification;
import px.funtional.crud.RequestData;

import java.util.List;

import static config.Config.isAdmin;

/**
 * Created by kgr on 6/29/2017.
 */
public class BrokerGrantsTest extends GrantsTest {
    private List<ObjectIdentityData> brokers;

    @BeforeClass
    protected void setObjects() {
        // set all available buyers by admin user
        this.brokers = adminDataProvider.getCreatedInstancesData("brokers");
        // select only automation created to prevent possible updates
        this.brokers = ObjectIdentityData.getObjectsByName(brokers, "Broker Name ");
    }

    @Test
    public void checkCreateBrokerTest() {
        // create request data with campaign test data
        RequestData requestData = new BrokerRequestData();
        // check create post request
        GrantsVerification.checkCreateRequest(requestData, isAdmin());
    }

    @Test
    public void checkUpdateBrokerTest() {
        // select any available buyer
        ObjectIdentityData broker = ObjectIdentityData.getAnyObjectFromList(brokers);
        // create request data with campaign test data
        RequestData requestData = new BrokerRequestData(broker);
        // check create post request
        GrantsVerification.checkUpdateRequest(requestData, isAdmin());
    }
}
