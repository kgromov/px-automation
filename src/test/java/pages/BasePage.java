package pages;

import configuration.browser.PXDriver;
import elements.ElementsHelper;
import org.apache.log4j.Logger;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.PageFactory;
import pages.groups.Wait;
import pages.locators.ElementLocators;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementDecorator;
import ru.yandex.qatools.htmlelements.loader.decorator.HtmlElementLocatorFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by kgr on 9/29/2016.
 */
public abstract class BasePage implements Wait {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected PXDriver pxDriver;
    protected ElementsHelper helper;

    public BasePage(PXDriver pxDriver) {
        this.pxDriver = pxDriver;
        this.helper = new ElementsHelper(pxDriver.getWrappedDriver(), pxDriver.getWait());
        PageFactory.initElements(new HtmlElementDecorator(
                new HtmlElementLocatorFactory(pxDriver.getWrappedDriver())), this);
        pxDriver.waitForPageLoaded();
//        pxDriver.waitForAjaxComplete();
        log.info(this.getClass() + " created");
    }

    public void waitAllProgressBarsLoaded() {
        long start = System.currentTimeMillis();
        try {
            helper.waitUntilElementsAttributeToBe(ElementLocators.PROGRESS_BAR, "clientWidth", "0", 20);
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("page is not loaded after timeout in '%d' seconds", 20));
        } finally {
            log.info(String.format("Loading all progress bars on page duration = '%d' seconds",
                    TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
        }
    }
}