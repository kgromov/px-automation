package px.objects.leads;

import org.json.JSONArray;

/**
 * Created by kgr on 10/4/2017.
 */
public interface Leadable {

    void setLead();

    void setLead(JSONArray allRowsArray);

//    void setLeads();

//    void setLeads(JSONArray allRowsArray);

    LeadObject getLead();

//    Set<LeadObject> getLeadResponses() ;
}