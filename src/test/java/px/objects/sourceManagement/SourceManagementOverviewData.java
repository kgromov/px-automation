package px.objects.sourceManagement;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import configuration.helpers.ValuesMapper;
import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.reports.ReportTestData;

import static pages.groups.Pagination.MAX_ROWS_LIMIT;

/**
 * Created by konstantin on 18.11.2017.
 */
public class SourceManagementOverviewData extends ReportTestData {
    private ObjectIdentityData campaign;

    {
        dataMap.put("Overruled", "Prioritized");
        dataMapping.add(new ValuesMapper("status", dataMap));
    }

    public SourceManagementOverviewData(ObjectIdentityData campaign) {
        this.campaign = campaign;
        setInstanceGroup("sources");
        setSorting("status", "desc");
        setAllRowsByDateRange();
        setHeaders();
        fields.forEach(field -> field.setIndex(field.getIndex() + 1));
        // update rows according to:
        // publisherName -> id -  name, campaignName -> id - name
        for (int i = 0; i < totalCount; i++) {
            JSONObject object = allRowsArray.getJSONObject(i);
            String campaignId = String.valueOf(object.get("campaignId"));
            String publisherId = String.valueOf(object.get("publisherId"));
            String subId = String.valueOf(object.get("subId"));
            String sourceId = campaignId + "_" + publisherId + (subId.equals("0") ? "" : "_" + subId.substring(subId.length() - 3));
            object.put("publisherName", publisherId + " - " + String.valueOf(object.get("publisherName")));
            object.put("campaignName", campaignId + " - " + String.valueOf(object.get("campaignName")));
            object.put("sourceId", sourceId);
        }
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter("BuyerInstanceGuid", campaign.getGuid())
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        // in case there are too many records
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

    public ObjectIdentityData getCampaign() {
        return campaign;
    }

    @Override
    public String toString() {
        return "SourceManagementOverviewData{" +
                "campaign=" + campaign +
                '}';
    }
}
