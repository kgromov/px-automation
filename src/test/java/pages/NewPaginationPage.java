package pages;

import configuration.browser.PXDriver;
import dto.PaginationObject;
import elements.dropdown.SelectElement;
import elements.table.TableElement;
import org.openqa.selenium.WebElement;
import pages.groups.Pagination;
import pages.groups.PaginationStrategy;
import utils.SoftAssertionHamcrest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static configuration.helpers.DataHelper.getRandomInt;
import static configuration.helpers.DataHelper.remainDigits;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.ElementLocators.*;
import static pages.locators.LoginPageLocators.LOGGED_USER_NAME;

/**
 * Created by kgr on 9/18/2017.
 */
public class NewPaginationPage extends PaginationPage {

    public NewPaginationPage(PXDriver pxDriver) {
        super(pxDriver);
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
            SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
            // check items per page are hidden
            hamcrest.assertThat("Go to page is hidden", helper.isElementPresent(
                    parentElement, GO_TO_PAGE), equalTo(false));
            hamcrest.assertThat("Pagination navigation is hidden", helper.isElementPresent(
                    parentElement, PAGINATION_PAGES_NAVIGATOR), equalTo(false));
            hamcrest.assertAll();
            // check empty table
            return checkEmptyTable();
        }
        return checkPagination(parentElement, strategy, totalRowsCount);
    }

    @Override
    public Pagination checkPaginationBigData(PaginationStrategy strategy, int totalRowsCount) {
        throw new UnsupportedOperationException("Not relevant method");
    }

    private Pagination checkPagination_(WebElement parentElement, PaginationStrategy strategy, int totalRowsCount) {
        log.info("Detailed pagination per page");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        SelectElement itemsContainer = new SelectElement(helper.getElement(parentElement, ITEMS_PER_PAGE));
        List<String> itemsPerPage = itemsContainer.getCollapsedItems();
        assertThat("There are items per page", !itemsPerPage.isEmpty());
        for (String item : itemsPerPage) {
            int rowsPerPage = Integer.parseInt(remainDigits(item));
            log.info(String.format("There are '%d' items per page", rowsPerPage));
            setItemPerPage(rowsPerPage);
            // check items count
            if (rowsPerPage >= totalRowsCount) {
                hamcrest.append(checkNoPagination(parentElement, rowsPerPage, totalRowsCount));
            } else {
                helper.scrollToElement(helper.getElement(ITEM_PER_PAGE));
                helper.waitUntilDisplayed(PAGINATION_PAGES_NAVIGATOR);
                int pagesCount = new BigDecimal((double) totalRowsCount / rowsPerPage).setScale(0, RoundingMode.UP).intValue();
                // to accumulate in 1 object
                PaginationObject pagination = new PaginationObject(rowsPerPage, totalRowsCount, pagesCount);
                hamcrest.append(checkPaginationOnPage(parentElement, pagination, strategy, 1));
                // check that previous disabled
                hamcrest.assertThat("Check that pagination 'Previous' button is disabled on the 1st page\titems per page = " + rowsPerPage,
//                        Boolean.parseBoolean(helper.getElement(parentElement, PAGINATION_PAGES_PREV).getAttribute("disabled")));
                        helper.getElement(parentElement, PAGINATION_PAGES_PREV).getAttribute("ng-click") == null);
                for (int i = 2; i <= pagesCount; i++) {
                    clickNext(parentElement);
                    // pagination stat
                    hamcrest.append(checkPaginationOnPage(parentElement, pagination, strategy, i));
                }
                // check that next disabled
                hamcrest.assertThat("Check that pagination 'Next' button is disabled on the last page\titems per page = " + rowsPerPage,
//                        Boolean.parseBoolean(helper.getElement(parentElement, PAGINATION_PAGES_NEXT).getAttribute("disabled")));
                        helper.getElement(parentElement, PAGINATION_PAGES_NEXT).getAttribute("ng-click") == null);
            }
        }
        hamcrest.assertAll();
        return this;
    }

    /*  if total pages [1, 2]
  * check stat + table on 1st page
  * goTo rand page [2; last-1]
  * next
  * prev
  * goTo last page
  */
    @Override
    public Pagination checkPagination(WebElement parentElement, PaginationStrategy strategy, int totalRowsCount) {
        log.info("Detailed pagination per page");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        SelectElement itemsContainer = new SelectElement(helper.getElement(ITEMS_PER_PAGE));
        List<String> itemsPerPage = itemsContainer.getCollapsedItems();
        assertThat("There are items per page", !itemsPerPage.isEmpty());
        for (String item : itemsPerPage) {
            int rowsPerPage = Integer.parseInt(remainDigits(item));
            int pagesCount = new BigDecimal((double) totalRowsCount / rowsPerPage).setScale(0, RoundingMode.UP).intValue();
            int page = 1;
            // to accumulate in 1 object
            PaginationObject pagination = new PaginationObject(rowsPerPage, totalRowsCount, pagesCount);
            log.info(String.format("There are '%d' items per page", rowsPerPage));
            setItemPerPage(rowsPerPage);
            // to bring into view
            helper.scrollToElement(helper.getElement(parentElement, ITEMS_PER_PAGE));
            // detailed pagination verification
            if (rowsPerPage >= totalRowsCount) {
                hamcrest.append(checkNoPagination(parentElement, rowsPerPage, totalRowsCount));
            } else {
                // 1st page verification
                hamcrest.append(checkPaginationOnPage(parentElement, pagination, strategy, 1));
                // check that previous disabled
                hamcrest.assertThat("Check that pagination 'Previous' button is disabled on the 1st page\titems per page = " + rowsPerPage,
//                        Boolean.parseBoolean(helper.getElement(parentElement, PAGINATION_PAGES_PREV).getAttribute("disabled")));
                        helper.getElement(parentElement, PAGINATION_PAGES_PREV).getAttribute("ng-click") == null);
                // goto rand page if pageCount > 3
                if (pagesCount > 2) {
                    page = getRandomInt(2, pagesCount - 1);
                    goToPage(parentElement, page);
                    hamcrest.append(checkPaginationOnPage(parentElement, pagination, strategy, page));
                }
                // next navigation
                clickNext(parentElement);
                hamcrest.append(checkPaginationOnPage(parentElement, pagination, strategy, page + 1));
                // goto last page
                goToPage(parentElement, pagesCount);
                hamcrest.append(checkPaginationOnPage(parentElement, pagination, strategy, pagesCount));
                // check that next disabled
                hamcrest.assertThat("Check that pagination 'Next' button is disabled on the last page\titems per page = " + rowsPerPage,
//                        Boolean.parseBoolean(helper.getElement(parentElement, PAGINATION_PAGES_NEXT).getAttribute("disabled")));
                        helper.getElement(parentElement, PAGINATION_PAGES_NEXT).getAttribute("ng-click") == null);
                // prev navigation
                clickPrev(parentElement);
                hamcrest.append(checkPaginationOnPage(parentElement, pagination, strategy, pagesCount - 1));
            }
        }
        return this;
    }

    @Override
    public void setItemPerPage(int itemsPerPage) {
        if (getActiveItemsPerPage() == 0) return;
        WebElement activeItem = helper.getElement(ITEM_PER_PAGE);
        if (remainDigits(activeItem.getText()).equals(String.valueOf(itemsPerPage))) return;
        log.info(String.format("Set items per page = %d", itemsPerPage));
        activeItem.click();
        helper.waitUntilDisplayed(FILTERED_DROPDOWN);
        helper.getElement(FILTERED_DROPDOWN, String.format(".//*[@title='%s']", itemsPerPage)).click();
        waitPageIsLoaded();
    }

    @Override
    public int getActiveItemsPerPage() {
        try {
            return helper.isElementPresent(ITEM_PER_PAGE)
                    ? Integer.parseInt(remainDigits(helper.getElement(ITEM_PER_PAGE).getText())) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public int getLastPage() {
        try {
            return helper.isElementPresent(ITEM_PER_PAGE)
                    ? (helper.isElementPresent(GO_TO_PAGE_PAGES) ? Integer.parseInt(remainDigits(helper.getElement(GO_TO_PAGE_PAGES).getText())) : 1) : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private Pagination clickNext(WebElement parentElement) {
        helper.getElement(parentElement, PAGINATION_PAGES_NEXT).click();
        waitPageIsLoaded();
        helper.scrollToElement(helper.getElement(parentElement, ITEM_PER_PAGE));
        helper.waitUntilDisplayed(PAGINATION_PAGES_NAVIGATOR);
        return this;
    }

    private Pagination clickPrev(WebElement parentElement) {
        helper.getElement(parentElement, PAGINATION_PAGES_PREV).click();
        waitPageIsLoaded();
        helper.scrollToElement(helper.getElement(ITEM_PER_PAGE));
        helper.waitUntilDisplayed(PAGINATION_PAGES_NAVIGATOR);
        return this;
    }

    private Pagination goToPage(WebElement parentElement, int page) {
        log.info(String.format("Navigate to page '%d' using goto pagination", page));
        WebElement goTo = helper.getElement(parentElement, GO_TO_PAGE_INPUT);
        helper.doubleClickOnElement(goTo);
        helper.pause(500);
        goTo.sendKeys(String.valueOf(page));
//        goTo.sendKeys(ENTER);
        helper.getElement(LOGGED_USER_NAME).click(); // workaround to move away focus
        waitPageIsLoaded();
        helper.scrollToElement(helper.getElement(parentElement, ITEM_PER_PAGE));
        helper.waitUntilDisplayed(PAGINATION_PAGES_NAVIGATOR);
        return this;
    }

    // if 1 page
    private String checkNoPagination(WebElement parentElement, int rowsPerPage, int totalRowsCount) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // check all pagination stat is hidden
        hamcrest.assertThat("Go to page is hidden", helper.isElementPresent(
                parentElement, GO_TO_PAGE), equalTo(false));
        hamcrest.assertThat("Pagination navigation is hidden", helper.isElementPresent(
                parentElement, PAGINATION_PAGES_NAVIGATOR), equalTo(false));
        // table verification
        hamcrest.assertThat(String.format("There are '%d' rows in the table", rowsPerPage),
                tableElement.getRowsCount(), equalTo(totalRowsCount));
        return hamcrest.toString();
    }

    private String checkPaginationOnPage(WebElement parentElement, PaginationObject pagination, PaginationStrategy strategy, int page) {
        log.info(String.format("Check pagination on page '%d', items per page = '%d'", page, pagination.itemsPerPage));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // to shrink
        int itemsPerPage = pagination.itemsPerPage;
        int totalItems = pagination.totalItems;
        int totalPages = pagination.totalPages;
        // pages
        String totalPagesText = helper.getElement(parentElement, GO_TO_PAGE_PAGES).getText();
        int actualTotalPages = Integer.parseInt(remainDigits(totalPagesText));
        hamcrest.assertThat(String.format("There are total '%d' pages when items on page = '%d'", totalPages, itemsPerPage),
                actualTotalPages, equalTo(totalPages));
        hamcrest.assertThat("Current page ('Go to:') verification",
                helper.getElement(parentElement, GO_TO_PAGE_INPUT).getAttribute("value"), equalTo(String.valueOf(page)));
        // rows
        String expectedRowsRange = String.format("%d - %d", itemsPerPage * (page - 1) + 1,
                page == totalPages ? totalItems : itemsPerPage * page);
        String paginationStat = helper.getElement(parentElement, PAGINATION_PAGES_STAT).getText();
        String[] temp = paginationStat.split(" of ");
        String actualRowsRange = temp[0];
        // rows range
        hamcrest.assertThat("Rows range per page", actualRowsRange, equalTo(expectedRowsRange));
        // total rows
        int actualTotalRows = Integer.parseInt(remainDigits(temp[1]));
        hamcrest.assertThat("Total rows stat", actualTotalRows, equalTo(totalItems));
        // table verification
        int expectedRowsInTable = page == totalPages
                ? totalItems - itemsPerPage * (page - 1)
                : strategy.getRowsPerPage(itemsPerPage);
        hamcrest.assertThat(String.format("There are '%d' rows in the table", expectedRowsInTable),
                tableElement.getRowsCount(), equalTo(expectedRowsInTable));
        return hamcrest.toString();
    }
}
