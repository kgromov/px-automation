package px.reports.dto;

import org.json.JSONObject;

/**
 * Created by kgr on 6/1/2017.
 */
public interface InboundData {
    String getData();

    String getData(JSONObject jsonObject);
}