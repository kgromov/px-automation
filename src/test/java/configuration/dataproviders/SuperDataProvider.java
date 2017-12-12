package configuration.dataproviders;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

import static config.Config.user;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kgr on 11/7/2016.
 */
public class SuperDataProvider {
    protected final static LxpDataProvider dataProvider = new LxpDataProvider();

    static JSONObject getInstanceDetails(String group, String filterKey, String filterValue) {
        /*try {*/
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + group)
                .filter(filterKey, filterValue)
                .sort(filterKey, "asc")
                .build().getRequestedURL();
        return dataProvider.getAnyJSONObjectFromArray(requestedURL, filterKey, filterValue);
        /*} catch (IndexOutOfBoundsException | JSONException e) {
            String requestedURL = new RequestedURL.Builder()
                    .withRelativeURL("/api/" + group)
                    .filterByKey(filterKey)
                    .sort(filterKey, "asc")
                    .build().getRequestedURL();
            return dataProvider.getAnyJSONObjectFromArray(requestedURL);
        }*/
    }

    static JSONObject getInstanceDetailsWithDate(String group, String filterKey, String filterValue) {
        String period = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, new Date());
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + group)
                .filter(Arrays.asList(filterKey, "FromPeriod", "ToPeriod"), Arrays.asList(filterValue, period, period))
                .sort(filterKey, "asc")
                .build().getRequestedURL();
        return dataProvider.getAnyJSONObjectFromArray(requestedURL, filterKey, filterValue);
    }

    static JSONObject getInstanceContainsValue(JSONArray jsonArray, String filterKey, String filterValue) {
        return JSONWrapper.toList(jsonArray).stream().filter(row -> row.has(filterKey)
                && String.valueOf(row.get(filterKey)).contains(filterValue)).findAny().orElse(null);
    }

    static void checkBuyerUnderBuyerUser() {
        try {
            assertThat(String.format("There is 1 buyer under buyer\tuser: '%s'", user),
                    dataProvider.getCreatedInstancesData("buyers").size(), equalTo(1));
        } catch (AssertionError e) {
            throw new TestDataException(e.getMessage());
        }
    }
}