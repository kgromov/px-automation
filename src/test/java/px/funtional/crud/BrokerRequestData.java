package px.funtional.crud;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.brokers.BrokerTestData;

import java.util.HashSet;
import java.util.Set;

import static config.Config.isAdmin;

/**
 * Created by kgr on 6/30/2017.
 */
public final class BrokerRequestData implements RequestData {
    private BrokerTestData testData;
    private ObjectIdentityData identityData;

    public BrokerRequestData() {
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new BrokerTestData(dataMode);
    }

    public BrokerRequestData(ObjectIdentityData identityData) {
        this.identityData = identityData;
        DataMode dataMode = new DataMode.Builder()
                .createData()
                .positiveData()
                .build();
        this.testData = new BrokerTestData(dataMode);
    }

    @Override
    public String createURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/broker").build()
                .getRequestedURL();
    }

    @Override
    public String updateURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/broker/update").build()
                .getRequestedURL();
    }

    @Override
    public String getURL() {
        return new RequestedURL.Builder()
                .withRelativeURL("api/broker")
                .withParams("brokerGuid", identityData.getGuid())
                .build().getRequestedURL();
    }

    @Override
    public JSONObject asJSON() {
        JSONObject object = new JSONObject();
        object.put("brokerName", testData.getName());
        object.put("description", testData.getDescription());
        object.put("brokerType", testData.getBrokerType());
        // update
        if (identityData != null) {
            object.put("brokerGuid", identityData.getGuid());
            object.put("dailyCap", testData.getDailyCapacity());
        }
        return object;
    }

    @Override
    public Set<String> allowedFieldsToUpdate() {
        Set<String> fields = asJSON().keySet();
        fields.remove("brokerName");
        return isAdmin() ? fields  : new HashSet<>();
    }
}