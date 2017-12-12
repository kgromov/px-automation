package px.reports;

import configuration.browser.PXDriver;
import configuration.helpers.DataHelper;
import configuration.helpers.HTMLHelper;
import elements.date.CalendarElement;
import elements.dropdown.FilteredDropDown;
import elements.menu.ListMenuElement;
import org.hamcrest.Matcher;
import org.json.JSONArray;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.OverviewPage;
import pages.groups.Exportable;
import pages.groups.MetaData;
import pages.groups.Overviewable;
import pages.groups.PaginationStrategy;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.TableDataObject;
import utils.SoftAssertionHamcrest;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.PX_REPORT_DATE_PATTERN;
import static configuration.helpers.DataHelper.remainDigits;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.not;
import static pages.locators.ElementLocators.CHECKBOX_INPUT;
import static px.objects.offers.OffersPageLocators.CALENDAR_RANGE_DROPDOWN;
import static px.reports.ReportPageLocators.*;
import static utils.CustomMatcher.equalsToEscapeSpace;

/**
 * Created by kgr on 11/16/2016.
 */
public abstract class ReportsPage extends OverviewPage implements Overviewable, Exportable {
    protected SoftAssertionHamcrest hamcrest;
    @FindBy(xpath = CALENDAR_RANGE_DROPDOWN)
    protected CalendarElement calendarElement;
    protected String initialFromPeriod;
    protected String initialToPeriod;
    private String rangeInfo;

    public ReportsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void checkCalendarDateRanges(ReportTestData testData, int daysOffset) {
        // set initial ranges - should be the same as in test data
        this.initialFromPeriod = testData.getFromPeriod();
        this.initialToPeriod = testData.getToPeriod();
        ListMenuElement menuElement = calendarElement.getListMenuElement();
        // exclude custom range - not fully clear how to
        int originalRowsCount = tableElement.getTotalRowsCount();
        List<List<String>> originalCellsText = HTMLHelper.getTableCells(tableElement.getWrappedElement());
        for (int itemIndex = 0; itemIndex <= menuElement.getItemsCount(); itemIndex++) {
            // to prevent 'Page is not loading by timeout' for big data
            if (ignoreDefaultRange(testData, itemIndex + 1)) continue;
            helper.click(CALENDAR_PICKER);
            // custom range
            if (itemIndex == menuElement.getItemsCount()) {
                calendarElement.setRange(testData.getFromPeriodDate(), testData.getToPeriodDate());
            } else menuElement.setByIndex(itemIndex + 1);
            waitPageIsLoaded();
            // check table
            int newRowsCount = tableElement.getTotalRowsCount();
            List<List<String>> newCellsTextList = HTMLHelper.getTableCells(tableElement.getWrappedElement());
            if (isSuitableRange(daysOffset) && !skipEmptyData(originalRowsCount, newRowsCount, testData.hasTotalRow()))
                checkCellsData(originalCellsText, newCellsTextList, isTheSameDateRange(), true, getDateRanges());
            originalCellsText = newCellsTextList;
            originalRowsCount = newRowsCount;
        }
    }

    public void checkCalendarDateRanges(ReportTestData testData) {
        checkCalendarDateRanges(testData, 0);
    }

    protected void checkCalendarDateRanges(ReportTestData testData, String... exclusions) {
        // to make generic
        List<MetaData> columns = getTopicalColumns(testData, exclusions);
        // when no data - cells will be always the
        // set initial ranges - should be the same as in test data
        this.initialFromPeriod = testData.getFromPeriod();
        this.initialToPeriod = testData.getToPeriod();
        ListMenuElement menuElement = calendarElement.getListMenuElement();
        int originalRowsCount = tableElement.getTotalRowsCount();
        List<List<String>> originalCellsText = HTMLHelper.getTableCellsByFields(tableElement, columns);
        // skip active date range
        for (int itemIndex = 0; itemIndex <= menuElement.getItemsCount(); itemIndex++) {
            // to prevent 'Page is not loading by timeout' for big data
            if (ignoreDefaultRange(testData, itemIndex + 1)) continue;
            helper.click(CALENDAR_PICKER);
            // custom range
            if (itemIndex == menuElement.getItemsCount()) {
                calendarElement.setRange(testData.getFromPeriodDate(), testData.getToPeriodDate());
            } else menuElement.setByIndex(itemIndex + 1);
            waitPageIsLoaded();
            // check table
            int newRowsCount = tableElement.getTotalRowsCount();
            List<List<String>> newCellsTextList = HTMLHelper.getTableCellsByFields(tableElement, columns);
            if (!skipEmptyData(originalRowsCount, newRowsCount, testData.hasTotalRow()))
                checkCellsData(originalCellsText, newCellsTextList, isTheSameDateRange(), getDateRanges());
            originalCellsText = newCellsTextList;
            originalRowsCount = newRowsCount;
        }
    }

    /**
     * @param testData   - {@link ReportTestData} inherited data class ;
     * @param exclusions - String array, table column names that supposed to be not take part in data comparison
     */
    protected void checkCalendarDateRangesBigData(ReportTestData testData, String... exclusions) {
        // to make generic
        List<MetaData> columns = getTopicalColumns(testData, exclusions);
        hamcrest = new SoftAssertionHamcrest();
        ListMenuElement menuElement = calendarElement.getListMenuElement();
        // 1st page data
        int originalRowsCount = tableElement.getTotalRowsCount();
        List<List<String>> originalCellsText = HTMLHelper.getTableCellsByFields(tableElement, columns);
        // currently test not by total but pagination pages instead
        int pagesCount = getLastPage();
        for (int itemIndex = 0; itemIndex < menuElement.getItemsCount(); itemIndex++) {
            // to prevent 'Page is not loading by timeout' for big data
            if (ignoreDefaultRange(testData, itemIndex + 1)) continue;
            helper.click(CALENDAR_PICKER);
            // custom range
            if (itemIndex == menuElement.getItemsCount() - 1) {
                calendarElement.setRange(testData.getFromPeriodDate(), testData.getToPeriodDate());
            } else menuElement.setByIndex(itemIndex + 1);
            waitPageIsLoaded();
            // check table - 1st and last pages
            int newRowsCount = tableElement.getTotalRowsCount();
            List<List<String>> newCellsTextList = HTMLHelper.getTableCellsByFields(tableElement, columns);
            // instead possible hidden span with total value or last pagination page
            int newPagesCount = getLastPage();
            hamcrest.assertThat("Pagination pages number is different", newPagesCount, not(equalTo(pagesCount)));
            if (!skipEmptyData(originalRowsCount, newRowsCount, testData.hasTotalRow()))
                checkCellsData(originalCellsText, newCellsTextList, isTheSameDateRange(), true, getDateRanges());
            originalCellsText = newCellsTextList;
            pagesCount = newPagesCount;
            originalRowsCount = newRowsCount;
        }
        hamcrest.assertAll();
    }

    private List<MetaData> getTopicalColumns(ReportTestData testData, String... exclusions) {
        List<String> exclusionsList = new ArrayList<>(Arrays.asList(exclusions));
        exclusionsList.add(SELECT_ALL_COLUMN_FIELD);
        return FieldFormatObject.withoutNonFields(testData.getFields()).
                stream().filter(column -> !exclusionsList.contains(column.getName()))
                .collect(Collectors.toList());
    }

    public void setCustomRanges(ReportTestData testData) {
        helper.click(CALENDAR_PICKER);
        calendarElement.setRange(testData.getFromPeriodDate(), testData.getToPeriodDate());
        waitPageIsLoaded(testData.isBigData() ? 90 : 40);
        setDateRanges();
        log.info(String.format("Actual calendar date range [%s - %s]", initialFromPeriod, initialToPeriod));
        log.info(String.format("Expected calendar date range [%s - %s]", testData.getFromPeriod(), testData.getToPeriod()));
        hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat("Check start date set correctly", initialFromPeriod, equalTo(testData.getFromPeriod()));
        hamcrest.assertThat("Check end date set correctly", initialToPeriod, equalTo(testData.getToPeriod()));
        hamcrest.assertAll();
    }

    public void setCustomRangesShort(ReportTestData testData) {
        helper.click(CALENDAR_PICKER);
        calendarElement.setRange(testData.getFromPeriodDate(), testData.getToPeriodDate());
        waitPageIsLoaded(testData.isBigData() ? 90 : 40);
    }

    protected void setDateRanges() {
        String url = pxDriver.getCurrentUrl();
        String fromPeriodKey = url.contains("FromPeriod") ? "FromPeriod" : "PostDate";
        String toPeriodKey = url.contains("FromPeriod") ? "ToPeriod" : "PostDate2";
        this.initialFromPeriod = DataHelper.getParameterJSONFromURL(url, "filter", fromPeriodKey);
        this.initialToPeriod = DataHelper.getParameterJSONFromURL(url, "filter", toPeriodKey);
    }

    protected String getDateRanges() {
        return rangeInfo;
    }

    // there could be 2 situations:
    protected boolean isTheSameDateRange() {
        log.info(String.format("Previous date period [%s - %s]", this.initialFromPeriod, this.initialToPeriod));
        // set values to check for difference or equals
        String url = pxDriver.getCurrentUrl();
        String fromPeriodKey = url.contains("FromPeriod") ? "FromPeriod" : "PostDate";
        String toPeriodKey = url.contains("FromPeriod") ? "ToPeriod" : "PostDate2";
        String fromPeriod = DataHelper.getParameterJSONFromURL(url, "filter", fromPeriodKey);
        String toPeriod = DataHelper.getParameterJSONFromURL(url, "filter", toPeriodKey);
        log.info(String.format("Current date period [%s - %s]", fromPeriod, toPeriod));
        this.rangeInfo = String.format("Previous date period [%s - %s]", this.initialFromPeriod, this.initialToPeriod) + "\n" +
                String.format("Current date period [%s - %s]", fromPeriod, toPeriod);
        try {
            return this.initialFromPeriod.equals(fromPeriod) && this.initialToPeriod.equals(toPeriod);
        } catch (NullPointerException e) {
            return false;
        } finally {
            this.initialFromPeriod = fromPeriod;
            this.initialToPeriod = toPeriod;
        }
    }

    protected boolean isSuitableRange(int dayOffset) {
        try {
            String url = pxDriver.getCurrentUrl();
            String fromPeriodKey = url.contains("FromPeriod") ? "FromPeriod" : "PostDate";
            String toPeriodKey = url.contains("FromPeriod") ? "ToPeriod" : "PostDate2";
            String fromPeriod = DataHelper.getParameterJSONFromURL(url, "filter", fromPeriodKey);
            String toPeriod = DataHelper.getParameterJSONFromURL(url, "filter", toPeriodKey);
            Date previousStartDate = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, this.initialFromPeriod);
            Date previousEndDate = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, this.initialToPeriod);
            Date followingStartDate = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, fromPeriod);
            Date followingEndDate = DataHelper.getDateByFormatSimple(PX_REPORT_DATE_PATTERN, toPeriod);
            return Math.abs(TimeUnit.DAYS.convert(previousStartDate.getTime() - followingStartDate.getTime(), TimeUnit.MILLISECONDS)) > dayOffset ||
                    Math.abs(TimeUnit.DAYS.convert(previousEndDate.getTime() - followingEndDate.getTime(), TimeUnit.MILLISECONDS)) > dayOffset;
        } catch (RuntimeException e) {
            return false;
        }
    }

    protected boolean ignoreDefaultRange(ReportTestData testData, int itemIndex) {
        ListMenuElement menuElement = calendarElement.getListMenuElement();
        // if not big data neither custom range
        if (!testData.isBigData() || itemIndex > menuElement.getItemsCount()) return false;
        String optionText = menuElement.getItemByIndex(itemIndex).getAttribute("textContent");
        log.info(String.format("DEBUG\ttotal = %d, default range = %s", testData.getItemsTotalCount(), optionText));
        // today or yesterday
        return !remainDigits(optionText).isEmpty() || optionText.toLowerCase().contains("month");
    }

    protected boolean skipEmptyData(int previousRecordsCount, int currentRecordsCount, boolean hasTotal) {
        // decrease if total
        previousRecordsCount = hasTotal ? --previousRecordsCount : previousRecordsCount;
        currentRecordsCount = hasTotal ? --currentRecordsCount : currentRecordsCount;
        log.info(String.format("DEBUG\tprevious rows = '%d'\tcurrent rows = '%d'\thasTotal = '%s'",
                previousRecordsCount, currentRecordsCount, hasTotal));
        return previousRecordsCount == 0 && currentRecordsCount == 0;
    }

    // =============================== Report Filters ===============================

    public void setFilter(String filterLocator, String filterValue) {
        FilteredDropDown filter = new FilteredDropDown(helper.getElement(filterLocator));
        String filterTitle = filter.getValue();
        log.info(String.format("Set '%s' filter by value '%s'", filterTitle, filterValue));
        filter.setByText(filterValue);
        waitPageIsLoaded();
        filter = new FilteredDropDown(helper.getElement(filterLocator));
        assertThat(String.format("'%s' is filtered by '%s'", filterTitle, filterValue),
//                filter.getValue(), equalToIgnoringCase(filterValue));
                filter.getValue(), equalsToEscapeSpace(filterValue));
    }

    protected void setFilter(String filterLocator, Integer... indexes) {
        FilteredDropDown filter = new FilteredDropDown(helper.getElement(filterLocator));
        String filterTitle = filter.getValue();
        log.info(String.format("Set '%s' filter by indexes '%s'", filterTitle, Arrays.toString(indexes)));
        Arrays.asList(indexes).forEach(index -> {
            new FilteredDropDown(helper.getElement(filterLocator)).setByIndex(index);
            waitPageIsLoaded();
        });
        filter = new FilteredDropDown(helper.getElement(filterLocator));
        String filterValue = filter.getValue();
        assertThat(String.format("'%s' is filtered by '%s'", filterTitle, filterValue),
                filterValue, not(containsString(filterTitle)));
    }

    protected void setFilter(String filterLocator, List<String> filterValues) {
        FilteredDropDown filter = new FilteredDropDown(helper.getElement(filterLocator));
        String filterTitle = filter.getValue();
        log.info(String.format("Set '%s' filter by values '%s'", filterTitle, filterValues));
//        filter.setByText(filterValues);
        filter.setByTitle(filterValues);
        waitPageIsLoaded();
        filter = new FilteredDropDown(helper.getElement(filterLocator));
        if (filterValues.size() > 5) {
            assertThat(String.format("'%s' is filtered by '%s'", filterTitle, filterValues),
                    filter.getValue(), not(containsString(filterTitle)));
        } else {
            List<String> actualValues = Arrays.asList(filter.getValue().split(", "));
            Collections.sort(filterValues);
            Collections.sort(actualValues);
            log.info("After sorting:\nExpected values\t" + filterValues + "\nActualValues\t" + actualValues);
            assertThat(String.format("'%s' is filtered by '%s'", filterTitle, filterValues),
                    actualValues.toString().replaceAll("(^\\[)|(\\]$)", ""),
//                    containsString(filterValues.toString().replaceAll("(^\\[)|(\\]$)", "")));
                    equalsToEscapeSpace(filterValues.toString().replaceAll("(^\\[)|(\\]$)", "")));
        }
    }

    // possibly change to matcher containsInAnyOrder
    protected void checkFilterItems(String filterLocator, Collection<String> expectedItems) {
        log.info(String.format("Check dependent filter '%s' for items\t%s", filterLocator, expectedItems));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        helper.waitUntilDisplayed(filterLocator);
        hamcrest.assertThat(String.format("Filter by locator '%s' visible", filterLocator),
                helper.isElementAccessible(filterLocator), equalTo(true));
        FilteredDropDown filter = new FilteredDropDown(helper.getElement(filterLocator));
        filter.expand();
        List<String> actualItems = filter.getItems();
        if (expectedItems.size() == 0)
            hamcrest.assertThat("There are no items in filter\n" + actualItems, actualItems.size(), equalTo(0));
        else {
            log.info(String.format("Items in dropDown = %s\nitems in test data(response) = %s", actualItems, expectedItems));
            // sort cause order in UI and backend response do not match
            List<String> tempList = new ArrayList<>(expectedItems);
            // if there are too many items - in UI it could not be more than 20
            if (expectedItems.size() > 20)
                tempList = tempList.subList(0, 20);
            // missed errors
            tempList.removeAll(actualItems);
            tempList.forEach(item ->
                    hamcrest.assertThat(String.format("Item '%s' is missed", item), false)
            );
            // extra errors
            actualItems.removeAll(expectedItems);
            actualItems.forEach(item ->
                    hamcrest.assertThat(String.format("Extra item '%s'", item), false)
            );
        }
        hamcrest.assertAll();
        filter.expand();
        waitPageIsLoaded();
    }

    protected void checkFilter(String filterLocator, String filterValue) {
        FilteredDropDown filter = new FilteredDropDown(helper.getElement(filterLocator));
        hamcrest.assertThat(String.format("Filter '%s' is set to value '%s'", filterLocator, filterValue),
//                filter.getValue(), containsString(filterValue));
                filter.getValue(), equalsToEscapeSpace(filterValue));
    }

    protected void resetFilter(String filterLocator, String filterValue, boolean isKeepDefaultValue) {
        FilteredDropDown filter = new FilteredDropDown(helper.getElement(filterLocator));
        filter.resetFilter();
        filter.expand();
        helper.pause(2000);
        waitPageIsLoaded();
        filter = new FilteredDropDown(helper.getElement(filterLocator));
//        Matcher<String> matcher = isKeepDefaultValue ? containsString(filterValue) : not(containsString(filterValue));
        Matcher<String> matcher = isKeepDefaultValue ? equalToIgnoringCase(filterValue) : not(equalToIgnoringCase(filterValue));
        hamcrest.assertThat(String.format("Filter '%s' is reseted", filterLocator), filter.getValue(), matcher);
    }

    protected void resetFilter(String filterLocator, String filterValue, int tableRows) {
        resetFilter(filterLocator, filterValue, false);
        log.info("Expected current total = " + tableRows);
//        hamcrest.assertThat("Report table returns to previous state", tableElement.getTotalRowsCount(), equalTo(tableRows));
    }

    protected void resetFilter(String filterLocator, String filterValue, int tableRows, boolean isKeepDefaultValue) {
        resetFilter(filterLocator, filterValue, isKeepDefaultValue);
        hamcrest.assertThat("Report table returns to previous state", tableElement.getTotalRowsCount(), equalTo(tableRows));
    }

    protected void resetFilter(String filterLocator, String filterValue, String dependentFilterLocator) {
        resetFilter(filterLocator, filterValue, false);
        hamcrest.assertThat(String.format("Filter '%s' became invisible", dependentFilterLocator),
                helper.isElementAccessible(dependentFilterLocator), equalTo(false));
    }

    protected void resetFilter(String filterLocator, String filterValue, String dependentFilterLocator, int tableRows) {
        resetFilter(filterLocator, filterValue, dependentFilterLocator);
        hamcrest.assertThat("Report table returns to previous state", tableElement.getTotalRowsCount(), equalTo(tableRows));
    }

    /**
     * Default verification for empty table and rows quantity
     *
     * @param dataList - items by certain filter in as JSONArray
     * @param strategy - indicates how many elements (rows) take from JSONArray according to reports table (items per page)
     * @return object with proper JSON array length
     */
    protected TableDataObject checkDefaultCellsData(JSONArray dataList, PaginationStrategy strategy) {
        log.info("Table verification after set filter");
        log.info(String.format("DEBUG\tData length = '%d',\ttable rows = '%d'", dataList.length(), tableElement.getTotalRowsCount()));
        if (dataList.length() == 0) {
            checkEmptyTable();
            return new TableDataObject(dataList, new ArrayList<>());
        }
        // get know which items per page is active
        int rowsPerPage = dataList.length() > 0 ? getActiveItemsPerPage() : 0;
        JSONArray jsonArray = dataList.length() > rowsPerPage
                ? new JSONArray(dataList.toList().subList(0, strategy.getRowsPerPage(rowsPerPage))) : dataList;
        log.info(String.format("Check table by rows according to json array, expected row numbers - %d", jsonArray.length()));
//        List<List<String>> tableCellsText = helper.getTextFromElementsListParallel(tableElement.getTableCellsByRows());
        List<List<String>> tableCellsText = HTMLHelper.getTableCells(tableElement.getWrappedElement());
        if (dataList.length() <= rowsPerPage)
            assertThat("Check that rows number in table equals to received in response", tableCellsText.size(), equalTo(dataList.length()));
        else
            assertThat("Check that rows number in table equals to received 1st page in response", tableCellsText.size(), equalTo(jsonArray.length()));
        return new TableDataObject(jsonArray, tableCellsText);
    }

    protected void checkCellsData(ReportTestData testData, JSONArray dataList) {
        long start = System.currentTimeMillis();
        TableDataObject tableDataObject = checkDefaultCellsData(dataList, testData);
        checkCellsData(tableDataObject.getDataList(), tableDataObject.getTableCellsText(), testData.getFields());
        log.info(String.format("Time to check %d rows with tooltips = %d", tableDataObject.getDataList().length(), System.currentTimeMillis() - start));
    }

    protected void checkCellsData(ReportTestData testData, JSONArray dataList, List<FieldFormatObject> fields) {
        long start = System.currentTimeMillis();
        TableDataObject tableDataObject = checkDefaultCellsData(dataList, testData);
        checkCellsData(tableDataObject.getDataList(), tableDataObject.getTableCellsText(), fields);
        log.info(String.format("Time to check %d rows with tooltips = %d", tableDataObject.getDataList().length(), System.currentTimeMillis() - start));
    }

    protected void checkCellsDynamicTooltipsData(ReportTestData testData, JSONArray dataList, List<String> buyerCategories) {
        long start = System.currentTimeMillis();
        TableDataObject tableDataObject = checkDefaultCellsData(dataList, testData);
        checkCellsData(tableDataObject.getDataList(), tableDataObject.getTableCellsText(), testData.getFields(), buyerCategories);
        log.info(String.format("Time to check %d rows with tooltips = %d", tableDataObject.getDataList().length(), System.currentTimeMillis() - start));
    }

    protected void checkCellsDynamicTooltipsData(ReportTestData testData, JSONArray dataList, List<FieldFormatObject> fields, List<String> buyerCategories) {
        long start = System.currentTimeMillis();
        TableDataObject tableDataObject = checkDefaultCellsData(dataList, testData);
        checkCellsData(tableDataObject.getDataList(), tableDataObject.getTableCellsText(), fields, buyerCategories);
        log.info(String.format("Time to check %d rows with tooltips = %d", tableDataObject.getDataList().length(), System.currentTimeMillis() - start));
    }

    // check filters accordance under roles
    public void checkFiltersAccordance(ReportTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<String> expectedFilers = new ArrayList<>(testData.filters());
        if (testData.hasGraphics()) expectedFilers.add("type");
        List<String> temp = new ArrayList<>(expectedFilers);
        // check all filters are visible
        expectedFilers.forEach(filter ->
                hamcrest.assertThat(String.format("Filter '%s' is not displayed in UI", filter),
                        helper.isElementAccessible(String.format(GENERIC_PARAMETRIZED_FILTER, filter)))
        );
        List<String> actualFilters = helper.getElements(GENERIC_FILTER).stream().filter(WebElement::isDisplayed)
                .map(element -> element.getAttribute("data-field-name")).collect(Collectors.toList());
        // missed filters
        expectedFilers.removeAll(actualFilters);
        expectedFilers.forEach(filter ->
                hamcrest.assertThat(String.format("Filter '%s' is missed", filter), false)
        );
        // extra filters
        actualFilters.removeAll(temp);
        actualFilters.forEach(filter ->
                hamcrest.assertThat(String.format("Extra filter '%s'", filter), false)
        );
        hamcrest.assertAll();
    }

    public void checkFiltersAccordance(List<Valued> filerEnum) {
        List<String> expectedFilers = filerEnum.stream().map(Valued::getValue).collect(Collectors.toList());
        List<String> temp = new ArrayList<>(expectedFilers);
        // check all filters are visible
        expectedFilers.forEach(filter ->
                hamcrest.assertThat(String.format("Filter '%s' is not displayed in UI", filter),
                        helper.isElementAccessible(String.format(GENERIC_PARAMETRIZED_FILTER, filter)))
        );
        List<String> actualFilters = helper.getElements(GENERIC_FILTER).stream()
                .map(element -> element.getAttribute("data-field-name")).collect(Collectors.toList());
        // missed filters
        expectedFilers.removeAll(actualFilters);
        expectedFilers.forEach(filter ->
                hamcrest.assertThat(String.format("Filter '%s' is missed", filter), false)
        );
        // extra filters
        actualFilters.removeAll(temp);
        actualFilters.forEach(filter ->
                hamcrest.assertThat(String.format("Extra filter '%s'", filter), false)
        );
        hamcrest.assertAll();
    }

    // for reports with 'select all' column
    public void checkSelectAllColumn() {
        log.info("Check 'select all' rows functional");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        try {
            WebElement selectAllCell = helper.getElement(SELECT_ALL_COLUMN);
            // check
            helper.getElement(selectAllCell, CHECKBOX_INPUT).click();
            List<WebElement> columnCells = tableElement.getCellsByColumnIndex(tableElement.getCellIndex(selectAllCell) + 1);
            for (int i = 1; i < columnCells.size(); i++) {
                hamcrest.assertThat(String.format("Row [%d] is selected after select all", i + 1),
                        Boolean.parseBoolean(helper.getElement(columnCells.get(i),
                                CHECKBOX_INPUT).getAttribute("checked")));
            }
            // uncheck
            helper.getElement(selectAllCell, CHECKBOX_INPUT).click();
            columnCells = tableElement.getCellsByColumnIndex(tableElement.getCellIndex(selectAllCell) + 1);
            for (int i = 1; i < columnCells.size(); i++) {
                hamcrest.assertThat(String.format("Row [%d] is unselected after select all", i + 1),
                        !Boolean.parseBoolean(helper.getElement(columnCells.get(i),
                                CHECKBOX_INPUT).getAttribute("checked")));
            }
        } catch (NoSuchElementException e) {
            hamcrest.assertThat("Checkbox could not be found in report table", false);
        }
        hamcrest.assertAll();
    }

    @Override
    public void waitPageIsLoaded() {
        super.waitPageIsLoaded(90);
    }
}