package elements.date;

import configuration.helpers.DataHelper;
import elements.menu.ListMenuElement;
import elements.table.TableElement;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import px.objects.offers.OffersPageLocators;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static pages.locators.ElementLocators.AVAILABLE_DAY_MONTH_CALENDAR;
import static pages.locators.ElementLocators.AVAILABLE_DAY_RANGE_CALENDAR;
import static px.reports.ReportPageLocators.CALENDAR_PICKER;

/**
 * Created by kgr on 10/10/2016.
 */
@FindBy(xpath = OffersPageLocators.CALENDAR_DROPDOWN)
public class CalendarElement extends DateTimePicker {
    @FindBy(xpath = ".//table[@class='table-condensed']")
    private TableElement tableElement;

    //    @FindBy(xpath = ".//table[@class='table-condensed']")
    @FindBy(xpath = ".")
    private TimePickerElement timePickerElement;

    @FindBy(xpath = ".//table//*[contains(@class, 'prev')]")
    private WebElement previousMonth;

    @FindBy(xpath = ".//table//*[contains(@class, 'next')]")
    private WebElement nextMonth;

    @FindBy(xpath = ".//td[contains(@class, 'active')]")
    private WebElement activeDay;

    @FindBy(xpath = ".//a[@data-action='togglePicker']")
    private WebElement switchToTime;

    @FindBy(xpath = ".//div[@class='ranges']")
    private ListMenuElement listMenuElement;

    public void setDate(Date date) {
        setHelper();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DataHelper.getDateByFormatSimple(DataHelper.CALENDAR_TABLE_DATE_PATTERN, getValue()));
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        // test data
        calendar.setTime(date);
        // how much time click on piker
        int monthClickCount = (calendar.get(Calendar.YEAR) - year) * 12 + (calendar.get(Calendar.MONTH) - month);
        clickOnPicker(nextMonth, previousMonth, monthClickCount);
        // set day
//        tableElement.clickOnCellByText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        helper.getElement(tableElement, String.format(AVAILABLE_DAY_MONTH_CALENDAR, day, day)).click();
    }

    public void setTime(Date date) {
        switchToTime.click();
        timePickerElement.setDate(date);
    }

    /**
     * @param startDate - start Date in range
     * @param endDate   - end Date in range
     */
    public void setRange(Date startDate, Date endDate) {
        setHelper();
        log.info(String.format("Set calendar date range [%s-%s]", startDate, endDate));
        helper.waitUntilDisplayed(".//table[@class='table-condensed']");
        // move mouse to the up left corner not to overlap calendar cells
        helper.mouseMove();
        List<WebElement> tables = helper.getElements(getWrappedElement(), ".//table[@class='table-condensed']");
        if (tables.size() != 2)
            throw new IllegalArgumentException(String.format("Unknown Calendar range element format.\tShould be 2 months, but were '%d'", tables.size()));
        // start range date
        Date activeStartDate = getStartDateRange();
        // end range date
        Date activeEndDate = getEndDateRange();
        // debug info
        log.info(String.format("DEBUG\tCalendar active date range [%s-%s]", activeStartDate, activeEndDate));
        // check if ranges are the same
        if (startDate.getTime() == activeStartDate.getTime() && endDate.getTime() == activeEndDate.getTime()) {
            log.info("Custom date range is set correctly already");
            helper.click(CALENDAR_PICKER);
        } else {
            setRange(startDate);
            setRange(endDate);
        }
        helper.waitUntilToBeInvisible(".//table[@class='table-condensed']");
    }

    public void setRange(Date date) {
        Calendar calendar = Calendar.getInstance();
        // start date
        calendar.setTime(date);
        int expectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        // left - right months
        List<WebElement> tables = helper.getElements(getWrappedElement(), ".//table[@class='table-condensed']");
        // left month in calendar
        Date leftMonthDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_MONTH_PATTERN, getMonthDate(tables.get(0)));
        int monthToClickCount = DataHelper.getMonthOffset(leftMonthDate, date);
        log.info(String.format("Set correct month for date\t%s", date));
        // more generic
        int tableIndex = monthToClickCount > 0 ? 2 : 1;
        if (monthToClickCount > 0) --monthToClickCount;
        clickOnPicker(nextMonth, previousMonth, monthToClickCount);
        // get/refresh table element
        WebElement monthTableElement = helper.getElement(getWrappedElement(), String.format("(.//table[@class='table-condensed'])[%d]", tableIndex));
        // click on day
        helper.getElement(monthTableElement, String.format(AVAILABLE_DAY_RANGE_CALENDAR, expectedDay, expectedDay)).click();
    }

    public boolean isTimeMode() {
        return  !helper.getElement(switchToTime, ".//span").getAttribute("class").contains("time");
    }

    private Date getStartDateRange() {
        // start range date table element
        WebElement startMonthTable = helper.getElement(getWrappedElement(), ".//*[contains(@class, 'active start-date')]/ancestor::table");
        // start day
        String startDay = helper.getElement(startMonthTable, ".//*[contains(@class, 'active start-date')]").getText();
        // start month and year
        String startMonthYear = helper.getElement(startMonthTable, ".//th[@class='month']").getText();
        Date activeStartDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_MONTH_PATTERN, startMonthYear);
        return DataHelper.setDay(activeStartDate, Integer.parseInt(startDay));
    }

    private Date getEndDateRange() {
        // end range date table element
        WebElement endMonthTable = helper.getElement(getWrappedElement(), ".//*[contains(@class, 'end-date') and not(contains(@class, 'off'))]/ancestor::table");
        // start day
        String endDay = helper.getElement(endMonthTable, ".//*[contains(@class, 'end-date')]").getText();
        // start month and year
        String endMonthYear = helper.getElement(endMonthTable, ".//th[@class='month']").getText();
        Date activeEndDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_MONTH_PATTERN, endMonthYear);
        return DataHelper.setDay(activeEndDate, Integer.parseInt(endDay));
    }

    private String getMonthDate(WebElement table) {
        return helper.getElement(table, ".//th[@class='month']").getText();
    }

    public ListMenuElement getListMenuElement() {
        return listMenuElement;
    }

    @Override
    public String getValue() {
        return activeDay.getAttribute("data-day");
    }
}
