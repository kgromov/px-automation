package pages;

import configuration.browser.PXDriver;
import elements.table.TableElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import pages.groups.Pagination;
import pages.groups.PaginationStrategy;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static pages.locators.ElementLocators.ITEMS_TABLE;
import static pages.locators.ElementLocators.LOADING_TABLE;

/**
 * Created by kgr on 9/18/2017.
 */
public abstract class PaginationPage extends BasePage implements Pagination {
    @FindBy(xpath = ITEMS_TABLE)
    protected TableElement tableElement;

    public PaginationPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public PaginationPage setTable(String container) {
        helper.waitUntilDisplayed(container + ITEMS_TABLE.substring(1));
        this.tableElement = new TableElement(helper.getElement(container, ITEMS_TABLE));
        return this;
    }

    protected PaginationPage checkEmptyTable() {
        log.info("Check that overview table has no data");
        assertThat("Check that overview table has no data", tableElement.isTableEmpty());
        return this;
    }

    @Override
    public void waitPageIsLoaded() {
        waitPageIsLoaded(40);
    }

    @Override
    public void waitPageIsLoaded(int timeOut) {
        long start = System.currentTimeMillis();
        try {
            super.waitPageIsLoaded(timeOut);
            helper.waitUntilToBeInvisible(LOADING_TABLE, timeOut);
        } catch (TimeoutException e) {
            throw new RuntimeException(String.format("Page is not loaded after '%d' seconds",
                    TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
        }
        log.info(String.format("Loading page + data in table duration = '%d' seconds",
                TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
    }

    protected abstract Pagination checkPagination(WebElement parentElement, PaginationStrategy strategy, int totalRowsCount);
}
