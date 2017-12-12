package px.objects.access;

import configuration.helpers.DataHelper;
import configuration.helpers.HttpMethodsEnum;
import configuration.helpers.RequestedURL;
import px.objects.InstancesTestData;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONArray;
import px.objects.DataMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by kgr on 1/27/2017.
 */
public class AccessTestData extends InstancesTestData {
    public final static String PUBLISHERS = "publishers";
    public final static String OFFERS = "offers";
    private final String parentGroup;
    private ObjectIdentityData parent;
    // access statuses
    private String approvedToUnApprovedItemID;
    private String approvedToBlockedItemID;
    private String unApprovedToApprovedItemID;
    private String unApprovedToBlockedItemID;
    private String unBlockedItemID;
    private String approvedToUnApprovedItemName;
    private String approvedToBlockedItemName;
    private String unApprovedToApprovedItemName;
    private String unApprovedToBlockedItemName;
    private String unBlockedItemName;
    // to check in UI list
    private List<String> approvedIDItems;
    private List<String> unApprovedIDItems;
    private List<String> blockedIDItems;
    private List<String> unBlockedIDItems;
    // double groups
    private Map<String, String> blockedApprovedItemsMap = new HashMap<>();
    private Map<String, String> blockedUnApprovedItemsMap = new HashMap<>();
    private Map<String, String> approvedUnApprovedItemsMap = new HashMap<>();

    /*
    1. approved:
        unapprove: approved -> unapproved;
        block: approved -> blocked
    2. unapproved:
        approve: unapproved -> approved;
        block: unapproved -> blocked
    3. blocked:
        unblock: blocked -> approved/unapproved
     */

    // http://lxpui3.stagingrevi.com/api/offerpublisheraccess?offerId=305
    // http://lxpui3.stagingrevi.com/api/offerpublisheraccess?publisherId=305
    public AccessTestData(DataMode dataMode, String parentGroup) {
        super(dataMode);
        this.parentGroup = parentGroup;
        setInstanceGroup("offerpublisheraccess");
        Map<String, String> blockingStatusesMap = dataProvider.getPossibleValueFromJSON("offerBlockingStatus", "enumSequenceIndex");
        String parentIdKey = isOffersAccess() ? "publisherId" : "offerId";
        String dependentIdKey = isOffersAccess() ? "offerId" : "publisherId";
        String dependentName = isOffersAccess() ? "offerName" : "publisherName";
        String targetGroup = isOffersAccess() ? "offers" : "publishers";
        String filterValue = isOffersAccess() ? "Publisher Name " : "Offer Name ";
        // get any from all available parent instances {publishers, offers}
        List<ObjectIdentityData> parentObjectsList = dataProvider.getCreatedInstancesData(parentGroup);
        // filter by only automation created
        parentObjectsList = ObjectIdentityData.getObjectsByName(parentObjectsList, filterValue);
        this.parent = ObjectIdentityData.getAnyObjectFromList(parentObjectsList);
        // all accessible target object (publishers/offers) and select any of them
        List<ObjectIdentityData> targetObjectsList = dataProvider.getCreatedInstancesData("offersAnyStatus");
        // get dependent access list by groups by parentID
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .withParams(parentIdKey, parent.getId())
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL, HttpMethodsEnum.GET));
/*        // exclude IDs that are not present in JSON (unfortunately it's possible)
        List<String> availableIDsList = DataHelper.getListFromJSONArrayByKey(targetObjectsArray, dependentIdKey);*/
        // set all access items by status
        this.approvedIDItems = DataHelper.getListFromJSONArrayByKeyByCondition(jsonArray, dependentIdKey, "blockingStatus", blockingStatusesMap.get("Approved"));
        this.unApprovedIDItems = DataHelper.getListFromJSONArrayByKeyByCondition(jsonArray, dependentIdKey, "blockingStatus", blockingStatusesMap.get("UnApproved"));
        this.blockedIDItems = DataHelper.getListFromJSONArrayByKeyByCondition(jsonArray, dependentIdKey, "blockingStatus", blockingStatusesMap.get("Blocked"));
        this.unBlockedIDItems = DataHelper.getListFromJSONArrayByKeyByCondition(jsonArray, dependentIdKey, "blockingStatus", blockingStatusesMap.get("UnBlocked"));
        // missed items
        List<String> inter = new ArrayList<>(unBlockedIDItems);
        inter.addAll(blockedIDItems);
        inter.retainAll(ObjectIdentityData.getAllIDs(targetObjectsList));
        // missed
        List<String> missed = new ArrayList<>(unBlockedIDItems);
        missed.addAll(blockedIDItems);
        missed.removeAll(ObjectIdentityData.getAllIDs(targetObjectsList));
        if (!missed.isEmpty())
            throw new TestDataException(String.format("There are missed offers %s in offer access for publisher '%S'(absent in OffersAnyStatus)",
                    parent.getId() + " - " + parent.getName(), missed));
        // return if no groups
        if (approvedIDItems.isEmpty() & unApprovedIDItems.isEmpty() & blockedIDItems.isEmpty()) return;
        // offer id in more than 2 groups
        List<String> blockedApprovedItems = new ArrayList<>(approvedIDItems);
        blockedApprovedItems.retainAll(blockedIDItems);
        List<String> blockedUnApprovedItems = new ArrayList<>(unApprovedIDItems);
        blockedUnApprovedItems.retainAll(blockedIDItems);
        List<String> approvedUnApprovedItems = new ArrayList<>(approvedIDItems);
        approvedUnApprovedItems.retainAll(unApprovedIDItems);
        // cause error often happens, till stable
        try {
            // convert to names
            this.blockedApprovedItemsMap = blockedApprovedItems.stream().collect(Collectors.toMap(id -> id,
                    id -> ObjectIdentityData.getObjectFromListByID(targetObjectsList, id).getName()));
            this.blockedUnApprovedItemsMap = blockedUnApprovedItems.stream().collect(Collectors.toMap(id -> id,
                    id -> ObjectIdentityData.getObjectFromListByID(targetObjectsList, id).getName()));
            this.approvedUnApprovedItemsMap = approvedUnApprovedItems.stream().collect(Collectors.toMap(id -> id,
                    id -> ObjectIdentityData.getObjectFromListByID(targetObjectsList, id).getName()));
            // as blocked has highest priority
            approvedIDItems.removeAll(blockedIDItems);
            unApprovedIDItems.removeAll(blockedIDItems);
            // set random from list by each status group
            if (!unApprovedIDItems.isEmpty()) {
                // unapproved -> approved
                this.unApprovedToApprovedItemID = DataHelper.getRandomValueFromList(unApprovedIDItems);
                log.info("unApprovedToApprovedItemID\t" + unApprovedToApprovedItemID);
//                this.unApprovedToApprovedItemName = getJSONFromJSONArrayByCondition(targetObjectsArray, dependentIdKey, unApprovedToApprovedItemID).getString(dependentName);
                this.unApprovedToApprovedItemName = ObjectIdentityData.getObjectFromListByID(targetObjectsList, unApprovedToApprovedItemID).getName();
                approvedIDItems.add(unApprovedToApprovedItemID);
                unApprovedIDItems.remove(unApprovedToApprovedItemID);
                // unapproved -> blocked
                if (!unApprovedIDItems.isEmpty()) {
                    this.unApprovedToBlockedItemID = DataHelper.getRandomValueFromList(unApprovedIDItems);
                    log.info("unApprovedToBlockedItemID\t" + unApprovedToBlockedItemID);
//                    this.unApprovedToBlockedItemName = getJSONFromJSONArrayByCondition(targetObjectsArray, dependentIdKey, unApprovedToBlockedItemID).getString(dependentName);
                    this.unApprovedToBlockedItemName = ObjectIdentityData.getObjectFromListByID(targetObjectsList, unApprovedToBlockedItemID).getName();
                    unApprovedIDItems.remove(unApprovedToBlockedItemID);
                    blockedIDItems.add(unApprovedToBlockedItemID);
                }
            }
            // approved group
            if (!approvedIDItems.isEmpty()) {
                // approved -> unapproved
                this.approvedToUnApprovedItemID = DataHelper.getRandomValueFromList(approvedIDItems);
                log.info("approvedToUnApprovedItemID\t" + approvedToUnApprovedItemID);
//                this.approvedToUnApprovedItemName = getJSONFromJSONArrayByCondition(targetObjectsArray, dependentIdKey, approvedToUnApprovedItemID).getString(dependentName);
                this.approvedToUnApprovedItemName = ObjectIdentityData.getObjectFromListByID(targetObjectsList, approvedToUnApprovedItemID).getName();
                unApprovedIDItems.add(approvedToUnApprovedItemID);
                approvedIDItems.remove(approvedToUnApprovedItemID);
                // approved -> blocked
                if (!approvedIDItems.isEmpty()) {
                    this.approvedToBlockedItemID = DataHelper.getRandomValueFromList(approvedIDItems);
                    log.info("approvedToBlockedItemID\t" + approvedToBlockedItemID);
//                    this.approvedToBlockedItemName = getJSONFromJSONArrayByCondition(targetObjectsArray, dependentIdKey, approvedToBlockedItemID).getString(dependentName);
                    this.approvedToBlockedItemName = ObjectIdentityData.getObjectFromListByID(targetObjectsList, approvedToBlockedItemID).getName();
                    approvedIDItems.remove(approvedToBlockedItemID);
                    blockedIDItems.add(approvedToBlockedItemID);
                }
            }
            // blocked group
            if (!blockedIDItems.isEmpty()) {
                this.unBlockedItemID = DataHelper.getRandomValueFromList(blockedIDItems);
                if (unBlockedItemID != null) {
                    log.info("unBlockedItemID\t" + unBlockedItemID);
//                    this.unBlockedItemName = getJSONFromJSONArrayByCondition(targetObjectsArray, dependentIdKey, unBlockedItemID).getString(dependentName);
                    this.unBlockedItemName = ObjectIdentityData.getObjectFromListByID(targetObjectsList, unBlockedItemID).getName();
//                    blockedIDItems.remove(unBlockedItemID);
                }
            }
            // if approved group was empty but items were approved/unblocked
            // if unapproved group was empty but items were unapproved/unblocked
            // if blocked group was empty but items were blocked
        } catch (Exception e) {
            throw new TestDataException("Unable to initialize Offers Access data\tCause\t" + e.getMessage(), e);
        }
    }

    public boolean isOffersAccess() {
        return parentGroup.equals(PUBLISHERS);
    }

    public boolean isPublishersAccess() {
        return parentGroup.equals(OFFERS);
    }

    public String getParentGroup() {
        return parentGroup;
    }

    public ObjectIdentityData getParent() {
        return parent;
    }

    // by IDs
    public String getApprovedToUnApprovedItemID() {
        return approvedToUnApprovedItemID;
    }

    public String getApprovedToBlockedItemID() {
        return approvedToBlockedItemID;
    }

    public String getUnApprovedToApprovedItemID() {
        return unApprovedToApprovedItemID;
    }

    public String getUnApprovedToBlockedItemID() {
        return unApprovedToBlockedItemID;
    }

    public String getUnBlockedItemID() {
        return unBlockedItemID;
    }

    // by names
    public String getApprovedToUnApprovedItemName() {
        return approvedToUnApprovedItemName;
    }

    public String getApprovedToBlockedItemName() {
        return approvedToBlockedItemName;
    }

    public String getUnApprovedToApprovedItemName() {
        return unApprovedToApprovedItemName;
    }

    public String getUnApprovedToBlockedItemName() {
        return unApprovedToBlockedItemName;
    }

    public String getUnBlockedItemName() {
        return unBlockedItemName;
    }

    public List<String> getApprovedIDItems() {
        return approvedIDItems;
    }

    public List<String> getUnApprovedIDItems() {
        return unApprovedIDItems;
    }

    public List<String> getBlockedIDItems() {
        return blockedIDItems;
    }

    public List<String> getUnBlockedIDItems() {
        return unBlockedIDItems;
    }

    // double groups
    public Map<String, String> getBlockedApprovedItemsMap() {
        return blockedApprovedItemsMap;
    }

    public Map<String, String> getBlockedUnApprovedItemsMap() {
        return blockedUnApprovedItemsMap;
    }

    public Map<String, String> getApprovedUnApprovedItemsMap() {
        return approvedUnApprovedItemsMap;
    }

    public boolean hasApprovedItems() {
        return !approvedIDItems.isEmpty();
    }

    public boolean hasUnApprovedItems() {
        return !unApprovedIDItems.isEmpty();
    }

    public boolean hasBlockedItems() {
        return !blockedIDItems.isEmpty();
    }

    public boolean isUnblockedJoinedApproved() {
        return approvedIDItems.contains(unBlockedItemID);
    }

    @Override
    public String toString() {
        return "AccessTestData{" +
                "parent'" + parent.toString() + '\'' +
                // unapproved
                ", unApprovedToApprovedItemID='" + unApprovedToApprovedItemID + '\'' +
                ", unApprovedToApprovedItemName='" + unApprovedToApprovedItemName + '\'' +
                ", unApprovedToBlockedItemID='" + unApprovedToBlockedItemID + '\'' +
                ", unApprovedToBlockedItemName='" + unApprovedToBlockedItemName + '\'' +
                // approved
                ", approvedToUnApprovedItemID='" + approvedToUnApprovedItemID + '\'' +
                ", approvedToUnApprovedItemName='" + approvedToUnApprovedItemName + '\'' +
                ", approvedToBlockedItemID='" + approvedToBlockedItemID + '\'' +
                ", approvedToBlockedItemName='" + approvedToBlockedItemName + '\'' +
                // blocked
                ", unBlockedItemID='" + unBlockedItemID + '\'' +
                ", unBlockedItemName='" + unBlockedItemName + '\'' +
                // all groups
                ", approvedIDItems=" + approvedIDItems +
                ", unApprovedIDItems=" + unApprovedIDItems +
                ", blockedIDItems=" + blockedIDItems +
                ", unBlockedIDItems=" + unBlockedIDItems +
                // double groups
                ", approvedUnApprovedItemsMap=" + approvedUnApprovedItemsMap +
                ", blockedApprovedItemsMap=" + blockedApprovedItemsMap +
                ", blockedUnApprovedItemsMap=" + blockedUnApprovedItemsMap +
                '}';
    }
}
