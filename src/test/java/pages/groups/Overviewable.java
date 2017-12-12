package pages.groups;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.table.TableElement;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.*;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.PopupData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.List;

import static configuration.helpers.DataHelper.FRACTION_SPLITTER;
import static configuration.helpers.PXDataHelper.getValueByType;
import static pages.locators.ElementLocators.*;
import static px.reports.ReportTestData.FORMULA_FIELDS;
import static utils.CustomMatcher.equalsToEscapeSpace;

/**
 * Created by kgr on 3/9/2017.
 */
public interface Overviewable extends Popupable {
    Logger logger = Logger.getLogger(Overviewable.class);

    default void checkCellsData(JSONArray expectedData, List<List<String>> tableCellsText, List<FieldFormatObject> fieldFormatObjects) {
        long start = System.currentTimeMillis();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        TableElement tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        List<FieldFormatObject> fields = FieldFormatObject.withoutNonFields(fieldFormatObjects);
        // check headers tooltip
        fields.stream().filter(FieldFormatObject::hasHeaderTooltip).forEach(field ->
                hamcrest.append(checkHeaderTooltipData(field)));
        for (int i = 0; i < expectedData.length(); i++) {
            JSONObject jsonObject = expectedData.getJSONObject(i);
            for (FieldFormatObject field : fields) {
                try {
                    // check cell text
                    String expectedValue = JSONWrapper.getString(jsonObject, field.getName());
                    // generic solution
                    expectedValue = field.hasMappedValues() ? field.getMappedValue(expectedValue) : expectedValue;
                    String actualValue = tableCellsText.get(i).get(field.getIndex());
                    hamcrest.assertThat(String.format("Data in '%s' column at row '%d' in table equals to original data in json", field.getName(), i + 1),
//                            actualValue, field.getMatherByMetricType(expectedValue));
                            actualValue, equalsToEscapeSpace(getValueByType(field, expectedValue)));
                    // check cell tooltip
                    if (field.hasTooltip()) {
                        if (field.hasFormula()) field.getFormula().calculateFormula(jsonObject);
                        hamcrest.append(checkCellsTooltipData(tableElement, field, expectedValue, i));
                    }
                    // check cell popup
                    if (field.hasPopup()) hamcrest.append(checkCellsPopup(tableElement, field, actualValue, i));
                } catch (JSONException e) {
                    hamcrest.assertThat(String.format("There is no data in response JSON array by index '%d'\tDetails:\n%s", i, e.getMessage()), false);
//                    break;
                } catch (IndexOutOfBoundsException e1) {
                    hamcrest.assertThat(String.format("There is no row in table array by index '%d'", i), false);
                    break;
                } catch (NoSuchElementException e2) {
                    hamcrest.assertThat(String.format("No data inside tooltip for field '%s' at row '%d'", field.getName(), i), false);
                }
            }
        }
        logger.info(String.format("Time to check %d rows with tooltips and popups = %d, ms", expectedData.length(), System.currentTimeMillis() - start));
        hamcrest.assertAll();
    }

    /*
   * 1) cell value - present in response;
   * 2) tooltip:
   * 2.1) if category is not selected - all categories
   * 2.2) if category(es) is(are) selected - data by chosen categories
   * 3) Tooltips by categories are shown for all fields except 3 first literals
   * Flow:
   * 1) iterate by dataList length
   * 2) nested cycle by columns size
   * 3) get each cell by coordinates
   * 4) mouse move to cell to check tooltip
    */
    default void checkCellsData(JSONArray expectedData, List<List<String>> tableCellsText, List<FieldFormatObject> fieldFormatObjects, List<String> buyerCategories) {
        long start = System.currentTimeMillis();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        TableElement tableElement = new TableElement(helper.getElement(ITEMS_TABLE));
        List<FieldFormatObject> fields = FieldFormatObject.withRelevantFields(fieldFormatObjects);
        for (int i = 0; i < expectedData.length(); i++) {
            JSONObject jsonObject = expectedData.getJSONObject(i);
            for (FieldFormatObject field : fields) {
                try {
                    // check cell text
                    String expectedValue = JSONWrapper.getString(jsonObject, field.getName());
                    // generic solution
                    expectedValue = field.hasMappedValues() ? field.getMappedValue(expectedValue) : expectedValue;
                    hamcrest.assertThat(String.format("Data in '%s' column at row '%d' in table equals to original data in json", field.getName(), i + 1),
//                            tableCellsText.get(i).get(field.getIndex()), field.getMatherByMetricType(expectedValue));
                            tableCellsText.get(i).get(field.getIndex()), equalsToEscapeSpace(getValueByType(field, expectedValue)));
                    // check tooltip - put to another method
                    if (field.hasTooltip()) {
                        helper.moveToElement(tableElement.getCellAt(i + 1, field.getIndex() + 1));
                        try {
                            helper.waitUntilDisplayed(TABLE_TOOLTIP, 2);
                        } catch (TimeoutException e) {
                            hamcrest.assertThat(String.format("Tooltip is missed for field '%s'  at row '%d'",
                                    field.getName(), i), false);
                            continue;
                        }
                        WebElement tooltip = helper.getElement(TABLE_TOOLTIP);
                        logger.info("DEBUG\tTooltip text = " + tooltip.getText());
                        List<String> values = new ArrayList<>();
                        for (String buyerCategory : buyerCategories) {
                            String key = field.getName() + buyerCategory;
                            expectedValue = String.valueOf(jsonObject.get(key));
                            try {
                                String actualValue = helper.getElement(tooltip, By.cssSelector("div[ng-show*='" + key + "'] > .value")).getText();
                                values.add(actualValue);
                                hamcrest.assertThat(String.format("Data in '%s' column tooltip at row '%d' equals to original data in json", field.getName(), i + 1),
//                                        actualValue, field.getMatherByMetricType(expectedValue));
                                        actualValue, equalsToEscapeSpace(getValueByType(field, expectedValue)));
                            } catch (NoSuchElementException e1) {
                                hamcrest.assertThat(String.format("No data inside tooltip for field '%s' by category '%s' at row '%d'",
                                        field.getName(), buyerCategory, i), false);
                            } catch (JSONException e2) {
                                hamcrest.assertThat(String.format("There is no data by key '%s' in response JSON array by index '%d'\tDetails:\n%s", key, i, jsonObject), false);
                            }
                        }
                        // check cell and tooltip accordance
                        // as not all fields belong to formula {cellValue = tooltipOption1 + tooltipOption12 +...+ tooltipOption}
                        if (FORMULA_FIELDS.contains(field.getName())) continue;
                        try {
                            String cellValue = tableCellsText.get(i).get(field.getIndex());
                            String tooltipValue = ((Double) values.stream().mapToDouble(DataHelper::getFloat).sum()).toString();
                            if (!tooltipValue.equals(cellValue))
                                logger.info(String.format("DEBUG\tTooltip data verification\tCell = '%s', tooltip = '%s', line = '%d'\tvalues = '%s'", cellValue, tooltipValue, i + 1, values));
                            hamcrest.assertThat(String.format("Data in '%s' column tooltip at row '%d' equals to cell value", field.getName(), i + 1),
//                                    cellValue, field.getMatherByMetricType(tooltipValue));
                                    cellValue, equalsToEscapeSpace(getValueByType(field, tooltipValue)));
                        } catch (NumberFormatException ignored) {
                        }
                    }
                } catch (JSONException e) {
                    hamcrest.assertThat(String.format("There is no data in response JSON array by index '%d'\tDetails:\n%s", i, e.getMessage()), false);
//                    break;
                } catch (IndexOutOfBoundsException e2) {
                    hamcrest.assertThat(String.format("There is no row in table array by index '%d', column name = '%s'", i, field.getName()), false);
                    break;
                }
            }
        }
        logger.info(String.format("Time to check %d rows with tooltips = %d, ms", expectedData.length(), System.currentTimeMillis() - start));
        hamcrest.assertAll();
    }

    default String checkHeaderTooltipData(FieldFormatObject field) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        try {
            WebElement header = helper.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, field.getName()));
            helper.moveToElement(helper.getElement(header, HEADER_TOOLTIP_ICON));
            hamcrest.assertThat(String.format("Data in '%s' header tooltip equals to original data in json", field.getName()),
                    helper.getElement(TABLE_TOOLTIP).getText(),
                    equalsToEscapeSpace(field.getHeaderTooltip()));
        } catch (NoSuchElementException e) {
            hamcrest.assertThat(String.format("No tooltip appears after moving point to header '%s' icon", field.getName()), false);
        }
        return hamcrest.toString();
    }

    default String checkCellsTooltipData(TableElement tableElement, FieldFormatObject field, String expectedValue, int rowIndex) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        try {
            expectedValue = field.hasMappedTooltipValues() ? field.getMappedTooltipValue(expectedValue) : expectedValue;
            helper.moveToElement(tableElement.getCellAt(rowIndex + 1, field.getIndex() + 1));
            hamcrest.assertThat(String.format("Data in '%s' column tooltip at row '%d' equals to original data in json", field.getName(), rowIndex + 1),
                    helper.getElement(TABLE_TOOLTIP).getText(),
//                    field.getMatherByMetricType(expectedValue));
                    equalsToEscapeSpace(getValueByType(field, expectedValue)));
        } catch (NoSuchElementException e) {
            hamcrest.assertThat(String.format("No tooltip appears after moving point to cell (%d, %d)", rowIndex + 1, field.getIndex() + 1), false);
        }
        return hamcrest.toString();
    }

    default String checkCellsPopup(TableElement tableElement, FieldFormatObject field, String cellValue, int rowIndex) {
        long start = System.currentTimeMillis();
        logger.info(String.format("Check popup for field '%s', cellValue = '%s'", field.getName(), cellValue));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        PopupData popup = field.getPopupData();
        int x = rowIndex + 1;
        int y = field.getIndex() + 1;
        tableElement.clickOnCellAt(x, y);
        try {
            helper.waitUntilDisplayed(POPUP, 1);
            String expectedValue = (popup.hasValueInTitle() ? cellValue + " " : "") + popup.getTitle();
            String actualValue = helper.getElement(POPUP, POPUP_TEXT).getText();
            // cast to 1 format
            if (popup.hasValueInTitle()) {
                expectedValue = expectedValue.replaceAll("[.,]", String.valueOf(FRACTION_SPLITTER));
                actualValue = actualValue.replaceAll("[.,]", String.valueOf(FRACTION_SPLITTER));
            }
            logger.info("Check popup title");
            hamcrest.assertThat(String.format("Check popup title accordance, field - '%s'", field.getName()),
                    actualValue, equalsToEscapeSpace(expectedValue));
         /*   if (!popup.hasValueInTitle()) {
                boolean isChartVisible = !Pattern.compile("0+").matcher(DataHelper.remainDigits(cellValue)).matches();
                hamcrest.assertThat("Popup chart is " + (isChartVisible ? "visible" : "hidden"),
                        isChartVisible, equalTo(helper.isElementAccessible(POPUP_CHART)));
                if (isChartVisible) {
                    // by type
                    String chartType = popup.getType();
                    switch (chartType) {
                        case AREA_GRAPHIC:
                            hamcrest.assertThat(String.format("Popup chart type '%s' is visible", AREA_GRAPHIC),
                                    helper.isElementAccessible(POPUP_AREA_GRAPHIC));
                            break;
                        case PIE_GRAPHIC:
                            hamcrest.assertThat(String.format("Popup chart type '%s' is visible", PIE_GRAPHIC),
                                    helper.isElementAccessible(POPUP_PIE_GRAPHIC));
                            break;
                        default:
                            hamcrest.assertThat("Unknown popup chart type - " + chartType, false);

                            break;
                    }
                }
            }*/
            closePopupWithAttempts();
        } catch (WebDriverException e) {
            throw new RuntimeException(String.format("%s cell (%d, %d), field - '%s'\tDetails = %s",
                    e.getMessage(), x, y, field.getName(), e.getMessage()), e);
           /* hamcrest.assertThat(String.format("%s cell (%d, %d), field - '%s'\tDetails = %s",
                    e.getMessage(), x, y, field.getName(), e.getMessage()), false);*/
        } finally {
            logger.info("Finally check whether popup was closed");
            if (helper.isElementAccessible(POPUP)) {
                helper.getElement(POPUP_CLOSE).click();
                helper.waitUntilToBeInvisible(POPUP, 1);
            }
        }
        logger.info(String.format("Time to check 1 cell popup - %d, ms", System.currentTimeMillis() - start));
        return hamcrest.toString();
    }

}