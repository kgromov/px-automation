package px.objects.subIds;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import px.reports.ReportTestData;

import java.math.BigDecimal;
import java.util.List;

import static px.reports.dto.FieldFormatObject.PERCENTAGE_FORMAT;

/**
 * Created by kgr on 7/14/2017.
 */
public class SubIdsPreviewTestData extends ReportTestData {
    private int startMonthOffset = super.startMonthOffset = 0;
    private JSONObject publisherObject;
    private ObjectIdentityData publisher;
    private ObjectIdentityData subId;
    private JSONArray subIDs;

    static {
        // missed fields formats
        missedHeadersMetricsMap.put("margin", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("escoreCheckPercentage", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("incLRinMarginPerc", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("fraudCheckPercentage", PERCENTAGE_FORMAT);
        // data mapping
        dataMap.put("0", "No");
        dataMap.put("1", "Yes");
        dataMapping.add(new ValuesMapper("addUpsellToBalance", dataMap));
        dataMapping.add(new ValuesMapper("fixedPricing", dataMap));
    }

    public SubIdsPreviewTestData() {
        super(false);
        // first - get publisher with subId(s)
        setInstanceGroup("publisherperformance/report");
        setSorting("spending", "desc");
        setDateRanges();
        setAllRowsByDateRange();
        // subID fields in overview
        setInstanceGroup("publisherInstances");
        setHeaders();
        // get any publisher with 'leadSubmits' > 1 (like criteria for subId presence)
        // validation if there any available data
        if (allRowsArray.length() == 0)
            throw new TestDataException(String.format("There is no data in date range [%s - %s]", fromPeriod, toPeriod));
        List<ObjectIdentityData> publishers = dataProvider.getCreatedInstancesData("publishers");
        subIDs = new JSONArray();
        for (int i = 1; i < allRowsArray.length(); i++) {
            JSONObject object = allRowsArray.getJSONObject(i);
            // another condition - automation created only
            if (String.valueOf(object.get("publisherName")).contains("Publisher Name ")) {
                try {
                    int leadSubmits = new BigDecimal(String.valueOf(object.get("leadSubmits"))).intValue();
                    if (leadSubmits > 1) {
                        this.publisherObject = object;
                        // cause success criteria does not work
                        String publisherName = String.valueOf(publisherObject.get("publisherName"));
                        this.publisher = ObjectIdentityData.getObjectFromListByName(publishers, publisherName);
                        // all subIDs in overview table by certain publisher
                        String requestURL = new RequestedURL.Builder()
                                .withRelativeURL("api/publisherInstances")
                                .filter("ParentPublisherGuid", publisher.getGuid())
                                .sort("PublisherInstanceName", "asc")
                                .build().getRequestedURL();
                        this.subIDs = dataProvider.getDataAsJSONArray(requestURL);
                        if (subIDs.length() > 0) break;
                    }
                } catch (NumberFormatException e) {
                    throw new TestDataException("Unable to parse 'leadSubmits' value to int\tJSON = " + object, e);
                }
            }
        }
        // check if find
        if (publisherObject == null)
            throw new TestDataException("No rows with LeadSubmits > 1 => no Publishers with subID");
        /*String publisherName = String.valueOf(publisherObject.get("publisherName"));
        this.publisher = ObjectIdentityData.getObjectFromListByName(publishers, publisherName);
        // all subIDs in overview table by certain publisher
        String requestURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherInstances")
                .filter("ParentPublisherGuid", publisher.getGuid())
                .sort("PublisherInstanceName", "asc")
                .build().getRequestedURL();
        this.subIDs = dataProvider.getDataAsJSONArray(requestURL);*/
        // select any subId
        if (subIDs.length() < 1)
            throw new TestDataException(String.format("No subIDs for publisher = '%s', " +
                    "but success criteria passed - LeadSubmits = '%s'", publisher, publisherObject.get("leadSubmits")));
        int subIdIndex = DataHelper.getRandomInt(subIDs.length());
        log.info(String.format("Get '%d' subID from total '%d'", subIdIndex, subIDs.length()));
        this.subId = new ObjectIdentityData(subIDs.getJSONObject(subIdIndex));
    }

    public JSONObject getPublisherObject() {
        return publisherObject;
    }

    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getSubId() {
        return subId;
    }

    public JSONArray getSubIDs() {
        return subIDs;
    }

    @Override
    public String toString() {
        return "SubIdsPreviewTestData{" +
                "publisherObject=" + publisherObject +
                ", publisher=" + publisher +
                ", subIds=" + ObjectIdentityData.getObjectsByJSONArray(subIDs) +
                ", subId=" + subId +
                '}';
    }
}
