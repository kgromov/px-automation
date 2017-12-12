package px.objects.leads;

import org.json.JSONObject;

/**
 * Created by kgr on 10/17/2017.
 */
public abstract class LeadObject {
    private JSONObject jsonObject;

    public LeadObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObject toJSON() {
        return jsonObject;
    }
}