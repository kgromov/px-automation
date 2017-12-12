package elements.table;

import configuration.helpers.DataHelper;
import elements.SuperTypifiedElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kgr on 10/7/2016.
 */
public class TableElement extends SuperTypifiedElement {

    public TableElement(WebElement element) {
        super(element);
    }

    /**
     * @return all table cells data by rows
     */
    public List<List<WebElement>> getTableCellsByRows() {
        long start = System.currentTimeMillis();
        try {
            log.info("Getting all cells data by rows");
            return getWrappedElement()
                    .findElements(By.cssSelector("tbody > tr"))
                    .stream()
                    .map(rowElement -> rowElement.findElements(By.xpath(".//td")))
                    .filter(row -> row.size() > 0) // ignore rows with no <td> tags
                    .collect(Collectors.toList());
        } finally {
            log.info(String.format("Get all cells by rows, time = '%d'", System.currentTimeMillis() - start));
        }
    }

    /**
     * @param startRowIndex start row index
     * @param endRowIndex   end row index
     * @return table cells data by rows range
     */
    public List<List<WebElement>> getTableCellsByRows(int startRowIndex, int endRowIndex) {
        log.info(String.format("Getting all cells data by rows range [%d - %d]", startRowIndex, endRowIndex));
        if (startRowIndex > endRowIndex)
            throw new NumberFormatException(String.format("End index '%d' should be greater than start '%d' in rows range", endRowIndex, startRowIndex));
        List<List<WebElement>> list = new ArrayList<>(endRowIndex - startRowIndex + 1);
        if (startRowIndex == endRowIndex) {
            list.add(getCellsByRowIndex(startRowIndex));
            return list;
        }
        return DataHelper.getListFromRange(startRowIndex, endRowIndex).parallelStream()
                .map(this::getCellsByRowIndex)
                .collect(Collectors.toList());
    }

    /**
     * @param headers - string array of columns names
     *                First get column index by it's header text, after get it cell
     * @return cells as WebElement of columns by their headers
     */
    public List<List<WebElement>> getTableCellsByColumnsHeader(String... headers) {
        long start = System.currentTimeMillis();
        try {
            List<String> headersList = Arrays.asList(headers);
            log.info(String.format("Getting all cells data by column headers text\t%s", headersList));
            return headersList.parallelStream()
                    .map(headerName -> getCellsByColumnIndex(getColumnIndexByHeaderText(headerName) + 1))
                    .collect(Collectors.toList());
        } finally {
            log.info(String.format("Get cells elements in parallel from '%d' columns, time = '%d'", headers.length, System.currentTimeMillis() - start));
        }
    }

    /**
     * @param attribute - string attribute to get column index
     * @param values    - string array of values, length of array shows columns number
     * @return cells as WebElement of columns by attribute-value
     */
    public List<List<WebElement>> getTableCellsByColumnsAttribute(String attribute, String... values) {
        long start = System.currentTimeMillis();
        try {
            List<String> valuesList = Arrays.asList(values);
            log.info(String.format("Getting all cells data by column attribute '%s' values\t%s", attribute, valuesList));
            return valuesList.parallelStream()
                    .map(value -> getCellsByColumnIndex(getColumnIndexByAttribute(attribute, value) + 1))
                    .collect(Collectors.toList());
        } finally {
            log.info(String.format("Get cells elements in parallel from '%d' columns, time = '%d'", values.length, System.currentTimeMillis() - start));
        }
    }

    /**
     * Ues 'data-field-name'  as default attribute
     *
     * @param values - string array of values, length of array shows columns number
     * @return cells as WebElement of columns by attribute-value
     */
    public List<List<WebElement>> getTableCellsByColumnsAttribute(String... values) {
        return getTableCellsByColumnsAttribute("data-field-name", values);
    }

    // ==================================== rows ====================================

    /**
     * @param rowIndex - rowIndex, 1 based
     * @return Row cell as List of WebElements
     */
    public List<WebElement> getCellsByRowIndex(int rowIndex) {
        return helper.getElements(getWrappedElement(), By.cssSelector(String.format("tbody > tr:nth-of-type(%d) > td", rowIndex)));
    }

    /**
     * @param startRowIndex - start row index
     * @param endRowIndex   - end row index
     * @return cells of rows range as List of WebElements
     */
    public List<WebElement> getRowsByIndex(int startRowIndex, int endRowIndex) {
        return DataHelper.getListFromRange(startRowIndex, endRowIndex).parallelStream()
                .map(rowIndex -> getWrappedElement().findElement(By.cssSelector(String.format("tbody > tr:nth-of-type(%d)", rowIndex))))
                .collect(Collectors.toList());
    }

    /**
     * Check if table has no rows with data
     *
     * @return true if table has no rows with data
     */
    public boolean isTableEmpty() {
        return getTotalRowsCount() == 0;
    }

    /**
     * @return number of visible rows on the page
     */
    public int getRowsCount() {
        return helper.getElements(getWrappedElement(),
                helper.isElementPresent(getWrappedElement(), ".//th")
                        ? ".//tr" : ".//tbody//tr ").size();
    }

    /**
     * @return total table rows by 'data-rows-total' attribute
     */
    public int getTotalRowsCount() {
        try {
            return Integer.parseInt(getWrappedElement().getAttribute("data-rows-total"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Column index id 0 based
     *
     * @param text        - seeking cell text
     * @param columnIndex - column index that cell supposed to belong
     * @return rowIndex of 1st matched cell in column
     */
    public int getRowIndexByCellText(String text, int columnIndex) {
        try {
//            return Integer.parseInt(getCellByText(text, columnIndex).findElement(By.xpath("..")).getAttribute("rowIndex"));
            By by = By.xpath(String.format(".//td[%d]/descendant-or-self::*[text()='%s' or @innerHTML='%s']/ancestor-or-self::tr", columnIndex + 1, text, text));
            return Integer.parseInt(helper.getElement(by).getAttribute("rowIndex"));
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(String.format("Cell with text '%s' is missed in table", text));
        }
    }

    /**
     * Differs to getRowIndexByCellText that search through all table
     *
     * @param text -  seeking cell text
     * @return rowIndex of 1st matched cell
     */
    public int getRowIndexByCellText(String text) {
        try {
            String xpath = String.format(".//td/descendant-or-self::*[text()='%s' or @innerHTML='%s']/ancestor-or-self::tr", text, text);
//            String xpath = String.format(".//td[text()='%s'" + " or @innerHTML='%s']/..", text, text);
            return Integer.parseInt(helper.getElement(getWrappedElement(), xpath).getAttribute("rowIndex"));
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(String.format("Cell with text '%s' is missed in table", text));
        }
    }

    /**
     * @return headers as list of WebElement
     */
    public List<WebElement> getHeaders() {
        return helper.isElementPresent(getWrappedElement(), By.xpath(".//th"))
                ? helper.getElements(getWrappedElement(), By.xpath(".//th"))
                : helper.getElements(getWrappedElement(), By.xpath(".//thead//td"));
    }

    public List<WebElement> getHeaders(String[] values) {
        return Arrays.stream(values)
                .map(value -> helper.getElement(getWrappedElement(), String.format(".//*[@%s='%s']", "data-field-name", value)))
                .collect(Collectors.toList());
    }

    // ================================== columns ===================================

    /**
     * @param columnIndex - column index 1 based
     * @return column cells as list of WebElements
     */
    public List<WebElement> getCellsByColumnIndex(int columnIndex) {
        log.info(String.format("Get column cells by index '%d'", columnIndex));
//        return table.getColumnByIndex(columnIndex);
        return helper.getElements(getWrappedElement(), By.cssSelector(String.format("tr > td:nth-of-type(%d)", columnIndex)));
    }

    /**
     * @param headerText - seeking header text
     * @return Index of column with header equal to headerText
     */
    public int getColumnIndexByHeaderText(String headerText) {
        log.info(String.format("Get header index by text '%s'", headerText));
        List<WebElement> headers = getHeaders();
        for (WebElement element : headers) {
            if (element.getText().equals(headerText)) return Integer.parseInt(element.getAttribute("cellIndex"));
        }
        throw new Error(String.format("Header with text '%s' is absent in table", headerText));
    }

    /**
     * @param attribute - column header attribute
     * @param value     - seeking value of attribute
     * @return index of column header with attribute equal to value
     */
    public int getColumnIndexByAttribute(String attribute, String value) {
        log.info(String.format("Get header index by attribute '%s' with value '%s'", attribute, value));
        try {
            return Integer.parseInt(helper.getElement(getWrappedElement(), String.format(".//*[@%s='%s']", attribute, value)).getAttribute("cellIndex"));
        } catch (NoSuchElementException e) {
            throw new Error(String.format("Header with attribute '%s' value '%s' is absent in table", attribute, value));
        }
    }

    public List<String> getColumnCellsText(String headerName) {
        int columnIndex = getColumnIndexByAttribute("data-field-name", headerName) + 1;
        List<WebElement> cells = helper.getElements(getWrappedElement(), By.cssSelector(String.format("tbody>tr> td:nth-of-type(%d)", columnIndex)));
        List<String> values = new ArrayList<>(cells.size());
        cells.forEach(cell -> values.add(cell.getText()));
        return values;
    }

    // =================================== cells ====================================

    /**
     * Click on cell with text equal to text
     *
     * @param text - cell text
     */
    public void clickOnCellByText(String text) {
        log.info(String.format("Click on cell by text '%s'", text));
        getCellByText(text).click();
    }

    /**
     * Click on cell by coordinates - rowIndex and cellIndex
     * Both indexes are 0 based
     *
     * @param rowIndex    - index of the row
     * @param columnIndex - index of the column
     */
    public void clickOnCellAt(int rowIndex, int columnIndex) {
        getCellAt(rowIndex, columnIndex).click();
    }

    public void clickOnClickableCellAt(int rowIndex, int columnIndex, long timeout) {
        helper.waitUntilToBeClickable(getWrappedElement(), By.cssSelector(String.format(
                "tbody > tr:nth-of-type(%d) > td:nth-of-type(%d)", rowIndex, columnIndex)), timeout);
        getCellAt(rowIndex, columnIndex).click();
    }

    /**
     * @param text - cell text
     * @return 1st cell matched by text as WebElement
     */
    public WebElement getCellByText(String text) {
        try {
            String xpath = String.format(".//td/descendant-or-self::*[text()='%s' or @innerHTML='%s']/ancestor-or-self::td", text, text);
            return helper.getElement(xpath);
        } catch (NumberFormatException e) {
            throw new NoSuchElementException(String.format("Cell with text '%s' is missed in table", text));
        }
    }

    /**
     * @param rowIndex    - index of the row
     * @param columnIndex - index of the column
     * @return cell as WebElement by coordinates [rowIndex, cellIndex]
     * indexes are 1-based
     */
    public WebElement getCellAt(int rowIndex, int columnIndex) {
        log.info(String.format("Click on cell (%d, %d)", rowIndex, columnIndex));
        return helper.getElement(getWrappedElement(), By.cssSelector(String.format("tbody > tr:nth-of-type(%d) > td:nth-of-type(%d)", rowIndex, columnIndex)));
    }

    /**
     * @param rowIndex    - index of the row
     * @param columnIndex - index of the column
     * @return cell text by coordinates [rowIndex, cellIndex]
     */
    public String getCellTextAt(int rowIndex, int columnIndex) {
        return getCellAt(rowIndex, columnIndex).getText();
    }

    /**
     * Check that table contains cell with proper text
     *
     * @param text - seeking cell text
     * @return true if table has cell with text
     */
    public boolean isCellPresentByText(String text) {
        By by = By.xpath(String.format(".//td/descendant-or-self::*[text()='%s' or @innerHTML='%s']/ancestor-or-self::td", text, text));
        return helper.isElementPresent(by, 2);
    }

    /**
     * @param text        - seeking cell text
     * @param columnIndex - seek in column by index, o based
     * @return true if column table by index has cell with text
     */
    public boolean isCellPresentByText(String text, int columnIndex) {
        By by = By.xpath(String.format(".//td[%d]/descendant-or-self::*[text()='%s' or @innerHTML='%s']/ancestor-or-self::td", columnIndex, text, text));
        return helper.isElementPresent(by, 2);
    }

    /**
     * @param element - cell as WebElement
     * @return its index by attribute 'cellIndex'
     */
    public int getCellIndex(WebElement element) {
        return Integer.parseInt(element.getAttribute("cellIndex"));
    }

}