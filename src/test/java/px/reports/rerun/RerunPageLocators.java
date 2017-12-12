package px.reports.rerun;

import org.openqa.selenium.By;

/**
 * Created by konstantin on 05.10.2017.
 */
public class RerunPageLocators {
    // ========================================= Overview page ==========================================
    public static By PLANNED_TASKS_CONTAINER = By.cssSelector("#plannedTasks");
    public static By SCHEDULE_BUTTON = By.xpath(".//a[contains(@href, '/createTask')]");
    // ========================================= Create Task page ==========================================
    public static By LEAD_RERUN_TASK_CONTAINER = By.cssSelector("#general");
    // inputs
    public final static String AGE_MIN = ".//*[@data-field-name='leadAgeMin']";
    public final static String AGE_MAX = ".//*[@data-field-name='leadAgeMax']";
    public final static String FILTER_NAME = ".//*[@data-field-name='rerunFilterName']";
    public final static String LAST_AGE_MIN = ".//*[@data-field-name='leadAgeRepostedMin']";
    public final static String LAST_AGE_MAX = ".//*[@data-field-name='leadAgeRepostedMax']";
    // input + datetime
    public final static String ACTIVE_FROM_DATE = ".//*[@data-field-name='filterFromDate']"; // input + calendar
    public final static String ACTIVE_TO_FATE = ".//*[@data-field-name='filterToDate']"; // input + calendar
    public final static String START_TIME = ".//*[@ng-switch-when='time']"; // input + time picker ?
    // select
    public final static String FILTER_GUID = ".//*[@data-field-name='rerunFilterGuid']";
    public final static String REPEAT_CYCLE = ".//*[@data-field-name='runCycle']";
    public final static String VERTICALS = ".//*[@data-field-name='vertical']"; // multiple
    public final static String CAMPAIGNS = ".//*[@data-field-name='buyerList']"; // multiple
    // radio
    public final static String ALLOW_MULTIPLE_RERUN = ".//*[@data-field-name='allowReposted']";
    public final static String IGNORE_RERUN_STATUS = ".//*[@data-field-name='ignoreRerunStatus']";
    public final static String LEAD_SOLD_BEFORE = ".//*[@data-field-name='soldStatus']";
    public final static String DO_PIXEL = ".//*[@data-field-name='doPixel']";
    public final static String ALLOW_BIDDING = ".//*[@data-field-name='allowBidding']";
    public final static String LEAD_SOLD_TO_CAMPAIGN = ".//*[@data-field-name='buyerSoldStatus']";
    // buttons
    public final static String SCHEDULE_TASK_BUTTON = ".//*[contains(text(), 'Schedule')]";
    public final static String PROCESS_FILTERS_BUTTON = ".//*[contains(text(), 'Process')]";
    public final static String CLONE_FILTER_BUTTON = ".//*[contains(text(), 'Clone')]";
    public final static String EDIT_EXISTED_BUTTON = ".//*[contains(text(), 'Edit')]";
    // ========================================= Processed Leads ==========================================
    public static final String REMOVE_LEAD_ITEM = "Remove Lead from Batch";
    public final static String REMOVE_FROM_BATCH_BUTTON = ".//*[contains(@class, 'button') and contains(text(), 'Remove')]";
//    public final static String REMOVE_FROM_BATCH_BUTTON = ".//*[@buttons]//*[contains(text(), 'Remove')]";
}
