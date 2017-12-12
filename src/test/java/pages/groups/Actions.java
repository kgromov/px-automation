package pages.groups;

import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.dropdown.TableDropDown;
import elements.table.TableElement;
import org.openqa.selenium.WebElement;

import java.util.List;

import static pages.locators.ElementLocators.*;
import static px.reports.ReportPageLocators.SELECT_ALL_COLUMN;

/**
 * Created by kgr on 7/6/2017.
 */
public interface Actions extends Wait {
    // ============================== Table {find + actions} ==============================
    default WebElement findCellByValue(TableElement tableElement, String cellValue) {
        ElementsHelper helper = HelperSingleton.getHelper();
        // find rowIndex by cellValue
        int rowIndex = tableElement.getRowIndexByCellText(cellValue);
        // set actions column index
        int actionColumnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL));
        return tableElement.getCellAt(rowIndex, actionColumnIndex + 1);
    }

    default WebElement findCellByValue(TableElement tableElement, String cellValue, String headerName) {
        ElementsHelper helper = HelperSingleton.getHelper();
        // set columnIndex by headerName
        int columnIndex = getHeaderIndex(tableElement, headerName);
        // find rowIndex by cellValue within specific column
        int rowIndex = tableElement.getRowIndexByCellText(cellValue);
        // click on actions cell in specific row
        return tableElement.getCellAt(rowIndex, columnIndex + 1);
    }

    default int getHeaderIndex(TableElement tableElement, String headerName) {
        ElementsHelper helper = HelperSingleton.getHelper();
        return tableElement.getCellIndex(helper.getElement(tableElement, String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, headerName)));
    }

    default void setActionByItemText(String itemText) {
        ElementsHelper helper = HelperSingleton.getHelper();
        helper.waitUntilDisplayed(DROPDOWN_TABLE_CONTAINER);
        TableDropDown tableDropDown = new TableDropDown(helper.getElement(DROPDOWN_TABLE_CONTAINER));
        tableDropDown.setByText(itemText);
        waitPageIsLoaded();
    }

    default void setActionByItemLink(WebElement cell, String itemLink) {
        ElementsHelper helper = HelperSingleton.getHelper();
        helper.waitUntilDisplayed(DROPDOWN_TABLE_CONTAINER);
        helper.getElement(cell, itemLink).click();
        waitPageIsLoaded();
    }

    default void selectRowByCellValue(TableElement tableElement, String cellValue, String headerName) {
        ElementsHelper helper = HelperSingleton.getHelper();
        // set columnIndex by headerName
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, headerName)));
        // find rowIndex by cellValue within specific column
        int rowIndex = tableElement.getRowIndexByCellText(cellValue);
        // select row - click on checkbox of proper row cell
        WebElement cell = tableElement.getCellAt(rowIndex, columnIndex + 1);
        helper.getElement(cell, CHECKBOX_INPUT).click();
    }

    default void selectRowByCellValue(TableElement tableElement, List<String> cellValues, String headerName) {
        ElementsHelper helper = HelperSingleton.getHelper();
        // set columnIndex by headerName
        int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, headerName)));
        int checkboxIndex = tableElement.getCellIndex(helper.getElement(SELECT_ALL_COLUMN)) + 1;
        // set all rows
        cellValues.forEach(cellValue -> {
            // find rowIndex by cellValue within specific column
            int rowIndex = tableElement.getRowIndexByCellText(cellValue, columnIndex);
            // select row - click on checkbox of proper row cell
            WebElement cell = tableElement.getCellAt(rowIndex, checkboxIndex);
            helper.getElement(cell, CHECKBOX_INPUT).click();
        });
    }

    default void confirm(boolean confirm) {
        ElementsHelper helper = HelperSingleton.getHelper();
        helper.waitUntilDisplayed(DELETE_POPUP);
        if (confirm) helper.click(POPUP_CONFIRM);
        else helper.click(POPUP_DISMISS);
        helper.waitUntilToBeInvisible(DELETE_POPUP);
    }
}
