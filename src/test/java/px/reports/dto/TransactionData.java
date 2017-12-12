package px.reports.dto;

import org.json.JSONObject;

/**
 * Created by kgr on 6/6/2017.
 */
public interface TransactionData {

    String getRequestData();

    String getResponseData();

    void setData(JSONObject jsonObject);
}
