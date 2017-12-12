package pages.groups;

import configuration.helpers.DataHelper;
import dto.TestDataException;
import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.table.FilterElement;
import elements.table.TableElement;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.WebElement;
import px.reports.ReportTestData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Set;

import static configuration.helpers.DataHelper.getRandomValueFromList;
import static configuration.helpers.DataHelper.getSetFromJSONArrayByKey;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static pages.locators.ElementLocators.*;

/**
 * Created by kgr on 7/6/2017.
 */

public interface Searchable extends Wait {

    // ============================== PX Objects Verification ==============================

    default void checkTableByCellValue(String cellValue, String headerName) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        TableElement tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        // check table is not empty
        assertThat("Table is not empty", tableElement.isTableEmpty(), equalTo(false));
        hamcrest.assertThat(String.format("Value '%s' is present in filtered table", cellValue),
                getCellByValue(headerName), equalTo(cellValue));
        hamcrest.assertAll();
    }

    /**
     * Set search filter with cellValue;
     * Find cell in table by cellValue and headerName
     *
     * @param cellValue  - cellValue in table and value for search filter at the same time
     * @param headerName - fieldName of the table column
     */
    default void checkTableByCellValuePresence(String cellValue, String headerName) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        TableElement tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        int rowsCount = tableElement.getTotalRowsCount();
        // filter by cellValue
        filterTable(cellValue);
        // check table is not empty
        assertThat("Table is not empty", tableElement.isTableEmpty(), equalTo(false));
        hamcrest.assertThat(String.format("Value '%s' is present in filtered table", cellValue),
                getCellByValue(headerName), equalTo(cellValue));
        // check reset
        resetSearch();
        hamcrest.assertThat("After search filter reset there are the same rows in table",
                tableElement.getTotalRowsCount(), equalTo(rowsCount));
        hamcrest.assertAll();
    }

    /**
     * Set search filter by specific category;
     * Than set search filter with cellValue;
     * Find cell in table by cellValue and headerName
     *
     * @param cellValue  - cellValue in table and value for search filter at the same time
     * @param headerName - fieldName of the table column
     * @param searchBy   - item to be search by, e.g. OfferName, OfferId etc
     */
    default void checkTableByCellValuePresence(String cellValue, String headerName, String searchBy) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        TableElement tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        int rowsCount = tableElement.getTotalRowsCount();
        // filter by cellValue
        filterTable(cellValue, searchBy);
        // check table is not empty
        assertThat("Table is not empty", tableElement.isTableEmpty(), equalTo(false));
        hamcrest.assertThat(String.format("Value '%s' is present in filtered table", cellValue),
                getCellByValue(headerName), equalTo(cellValue));
        // check reset
        resetSearch();
        hamcrest.assertThat("After search filter reset there are the same rows in table",
                tableElement.getTotalRowsCount(), equalTo(rowsCount));
        hamcrest.assertAll();
    }

    // ============================== Search filter ==============================
    default void filterTable(String filterValue) {
        ElementsHelper helper = HelperSingleton.getHelper();
        FilterElement searchFilter = new FilterElement(helper.getElement(SEARCH_FILTER));
        searchFilter.setByText(filterValue);
        helper.pause(2000);
        waitPageIsLoaded();
    }

    default void filterTable(String filterValue, String searchBy) {
        ElementsHelper helper = HelperSingleton.getHelper();
        FilterElement searchFilter = new FilterElement(helper.getElement(SEARCH_FILTER));
        searchFilter.setByText(filterValue, searchBy);
        helper.pause(2000);
        waitPageIsLoaded();
    }

    default void resetSearch() {
        filterTable("");
    }

    // ============================== Table {find + actions} ==============================
    default WebElement getCellByValue(String cellValue, String headerName) {
        ElementsHelper helper = HelperSingleton.getHelper();
        TableElement tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        // set columnIndex by headerName
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, headerName)));
        // find rowIndex by cellValue within specific column
        int rowIndex = tableElement.getRowIndexByCellText(cellValue, columnIndex + 1);
        return tableElement.getCellAt(rowIndex, columnIndex + 1);
    }

    default WebElement getCellByValue(String headerName) {
        ElementsHelper helper = HelperSingleton.getHelper();
        TableElement tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        // set columnIndex by headerName
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, headerName)));
        return tableElement.getCellAt(1, columnIndex + 1);
    }

    default JSONObject getOneRowFromTableRows(ReportTestData testData) {
        // validation if there any available data
        JSONArray allRowsArray = testData.getAllRowsArray();
        if (allRowsArray.length() == 0)
            throw new TestDataException(String.format("There is no data in date range [%s - %s]", testData.getFromPeriod(), testData.getToPeriod()));
        // choose any row from array
        int rowIndex = DataHelper.getRandomInt(allRowsArray.length());
        System.out.println(String.format("Get '%d' row from total '%d'", rowIndex, allRowsArray.length()));
        return allRowsArray.getJSONObject(rowIndex);
    }

    default String getAnyValueFromColumn(JSONArray allRowsArray, String key) {
        if (allRowsArray.length() == 0)
            throw new TestDataException("There is no data in json array " + allRowsArray);
        Set<String> values = getSetFromJSONArrayByKey(allRowsArray, key);
        if (values.isEmpty())
            throw new TestDataException(String.format("There is no data in json array by key '%s'", key));
        return getRandomValueFromList(new ArrayList<>(values));
    }

}
