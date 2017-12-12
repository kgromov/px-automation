package dto;

import org.json.JSONArray;

import java.util.Map;

/**
 * Created by kgr on 2/7/2017.
 */

/**
 *  use for to gather responses in parallel
 */
public class Responses {
    private Map<String, JSONArray> responseByRequestMap;
    private Map<String, String> responseByRequestMap2;

  /*  public Responses(Map<String, JSONArray> responseByRequestMap) {
        this.responseByRequestMap = responseByRequestMap;
    }*/

    public Responses(Map<String, String> responseByRequestMap2) {
        this.responseByRequestMap2 = responseByRequestMap2;
    }

    public JSONArray getResponseByRequestedURLAsJSONArray(String requestedURL) {
        return responseByRequestMap.get(requestedURL);
    }

    public String getResponseByRequestedURLAsString(String requestedURL) {
        return responseByRequestMap2.get(requestedURL);
    }
}
