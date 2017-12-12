package px.objects.sourceManagement;

import configuration.dataproviders.CampaignDataProvider;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.*;
import elements.HelperSingleton;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.subIds.SubIdsPreviewTestData;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static configuration.dataproviders.CampaignDataProvider.PING_POST_LEADS_CAMPAIGN;
import static configuration.helpers.DataHelper.*;

/**
 * Created by konstantin on 16.11.2017.
 */
public class SourceManagementTestData implements TestData {
    private static Logger log = Logger.getLogger(SourceManagementTestData.class);
    private final LxpDataProvider dataProvider = new LxpDataProvider();
    private boolean isPositive;
    // data
    private ObjectIdentityData publisher;
    private ObjectIdentityData campaign;        // use its campaignId as first part of sourceId
    private ObjectIdentityData cdsCampaign;
    private List<ObjectIdentityData> subIDs;
    private ObjectIdentityData subID;
    private String leadId;
    private String leadGUID;
    private String vertical;
    private String leadData;
    private String sourceID;
    // auxiliary
    private JSONArray sources;
    private boolean hasSubId;
    // test data
    private String campaignStatus;
    private String status;
    private boolean isCherryPicked;
    // static data
    public static String BLOCKED_SOURCE = "Publisher Blocked";
    private static final List<String> CAMPAIGN_STATUSES = Arrays.asList("On", "Off");
    private static final List<String> STATUSES = Arrays.asList("Block", "Prioritize");
    private static final Predicate<JSONObject> PUBLISHER_FILTER = item ->
            item.has("status") && item.getString("status").equals("Published");
    // sources file
    public static final String SOURCES_FILE = "\\\\37.97.221.171\\g$\\demo_02\\virtualstore\\allpublishertobuyerexceptions.csv";

    public SourceManagementTestData() {
        // prerequisite the same as filter management
        JSONObject object = CampaignDataProvider.getCampaignByType(PING_POST_LEADS_CAMPAIGN);
        this.campaign = new ObjectIdentityData(String.valueOf(object.get("buyerInstanceID")),
                String.valueOf(object.get("buyerInstanceName")),
                String.valueOf(object.get("buyerInstanceGuid"))
        );
        // publisher that has subId
        SubIdsPreviewTestData previewTestData = new SubIdsPreviewTestData();
        this.publisher = previewTestData.getPublisher();
        // probably add filter by subId status 'Published'
        List<JSONObject> publishedSubIds = JSONWrapper.toList(previewTestData.getSubIDs())
                .stream().filter(PUBLISHER_FILTER).collect(Collectors.toList());
        this.subID = !publishedSubIds.isEmpty() ? new ObjectIdentityData(publishedSubIds.get(getRandomInt(publishedSubIds.size()))) : null;
        this.hasSubId = subID != null;
        // test data
        this.campaignStatus = getRandomValueFromList(CAMPAIGN_STATUSES);
        this.status = getRandomValueFromList(STATUSES);
        this.isCherryPicked = isTurnOnCherryPick() && getRandBoolean();
    }

    public SourceManagementTestData(JSONObject outboundTransaction, JSONObject inboundTransaction, String leadData) {
        // lead data
        this.leadData = leadData;
        this.leadId = String.valueOf(outboundTransaction.get("leadId"));
        this.leadGUID = outboundTransaction.getString("leadGuid");
        // other data
        this.vertical = inboundTransaction.getString("vertical");
        // set cds campaign
        this.cdsCampaign = new ObjectIdentityData(String.valueOf(outboundTransaction.get("campaignId")),
                String.valueOf(outboundTransaction.get("campaignName")), null);
        // set buyer campaign
        this.campaign = new ObjectIdentityData(String.valueOf(outboundTransaction.get("buyerId")),
                String.valueOf(outboundTransaction.get("buyerName")), null);
        // publisher
        String requestURL = new RequestedURL.Builder()
                .withRelativeURL("api/publishers")
                .filter("publisherId", String.valueOf(inboundTransaction.get("publisherId")))
                .sort("publisherName", "asc")
                .build().getRequestedURL();
        this.publisher = new ObjectIdentityData(dataProvider.getDataAsJSON(requestURL));
        // subId by id from transaction
        requestURL = new RequestedURL.Builder()
                .withRelativeURL("api/publisherInstances")
                .filter(Arrays.asList("ParentPublisherGuid", "status"),
                        Arrays.asList(publisher.getGuid(), "Published"))
                .sort("PublisherInstanceName", "asc")
                .build().getRequestedURL();
        this.subIDs = ObjectIdentityData.getObjectsByJSONArray(dataProvider.getDataAsJSONArray(requestURL));
//        this.subID = !subIDs.isEmpty() ? ObjectIdentityData.getObjectFromListByID(subIDs, inboundTransaction.getString("subId")) : null;
        this.subID = !subIDs.isEmpty() ? ObjectIdentityData.getAnyObjectFromList(subIDs) : null;
    }

    public SourceManagementTestData setExistedSources() {
        SourceManagementOverviewData overviewData = new SourceManagementOverviewData(campaign);
        this.sources = overviewData.getAllRowsArray();
        JSONObject object = sources.length() > 0 ? sources.getJSONObject(getRandomInt(sources.length())) : null;
        // probably subId=0 -> empty string
        this.sourceID = object != null ? object.get("campaignId") + "_" + object.get("publisherId") +
                (object.has("subId") && !String.valueOf(object.get("subId")).equals("0") ? "_" + object.get("subId") : "")
                : sourceID;
        return this;
    }

    public ObjectIdentityData getPublisher() {
        return publisher;
    }

    public ObjectIdentityData getCampaign() {
        return campaign;
    }

    public ObjectIdentityData getCdsCampaign() {
        return cdsCampaign;
    }

    public List<ObjectIdentityData> getSubIDs() {
        return subIDs;
    }

    public ObjectIdentityData getSubID() {
        return subID;
    }

    public String getLeadId() {
        return leadId;
    }

    public String getLeadGUID() {
        return leadGUID;
    }

    public String getVertical() {
        return vertical;
    }

    public String getLeadData() {
        return leadData;
    }

    public String getSourceID() {
        return sourceID;
    }

    // combination of possible source values
    public String getAllSubIDsSource() {
        return cdsCampaign.getId() + "_" + publisher.getId();
    }

    public String getAllCampaignsSource() {
        return subID != null ? "0_" + publisher.getId() + "_" + subID.getId().substring(subID.getId().length() - 3) : getMostMatchesSource();
    }

    public String getMostMatchesSource() {
        return "0_" + publisher.getId();
    }

    public boolean hasSubId() {
        return hasSubId;
    }

    // existed sources
    public JSONArray getSources() {
        return sources;
    }

    public boolean hasSources() {
        return sources != null && sources.length() > 0;
    }

    // test data
    public String getCampaignStatus() {
        return campaignStatus;
    }

    public String getStatus() {
        return status;
    }

    public boolean isCherryPicked() {
        return isCherryPicked;
    }

    public boolean isTurnOnCherryPick() {
        return campaignStatus != null && campaignStatus.equals("On");
    }

    public boolean isBlocked() {
        return status != null && status.equals("Block");
    }

    // ==================================== Check sources file =====================================
    public static void waitSourceFileUpdated(String sourceId, long lastModified) {
        log.info("Check that sources file applies changes for sourceID = " + sourceId);
        File sourceFile = new File(SOURCES_FILE);
        if (!sourceFile.exists())
            throw new TestDataException(String.format("Source file by path %s was not found", SOURCES_FILE));
        long startWait = System.currentTimeMillis();
        while (TimeUnit.MINUTES.convert(System.currentTimeMillis() - startWait, TimeUnit.MILLISECONDS) < 6
                && sourceFile.lastModified() == lastModified) {
            HelperSingleton.getHelper().pause(10_000);
        }
        if (sourceFile.lastModified() == lastModified)
            throw new TestDataException(String.format("Source file %s is not modified after 6 minutes", SOURCES_FILE));
        log.info(String.format("Source file is modified after %d seconds",
                TimeUnit.SECONDS.convert(System.currentTimeMillis() - startWait, TimeUnit.MILLISECONDS)));
    }

    public static long getSourceFileLastModified(String sourceId) {
        File sourceFile = new File(SOURCES_FILE);
        if (!sourceFile.exists())
            throw new TestDataException(String.format("Source file by path %s was not found", SOURCES_FILE));
        return sourceFile.lastModified();
    }

    @Override
    public SourceManagementTestData setPositiveData() {
        /* Different:
         * 1) campaignId = 0 - all cds campaigns;
         * 2) 0_publisherId  - all cds campaigns;
         * 3) campaignId_publisherId_subId - all
         */
        this.isPositive = true;
        String subId = subID != null ? "_" + subID.getId().substring(subID.getId().length() - 3) : "";
        this.sourceID = (cdsCampaign == null ? "0" : cdsCampaign.getId())
                + "_" + publisher.getId() + subId;
        return this;
    }

    @Override
    public SourceManagementTestData setNegativeData() {
        this.isPositive = false;
        LocaleData localeData = new LocaleData();
        this.sourceID = getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 3))
                + "_" + publisher.getId() + "_"
                + getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 3));
        return this;
    }

    @Override
    public boolean isPositive() {
        return isPositive;
    }

    @Override
    public String toString() {
        return "SourceManagementTestData{" +
                "publisher=" + publisher +
                ", campaign=" + campaign +
                ", cdsCampaign=" + cdsCampaign +
                ", subIDs=" + subIDs +
                ", subID=" + subID +
                ", sourceID=" + sourceID +
                ", leadId='" + leadId + '\'' +
                ", leadGUID='" + leadGUID + '\'' +
                ", vertical='" + vertical + '\'' +
                ", leadData='" + leadData + '\'' +
                '}';
    }
}
