package px.reports;

import config.UserRoleEnum;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static config.Config.userGroup;

/**
 * Created by konstantin on 05.10.2016.
 */
public enum ReportsEnum {
    AUDIENCE_REPORT("/reports/audience"),
    BROKER_VIEWS_REPORT("/reports/brokerviews"),
    BUYER_CAMPAIGN_REPORT("/reports/buyercampaign"),
    BUYER_PERFORMANCE_REPORT("/reports/buyerperformance"),
    BUYER_REPORT("/reports/buyersummary"),
    DAILY_MARGIN_REPORT("/reports/publishermargin"),
    DISPOSITION_BREAKDOWN_REPORT("/reports/dispositionBreakdown"),
    EXPORT_DOWNLOADS("/reports/downloads"),
    PING_POST_REPORT("/reports/pingPost"),
    PUBLISHER_CONVERSION_REPORT("/reports/publisherconversions"),
    PUBLISHER_DAILY_REPORT("/reports/publisherstats"),
    PUBLISHER_PERFORMANCE_REPORT("/reports/publisherperformance"),
    SOURCE_QUALITY_SCORE_REPORT("/reports/sourceqiqscore"),
    TRANSACTIONS_INBOUND_REPORT("/reports/inboundtransactions"),
    TRANSACTIONS_OUTBOUND_REPORT("/reports/outboundtransactions"),
    TRANSACTIONS_PING_POST_REPORT("/reports/pingposttransactions");

    private final String value;

    public String getValue() {
        return value;
    }

    ReportsEnum(String value) {
        this.value = value;
    }

    public static List<ReportsEnum> getAvailableReports() {
        List<ReportsEnum> items = new ArrayList<>(Arrays.asList(
                ReportsEnum.AUDIENCE_REPORT,
                ReportsEnum.SOURCE_QUALITY_SCORE_REPORT,
                ReportsEnum.EXPORT_DOWNLOADS));
        // different left menu items set on beta
//        if (Config.isBetaEnvironment()) items.remove(ReportsEnum.BILLING);
        // switch by user role
        switch (UserRoleEnum.valueOf(userGroup)) {
            case ADMIN:
                return Arrays.asList(ReportsEnum.values());
            case BUYER:
                items.add(ReportsEnum.BUYER_CAMPAIGN_REPORT);
                items.add(ReportsEnum.BUYER_REPORT);
                items.add(ReportsEnum.TRANSACTIONS_OUTBOUND_REPORT);
                return items;
            case PUBLISHER:
                items.add(ReportsEnum.PUBLISHER_CONVERSION_REPORT);
                items.add(ReportsEnum.PUBLISHER_DAILY_REPORT);
                items.add(ReportsEnum.TRANSACTIONS_INBOUND_REPORT);
                return items;
            default:
                throw new IllegalArgumentException("Unknown user role - " + userGroup);
        }
    }
}
