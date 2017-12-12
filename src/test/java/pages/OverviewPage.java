package pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import dto.CheckTestData;
import elements.dropdown.TableDropDown;
import elements.menu.MenuElement;
import elements.popup.WelcomePopup;
import elements.table.FilterElement;
import elements.table.TableElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import px.objects.InstancesTestData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static config.Config.isAdmin;
import static config.DashboardMenuEnum.getMenuItems;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static pages.locators.DashboardPageLocators.*;
import static pages.locators.ElementLocators.*;

/**
 * Created by kgr on 10/25/2016.
 */
public abstract class OverviewPage extends NewPaginationPage {
    @FindBy(xpath = LEFT_MENU)
    protected MenuElement menuElement;
    /* @FindBy(xpath = ITEMS_TABLE)
     protected TableElement tableElement;*/
    @FindBy(xpath = SEARCH_FILTER)
    private FilterElement searchFilter;
    @FindBy(xpath = COLUMNS_FILTER)
    protected FilterElement columnsFilter;
    private WelcomePopup popup;

    private TableDropDown tableDropDown;
    // additional
    protected String instanceGroup;

    public OverviewPage(PXDriver pxDriver) {
        super(pxDriver);
        this.instanceGroup = this.getClass().getSimpleName().replace("Page", "").toLowerCase();
    }

    @Override
    public OverviewPage setTable(String container) {
        helper.waitUntilDisplayed(container + ITEMS_TABLE.substring(1));
        this.tableElement = new TableElement(helper.getElement(container, ITEMS_TABLE));
        return this;
    }

    public OverviewPage fillInPage() {
        closeWelcomePopup();
        helper.waitUntilDisplayed(String.format(CREATE_INSTANCE_PARAMETERIZED_BUTTON, instanceGroup));
        helper.click(String.format(CREATE_INSTANCE_PARAMETERIZED_BUTTON, instanceGroup));
        pxDriver.waitForAjaxComplete();
        return this;
    }

    public void checkPage() {
        closeWelcomePopup();
        log.info("Check that overview table");
//        helper.waitUntilDisplayed(ITEMS_TABLE);
        helper.waitUntilDisplayed(tableElement);
        waitPageIsLoaded(40);
        displayAllTableColumns();
    }

    public void displayAllTableColumns() {
        columnsFilter.setCheckboxes();
    }

    // ============================== Dashboard menu ==============================
    protected OverviewPage navigateToObjects() {
        throw new UnsupportedOperationException("Implementation required in child class - " + this.getClass().getName());
    }

    protected void waitMenuItems() {
        long start = System.currentTimeMillis();
        // workaround
        int itemsCount = getMenuItems().size();
        try {
            helper.waitUntilDisplayed(menuElement.getWrappedElement());
            waitPageIsLoaded();
            helper.waitUntilChildrenNumber(By.xpath(LEFT_MENU + "//a"), itemsCount);
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Not all menu items have been loaded/present. Expected number is '%d'", itemsCount));
        } finally {
            log.info(String.format("Loading page with main menu items = '%d' seconds",
                    TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
        }
    }

    protected void setMenu(DashboardMenuEnum menuEnum) {
        waitMenuItems();
        closeWelcomePopup();
        menuElement.setByText(menuEnum.getValue());
        closeWelcomePopup();
    }

    // popup
    public void closeWelcomePopup() {
        if (helper.isElementPresent(WELCOME_POPUP, 2)) {
            popup.closePopup();
            helper.waitUntilToBeInvisible(WELCOME_POPUP);
        }
        // publisher/buyer user support popup
        closeSupportPopup();
    }

    protected void closeSupportPopup() {
        if (!isAdmin()) {
            WebElement minimize = helper.getElementInAnyFrame(MINIMIZE_SUPPORT_POPUP);
            if (minimize != null) {
                if (minimize.isDisplayed()) {
                    log.info("Minimize support popup");
                    minimize.click();
                    helper.pause(500);
//                helper.waitUntilToBeInvisible(SUPPORT_POPUP); // to make this it's required to reswitch to frame
                }
                pxDriver.getWrappedDriver().switchTo().defaultContent();
            }
        }
    }

    // ============================== Table {filtering + actions} ==============================
    public void findAndEditByName(InstancesTestData testData) {
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL));
        int rowIndex = tableElement.getRowIndexByCellText(testData.getName());
        WebElement cell = tableElement.getCellAt(rowIndex, columnIndex + 1);
        cell.click();
        helper.waitUntilDisplayed(DROPDOWN_TABLE_CONTAINER);
//        helper.getElement(cell, key.equals("Edit") ? EDIT_LINK : DELETE_LINK).click();
    }

    public void findAndEditByValue(String value) {
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL));
        int rowIndex = tableElement.getRowIndexByCellText(value);
        WebElement cell = tableElement.getCellAt(rowIndex, columnIndex + 1);
        cell.click();
//        helper.waitUntilDisplayed(DROPDOWN_TABLE_CONTAINER);
//        helper.getElement(cell, key.equals("Edit") ? EDIT_LINK : DELETE_LINK).click();
    }

    public void setAction(String menuItem) {
        tableDropDown.setByText(menuItem);
    }

    // ==============================Check created instance in table ==============================
    public void checkCreatedInstanceByName(InstancesTestData instancesTestData) {
        checkCreatedInstanceByName(instancesTestData, null);
    }

    public void checkCreatedInstanceByName(InstancesTestData instancesTestData, String headerName) {
        checkPage();
        log.info(String.format("Check that created instance '%s' present in %ss overview table", instancesTestData.getName(), instancesTestData.getInstanceGroup()));
        int columnIndex = headerName == null ? 1 : tableElement.getColumnIndexByHeaderText(headerName) + 1;
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat(String.format("%ss table is not empty", instancesTestData.getInstanceGroup()), tableElement.isTableEmpty(), equalTo(false));
        if (headerName == null) filterInstanceInTable(instancesTestData.getName());
        else filterInstanceInTable(instancesTestData.getName(), headerName);
        String nameInTable = tableElement.getCellTextAt(1, columnIndex);
        hamcrest.assertThat(String.format("'%s' is present in %ss filtered table",
                instancesTestData.getName(), instancesTestData.getInstanceGroup()), nameInTable, equalTo(instancesTestData.getName()));
        hamcrest.assertAll();
    }

    public void checkCreatedInstanceByID(InstancesTestData instancesTestData, int columnIndex) {
        checkPage();
        log.info(String.format("Check that created instance with id'%s' present in %ss overview table", instancesTestData.getId(), instancesTestData.getInstanceGroup()));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat(String.format("%ss table is not empty", instancesTestData.getInstanceGroup()), tableElement.isTableEmpty(), equalTo(false));
        filterInstanceInTable(instancesTestData.getId());
        String idInTable = tableElement.getCellTextAt(1, columnIndex);
        hamcrest.assertThat(String.format("'%s' is present in %ss filtered table",
                instancesTestData.getId(), instancesTestData.getInstanceGroup()), idInTable, equalTo(instancesTestData.getName()));
        hamcrest.assertAll();
    }

    // later on get rid of useFilter when @data-field-name will be on all headers
    public void checkCreatedInstanceByName(String name, int columnIndex, boolean useFilter) {
        checkPage();
        log.info(String.format("Check that created instance '%s' present in users overview table", name));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat(String.format("'%s' table is not empty", instanceGroup), tableElement.isTableEmpty(), equalTo(false));
        if (useFilter) filterInstanceInTable(name);
        int rowIndex = tableElement.getRowIndexByCellText(name, columnIndex);
        String nameInTable = tableElement.getCellTextAt(rowIndex, columnIndex + 1);
        hamcrest.assertThat(String.format("'%s' is present in uses filtered table", name), nameInTable, equalTo(name));
        hamcrest.assertAll();
    }

    public void checkCreatedInstanceByID(InstancesTestData instancesTestData) {
        checkCreatedInstanceByID(instancesTestData, 1);
    }

    public void checkCreatedInstanceByID(InstancesTestData instancesTestData, String headerName) {
        checkPage();
        checkCreatedInstanceByID(instancesTestData, tableElement.getColumnIndexByHeaderText(headerName));
    }

    // ============================== Check deleted instance in table ==============================
    public OverviewPage checkDeletedInstanceByName(InstancesTestData instancesTestData) {
        return checkDeletedInstanceByName(instancesTestData, null);
    }

    public OverviewPage checkDeletedInstanceByName(InstancesTestData instancesTestData, String headerName) {
        checkPage();
        log.info(String.format("Check that deleted instance '%s' absent in %ss overview table", instancesTestData.getName(), instancesTestData.getInstanceGroup()));
/*        int columnIndex = headerName == null ? 0 : tableElement.getColumnIndexByHeaderText(headerName);
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        hamcrest.assertThat(String.format("%ss table is not empty", testData.getInstanceGroup()), tableElement.isTableEmpty(), equalTo(false));*/
        if (headerName == null) filterInstanceInTable(instancesTestData.getName());
        else filterInstanceInTable(instancesTestData.getName(), headerName);
        checkEmptyTable();
        return this;
    }

    protected void filterInstanceInTable(String filterValue) {
        helper.waitUntilDisplayed(searchFilter.getWrappedElement());
        searchFilter.setByText(filterValue);
        pxDriver.waitForAjaxComplete();
        waitPageIsLoaded(40);
    }

    protected void filterInstanceInTable(String filterValue, String searchBy) {
        helper.waitUntilDisplayed(searchFilter.getWrappedElement());
        searchFilter.setByText(filterValue, searchBy);
        pxDriver.waitForAjaxComplete();
        waitPageIsLoaded(40);
    }

    // ============================== Check table cells data  ==============================
    protected void checkCellsData(List<List<String>> rowsList, List<List<String>> newRowList, boolean isEqual, String details) {
        checkCellsData(rowsList, newRowList, isEqual, false, details);
    }

    protected void checkCellsData(List<List<String>> rowsList, List<List<String>> newRowList, boolean isEqual, boolean byRows, String details) {
        int differentCells = 0;
        if (!byRows) assertThat("Columns numbers are different", rowsList.size(), equalTo(newRowList.size()));
        int rowsCount = Math.min(rowsList.size(), newRowList.size());
        for (int rowIndex = 0; rowIndex < rowsCount; rowIndex++) {
            differentCells += getRowDataDifference(rowsList.get(rowIndex), newRowList.get(rowIndex));
        }
        log.info(String.format("Different cells = '%d'", differentCells));
        // if there are different rows table would be different
        if ((!rowsList.isEmpty() || !newRowList.isEmpty()) && rowsList.size() != newRowList.size()) {
            List<List<String>> remainList = rowsList.size() > newRowList.size()
                    ? rowsList.subList(rowsCount, rowsList.size()) : newRowList.subList(rowsCount, newRowList.size());
            try {
                differentCells += remainList.size() * remainList.get(0).size();
            } catch (IndexOutOfBoundsException e) {
                log.info(String.format("HOW IT COULD BE?\trowsList = '%s',\tnewRowList = '%s',\tremainList = '%s'",
                        rowsList, newRowList, remainList));
            }
            log.info(String.format("Different cells by diff rows = '%d'", differentCells));
        }
        if (isEqual)
            assertThat(String.format("Table cells do not have different data\nDetails:\t%s", details), differentCells, equalTo(0));
        else
            assertThat(String.format("Table cells have different data\nDetails:\t%s", details), differentCells, greaterThanOrEqualTo(1));
    }

    protected void checkCellsData(List<List<String>> rowsList, List<List<String>> newRowList, boolean isEqual, float minDiff, String details) {
        int differentCells = 0;
        assertThat("Row numbers are different", rowsList.size(), equalTo(newRowList.size()));
        for (int rowIndex = 0; rowIndex < rowsList.size(); rowIndex++) {
            differentCells += getRowDataDifference(rowsList.get(rowIndex), newRowList.get(rowIndex));
        }
        float difference = differentCells / (rowsList.size() * rowsList.get(0).size());
        log.info(String.format("Different cells = '%d'", differentCells));
        if (isEqual)
            assertThat(String.format("Table cells do not have different data\nDetails:\t%s", details), differentCells, equalTo(0));
        else
            assertThat(String.format("Table cells have different data\nDetails:\t%s", details), difference, greaterThan(minDiff));
    }

    private int getRowDataDifference(List<String> dataList1, List<String> dataList2) {
        List<String> copyList = dataList1.size() == 0 ? new ArrayList<>(dataList2) : new ArrayList<>(dataList1);
        if (!copyList.isEmpty())
            assertThat("Data list does not contain empty elements\t" + copyList, copyList.contains(""), equalTo(false));
        copyList.removeAll(dataList1.size() == 0 ? dataList1 : dataList2);
        log.info("DEBUG\tList after removing" + copyList);
        return copyList.size();
    }

    private boolean skipEmptyData(List<List<String>> rowsList, List<List<String>> newRowList) {
        int previousRecordsCount = rowsList.size();
        int currentRecordsCount = newRowList.size();
        boolean hasTotal = false;
        try {
            hasTotal = previousRecordsCount == 1 && rowsList.get(0).get(0).equals("Total");
        } catch (IndexOutOfBoundsException ignored) {
        }
        return previousRecordsCount == 0 & currentRecordsCount == 0 || hasTotal;
    }

    // ============================== Check instance requesting by response  ==============================
    public void checkCreatedInstanceByResponse(InstancesTestData instancesTestData) {
        checkPage();
        CheckTestData.checkInstanceCreated(instanceGroup, instancesTestData);
    }

    public void checkDeletedInstanceByResponse(InstancesTestData instancesTestData) {
        checkPage();
        CheckTestData.checkInstanceDeleted(instanceGroup, instancesTestData);
    }
}