package px.objects.disposition;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.LocaleData;
import dto.TestData;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import px.objects.leads.LeadsTestData;
import px.reports.leads.LeadsReportTestData;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import static configuration.helpers.DataHelper.*;

/**
 * Created by kgr on 5/16/2017.
 */
public class DispositionTestData extends LeadsTestData implements TestData {
    // disposition date
    private String dispositionStatus;
    private String dispositionExplanation;
    private String reachTime;   // days diff between dispositionDate and lead creation/sold
    private Date dispositionDate;
    // test mode
    private boolean isPositive = true;
    // for  reiteration
    private final static Predicate<JSONObject> SOLD_LEAD_CONDITION = lead -> !String.valueOf(lead.get("buyerId")).equals("43");
    private final static Predicate<JSONObject> UPDATE_DISPOSITION_CONDITION = lead -> !String.valueOf(lead.get("buyerId")).equals("43")
            && lead.has("canChangeDisposition") && lead.getBoolean("canChangeDisposition");

    // mapping
    static {
        dataMap.put("0", "a few seconds");
        dataMap.put("1", "a minute");
    }

    public DispositionTestData() {
        super(true);
      /*  // choose any sold lead from array
        setLead();*/
        // disposition status
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/dispositionStatuses")
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONArray(dataProvider.getDataAsString(requestedURL));
        List<String> dispositionStatuses = DataHelper.getListFromJSONArrayByKey(jsonArray, "ourStatus");
        this.dispositionStatus = DataHelper.getRandomValueFromList(dispositionStatuses);
    }

    public String getDispositionStatus() {
        return dispositionStatus;
    }

    public String getDispositionExplanation() {
        return dispositionExplanation;
    }

    public Date getDispositionDate() {
        return dispositionDate;
    }

    public boolean isPositive() {
        return isPositive;
    }

    /**
     * @param jsonArray - report table records
     * @param timeKey   - json object key {reachTime, averageReachTime}
     * @return updated report table records with reachTime logic/mapping
     */
    public static JSONArray updateWithReachTime(JSONArray jsonArray, String timeKey) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            try {
                int minutes = new BigDecimal(String.valueOf(object.get(timeKey))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
                int hours = minutes > 60 ? new BigDecimal((double) minutes / 60).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() : 0;
                int days = minutes > 1440 ? new BigDecimal((double) minutes / 1440).setScale(0, BigDecimal.ROUND_HALF_UP).intValue() : 0;
                // more common
                String reachTime = dataMap.containsKey(String.valueOf(minutes)) ? dataMap.get(String.valueOf(minutes)) : minutes + " minutes";
                // usually after update disposition
                reachTime = days > 0 ? days > 1 ? days + " days" : "a day"
                        : hours > 0 ? hours > 1 ? hours + " hours" : "an hour" : reachTime;
                object.put(timeKey, reachTime);
            } catch (NumberFormatException | JSONException ignored) {
            }
        }
        return jsonArray;
    }

    @Override
    public DispositionTestData setPositiveData() {
        this.dispositionExplanation = "Update disposition with " + RandomStringUtils.random(6, true, true);
      /*  // probable minus few minutes
        int minutes = getRandomInt(1, 5);
        this.dispositionDate = new Date(new Date().getTime() - minutes * 60_000);*/
        // or just now - when test navigates to UI it will be already in the past
        this.dispositionDate = new Date();
        return this;
    }

    @Override
    public DispositionTestData setNegativeData() {
        this.isPositive = false;
        LocaleData localeData = new LocaleData();
        this.dispositionExplanation = "Update disposition with " +
                getArrayAsString(getRandomListFromList(localeData.getAlphabetList(), 6));
        if (getRandBoolean()) {
            log.info("Set date in the past - older than lead creation date");
            Date creationDate = getDateByFormatSimple(EXPIRATION_DATE_PATTERN, lead.getCreationDate());
            this.dispositionDate = new Date(creationDate.getTime() - new Random().nextInt() * 1000);
        } else {
            log.info("Set date in future");
            this.dispositionDate = DataHelper.getDateByDaysOffset(getRandomInt(100));
        }
        return this;
    }

    // ------------------ Select proper lead with 5 attempts ------------------
    @Override
    public void setLead(JSONArray allRowsArray) {
        // choose any sold lead from array
        List<JSONObject> leads = JSONWrapper.toList(allRowsArray);
        Collections.shuffle(leads);
        JSONObject lead = leads.stream().filter(UPDATE_DISPOSITION_CONDITION).findFirst().orElse(null);
        this.lead = lead != null ? LeadsReportTestData.getResponseObject(lead) : null;
        this.isSoldLead = this.lead != null;
        setInstanceGroup("leadpreview");
        if (lead != null) log.info("DEBUG:\tDisposition lead = " + lead.toString());
    }

    @Override
    public String toString() {
        return "DispositionTestData{" +
                "dispositionStatus='" + dispositionStatus + '\'' +
                ", dispositionExplanation='" + dispositionExplanation + '\'' +
                ", dispositionDate=" + dispositionDate +
                ", fromPeriod=" + fromPeriod +
                ", toPeriod=" + toPeriod +
                ", lead=" + lead +
                ", leads=" + leads +
                '}';
    }

    // ------------------ Deprecated ------------------
    @Deprecated
    public DispositionTestData setPositiveData_() {
        this.dispositionExplanation = "Update disposition with " + RandomStringUtils.random(6, true, true);
        // to get latest disposition date
        setDispositionHistory();
        // disposition date for positive test should be in range [last_updated_disposition; now]
//        Date startDate = DataHelper.getDateByFormatSimple(DataHelper.EXPIRATION_DATE_PATTERN, lead.getCreationDate());
        Date startDate = getNewestDispositionDate();
        int startMonthOffset = DataHelper.getMonthOffset(startDate);
        // if startMonthOffset = month of start date - prevent day before
        int startConfigDay = DataHelper.getDayInDate(startDate);
        // logic to calculate data ranges
        int month = getRandomInt(-startMonthOffset, 0);
        int leastAvailableDay = startMonthOffset == Math.abs(month) ? startConfigDay : 1;
        int day = getRandomInt(leastAvailableDay, DataHelper.getLastActualDayOfMonth(month));
        // if current month - day offset could not be positive - date in future
        day = month == 0 ? getRandomInt(-DataHelper.getLastActualDayOfMonth(month) + leastAvailableDay, 0) : day;
        this.dispositionDate = DataHelper.getDateByOffset(month, day);
        // validation if dispositionDate less than startDate
        if (dispositionDate.getTime() < startDate.getTime()) {
            log.info(String.format("Lead date creation '%s' is more than dispositionDate = '%s'", startDate, dispositionDate));
            dispositionDate = new Date(getRandomInt(startDate.getTime(), new Date().getTime(), 60 * 1000));
            log.info(String.format("After validation creationDate = '%s', dispositionDate = '%s'", startDate, dispositionDate));
        }
        return this;
    }

    @Deprecated
    private Date getNewestDispositionDate() {
        Date startDate = getDateByFormatSimple(EXPIRATION_DATE_PATTERN, lead.getCreationDate());
        for (int i = 0; i < dispositionHistory.length(); i++) {
            JSONObject dispositionObject = dispositionHistory.getJSONObject(i);
            if (dispositionObject.has(DispositionHistoryColumnsEnum.DATE.getValue())) {
                try {
                    String tempDate = String.valueOf(dispositionObject.get(DispositionHistoryColumnsEnum.DATE.getValue()));
                    Date dispositionDate = getDateByFormatSimple(EXPIRATION_DATE_PATTERN, lead.getCreationDate(tempDate));
                    startDate = dispositionDate.getTime() > startDate.getTime() ? dispositionDate : startDate;
                } catch (RuntimeException ignored) {
                }
            }
        }
        return startDate;
    }
}