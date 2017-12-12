package tests.functional.lead;

import config.Config;
import configuration.dataproviders.LeadDataProvider;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import px.funtional.lead.LeadDataUtils;
import px.reports.leads.LeadsReportTestData;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by kgr on 11/9/2017.
 */
public class LeadDataVerificationTest {
    @BeforeTest
    public void init() {
        Config.setAdminUser();
    }

    @Test(dataProvider = "checkLeadXmlData", dataProviderClass = LeadDataProvider.class)
    public void checkCorruptedXML(String vertical, LeadsReportTestData.ResponseObject lead) {
        LxpDataProvider dataProvider = new LxpDataProvider();
        // inbound  data
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/leadpreview/inbound")
                .withParams("leadGuid", lead.getLeadGuid())
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONWrapper(dataProvider.getDataAsString(requestedURL)).getJSONArray();
        JSONObject xmlBody = JSONWrapper.toList(jsonArray).stream().filter(field ->
                field.has("field") && field.getString("field").equals("XmlBody"))
                .findFirst().orElse(null);
        assertThat(String.format("Lead %s has inbound data with XmlBody", lead), xmlBody != null && xmlBody.has("default"));
        String xmlData = xmlBody != null ? xmlBody.getString("default") : null;
        if (!LeadDataUtils.isHttpPost(xmlData)) {
            try {
                LeadDataUtils.stringToXml(xmlData);
            } catch (Exception e) {
                throw new AssertionError(String.format("Corrupted xml for vertical '%s', xml = \n%s", vertical, xmlData), e);
            }
        }
    }

}
