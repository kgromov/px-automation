package px.reports.dashboard.data;

import configuration.helpers.RequestedURL;
import px.reports.ReportTestData;

import static px.reports.campaigns.CampaignsReportTestData.CAMPAIGNS_INSTANCE_NAME;

/**
 * Created by konstantin on 23.08.2017.
 */
public class DashboardReportsTestData {
    private ReportTestData campaignsData;
    private ReportTestData offersData;
    // static data for links verification
    public static final String CAMPAIGNS_LINK = "campaigns";
    public static final String OFFERS_LINK = "offers";

    public DashboardReportsTestData withCampaigns() {
        this.campaignsData = new DashboardCampaignsReportTestData();
        return this;
    }

    public DashboardReportsTestData withOffers() {
        this.offersData = new DashboardOffersReportTestData();
        return this;
    }


    public ReportTestData getCampaignsData() {
        return campaignsData;
    }

    public ReportTestData getOffersData() {
        return offersData;
    }

    private static final class DashboardOffersReportTestData extends ReportTestData {

        DashboardOffersReportTestData() {
            setInstanceGroup("offers");
            setSorting("totalCost", "desc");
            setDateRanges();
            setAllRowsByDateRange();
            setHeaders();
        }

        @Override
        protected void setHeadersURL() {
            this.headersURL = new RequestedURL.Builder()
                    .withRelativeURL("api/offers/dashboard")
                    .build().getRequestedURL();
        }
    }

    private static final class  DashboardCampaignsReportTestData extends ReportTestData {

        DashboardCampaignsReportTestData() {
            setInstanceGroup(CAMPAIGNS_INSTANCE_NAME);
            setSorting("totalSpend", "desc");
            setDateRanges();
            setAllRowsByDateRange();
            setHeaders();
        }

        @Override
        protected void setHeadersURL() {
            this.headersURL = new RequestedURL.Builder()
                    .withRelativeURL("api/buyerInstances/dashboard")
                    .build().getRequestedURL();
        }
    }

}