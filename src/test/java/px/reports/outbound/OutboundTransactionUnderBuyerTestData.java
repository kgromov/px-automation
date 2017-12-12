package px.reports.outbound;

import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import px.reports.UserReports;
import px.reports.dto.AbstractFiltersResetData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kgr on 12/21/2016.
 */
public class OutboundTransactionUnderBuyerTestData extends OutboundTransactionTestData implements UserReports {

    public OutboundTransactionUnderBuyerTestData() {
        super(false);
        this.isInstances = true;
        setInstanceGroup("outboundtransactions/report");
        setSorting("postDate", "desc");
        setDateRanges();
        validateDateRangeBigData();
        setAllRowsByDateRange();
        setHeaders();
        if (isInstances) {
            setFiltersData();
            // rows in table by campaign IDs
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(campaigns), "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByBuyerCampaignsID = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by result codes
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_RESULT_CODE_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(resultCodeList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByResultCodes = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by post types
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(POST_TYPE_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(postTypeList, "|"), fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByPostTypes = dataProvider.getDataAsJSONArray(requestedURL);
            // rows in table by vertical
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(VERTICAL_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(vertical, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByVertical = dataProvider.getDataAsJSONArray(requestedURL);
            // all filters
            requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .filter(Arrays.asList(BUYER_CAMPAIGN_FILTER, BUYER_RESULT_CODE_FILTER, POST_TYPE_FILTER, VERTICAL_FILTER, "PostDate", "PostDate2"),
                            Arrays.asList(StringUtils.join(ObjectIdentityData.getAllIDs(campaigns), "|"),
                                    StringUtils.join(resultCodeList, "|"), StringUtils.join(postTypeList, "|"), vertical, fromPeriod, toPeriod))
                    .sort(sortBy, sortHow)
                    .build().getRequestedURL();
            this.itemsByAllFilters = dataProvider.getDataAsJSONArray(requestedURL);
            // set filter values map
            this.resetData = new OutboundTransactionUnderBuyerTestData.FiltersResetData();
            resetData.setFilterValuesMap();
            // set inbound data not create each time
            this.transactionData = new OutboundData();
            checkCampaignsTo1Buyer(campaigns);
        }
    }

    @Override
    public List<String> filters() {
        return Arrays.asList(BUYER_CAMPAIGN_FILTER, BUYER_RESULT_CODE_FILTER, POST_TYPE_FILTER, VERTICAL_FILTER);
    }

    @Override
    public String toString() {
        String instanceDetails = isInstances ?
                "campaigns=" + campaigns.stream().map(ObjectIdentityData::toString).collect(Collectors.joining(", ")) +
                        ", resultCodeLiteralsList=" + resultCodeLiteralsList +
                        ", resultCodeList=" + resultCodeList +
                        ", postTypeList=" + postTypeList +
                        ", vertical=" + vertical : "";
        return "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", totalCount='" + totalCount + '\'' +
                "OutboundTransactionTestData{" +
                instanceDetails +
                '}';
    }

    private class FiltersResetData extends AbstractFiltersResetData {
        FiltersResetData() {
            this._instanceGroup = instanceGroup;
            this._url = new RequestedURL.Builder()
                    .withRelativeURL("api/" + instanceGroup)
                    .build().getRequestedURL().replace("?", "&");
            this._sortBy = sortBy;
            this._sortBy = sortBy;
            this._sortHow = sortHow;
            this._fromPeriod = fromPeriod;
            this._toPeriod = toPeriod;
            this._fromPeriodKey = "PostDate";
            this._toPeriodKey = "PostDate2";
        }

        @Override
        public void setFilterValuesMap() {
            // for requests
            this.filterValuesMap = new HashMap<>();
            filterValuesMap.put(BUYER_CAMPAIGN_FILTER, StringUtils.join(ObjectIdentityData.getAllIDs(campaigns), "|"));
            filterValuesMap.put(BUYER_RESULT_CODE_FILTER, StringUtils.join(resultCodeList, "|"));
            filterValuesMap.put(POST_TYPE_FILTER, StringUtils.join(postTypeList, "|"));
            filterValuesMap.put(VERTICAL_FILTER, vertical);
            // for filtered value in UI
            this.filterLabelValuesMap = new HashMap<>();
            filterLabelValuesMap.put(BUYER_CAMPAIGN_FILTER, campaigns.stream().map(p -> p.getId() +
                    "-" + p.getName()).collect(Collectors.joining(", ")));
            filterLabelValuesMap.put(BUYER_RESULT_CODE_FILTER, resultCodeLiteralsList.toString());
            filterLabelValuesMap.put(POST_TYPE_FILTER, postTypeList.toString());
            filterLabelValuesMap.put(VERTICAL_FILTER, vertical);
        }

        @Override
        public JSONArray getItemsByFiltersCombination() {
            filterValuesMap.remove(filterReset);
            if (filterValuesMap.size() > 1)
                return itemsByFiltersCombination;
            else if (filterValuesMap.size() == 0)
                return allRowsArray;
            // last iteration
            switch (new ArrayList<>(filterValuesMap.keySet()).get(0)) {
                case BUYER_CAMPAIGN_FILTER:
                    return itemsByBuyerCampaignsID;
                case BUYER_RESULT_CODE_FILTER:
                    return itemsByResultCodes;
                case POST_TYPE_FILTER:
                    return itemsByPostTypes;
                case VERTICAL_FILTER:
                    return itemsByVertical;
            }
            return null;
        }
    }
}