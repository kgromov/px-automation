package pages.groups;

import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.dropdown.TableDropDown;
import elements.table.TableElement;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import px.reports.dto.InboundData;
import px.reports.dto.TransactionData;
import utils.CustomMatcher;
import utils.SoftAssertionHamcrest;

import java.util.List;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static pages.locators.ElementLocators.*;
import static px.objects.leads.LeadsPreviewPageLocators.REQUEST_ITEM_LINK;
import static px.objects.leads.LeadsPreviewPageLocators.RESPONSE_ITEM_LINK;

/**
 * Created by kgr on 3/9/2017.
 */
public interface Transactionable extends Popupable {
    Logger logger = Logger.getLogger(Transactionable.class);

    // add mapping for error response and check it's absence
    default String checkInboundData(TableElement tableElement, InboundData inboundData, JSONObject jsonObject, int rowIndex) {
        long start = System.currentTimeMillis();
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        try {
            logger.info("Check 'Inbound data' popup");
            String expectedValue = inboundData.getData(jsonObject);
            int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL));
            tableElement.clickOnCellAt(rowIndex + 1, columnIndex + 1);
            helper.waitUntilDisplayed(By.xpath(DELETE_POPUP), 1);
            // some time out here
            waitPopupTextLoaded();
            expectedValue = expectedValue.replaceAll("[\r\t\n]*", "").replaceAll("\\s+", " ").replaceAll(">\\s*<", "><");
            String actualValue = helper.getElement(POPUP_TEXT).getText().replaceAll("[\r\t\n]*", "").replaceAll("\\s+", " ").replaceAll(">\\s*<", "><");
            hamcrest.assertThat("Inbound data in popup equals to original in json at row " + (rowIndex + 1),
                    actualValue, equalToIgnoringCase(expectedValue));
            // extra verification for password is absent or hidden
            Elements leadData = Jsoup.parse(expectedValue).getElementsByTag("LeadData");
            if (leadData.hasAttr("Password"))
                hamcrest.assertThat("Password is shown", Pattern.compile("\\*+").matcher(
                        StringEscapeUtils.unescapeJava(leadData.attr("Password"))).matches());
            closePopupWithAttempts();
        } catch (JSONException e) {
            hamcrest.assertThat(String.format("Unable to get inbound data for row %d\tDetails:\t%s\nCause\t%s",
                    rowIndex + 1, jsonObject, e.getMessage()), false);
        } catch (NoSuchElementException | TimeoutException e) {
            hamcrest.assertThat("No popup with inbound data\tTable row = " + (rowIndex + 1), false);
        } catch (WebDriverException e2) {
            logger.info(String.format("DEBUG:\tFailed inbound data verification, row = %d,\tCause = %s", rowIndex + 1, e2.getMessage()));
            throw new RuntimeException(String.format("DEBUG:\tInbound popup was not closed\trow = %d\nDetails=%s", rowIndex + 1, e2.getMessage()), e2);
        }
        logger.info(String.format("Time to check 1 row with Inbound data = %d, ms", System.currentTimeMillis() - start));
        return hamcrest.toString();
    }

    default String checkTransactionData(TableElement tableElement, TransactionData transactionData, JSONObject jsonObject, int rowIndex) {
        long start = System.currentTimeMillis();
        ElementsHelper helper = HelperSingleton.getHelper();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        try {
            logger.info("Check 'Transaction data' popup");
            transactionData.setData(jsonObject);
            int columnIndex = tableElement.getCellIndex(helper.getElement(tableElement, ACTIONS_CELL));
//            WebElement cell = tableElement.getCellAt(rowIndex + 1, columnIndex + 1);
            TableDropDown actions = new TableDropDown(tableElement.getCellAt(rowIndex + 1, columnIndex + 1));
            List<String> items = actions.getItems();
            hamcrest.assertThat("There are no Response/Request items in table\trow = " + (rowIndex + 1), items.size() > 1);
            // protection for non-accordance not to preview lead
            if (items.size() > 1) {
                hamcrest.append(checkTransactionData(actions.getWrappedElement(), "Request", transactionData.getRequestData(), "\tTable row = " + (rowIndex + 1)));
                hamcrest.append(checkTransactionData(actions.getWrappedElement(), "Response", transactionData.getResponseData(), "\tTable row = " + (rowIndex + 1)));
            }
        } catch (JSONException e) {
            hamcrest.assertThat(String.format("Unable to get transaction data for row %d\tDetails:\t%s\nCause\t%s",
                    rowIndex + 1, jsonObject, e.getMessage()), false);
        }
        logger.info(String.format("Time to check 1 row with Transaction data = %d, ms", System.currentTimeMillis() - start));
        return hamcrest.toString();
    }

    default String checkTransactionData(WebElement cell, String key, String expectedData, String description) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        ElementsHelper helper = HelperSingleton.getHelper();
        hamcrest.assertThat("There are no unescape java\tExpectedData = " + expectedData, !expectedData.contains("\\\""));
        try {
            cell.click();
            helper.waitUntilDisplayed(DROPDOWN_TABLE_CONTAINER, 1);
            logger.info(String.format("Check '%s' in popup", key));
//            helper.getElement(cell, key.equals("Request") ? REQUEST_ITEM : RESPONSE_ITEM).click();
            helper.getElement(cell, key.equals("Request") ? REQUEST_ITEM_LINK : RESPONSE_ITEM_LINK).click();
            helper.waitUntilDisplayed(DELETE_POPUP, 1);
            // some time out here
            waitPopupTextLoaded();
            hamcrest.assertThat(String.format("Lead %s in popup equals to original in json%s", key, description),
//                    helper.getElement(POPUP_TEXT).getText(), equalToIgnoringCase(expectedData));
                    helper.getElement(POPUP_TEXT).getText(), CustomMatcher.equalsIgnoreBlanketsAndNewLines(expectedData));
            closePopupWithAttempts();
        } catch (NoSuchElementException | TimeoutException e) {
            logger.info(String.format("DEBUG:\tFailed transaction data verification, cell = %s,\tCause = %s", cell, e.getMessage()));
            hamcrest.assertThat(String.format("No popup with %s %s", key, description), false);
        }
        return hamcrest.toString();
    }
}
