package px.reports.rerun;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.ObjectIdentityData;
import dto.TestDataException;
import org.json.JSONArray;
import org.json.JSONObject;
import px.objects.leads.LeadObject;
import px.objects.leads.Leadable;
import px.reports.ReportTestData;
import px.reports.dto.DateRange;

import java.util.*;

import static configuration.helpers.DataHelper.*;
import static pages.groups.Pagination.MAX_ROWS_LIMIT;

/**
 * Created by kgr on 10/17/2017.
 */
public class ProcessedLeadTestData extends ReportTestData implements Leadable {
    {
        this.startMonthOffset = 3;
        this.durationDays = 1;
    }

    // by default
    private String ageMin = "";
    private String ageMax = "";
    private ObjectIdentityData filter;
    private List<ObjectIdentityData> filters;
    // precessed lead(s) to remove from batch
    protected ResponseObject lead;
    protected Set<ResponseObject> leads;

    public ProcessedLeadTestData() {
        // prerequisite
        this.filters = dataProvider.getCreatedInstancesData("rerunTaskFilters");
        this.filter = ObjectIdentityData.getAnyObjectFromList(filters);
        setInstanceGroup("rerunplanningtask/leads");
        setSorting("dateTime", "desc");
        setAllRowsByDateRange();
    }

    public ProcessedLeadTestData(RerunedLeadsReportTestData testData) {
        // prerequisite
        this.filters = dataProvider.getCreatedInstancesData("rerunTaskFilters");
        this.ageMin = testData.getAgeMin();
        this.ageMax = testData.getAgeMax();
        this.filter = testData.getFilter();
        this.allRowsArray = testData.getAllRowsArray();
        this.totalCount = testData.getItemsTotalCount();
        setInstanceGroup("rerunplanningtask/leads");
        setSorting("dateTime", "desc");
    }

    @Override
    public void setLead() {
        Set<ObjectIdentityData> attempts = new HashSet<>();
        do {
            setLead(allRowsArray);
            if (lead != null) return;
            this.filter = ObjectIdentityData.getAnyObjectFromList(filters);
            attempts.add(filter);
            setAllRowsByDateRange();
        } while (attempts.size() < 5);
        throw new TestDataException(String.format("No leads to rerun after 5 attempts" +
                " for the following filters %s", attempts));
    }

    @Override
    public void setLead(JSONArray allRowsArray) {
        if (allRowsArray.length() == 0) return;
        // choose any sold lead from array
        int leadIndex = DataHelper.getRandomInt(allRowsArray.length());
        log.info(String.format("Get '%d' lead from total '%d'", leadIndex, allRowsArray.length()));
        this.lead = new ResponseObject(allRowsArray.getJSONObject(leadIndex));
    }

    @Override
    public ResponseObject getLead() {
        return lead;
    }

    public void setLeads() {
        Set<DateRange> ranges = new HashSet<>();
        do {
            int totalAvailable = getItemsTotalCount() > 100 ? 100 : getItemsTotalCount();
            if (totalAvailable > 0) {
                int count = getRandomInt(1, totalAvailable > 10 ? 10 : totalAvailable);
                this.leads = new HashSet<>();
                for (int i = 0; i < count; i++) {
                    int index = getRandomInt(totalAvailable);
                    leads.add(new ResponseObject(allRowsArray.getJSONObject(index)));
                }
            }
            if (!leads.isEmpty()) return;
            setInstanceGroup("leaddetailsfastreport/data");
            ranges.add(new DateRange(fromPeriod, toPeriod));
            setDateRanges();
            setAllRowsByDateRange();
        } while (ranges.size() < 5);
        throw new TestDataException(String.format("No leads to rerun after 5 attempts" +
                " in the following ranges %s", ranges));
    }

    public Set<ResponseObject> getLeadResponses() {
        return leads;
    }

    public ObjectIdentityData getFilter() {
        return filter;
    }

    @Override
    public void setAllRowsByDateRange() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/" + instanceGroup)
                .filter(Arrays.asList("leadagemin", "leadagemax", "filterguid"),
                        Arrays.asList(ageMin, ageMax, filter.getGuid()))
                .sort(sortBy, sortHow)
                .build().getRequestedURL();
        this.allRowsArray = dataProvider.getDataAsJSONArray(requestedURL);
        this.totalCount = dataProvider.getCurrentTotal();
        this.isBigData = totalCount > MAX_ROWS_LIMIT;
        this.hasTotalRow = allRowsArray.length() > 0 && DataHelper.hasJSONValue(allRowsArray.getJSONObject(0), "Total");
    }

    public static final class ResponseObject extends LeadObject {
        private String creationDate;
        private String vertical;
        private String soldToBuyers;
        private String payout;
        private String publisherName;
        private String publisherId;
        private String offerId;
        private String email;
        private String phoneNumber;
        private String qiqScore;

        private ResponseObject(JSONObject jsonObject) {
            super(jsonObject);
            this.creationDate = String.valueOf(jsonObject.get("creationDate"));
            this.vertical = String.valueOf(jsonObject.get("vertical"));
            this.soldToBuyers = String.valueOf(jsonObject.get("soldToBuyers"));
            this.payout = String.valueOf(jsonObject.get("payout"));
            this.email = String.valueOf(jsonObject.get("email"));
            this.phoneNumber = String.valueOf(jsonObject.get("phoneNumber"));
            this.qiqScore = String.valueOf(jsonObject.get("qiqScore"));
            this.publisherName = String.valueOf(jsonObject.get("publisherName"));
            this.publisherId = String.valueOf(jsonObject.get("publisherId"));
            this.offerId = String.valueOf(jsonObject.get("offerId"));
        }

        public String getCreationDate() {
            Date temp = getDateByFormatSimple(LEADS_REPORT_PATTERN_IN, creationDate);
            return getDateByFormatSimple(EXPIRATION_DATE_PATTERN, temp).replace("AM", "am").replace("PM", "pm");
        }

        public Date getDate() {
            return getDateByFormatSimple(LEADS_REPORT_PATTERN_IN, creationDate);
        }

        public String getVertical() {
            return vertical;
        }

        public String getSoldToBuyers() {
            return soldToBuyers;
        }

        public String getPayout() {
            return payout;
        }

        public String getPublisherName() {
            return publisherName;
        }

        public String getPublisherId() {
            return publisherId;
        }

        public String getOfferId() {
            return offerId;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getQiqScore() {
            return qiqScore;
        }

        @Override
        public String toString() {
            return "ResponseObject{" +
                    "creationDate='" + creationDate + '\'' +
                    ", vertical='" + vertical + '\'' +
                    ", soldToBuyers='" + soldToBuyers + '\'' +
                    ", payout='" + payout + '\'' +
                    ", publisherName='" + publisherName + '\'' +
                    ", publisherId='" + publisherId + '\'' +
                    ", offerId='" + offerId + '\'' +
                    ", email='" + email + '\'' +
                    ", phoneNumber='" + phoneNumber + '\'' +
                    ", qiqScore='" + qiqScore + '\'' +
                    '}';
        }
    }
}
