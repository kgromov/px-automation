package px.funtional.crud;

import config.DashboardMenuEnum;
import configuration.helpers.HtppClient;
import configuration.helpers.RequestHelper;
import configuration.helpers.SecurityToken;
import okhttp3.*;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import utils.CustomMatcher;
import utils.SoftAssertionHamcrest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static config.Config.userToString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kgr on 6/29/2017.
 */
public class GrantsVerification {
    private static final Logger log = Logger.getLogger(RequestHelper.class);
    private static final OkHttpClient client = HtppClient.getHtppClient(60, 60, 60);
    private List<DashboardMenuEnum> allowedObjects = DashboardMenuEnum.getMenuItems();

    /* Only campaign has normal create response structure, other are different:
     * 1) broker - {"success":true,"data":false}
     * 2) buyer, publisher - {"success":true,"data":<GUID>}
     * 3) offer - {"success":true,"data":485}
     */
    public static void checkCreateRequest(RequestData requestData, boolean isAllowed) {
        log.info(String.format("Check ability to create object by url '%s' for user '%s'", requestData.createURL(), userToString()));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Response response = getResponse(requestData.createURL(), requestData.asJSON().toString());
        String responseText = getResponseText(response);
        log.info("Response text\t" + responseText);
        // check status of CRUD response
        hamcrest.assertThat("Check response status of create post: " + requestData.createURL(), String.valueOf(response.networkResponse().code()),
                CustomMatcher.matchesPattern(isAllowed ? "20\\d" : "40\\d"));
        // check that object is created with new id
        if (isAllowed) {
            try {
                JSONObject object = new JSONObject(responseText);
                try {
                    assertThat("Check response contains newly created object id", object.has(requestData.getIdKey()));
                    hamcrest.assertThat("Check response for identifier in proper format",
                            String.valueOf(object.get(requestData.getIdKey())),
                            CustomMatcher.matchesPattern("([a-z0-9-]{32,36})|(\\d{3,})"));
                } catch (AssertionError e) {
                    hamcrest.assertThat(String.format("Response '%s' does not contain identifier of created instance", object), false);
                }
            } catch (JSONException e2) {
                hamcrest.assertThat("Unable to parse response text\t" + responseText, false);
            }
        }
        hamcrest.assertAll();
    }

    public static void checkUpdateRequest(RequestData requestData, boolean isAllowed) {
        log.info(String.format("Check ability to update object by url '%s' for user '%s'", requestData.updateURL(), userToString()));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        boolean forbiddenBecameAllowed = false;
//        Map<String, String> objectDetails = isAllowed ? requestData.asMap(requestData.getURL()) : null;
        // if not allowed - get nevertheless
        Map<String, String> objectDetails = getObjectDetails(requestData, requestData.getURL());
        Response response = getResponse(requestData.updateURL(), requestData.asJSON().toString());
        String responseText = getResponseText(response);
        log.info("Response text\t" + responseText);
        // check status of CRUD response
        try {
//            hamcrest.assertThat("Check response status of update post: " + requestData.updateURL(),
            assertThat("Check response status of update post: " + requestData.updateURL(),
                    String.valueOf(response.networkResponse().code()),
                    CustomMatcher.matchesPattern(isAllowed ? "20\\d" : "40\\d"));
        } catch (AssertionError e) {
            forbiddenBecameAllowed = true;
        }
        if (isAllowed || forbiddenBecameAllowed) {
            // check fields updated
            Set<String> allowedToUpdateFields = requestData.allowedFieldsToUpdate();
            // get updated object details
            Map<String, String> updatedObjectDetails = requestData.asMap(requestData.getURL());
            // check that fields have been updated/not updated
            for (Map.Entry<String, String> entry : objectDetails.entrySet()) {
                String fieldKey = entry.getKey();
                String originalValue = entry.getValue();
                String updatedValue = updatedObjectDetails.get(entry.getKey());
                boolean isValueUnique = requestData.asJSON().has(fieldKey) && !requestData.asJSON().get(fieldKey).equals(originalValue);
                hamcrest.assertThat(String.format("Field '%s' change value after update request", fieldKey),
                        updatedValue, isValueUnique && allowedToUpdateFields.contains(fieldKey)
                                ? not(equalTo(originalValue)) : equalTo(originalValue));
            }
        }
        hamcrest.assertAll();
    }

    public static void checkUpdateRequest(RequestData requestData, JSONObject jsonObject, String updateURL, String getURL, boolean isAllowed) {
        log.info(String.format("Check ability to update object by url '%s' for user '%s'", updateURL, userToString()));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        boolean forbiddenBecameAllowed = false;
//        Map<String, String> objectDetails = isAllowed ? requestData.asMap(getURL) : null;
        // if not allowed - get nevertheless
        Map<String, String> objectDetails = getObjectDetails(requestData, getURL);
        Response response = getResponse(updateURL, jsonObject.toString());
        String responseText = getResponseText(response);
        log.info("Response text\t" + responseText);
        // check status of CRUD response
        try {
//        hamcrest.assertThat("Check response status of update post: " + updateURL,
            assertThat("Check response status of update post: " + updateURL,
                    String.valueOf(response.networkResponse().code()),
                    CustomMatcher.matchesPattern(isAllowed ? "20\\d" : "40\\d"));
        } catch (AssertionError e) {
            forbiddenBecameAllowed = true;
        }
        if (isAllowed || forbiddenBecameAllowed) {
            // check fields updated
            Set<String> allowedToUpdateFields = requestData.allowedFieldsToUpdate(jsonObject);
            // get updated object details
            Map<String, String> updatedObjectDetails = requestData.asMap(getURL);
            // check that fields have been updated/not updated
            for (Map.Entry<String, String> entry : objectDetails.entrySet()) {
                String fieldKey = entry.getKey();
                String originalValue = entry.getValue();
                String updatedValue = updatedObjectDetails.get(entry.getKey());
                boolean isValueUnique = jsonObject.has(fieldKey) && !jsonObject.get(fieldKey).equals(originalValue);
                hamcrest.assertThat(String.format("Field '%s' change value after update request", fieldKey),
                        updatedValue, isValueUnique && allowedToUpdateFields.contains(fieldKey)
                                ? not(equalTo(originalValue)) : equalTo(originalValue));
            }
        }
        hamcrest.assertAll();
    }

    private static Response getResponse(String requestedURL, String json) {
        Response response = null;
        try {
            log.info(String.format("Execute crud post request by url: %s, request payload json\n%s", requestedURL, json));
            MediaType mediaType = MediaType.parse(ContentType.APPLICATION_JSON.getMimeType());
            RequestBody body = RequestBody.create(mediaType, json);
            Request request = new Request.Builder()
                    .url(requestedURL)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + SecurityToken.getToken())
                    .build();
            response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            throw new RuntimeException(String.format("Unable to execute request by url = '%s' and request data = '%s'" +
                    "\n cause of '%s'", requestedURL, json, e.getMessage()));
        } /*finally {
            try {
                log.info("Response text\t" + (response != null ? response.body().string() : ""));
            } catch (IOException ignored) {
            }
        }*/
    }

    private static String getResponseText(Response response) {
        try {
            return response != null ? response.body().string() : "";
        } catch (IOException | IllegalStateException e) {
            throw new RuntimeException("Unable to get response text\ncause" + e.getMessage(), e);
        }
    }

    private static Map<String, String> getObjectDetails(RequestData requestData, String getURL) {
        try {
            return requestData.asMap(getURL);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
