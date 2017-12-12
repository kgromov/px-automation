package px.reports.dto;

import java.util.Date;

import static configuration.helpers.DataHelper.*;

/**
 * Created by konstantin on 23.08.2017.
 */
public class DateRange {
    public static final int DEFAULT_START_MONTH_OFFSET = 3;
    public static final int DEFAULT_DURATION_DAYS = 30;
    protected int startMonthOffset;
    protected int durationDays;
    private String fromPeriod;
    private String toPeriod;
    private Date fromPeriodDate;
    private Date toPeriodDate;

    public DateRange(Date fromPeriodDate) {
        this.fromPeriodDate = fromPeriodDate;
        this.toPeriodDate = getDateByDaysOffset(fromPeriodDate, 1);
        this.fromPeriod = getDateByFormatSimple(PX_REPORT_DATE_PATTERN, fromPeriodDate);
        this.toPeriod = getDateByFormatSimple(PX_REPORT_DATE_PATTERN, toPeriodDate);
        toPeriodDate = fromPeriodDate;
    }

    public DateRange(String fromPeriod, String toPeriod) {
        this.fromPeriod = fromPeriod;
        this.toPeriod = toPeriod;
        this.fromPeriodDate = getDateByFormatSimple(PX_REPORT_DATE_PATTERN, fromPeriod);
        this.toPeriodDate = getDateByFormatSimple(PX_REPORT_DATE_PATTERN, toPeriod);
        toPeriodDate = getDateByDaysOffset(toPeriodDate, -1);
    }

    public DateRange(int startMonthOffset, int durationDays) {
        this.startMonthOffset = startMonthOffset;
        this.durationDays = durationDays;
    }

    public String getFromPeriod() {
        return fromPeriod;
    }

    public String getToPeriod() {
        return toPeriod;
    }

    public Date getFromPeriodDate() {
        return fromPeriodDate;
    }

    public Date getToPeriodDate() {
        return toPeriodDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DateRange))
            return false;
        DateRange that = (DateRange) obj;
        return this.fromPeriod.equals(that.fromPeriod) && this.toPeriod.equals(that.toPeriod);
    }

    @Override
    public String toString() {
        return "DateRange{" +
                "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                '}';
    }
}
