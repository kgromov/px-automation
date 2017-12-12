package utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


@SuppressWarnings(value = "unchecked")
public class JsUtils {
    protected final Logger log = Logger.getLogger(this.getClass());
    private JavascriptExecutor js;

    public JsUtils(WebDriver driver) {
        this.js = (JavascriptExecutor) driver;
    }

    private Object exec(String script) {
        log.info("Execute Script on page: " + script);
        try {
            return js.executeScript(script);
        } catch (Exception e) {
            throw new Error("Unable to execute script:\n" + script + "cause " + e.getMessage());
        }
    }

    public boolean isAjaxComplete() {
        boolean result = false;
        try {
//            result = (Boolean) js.executeScript("return typeof($) !== 'undefined' && ($.active == 0 || $.active == undefined)");
            result = (Boolean) js.executeScript("return typeof($) !== 'undefined' && $.active == 0");
            log.info("Ajax return $.active == 0  is: '" + result + "'");
        } catch (Exception e) {
            return false;
        }
        return result;
    }

    public boolean isPageLoaded() {
        try {
            return js.executeScript("return document.readyState").equals("complete");
        } catch (Exception e) {
            return false;
        }
    }

    public void markElementWithBorder(WebElement element){
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "border: 2px solid red;");
    }

    public void unmarkElementWithBorder(WebElement element){
        js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
    }

    public static <T> T execute(String js, WebDriver driver) {
        return (T) execute(js, driver, new Object[0]);
    }

    private static <T> T execute(String js, WebDriver driver, Object... arguments) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        return (T) jsExecutor.executeScript(js, arguments);
    }

}
