package pages;

import configuration.browser.PXDriver;
import elements.table.TableElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pages.groups.Pagination;
import pages.groups.PaginationStrategy;
import utils.SoftAssertionHamcrest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static configuration.helpers.DataHelper.remainDigits;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static pages.locators.ElementLocators.*;

/**
 * Created by kgr on 9/18/2017.
 */
public class OldPaginationPage extends PaginationPage {

    public OldPaginationPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    private void waitPagination(int totalRows, int rowsPerPage) {
        if (totalRows > rowsPerPage) {
            log.info(String.format("Wait till pagination items are displayed and clickable" +
                    "\tTotal rows in table='%d', items per page='%d'", totalRows, rowsPerPage));
            int pagesCount = (totalRows % rowsPerPage > 0 ? 1 : 0) + (totalRows / rowsPerPage);
            // there are some limited number of shown pages in pagination block - in future could be calculated
            if (pagesCount > MAX_VISIBLE_PAGES_PAGINATION)
                helper.waitUntilChildrenNumber(By.xpath(PAGES_PAGINATION), MIN_VISIBLE_PAGES_PAGINATION, MAX_VISIBLE_PAGES_PAGINATION + 1);
            else
                helper.waitUntilChildrenNumber(By.xpath(PAGES_PAGINATION), pagesCount);
            helper.waitUntilToBeClickable(PAGES_PAGINATION);
        }
    }

    private void waitPagination(int minRows, int maxRows, int rowsPerPage) {
        log.info(String.format("Wait till pagination items are displayed and clickable" +
                "\tItems per page='%d'", rowsPerPage));
        helper.waitUntilChildrenNumber(By.xpath(PAGES_PAGINATION), minRows, maxRows);
        helper.waitUntilToBeClickable(PAGES_PAGINATION);
    }

    @Override
    public Pagination checkPagination(PaginationStrategy strategy, int totalRowsCount) {
        return checkPagination(RIGHT_PART_CONTAINER, strategy, totalRowsCount);
    }

    @Override
    public Pagination checkPagination(String parentContainer, PaginationStrategy strategy, int totalRowsCount) {
        log.info(String.format("Check table rows accordance to items per page and pages pagination\tTotal rows = '%d'", totalRowsCount));
        // set table according to parent element
        helper.waitUntilDisplayed(parentContainer);
        WebElement parentElement = helper.getElement(parentContainer);
        tableElement = new TableElement(helper.getElement(parentElement, ITEMS_TABLE));
        // empty table
        if (totalRowsCount == 0) {
            // check items per page are hidden
            assertThat("Items per page are hidden", helper.isElementPresent(
                    parentElement, ITEMS_PER_PAGE), equalTo(false));
            // check empty table
            return checkEmptyTable();
        }
        return totalRowsCount > MAX_ROWS_LIMIT
                // another method for big data
                ? checkPaginationBigData(strategy, totalRowsCount)
                // common pagination verification with items in table less than MAX_ROWS_LIMIT
                : checkPagination(parentElement, strategy, totalRowsCount);
    }

    @Override
    protected Pagination checkPagination(WebElement parentElement, PaginationStrategy strategy, int totalRowsCount) {
        log.info("Detailed pagination per page");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<WebElement> itemsPerPage = helper.getElements(parentElement, ITEM_PER_PAGE);
        for (WebElement item : itemsPerPage) {
            if (!item.isDisplayed()) continue;
            int rowsPerPage = Integer.parseInt(item.getText());
            int totalTableRows = totalRowsCount;
            log.info(String.format("There are '%d' rows in the table", rowsPerPage));
//            if (rowsPerPage != activeItemPerPage)
            helper.click(item);
            // wait till table refreshed
            waitPageIsLoaded();
//            waitPagination(totalRowsCount, rowsPerPage);
            if (rowsPerPage >= totalRowsCount) {
                hamcrest.assertThat(String.format("There are '%d' rows in the table", rowsPerPage),
                        tableElement.getRowsCount(), equalTo(totalRowsCount));
                hamcrest.assertThat("Page pagination is hidden", helper.isElementPresent(parentElement, PAGE_PAGINATION), equalTo(false));
            } else {
                int pageCount = new BigDecimal((double) totalRowsCount / rowsPerPage).setScale(0, RoundingMode.UP).intValue();
                if (pageCount > MAX_VISIBLE_PAGES_PAGINATION) {
                    hamcrest.assertThat("Page pagination count verification when 'More pages'", helper.getElements(parentElement, PAGES_PAGINATION).size(), greaterThan(MIN_VISIBLE_PAGES_PAGINATION));
                    hamcrest.assertThat("'More pages' link is present", helper.getElements(parentElement, MORE_PAGES_PAGINATION).size(), greaterThanOrEqualTo(1));
                } else {
                    hamcrest.assertThat("Page pagination count verification", helper.getElements(parentElement, PAGES_PAGINATION).size(), equalTo(pageCount));
                }
                for (int i = 0; i < pageCount; i++) {
                    if (i > 0) {
                        helper.click(helper.getElement(parentElement, String.format(PAGES_PAGINATION_BY_TEXT, (i + 1))));
                        // wait till table refreshed
                        waitPageIsLoaded();
                        waitPagination(totalRowsCount, rowsPerPage);
                    }
                    int expectedRowsPerPage = strategy.getRowsPerPage(rowsPerPage);
                    hamcrest.assertThat(String.format("There are '%d' rows in the table", expectedRowsPerPage),
                            tableElement.getRowsCount(), equalTo(expectedRowsPerPage >= totalTableRows ? totalTableRows : expectedRowsPerPage));
                    totalTableRows -= rowsPerPage;
                }
            }
        }
        hamcrest.assertAll();
        return this;
    }

    @Override
    public Pagination checkPaginationBigData(PaginationStrategy strategy, int totalRowsCount) {
        log.info(String.format("Check pagination for big amount of records (more than '%d')", MAX_ROWS_LIMIT));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<WebElement> itemsPerPage = helper.getElements(ITEM_PER_PAGE);
        for (WebElement item : itemsPerPage) {
            int rowsPerPage = Integer.parseInt(item.getText());
            int pagesCount = (totalRowsCount % rowsPerPage > 0 ? 1 : 0) + (totalRowsCount / rowsPerPage);
            log.info(String.format("There are '%d' rows in the table", rowsPerPage));
            helper.click(item);
            // wait till table refreshed
            waitPageIsLoaded(40);
            // rewrite this method for big data
            waitPagination(1, pagesCount + 1, rowsPerPage);
            // check items count
            List<WebElement> pagesList = helper.getElements(PAGES_PAGINATION);
            String actualPagesCount = pagesList.get(pagesList.size() - 1).getText();
            hamcrest.assertThat(String.format("There are total '%d' pages when items on page = '%d'", pagesCount, rowsPerPage),
                    actualPagesCount, equalTo(String.valueOf(pagesCount)));
            if (!actualPagesCount.equals(String.valueOf(pagesCount))) continue;
            // check items on 1st page
            int expectedRowsPerPage = strategy.getRowsPerPage(rowsPerPage);
            hamcrest.assertThat(String.format("There are '%d' rows in the table", expectedRowsPerPage),
                    tableElement.getRowsCount(), equalTo(expectedRowsPerPage >= totalRowsCount ? totalRowsCount : expectedRowsPerPage));
            // click + check items on last page
            helper.click(helper.getElement(String.format(PAGES_PAGINATION_BY_TEXT, (pagesCount))));
            // wait till table refreshed
            waitPageIsLoaded(40);
            // rewrite this method for big data
            expectedRowsPerPage = totalRowsCount - rowsPerPage * (pagesCount - 1);
            waitPagination(1, pagesCount + 1, rowsPerPage);
            hamcrest.assertThat(String.format("There are '%d' rows in the table", expectedRowsPerPage),
                    tableElement.getRowsCount(), equalTo(expectedRowsPerPage >= totalRowsCount ? totalRowsCount : expectedRowsPerPage));
        }
        // scroll to items till full screen
        if (!hamcrest.toString().isEmpty())
            helper.scrollToElement(helper.getElement(ITEMS_PER_PAGE));
        hamcrest.assertAll();
        return this;
    }

    @Override
    public void setItemPerPage(int itemsPerPage) {
        if (getActiveItemsPerPage() == itemsPerPage) return;
        log.info(String.format("Set items per page = %d", itemsPerPage));
        helper.getElement(String.format(ITEM_PER_PAGE_PARAMETERIZED, itemsPerPage)).click();
        waitPageIsLoaded();
    }

    @Override
    public int getActiveItemsPerPage() {
        try {
            return helper.isElementPresent(ITEM_PER_PAGE)
                    ? Integer.parseInt(helper.getElement(ACTIVE_ITEM_PER_PAGE).getText()) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int getLastPage() {
        try {
            return helper.isElementPresent(ITEM_PER_PAGE)
                    ? Integer.parseInt(remainDigits(helper.getElement("(" + PAGES_PAGINATION + ")[last()]").getText())) : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
