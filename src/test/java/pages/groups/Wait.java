package pages.groups;

import elements.ElementsHelper;
import elements.HelperSingleton;
import org.openqa.selenium.TimeoutException;
import pages.locators.ElementLocators;

import java.util.concurrent.TimeUnit;

import static pages.locators.ElementLocators.LOADING_TABLE;

/**
 * Created by kgr on 7/6/2017.
 */
public interface Wait {

    default void waitPageIsLoaded() {
        waitPageIsLoaded(40);
    }

    default void waitPageIsLoaded(int timeOut) {
        long start = System.currentTimeMillis();
        try {
            ElementsHelper helper = HelperSingleton.getHelper();
            helper.waitUntilAttributeToBe(ElementLocators.PROGRESS_BAR, "clientWidth", "0", timeOut);
            helper.waitUntilToBeInvisible(LOADING_TABLE, timeOut);
//            helper.waitUntilElementsAttributeToBe(ElementLocators.PROGRESS_BAR, "clientWidth", "0", timeOut);
        } catch (TimeoutException e) {
            throw new RuntimeException(String.format("Page is not loaded after timeout in '%d' seconds",
                    TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
        }
        System.out.println(String.format("Loading page duration = '%d' seconds",
                TimeUnit.SECONDS.convert(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS)));
    }
}
