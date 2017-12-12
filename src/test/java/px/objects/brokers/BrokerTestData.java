package px.objects.brokers;

import configuration.helpers.DataHelper;
import px.objects.InstancesTestData;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import px.objects.DataMode;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by kgr on 10/18/2016.
 */
public class BrokerTestData extends InstancesTestData {
    // contact info
    private String brokerType;
    private String dailyCapacity;
    private String status;
    // maps for possible next verification
    private Map<String, String> brokerTypeMap;

    public BrokerTestData(DataMode dataMode) {
        super(dataMode);
        setInstanceGroup("brokers");
        // mapping from test data files
        this.brokerTypeMap = dataProvider.getPossibleValueFromJSON("BrokerTypes");
        this.brokerType = DataHelper.getRandomValueFromList(new ArrayList<>(brokerTypeMap.keySet()));
        this.dailyCapacity = getPercentage();
        if (isPositive()) setPositiveData();
        else setNegativeData();
    }

    public BrokerTestData(JSONObject jsonObject) {
        super(DataMode.getCreatedByResponse());
        setInstanceGroup("brokers");
        this.id = String.valueOf(jsonObject.get("brokerID"));
        this.guid = String.valueOf(jsonObject.get("brokerGuid"));
        this.name = String.valueOf(jsonObject.get("brokerName"));
        this.description = String.valueOf(jsonObject.get("description"));
        this.dailyCapacity = String.valueOf(jsonObject.get("dailyCap"));
        this.status = String.valueOf(jsonObject.get("status"));
//        this.jsonObject = dataProvider.getInstanceDetails(instanceGroup, "brokerName", name);
    }

    @Override
    public BrokerTestData setNegativeData() {
        super.setNegativeData();
        if (DataHelper.getRandBoolean())
            this.name = RandomStringUtils.randomNumeric(1, 33);
        return this;
    }

    public String getBrokerType() {
        return brokerType;
    }

    public String getDailyCapacity() {
        return dailyCapacity;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "BrokerTestData{" +
                "name='" + name + '\'' +
                "description='" + description + '\'' +
                "brokerType='" + brokerType + '\'' +
                "dailyCapacity='" + dailyCapacity + '\'' +
                '}';
    }
}
