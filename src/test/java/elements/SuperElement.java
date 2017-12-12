package elements;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

/**
 * Created by kgr on 10/13/2016.
 */
public abstract class SuperElement extends HtmlElement {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected ElementsHelper helper;
    protected WebDriver driver;
    protected By by;

    protected void setHelper() {
        this.helper = HelperSingleton.getHelper();
    }

    public void refreshElement() {
        this.setWrappedElement(helper.getElement(by));
    }
}