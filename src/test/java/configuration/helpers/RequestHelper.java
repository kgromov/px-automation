package configuration.helpers;

import config.Config;
import dto.TestDataException;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static config.Config.password;
import static config.Config.user;

/**
 * Created by kgr on 10/18/2016.
 */
public class RequestHelper {
    private static final Logger log = Logger.getLogger(RequestHelper.class);
    private OkHttpClient client;

    // save token once as it is too hard for lxp
    public RequestHelper() {
        this.client = HtppClient.getHtppClient(100, 60, 60);
    }

    // ============================ Builders ============================

    /**
     * @param map - representation of HTTP body in key-value form
     * @return Request HTTP body
     */
    private RequestBody buildBody(Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    /**
     * @param headersMap - representation of HTTP methods in key-value form
     * @param url        - requested url
     * @return - Decorated Request with proper headers
     */
    private Request buildRequest(Map<String, String> headersMap, String url) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        for (Map.Entry<String, String> entry : headersMap.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    /**
     * @param headersMap - representation of HTTP methods in key-value form
     * @param url        - requested url
     * @param methodType - HTTP method type
     * @return - Decorated Request with proper headers
     */
    public Request buildRequest(Map<String, String> headersMap, String url, HttpMethodsEnum methodType) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.method(methodType.name(), buildBody(headersMap));
        return builder.build();
    }

    // ================================ Response ================================

    /**
     * @param request - requested url with all headers, body etc
     * @return - response as string
     */
    private String getResponseText(Request request) {
        long start = System.currentTimeMillis();
        log.info(String.format("Getting response by requested url %s", request));
        String response = null;
        try {
            response = client.newCall(request).execute().body().string();
            return response;
        } catch (IOException e) {
            log.error(String.format("Unable to get response from request\n%s\ncause\n%s", request.toString(), e.getMessage()));
            throw new TestDataException(String.format("Unable to get response by request\n%s\ncause\n%s", request.toString(), e.getMessage()));
        } finally {
            log.info(String.format("Time to get response = %d\nRequest:\t%s", (System.currentTimeMillis() - start), request.toString()));
            log.info("Response\t" + response);
        }
    }

    /**
     * @param requestedURL - requested url as string
     * @return - response as string
     */
    public String getResponseText(String requestedURL) {
        return getResponseText(buildRequest(getSecurityHeaders(), requestedURL));
    }

    public String getResponseTextByHTTPMethod(String url, HttpMethodsEnum methodsEnum) {
        return getResponseByURLConnection(url, getSecurityHeaders(), methodsEnum);
    }

    /**
     * @param url - requested url
     * @param map - HTTP body or headers in form of map
     * @return response by url as string
     * <b>Usage:</b> Lower level implementation cause OkHTTP could not handle OPTION method type
     */
    private String getResponseByURLConnection(String url, Map<String, String> map) {
        long start = System.currentTimeMillis();
        StringBuilder response = new StringBuilder();
        try {
            String inputLine;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setInstanceFollowRedirects(false);
            HttpURLConnection.setFollowRedirects(false);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            log.info("getResponseByURLConnection for url '" + url + "'= " + (System.currentTimeMillis() - start));
            return response.toString();
        } catch (IOException e) {
            throw new TestDataException(String.format("Unable to get response by url\t%s\ncause\n%s", url, e.getMessage()));
        } finally {
            log.info(String.format("getResponseByURLConnection for url '%s', time = '%d'", url, (System.currentTimeMillis() - start)));
            log.info("Response\t" + response);
        }
    }

    /**
     * @param url        - requested url
     * @param map        - HTTP body or headers in form of map
     * @param methodType - HTTP method from HttpMethodsEnum
     * @return response by url as string
     * <b>Usage:</b> Lower level implementation cause OkHTTP could not handle OPTION method type
     */
    private String getResponseByURLConnection(String url, Map<String, String> map, HttpMethodsEnum methodType) {
        long start = System.currentTimeMillis();
        StringBuilder response = new StringBuilder();
        try {
            String inputLine;
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setInstanceFollowRedirects(false);
            HttpURLConnection.setFollowRedirects(false);
            for (Map.Entry<String, String> entry : map.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }
            connection.setRequestMethod(methodType.name());
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (IOException e) {
            throw new TestDataException(String.format("Unable to get response by url\t%s\ncause\n%s", url, e.getMessage()));
        } finally {
            log.info(String.format("getResponseByURLConnection for url '%s' http method '%s', time = '%d'",
                    url, methodType.name(), (System.currentTimeMillis() - start)));
            log.info("Response\t" + response);
        }
    }

    // ==================================== PX Specific ====================================

    /**
     * @return token for current security session
     * LXP specific low level security, used as security header value to establish connection
     */
    String getSecurityToken() {
        Map<String, String> map = new HashMap<>();
        map.put("username", user);
        map.put("password", password);
        map.put("grant_type", "password");
        try {
            Request request = new Request.Builder()
                    .url(Config.testUrl + "oauth/token")
                    .post(buildBody(map))
                    .build();
            JSONObject jsonObject = new JSONObject(getResponseText(request));
            return String.valueOf(jsonObject.get("access_token"));
        } catch (JSONException | NullPointerException e) {
            throw new TestDataException("Unable to get security token cause of\n" + e.getMessage());
        }
    }

    /**
     * @param useXMLContentType - use xml (if true) or default json content type
     * @return HTTP header with security token to transfer in get data requests
     */
    protected Map<String, String> getSecurityHeaders(boolean useXMLContentType) {
        Map<String, String> map = new HashMap<>();
        map.put("Authorization", "Bearer " + SecurityToken.getToken());
//        if (useXMLContentType) map.put("Accept", "application/xml");
        if (useXMLContentType) map.put("Accept", ContentType.APPLICATION_XML.getMimeType());
        return map;
    }

    /**
     * @return HTTP header with security token to transfer in get data requests
     * Use as default json content tupe
     */
    protected Map<String, String> getSecurityHeaders() {
        return getSecurityHeaders(false);
    }

    /**
     * @param enumName          - name of objects enum
     * @param useXMLContentType use xml (if true) or default json content type
     * @return PX Object identity data (usually description and index)
     * <b>Usage:</b> use only to update test data files by enums
     */
    public String getEnumTestDataByName(String enumName, boolean useXMLContentType) {
        return getResponseText(buildRequest(getSecurityHeaders(useXMLContentType),
                Config.testUrl + "api/enum?removeNone=true&enumNames=" + enumName));
    }

    public static String sendPostRequest(String requestUrl, String payload) {
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json,text/html");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", SecurityToken.getToken());
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            writer.write(payload);
            writer.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
            connection.disconnect();
            return jsonString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}