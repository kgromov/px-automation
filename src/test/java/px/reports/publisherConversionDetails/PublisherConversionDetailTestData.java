package px.reports.publisherConversionDetails;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import org.json.JSONArray;
import org.json.JSONObject;
import px.reports.ReportTestData;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kgr on 4/18/2017.
 */
public class PublisherConversionDetailTestData extends ReportTestData {
    private int startMonthOffset = super.startMonthOffset = 2;
    private int durationDays = super.durationDays = 3;
    // report data
    private JSONObject conversionObject;
    private String conversionID;
    private String transactionID;
    // details data
    private JSONArray conversionDetails;
    // aggregated map
    private Map<String, String> conversionDetailsMap;

    public PublisherConversionDetailTestData() {
        // report data - prerequisite
        setInstanceGroup("publisherconversions/report");
        setSorting("creationDate", "desc");
        setDateRanges();
        validateDateRangeBigData();
        setAllRowsByDateRange();
        setHeaders();
        // details data
        setInstanceGroup("publisherconversions/details");
        // choose any lead from array
        int leadIndex = DataHelper.getRandomInt(allRowsArray.length());
        log.info(String.format("Get '%d' conversion from total '%d'", leadIndex, allRowsArray.length()));
        this.conversionObject = allRowsArray.getJSONObject(leadIndex);
        // navigation data
        this.conversionID = String.valueOf(conversionObject.get("conversionID"));
        this.transactionID = String.valueOf(conversionObject.get("transactionID"));
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList("ConversionIdFilter", "FromPeriod", "ToPeriod"), Arrays.asList(conversionID, fromPeriod, toPeriod))
                .build().getRequestedURL();
        this.conversionDetails = new JSONArray(dataProvider.getDataAsString(requestedURL));
        // aggregate like key-value
        conversionDetailsMap = new HashMap<>();
        for (int i = 0; i < conversionDetails.length(); i++) {
            JSONObject object = conversionDetails.getJSONObject(i);
            if (object.has("field")) { // && object.has("default")
                String key = String.valueOf(object.get("field"));
                String value = object.has("default") ? String.valueOf(object.get("default")) : "";
                // mapping till there are no general mechanism
                value = value.equals("null") ? "" : value;
                // specific
                value = key.equals("adjustment") && value.equals("0") ? "No" : value;
                conversionDetailsMap.put(key, value);
            }
        }
        // remove data that should be hidden
        conversionDetailsMap.remove("offerID");
        conversionDetailsMap.remove("affiliateID");
        conversionDetailsMap.remove("conversionID");
        conversionDetailsMap.remove("offerUrlName");
        // for debug
        log.info(String.format("DEBUG\tjson length = '%d', map size = '%d'", conversionObject.length(), conversionDetailsMap.size()));
    }

    public String getConversionID() {
        return conversionID;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public Map<String, String> getConversionDetailsMap() {
        return conversionDetailsMap;
    }

    @Override
    public String toString() {
        return "PublisherConversionDetailTestData{" +
                "fromPeriod=" + fromPeriod +
                ", toPeriod=" + toPeriod +
                ", conversionID='" + conversionID + '\'' +
                ", transactionID='" + transactionID + '\'' +
                ", conversionObject='" + conversionObject + '\'' +
                ", conversionDetailsMap=" + conversionDetailsMap +
                '}';
    }
}