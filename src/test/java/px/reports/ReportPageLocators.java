package px.reports;

import org.openqa.selenium.By;

import static pages.locators.ElementLocators.DATA_FIELD_PARAMETERIZED_ELEMENT;

/**
 * Created by kgr on 11/17/2016.
 */
public class ReportPageLocators {
    // ========================================= Buyer Report page ==========================================
    public final static String BUYER_CATEGORIES_FILTER = ".//*[@data-field-name='ReportType']/ancestor-or-self::px-report-filter";
    // ============================================== Common ================================================
    // generic filter
    public final static String GRAPHICS_FILTER = ".//*[@data-field-name='type']/ancestor-or-self::px-report-filter";
    public final static String GENERIC_PARAMETRIZED_FILTER = ".//*[@data-field-name='%s']/ancestor-or-self::px-report-filter";
    public final static String GENERIC_FILTER = ".//px-report-filter/descendant-or-self::div[@data-field-name]";
    public final static String REPORT_FILTER_CONTAINER = ".//px-report-filter";
    public final static String SELECT_ALL_COLUMN_FIELD = "rowSelected";
    public final static String SELECT_ALL_COLUMN = String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, SELECT_ALL_COLUMN_FIELD);
    // graphic types
    public final static By GRAPHICS_CONTAINER = By.xpath(".//*[@id='reportChart2']");
    public final static By BUBBLE_CHART_CONTAINER = By.cssSelector("px-grid-chart px-bubble-chart");
    public final static By COLUMN_CHART_CONTAINER = By.cssSelector("px-grid-chart px-column-chart");
    public final static By STACKED_AREA_CHART_CONTAINER = By.cssSelector("px-grid-chart px-stacked-area-chart");
    // calendar
    public final static String CALENDAR_CONTAINER = ".//div[contains(@class, 'px-calendar')]";
    public final static String CALENDAR_PICKER = CALENDAR_CONTAINER + "//i";
    public final static String CALENDAR_LABEL = CALENDAR_CONTAINER + "/span";
}