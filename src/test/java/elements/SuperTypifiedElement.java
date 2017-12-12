package elements;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;

import java.lang.reflect.Proxy;

import static pages.locators.ElementLocators.LABEL;

/**
 * Created by kgr on 9/29/2016.
 */
//InvocationHandler innerProxy = Proxy.getInvocationHandler(((Proxy) element));
public abstract class SuperTypifiedElement extends TypifiedElement {
    protected final Logger log = Logger.getLogger(this.getClass());
    //    protected WebElement element;
    protected ElementsHelper helper;
    protected String label;
    protected By by;

    protected SuperTypifiedElement(WebElement wrappedElement) {
        super(wrappedElement);
        setHelper();
    }

    // manage methods
    protected void setHelper() {
        this.helper = HelperSingleton.getHelper();
    }

    /*
    // later on implement such method to refresh element (e.g. after saving input could change type and wouldn't be present in DOM):
    // 1) store locator (somehow -> new constructor or by proxy element)
    protected void refreshElement() {
        setElement(helper.getElement(by));
    }

    protected void setElement() {
        this.element = element == null ? element = helper.getElement(by) : element;
    }

    public void setElement(WebElement element) {
        this.element = element;
    }

    public WebElement getElement() {
        return element;
    }*/

    public void setLabel(String label) {
        this.label = label;
    }

    protected void setLabel() {
//        this.label = helper.getElement(getWrappedElement(), LABEL).getText().trim();
        this.label = helper.getElement(getWrappedElement(), LABEL).getAttribute("textContent").replaceAll("\r|\n|\t|\\s{2,}", "");
    }

    public String getLabel() {
//        return label == null ? helper.getElement(getWrappedElement(), LABEL).getText().trim() : label;
        return label == null ? helper.getElement(getWrappedElement(), LABEL).getAttribute("textContent").replaceAll("\r|\n|\t|\\s{2,}", "") : label;
    }

    public By getBy() {
        return by = by == null ? (getWrappedElement() instanceof Proxy
                ? helper.getByWrapped(getWrappedElement()) : helper.getBy(getWrappedElement())) : by;
    }

    public void setByText(String text) {
        throw new UnsupportedOperationException("Implementation required in child class - " + this.getClass().getName());
    }

    public String getValue() {
        throw new UnsupportedOperationException("Implementation required in child class - " + this.getClass().getName());
    }

}