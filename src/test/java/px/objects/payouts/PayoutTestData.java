package px.objects.payouts;

import configuration.helpers.RequestedURL;
import px.objects.InstancesTestData;
import dto.ObjectIdentityData;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.DataMode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static px.reports.dto.FieldFormatObject.*;

/**
 * Created by kgr on 4/21/2017.
 */
public abstract class PayoutTestData extends InstancesTestData {
    public final static String OFFER_PAYOUT_INSTANCE = "offerpayouts";
    public final static String PUBLISHER_PAYOUT_INSTANCE = "publisherpayouts";
    // payout data
    private String payout;
    private String percentagePayout;
    private String revenue;
    private String percentageRevenue;
    private String conversionCap;
    private String monthlyConversion;
    private String payoutCap;
    private String monthlyPayout;
    private String revenueCap;
    private String monthlyRevenue;
    // offers/publishers
    protected List<ObjectIdentityData> offers;
    protected List<ObjectIdentityData> publishers;
    protected List<String> offerIDs;
    protected List<String> publisherIDs;
    // offer data
    protected String offerID;
    protected String offerName;
    // publisher data
    protected String publisherID;
    protected String publisherName;
    protected String publisherGUID;
    // existed payouts
    protected JSONArray existedPayouts;
    protected Set<String> existedPayoutIDs;
    // data required for edit mode
    // some values are taken from parent offer
    protected JSONObject offerObject;
    protected JSONObject payoutObject;

    static {
        // payout fields
        missedHeadersMetricsMap.put("payout", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("percPayout", NUMBER_PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("revenue", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("percRevenue", NUMBER_PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("conversionCap", NUMBER_FORMAT);
        missedHeadersMetricsMap.put("monthlyConversionCap", NUMBER_FORMAT);
        missedHeadersMetricsMap.put("payoutCap", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("monthlyPayoutCap", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("revenueCap", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("monthlyRevenueCap", CURRENCY_FORMAT);
        // offer fields
        missedHeadersMetricsMap.put("defaultPayout", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("percentPayout", PERCENTAGE_FORMAT);
        missedHeadersMetricsMap.put("maxPayout", CURRENCY_FORMAT);
        missedHeadersMetricsMap.put("maxPercentPayout", PERCENTAGE_FORMAT);
    }

    public PayoutTestData(DataMode dataMode) {
        super(dataMode);
//        setHeaderObjects();
        // according to mode
        if (isPositive()) setPositiveData();
        else setNegativeData();
        // payout data
        generatePayoutData();
    }

    // clone with field format objects and parents data
    public PayoutTestData clone(PayoutTestData testData){
        this.fields = testData.getFields();
        // later on get rid
        this.offerID = testData.getOfferID();
        this.offerName = testData.getOfferName();
        // init target
        this.publisherID = testData.getPublisherID();
        this.publisherName = testData.getPublisherName();
        this.publisherGUID = testData.getPublisherGUID();
    /*    // payout data
        generatePayoutData();*/
        this.payoutObject = asJSON();
        this.offerObject = testData.getOfferObject();
        return this;
    }

    protected void generatePayoutData() {
        this.payout = getQuantity(1000);// + ",00 €/$ ";
        this.revenue = getQuantity(1000); // + ",00 €";
        this.payoutCap = getQuantity(1000); // + ",00 €";
        this.monthlyPayout = getQuantity(1000); // + ",00 €";
        this.revenueCap = getQuantity(1000); // + ",00 €";
        this.monthlyRevenue = getQuantity(1000); // + ",00 €";
        this.percentagePayout = getPercentage(); // + " %";
        this.percentageRevenue = getPercentage(); // + " %";
        this.conversionCap = getQuantity(10);
        this.monthlyConversion = getQuantity(10);
    }

    protected JSONObject asJSON() {
        JSONObject object = new JSONObject();
        object.put("payout", payout);
        object.put("percPayout", percentagePayout);
        object.put("revenue", revenue);
        object.put("percRevenue", percentageRevenue);
        object.put("conversionCap", conversionCap);
        object.put("monthlyConversionCap", monthlyConversion);
        object.put("payoutCap", payoutCap);
        object.put("monthlyPayoutCap", monthlyPayout);
        object.put("revenueCap", revenueCap);
        object.put("monthlyRevenueCap", monthlyRevenue);
        return object;
    }

    // offers/publishers
    protected void setParents(){
        this.offers = dataProvider.getCreatedInstancesData("offers");
        // filter by only automation created
        List<ObjectIdentityData> tempList = ObjectIdentityData.getObjectsByName(offers, "Offer Name ");
        this.offerIDs = ObjectIdentityData.getAllIDs(tempList);
        // all accessible target object (publishers/offers) and select any of them
        this.publishers = dataProvider.getCreatedInstancesData("publishers");
        // filter by only automation created
        tempList = ObjectIdentityData.getObjectsByName(publishers, "Publisher Name ");
        this.publisherIDs = ObjectIdentityData.getAllIDs(tempList);
    }

    // set offer details
    public void setOfferObject() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/offer")
                .withParams("offerID", offerID)
                .build().getRequestedURL();
        this.offerObject = new JSONObject(dataProvider.getDataAsString(requestedURL));
    }

    protected abstract void setPayoutObject();

    protected abstract void setPayouts();

    public abstract boolean isDuplicatedTargetID();

    public String getPayout() {
        return payout;
    }

    public String getPercentagePayout() {
        return percentagePayout;
    }

    public String getRevenue() {
        return revenue;
    }

    public String getPercentageRevenue() {
        return percentageRevenue;
    }

    public String getConversionCap() {
        return conversionCap;
    }

    public String getMonthlyConversion() {
        return monthlyConversion;
    }

    public String getPayoutCap() {
        return payoutCap;
    }

    public String getMonthlyPayout() {
        return monthlyPayout;
    }

    public String getRevenueCap() {
        return revenueCap;
    }

    public String getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public String getOfferID() {
        return offerID;
    }

    public String getOfferName() {
        return offerName;
    }

    public String getPublisherID() {
        return publisherID;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public String getPublisherGUID() {
        return publisherGUID;
    }

    public String getTargetID(){
        return isOfferPayout() ? publisherID : offerID;
    }

    public String getTargetName(){
        return isOfferPayout() ? publisherName : offerName;
    }

    public Set<String> getExistedPayoutIDs() {
        return existedPayoutIDs;
    }

    public void addToExistedPayouts(String id) {
        existedPayoutIDs.add(id);
    }

    public JSONObject getOfferObject() {
        return offerObject;
    }

    public JSONObject getPayoutObject() {
        return payoutObject;
    }

    public boolean isAnyPayout() {
        return !existedPayoutIDs.isEmpty();
    }

    public boolean isOfferPayout() {
        return instanceGroup.equals(OFFER_PAYOUT_INSTANCE);
    }

    public boolean isPublisherPayout() {
        return instanceGroup.equals(PUBLISHER_PAYOUT_INSTANCE);
    }

    public static List<String> getOfferFields() {
        return Arrays.asList("defaultPayout", "percentPayout", "maxPayout", "maxPercentPayout");
    }

    public static Set<String> getPayoutFields() {
        Set<String> set = new HashSet<>(missedHeadersMetricsMap.keySet());
        set.removeAll(getOfferFields());
        return set;
    }

    @Override
    protected void setHeadersURL() {
        this.headersURL = new RequestedURL.Builder()
                .withRelativeURL("api/offerpayout")
                .build().getRequestedURL();
    }

    @Override
    public String toString() {
        return "PayoutTestData{" +
                "payout='" + payout + '\'' +
                ", percentagePayout='" + percentagePayout + '\'' +
                ", revenue='" + revenue + '\'' +
                ", percentageRevenue='" + percentageRevenue + '\'' +
                ", conversionCap='" + conversionCap + '\'' +
                ", monthlyConversion='" + monthlyConversion + '\'' +
                ", payoutCap='" + payoutCap + '\'' +
                ", monthlyPayout='" + monthlyPayout + '\'' +
                ", revenueCap='" + revenueCap + '\'' +
                ", monthlyRevenue='" + monthlyRevenue + '\'' +
                ", offerID='" + offerID + '\'' +
                ", offerName='" + offerName + '\'' +
                ", publisherID='" + publisherID + '\'' +
                ", publisherName='" + publisherName + '\'' +
                ", existedPayoutIDs=" + existedPayoutIDs +
                '}';
    }
}