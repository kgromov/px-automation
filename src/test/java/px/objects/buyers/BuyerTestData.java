package px.objects.buyers;

import config.Config;
import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.objects.DataMode;
import px.objects.InstancesTestData;
import px.objects.users.ContactTestData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 10/18/2016.
 */
public class BuyerTestData extends InstancesTestData {
    // contact info
    private String functionTitle;
    private String web;
    private String creditLimits;
    private String paymentTerms;
    private String previousPaymentTerms;
    private String additionalContactInfo;
    // edit mode
    private String monthlyCap;
    private String status;
    // address
    private String country;
    // managers
    private String customerManager;
    private String salesManager;
    // contact data (users as well)
    private ContactTestData contactTestData;
    // maps for possible next verification
    private Map<String, String> contactInfoMap;
    private Map<String, String> paymentTermsMap;
    private Map<String, String> countriesMap;
    private Map<String, String> statesMap;
    private Map<String, String> statusesMap;

    //http://lxpui3.stagingrevi.com/api/buyerinfo/contactitem?contactInfoId=&parentObjectId=
    public BuyerTestData(DataMode dataMode) {
        super(dataMode);
        setInstanceGroup("buyers");
        // mapping from test data files
        this.contactInfoMap = dataProvider.getPossibleValueFromJSON("ContactItemTypes");
        this.countriesMap = dataProvider.getPossibleValueFromJSON("Countries");
        this.statesMap = dataProvider.getPossibleValueFromJSON("UsStates");
        this.paymentTermsMap = dataProvider.getPossibleValueFromJSON("PaymentTerms");
        this.statusesMap = dataProvider.getPossibleValueFromJSON("Status");
        this.additionalContactInfo = getRandomValueFromList(new ArrayList<>(contactInfoMap.keySet()));
        this.country = getRandomValueFromList(new ArrayList<>(countriesMap.keySet()));
        this.paymentTerms = getRandomValueFromList(new ArrayList<>(paymentTermsMap.keySet()));
        this.status = getRandomValueFromList(new ArrayList<>(statusesMap.keySet()));
        // created instances
        // TODO: could be figure out with SessionPool
        if (Config.isAdmin()) {
            List<String> userEmailsList = ObjectIdentityData.getAllNames(dataProvider.getCreatedInstancesData("users"));
            this.customerManager = getRandomValueFromList(userEmailsList);
            this.salesManager = getRandomValueFromList(userEmailsList);
        }
        // contact data
        this.contactTestData = new ContactTestData(isPositive());
        // others
        if (isPositive()) setPositiveData();
        else setNegativeData();
        this.functionTitle = "Function for Buyer " + getRandomCharSequence();
        this.web = getWebSiteUnique();
        this.creditLimits = "$" + getQuantity(10000);
        this.monthlyCap = getQuantity(10000);
    }

    public BuyerTestData(JSONObject jsonObject) {
        super(DataMode.getCreatedByResponse());
        setInstanceGroup("buyers");
        this.guid = String.valueOf(jsonObject.get("buyerGuid"));
        this.name = String.valueOf(jsonObject.get("buyerName"));
//        this.jsonObject = dataProvider.getInstanceDetails(instanceGroup, "buyerName", name);
    }

    public String getFunctionTitle() {
        return functionTitle;
    }

    public String getWeb() {
        return web;
    }

    public String getCreditLimits() {
        return creditLimits;
    }

    public String getAdditionalContactInfo() {
        return additionalContactInfo;
    }

    public String getCountry() {
        return country;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public String getCustomerManager() {
        return customerManager;
    }

    public String getSalesManager() {
        return salesManager;
    }

    public String getMonthlyCap() {
        return monthlyCap;
    }

    public String getStatus() {
        return status;
    }

    public String getPreviousPaymentTerms() {
        return previousPaymentTerms;
    }

    // contact data + user data
    public ContactTestData getContactTestData() {
        return contactTestData;
    }

    public void setContactTestData() {
        this.contactTestData = new ContactTestData(isPositive());
    }

    public void setContactTestData(boolean isPositive) {
        this.contactTestData = new ContactTestData(isPositive);
    }

    // payment terms confirmation popup
    public void setPreviousPaymentTerms(String previousPaymentTerms) {
        if (isEditMode())
            this.previousPaymentTerms = previousPaymentTerms;
    }

    public boolean showPopup() {
        return isEditMode() && !previousPaymentTerms.contains("None") && !previousPaymentTerms.equals(paymentTerms);
    }

    @Override
    public String toString() {
        String instanceDetails = !isCreatedByResponse() ? "\nBuyerTestData{" +
                ", functionTitle='" + functionTitle + '\'' +
                ", web='" + web + '\'' +
                ", creditLimits='" + creditLimits + '\'' +
                ", additionalContactInfo='" + additionalContactInfo + '\'' +
                ", country='" + country + '\'' +
                ", customerManager='" + customerManager + '\'' +
                ", salesManager='" + salesManager + '\'' +
                ", monthlyCap='" + monthlyCap + '\'' +
                ", status='" + status + '\'' +
                contactTestData.toString() + '\'' +
                '}' : "";
        return super.toString() + instanceDetails;
    }
}
