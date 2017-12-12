package px.objects.users;

import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import px.reports.ReportTestData;
import px.reports.dto.FieldFormatObject;

import java.util.ArrayList;
import java.util.List;

import static pages.groups.Pagination.MAX_ROWS_LIMIT;
import static px.reports.dto.FieldFormatObject.DATE_TIME_FORMAT;

/**
 * Created by kgr on 8/15/2017.
 */
// TODO: -> later on implement MetaData
public class UsersData extends ReportTestData {
    private ObjectIdentityData parent;

    static {
        missedHeadersMetricsMap.put("loginDate", DATE_TIME_FORMAT);
    }

    public UsersData() {
        setInstanceGroup("users");
        // set fields
        setHeaders();
        // users in table
        setAllRowsByDateRange();
    }

    UsersData(ObjectIdentityData parent) {
        this.parent = parent;
    }

    public List<FieldFormatObject> getFields() {
        return fields;
    }

    public ObjectIdentityData getParent() {
        return parent;
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/users")
                .withEmptyFilter()
                .sort("description", "asc")
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        // in case there are too many records
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

    public static final class BuyerUsersData extends UsersData {

        public BuyerUsersData(ObjectIdentityData parent) {
            super(parent);
            setInstanceGroup("buyerusers");
            // set fields
            String requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/buyerusers")
                    .build().getRequestedURL();
            JSONArray headers = new JSONWrapper(dataProvider.getDataAsString(requestURL, HttpMethodsEnum.OPTIONS)).getJSONArray();
            this.fields = new ArrayList<>(headers.length());
            for (int i = 0; i < headers.length(); i++) {
                fields.add(new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i));
            }
            // users in table
            requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/buyerusers")
                    .withParams("buyerGuid", parent.getGuid())
                    .withEmptyFilter()
                    .sort("description", "asc")
                    .build().getRequestedURL();
            this.allRowsArray = dataProvider.getDataAsJSONArray(requestURL);
            this.totalCount = dataProvider.getCurrentTotal();
        }
    }

    public static final class PublisherUsersData extends UsersData {

        public PublisherUsersData(ObjectIdentityData parent) {
            super(parent);
            // set fields
            String requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publisherusers")
                    .build().getRequestedURL();
            JSONArray headers = new JSONWrapper(dataProvider.getDataAsString(requestURL, HttpMethodsEnum.OPTIONS)).getJSONArray();
            this.fields = new ArrayList<>(headers.length());
            for (int i = 0; i < headers.length(); i++) {
                fields.add(new FieldFormatObject(headers.getJSONObject(i), missedHeadersMetricsMap, i));
            }
            // users in table
            requestURL = new RequestedURL.Builder()
                    .withRelativeURL("api/publisherusers")
                    .withParams("publisherId", parent.getGuid())
                    .withEmptyFilter()
                    .sort("description", "asc")
                    .build().getRequestedURL();
            this.allRowsArray = dataProvider.getDataAsJSONArray(requestURL);
            this.totalCount = dataProvider.getCurrentTotal();
        }
    }
}