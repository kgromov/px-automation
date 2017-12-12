package elements;

import config.Config;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebElement;
import org.openqa.selenium.support.pagefactory.AjaxElementLocator;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.JsUtils;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kgr on 9/29/2016.
 */
public class ElementsHelper {
    private final Logger log = Logger.getLogger(this.getClass());
    private WebDriver driver;
    private WebDriverWait wait;

    public ElementsHelper(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    public ElementsHelper(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(this.driver, Config.DEFAULT_TIMEOUT);
        this.wait.ignoring(StaleElementReferenceException.class);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageSource() {
        return driver.getPageSource();
    }

    public WebElement parent(WebElement element) {
        return element.findElement(By.xpath(".."));
    }

    public WebElement getElement(By by) {
//        if (isElementPresent(by)) {
        try {
            waitUntilPresent(by);
            return driver.findElement(by);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("There is no element by locator '%s' in DOM", by));
        }
//        }
//        throw new NoSuchElementException(String.format("There is no element by locator '%s' in DOM", by));
    }

    public WebElement getElement(WebElement element, By by) {
        try {
//            waitUntilPresent(by);
            return element.findElement(by);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.format("There is no element by locator '%s' in DOM", by));
        }
    }

    public WebElement getElement(WebElement element, String by) {
        return getElement(element, By.xpath(by));
    }

    public WebElement getElement(String xpath) {
        return getElement(By.xpath(xpath));
    }

    public WebElement getElement(String parentXpath, String xpath) {
        return getElement(getElement(parentXpath), xpath);
    }

    public WebElement getElement(By parentXpath, By xpath) {
        return getElement(getElement(parentXpath), xpath);
    }

    public List<WebElement> getElements(By by) {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            return driver.findElements(by);
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
        }
    }

    public List<WebElement> getElements(String xpath) {
        return getElements(By.xpath(xpath));
    }

    public List<WebElement> getElements(WebElement element, By by) {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            return element.findElements(by);
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
        }
    }

    public List<WebElement> getElements(WebElement element, String xpath) {
        return element.findElements(By.xpath(xpath));
    }

    // locators
    public static String getTextContainsToIgnoreCaseLocator(String tag, String text) {
        String[] textItems = text.split(" ");
        List<String> items = Arrays.stream(textItems).map(item -> String.format("contains(text(), '%s')",
                item.substring(1))).collect(Collectors.toList());
        return String.format(".//%s[%s]", tag, StringUtils.join(items, " and "));
    }

    public static String getAttributeContainsToIgnoreCaseLocator(String tag, String attr, String value) {
        String[] values = value.split(" ");
        // try in the following way
        if (values.length > 1) {
            List<String> items = Arrays.stream(values).map(item -> String.format("contains(@%s, '%s')",
                    attr, item.substring(1))).collect(Collectors.toList());
            return String.format(".//%s[%s]", tag, StringUtils.join(items, " and "));
        }
        return String.format(".//%s[contains(@%s, '%s')]", tag, attr, value);
    }

    public String getLocator(WebElement element) {
        String[] searchContext = element.toString().split(" -> ");
        String locator = searchContext[searchContext.length - 1];
        // formatting
        locator = locator.contains("[") ? locator.replaceAll("\\]+", "\\]") : locator.replaceAll("]", "");
        return locator;
    }

    public By getBy(WebElement element) {
        String locator = getLocator(element);
        String type = locator.substring(0, locator.indexOf(" "));
        String find = locator.substring(locator.indexOf(" ") + 1);
        switch (type) {
            case "id:":
                return By.id(find);
            case "linkText:":
                return By.linkText(find);
            case "partialLinkText:":
                return By.partialLinkText(find);
            case "name:":
                return By.name(find);
            case "tagName:":
                return By.tagName(find);
            case "xpath:":
                return By.xpath(find);
            case "className:":
                return By.className(find);
            case "cssSelector:":
                return By.cssSelector(find);
        }
        throw new IllegalArgumentException(String.format("Unknown locator type\t'%s'", type));
    }

    // for wrapped elements
    public By getByWrapped(WebElement element) {
        try {
            InvocationHandler innerProxy = java.lang.reflect.Proxy.getInvocationHandler(((java.lang.reflect.Proxy) element));
            Field locatorField = innerProxy.getClass().getSuperclass().getDeclaredField("locator");
            locatorField.setAccessible(true);
            AjaxElementLocator locator = (AjaxElementLocator) locatorField.get(innerProxy);
            Field byField = locator.getClass().getSuperclass().getDeclaredField("by");
            byField.setAccessible(true);
            return (By) byField.get(locator);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to get locator from wrapped element");
        }
    }

    public List<String> getTextFromElements(List<WebElement> elements) {
        log.info("Start getting text from list of webElements");
        long start = System.currentTimeMillis();
        try {
            return elements.stream().map(WebElement::getText).collect(Collectors.toList());
        } finally {
            log.info(String.format("Get text sequentially from '%d' elements = '%d'", elements.size(), System.currentTimeMillis() - start));
        }
    }

    public List<String> getTextFromElementsParallel(List<WebElement> elements) {
        log.info("Start getting text from list of webElements");
        long start = System.currentTimeMillis();
        try {
            return elements.parallelStream().map(WebElement::getText).collect(Collectors.toList());
        } finally {
            log.info(String.format("Get text in parallel from '%d' elements = '%d'", elements.size(), System.currentTimeMillis() - start));
        }
    }

    public List<String> getTextFromElementsList(List<List<WebElement>> elements) {
        log.info("Start getting text list from list of webElements list");
        long start = System.currentTimeMillis();
        try {
            Stream<WebElement> webElementStreamStream = elements.parallelStream().flatMap(Collection::stream);
            return webElementStreamStream.map(WebElement::getText).collect(Collectors.toList());
        } finally {
            log.info(String.format("Get list of text list in parallel from '%d' list of elements list = '%d'", elements.size(), System.currentTimeMillis() - start));
        }
    }

    public List<List<String>> getTextFromElementsListParallel(List<List<WebElement>> elements) {
        log.info("Start getting list of text from list of webElements list");
        long start = System.currentTimeMillis();
        try {
            return elements.parallelStream().map(elements.size() > 10
                    ? this::getTextFromElements
                    : this::getTextFromElementsParallel)
                    .collect(Collectors.toList());
        } finally {
            log.info(String.format("Get list of text in parallel from '%d' list of elements list = '%d'", elements.size(), System.currentTimeMillis() - start));
        }
    }

    public void scrollToElement(WebElement element) {
        try {
            log.info("Scroll to element\t" + getLocator(element));
            if (!element.isDisplayed() || (element.getLocation().getX() == 0 && element.getLocation().getY() == 0)
                    || (element.getSize().getWidth() == 0 && element.getSize().getHeight() == 0)) {
                element = getClosestVisibleParent(element);
            }
            Actions actions = new Actions(((RemoteWebElement) element).getWrappedDriver());
            actions.moveToElement(element).build().perform();
//            new RemoteMouse().mouseMove(((RemoteWebElement) element).getCoordinates())
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable scroll to element\tcause\n" + e.getMessage());
        }
    }

    public void scrollToElement(By by) {
        WebElement element = HelperSingleton.getHelper().getElement(by);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView();", element);
    }

    public void clickOnElement(WebElement element) {
        try {
            Actions actions = new Actions(((RemoteWebElement) element).getWrappedDriver());
            actions.clickAndHold(element).build().perform();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable move to element\tcause\n" + e.getMessage());
        }
    }

    public void doubleClickOnElement(WebElement element) {
        try {
            Actions actions = new Actions(((RemoteWebElement) element).getWrappedDriver());
            actions.doubleClick(element).build().perform();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable move to element\tcause\n" + e.getMessage());
        }
    }

    public void moveToElement(WebElement element) {
        try {
            Actions actions = new Actions(((RemoteWebElement) element).getWrappedDriver());
            actions.moveToElement(element).build().perform();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unable move to element\tcause\n" + e.getMessage());
        }
    }

    public void mouseMove(int x, int y) {
        try {
            log.info(String.format("Move mouse to coordinates [%d; %d]", x, y));
            new Robot().mouseMove(x, y);
        } catch (AWTException e) {
            log.info(String.format("Unable to move mouse to coordinates [%d; %d]\nCause %s", x, y, e.getMessage()));
        }
    }

    public void mouseMove() {
        mouseMove(0, 0);
    }

    public void highlightElement(WebDriver driver, WebElement element) {
        try {
            JsUtils jsUtils = new JsUtils(driver);
            element = !element.isDisplayed() ? getClosestVisibleParent(element) : element;
            scrollToElement(element);
            jsUtils.markElementWithBorder(element);
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error("Unable to highlight element: '" + element.getTagName() + "' class='" + element.getAttribute("class") + "'" + e.getMessage());
        }
    }

    private WebElement getClosestVisibleParent(WebElement element) {
        WebElement parent = element.findElement(By.xpath(".."));
        while (!parent.isDisplayed() && element.getSize().getWidth() == 0 && element.getSize().getHeight() == 0) {
            parent = getClosestVisibleParent(parent);
        }
        return parent;
    }

    public void type(final WebElement element, final String text) {
        log.info("Type '" + text + "'");
        element.clear();
        element.sendKeys(text);
    }

    public void type(final By by, final String text) {
        type(getElement(by), text);
    }

    public void setValue(final WebElement element, String text) {
        ((JavascriptExecutor) driver).executeScript(String.format("arguments[0].value='%s'", text), element);
    }

    public void waitForAjaxComplete() {
        log.info("waiting for ajax completion");
        try {
            wait.withTimeout(Config.DEFAULT_SCRIPT_TIMEOUT, TimeUnit.SECONDS);
            wait.until(new ExpectedCondition<Boolean>() {
                public Boolean apply(WebDriver driver) {
                    return new JsUtils(driver).isAjaxComplete();
                }
            });
            log.info("All ajax calls are complete");
        } catch (TimeoutException e) {
            throw new Error("Ajax is not completed ($.active!=0) after timeout in " + Config.DEFAULT_SCRIPT_TIMEOUT + " seconds");
        } finally {
            wait.withTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void waitUntilAttributeToBeNotEmpty(WebElement element, String attribute) {
        log.info("waiting until attribute will be not empty");
        wait.until(ExpectedConditions.attributeToBeNotEmpty(element, attribute));
    }

    public void waitUntilAttributeToBe(final By by, String attribute, String value) {
        waitUntilAttributeToBe(by, attribute, value, 10);
    }

    public void waitUntilAttributeToBe(final By by, String attribute, String value, int timeOut) {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            wait.withTimeout(timeOut, TimeUnit.SECONDS);
            log.info(String.format("Wait until element's '%s' attribute '%s' to be '%s'", by, attribute, value));
            for (WebElement element : driver.findElements(by)) {
                try {
                    if (element.isDisplayed() && !element.getAttribute(attribute).equals(value)) {
                        log.info(String.format("Start wait attribute changed from '%s' to expected '%s'", element.getAttribute(attribute), value));
                        wait.until(ExpectedConditions.attributeToBe(by, attribute, value));
                        log.info(String.format("End wait attribute changed from '%s' to expected '%s'", element.getAttribute(attribute), value));
                        break;
                    }
                } catch (StaleElementReferenceException ignored) {
                }
            }
        } catch (TimeoutException | NoSuchElementException e) {
            throw new TimeoutException(String.format("Element's attribute '%s' by locator '%s' is not the same as expected '%s' after timeout '%d' seconds" +
                    "\nCause%s", by, attribute, value, timeOut, e.getMessage()));
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
            wait.withTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void waitUntilElementsAttributeToBe(final By by, String attribute, String value, int timeOut) {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            wait.withTimeout(timeOut, TimeUnit.SECONDS);
            log.info(String.format("Wait until all elements '%s' attribute '%s' to be '%s'", by, attribute, value));
            // wait all elements
            driver.findElements(by).stream()
                    .filter(WebElement::isDisplayed)
                    .filter(element -> !element.getAttribute(attribute).equals(value))
                    .forEach(element -> wait.until(ExpectedConditions.attributeToBe(element, attribute, value)));
        } catch (TimeoutException | NoSuchElementException e) {
            log.error(String.format("Element's attribute '%s' by locator '%s' is not the same as expected '%s' after timeout '%d' seconds" +
                    "\nCause%s", by, attribute, value, timeOut, e.getMessage()));
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
            wait.withTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
        }
    }

    public void waitUntilDisplayed(final WebElement element) {
        try {
            log.info("waiting for displayed");
            wait.until(ExpectedConditions.visibilityOf(element));
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Element by locator '%s' is not present in DOM " +
                    "after timeout in '%d' seconds", element.toString(),/*getBy(element)*/ Config.DEFAULT_TIMEOUT));
        }
    }

    public void waitUntilDisplayed(By by, long timeOut) {
        try {
//            waitUntilPresent(by);
            log.info(String.format("Waiting element %s to be visible ", by));
            new WebDriverWait(driver, timeOut).until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Element by locator '%s' is not present in DOM " +
                    "after timeout in '%d' seconds", by,/*getBy(element)*/ timeOut));
        }
//        waitUntilDisplayed(getElement(by));
    }

    public void waitUntilDisplayed(String by, long timeOut) {
        waitUntilDisplayed(By.xpath(by), timeOut);
    }

    public void waitUntilDisplayed(By by) {
        try {
            waitUntilPresent(by);
            log.info(String.format("Waiting element %s to be visible ", by));
            wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Element by locator '%s' is not present in DOM " +
                    "after timeout in '%d' seconds", by,/*getBy(element)*/ Config.DEFAULT_TIMEOUT));
        }
//        waitUntilDisplayed(getElement(by));
    }

    public void waitUntilDisplayed(String by) {
        waitUntilDisplayed(By.xpath(by));
    }

    public void waitUntilDisplayed(By by, String sub_by) {
        try {
            waitUntilPresent(by);
            log.info(String.format("Waiting element %s to be visible ", by));
            wait.until(ExpectedConditions.visibilityOfNestedElementsLocatedBy(by, By.xpath(sub_by)));
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Element by locator '%s' is not present in DOM " +
                    "after timeout in '%d' seconds", by,/*getBy(element)*/ Config.DEFAULT_TIMEOUT));
        }
    }

    public void waitUntilToBeClickable(final WebElement element, By sub_by, long timeOut) {
        log.info("Waiting for sub element to be clickable");
        new WebDriverWait(driver, timeOut).until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return isElementAccessible(element, sub_by);
            }
        });
    }

    public void waitUntilToBeClickable(final WebElement element) {
        log.info("waiting for element to be clickable");
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void waitUntilToBeClickable(By by) {
        try {
            log.info("waiting for element to be clickable");
            wait.until(ExpectedConditions.elementToBeClickable(by));
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Element by locator '%s' is not clickable " +
                    "after timeout in '%d' seconds", by, Config.DEFAULT_TIMEOUT));
        }
    }

    public void waitUntilToBeClickable(final String by) {
        waitUntilToBeClickable(By.xpath(by));
    }

    public void waitUntilPresent(final String locator) {
        waitUntilPresent(By.xpath(locator));
    }

    public void waitUntilPresent(final By locator) {
        log.info("waiting for element '" + locator + "' to be present in DOM");
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (NoSuchElementException | TimeoutException e) {
            throw new TimeoutException(String.format("Element by locator '%s' is not present in DOM " +
                    "after timeout in '%d' seconds", locator, Config.DEFAULT_TIMEOUT));
        }
    }

    public void waitUntilPresent(final WebElement element) {
        log.info("waiting for element to be present in DOM");
        wait.until(ExpectedConditions.not(ExpectedConditions.stalenessOf(element)));
    }

    public void waitUntilToDisappear(final WebElement element) {
        log.info("waiting for element to be present in DOM");
        wait.until(ExpectedConditions.stalenessOf(element));
    }

    public void waitUntilToBeInvisible(final By locator) {
        try {
            if (!isElementPresent(locator)) return;
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            log.info("waiting for element to be invisible in DOM");
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Element by locator '%s' is still present after timeout in '%d' seconds", locator, Config.DEFAULT_TIMEOUT));
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
        }
    }

    public void waitUntilToBeInvisible(By by, long timeOut) {
        try {
//            waitUntilPresent(by);
            if (!isElementPresent(by)) return;
            log.info(String.format("Waiting element %s to be invisible ", by));
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            new WebDriverWait(driver, timeOut).until(ExpectedConditions.invisibilityOfElementLocated(by));
        } catch (TimeoutException e) {
            throw new TimeoutException(String.format("Element by locator '%s' is still present in DOM " +
                    "after timeout in '%d' seconds", by, timeOut));
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
        }
    }

    // the same waitUntilDisplayed
    public void waitUntilToBeInvisible(WebElement element, final By locator) {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            log.info("waiting for element to be invisible in DOM");
            wait.until((ExpectedCondition<Boolean>) driver -> {
                try {
                    return !(element.findElement(locator).isDisplayed());
                } catch (NoSuchElementException | StaleElementReferenceException e) {
                    return true;
                }
            });
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
        }
    }

    public void waitUntilToBeInvisible(final String locator) {
        waitUntilToBeInvisible(By.xpath(locator));
    }

    public void waitUntilToBeInvisible(final String locator, long timeout) {
        log.info(String.format("Fluent waiting for element '%s'", locator));
        if (isElementPresent(locator, 2)) {
            log.info(String.format("Element by locator '%s' should be invisible after '%d' seconds", locator, timeout));
            try {
                wait.withTimeout(timeout, TimeUnit.SECONDS);
                waitUntilToBeInvisible(locator);
            } catch (TimeoutException | NoSuchElementException e) {
                log.error(String.format("Element by locator '%s' still visible after timeout '%d' seconds", locator, timeout));
                log.error("Wait until visibility error\n" + e);
                throw new TimeoutException(String.format("Element by locator '%s' still visible after timeout '%d' seconds", locator, timeout));
            } finally {
                wait.withTimeout(Config.DEFAULT_TIMEOUT, TimeUnit.SECONDS);
            }
        }
    }

    public void waitUntilChildrenNumber(By childLocator, int expectedCount) {
        log.info(String.format("Wait till there will be '%d' child elements by locator '%s'", expectedCount, childLocator));
        wait.until(ExpectedConditions.numberOfElementsToBe(childLocator, expectedCount));
    }

    public void waitUntilChildrenNumber(By childLocator, int min, int max) {
        log.info(String.format("Wait till there will be no less than '%d' and no more than '%d' child elements by locator '%s'", min, max, childLocator));
        wait.until(ExpectedConditions.and(ExpectedConditions.numberOfElementsToBeMoreThan(childLocator, min),
                ExpectedConditions.numberOfElementsToBeLessThan(childLocator, max)));
    }

    public void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    public void click(By by) {
        wait.until(ExpectedConditions.elementToBeClickable(by));
        getElement(by).click();
    }

    public void click(String by) {
        click(By.xpath(by));
    }

    public boolean isElementPresent(String by) {
        return isElementPresent(By.xpath(by));
    }

    public boolean isElementPresent(String by, long timeOut) {
        return isElementPresent(By.xpath(by), timeOut);
    }

    public boolean isElementPresent(By by) {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            return driver.findElements(by).size() > 0;
        } catch (NoSuchElementException e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
        }
    }

    public boolean isElementPresent(By by, long timeOutSeconds) {
        try {
            driver.manage().timeouts().implicitlyWait(timeOutSeconds, TimeUnit.SECONDS);
            return driver.findElements(by).size() > 0;
        } catch (NoSuchElementException e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
        }
    }

    public boolean isElementPresent(WebElement element, By by) {
        try {
            driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
            return element.findElements(by).size() > 0;
        } catch (StaleElementReferenceException e) {
            return false;
        } finally {
            driver.manage().timeouts().implicitlyWait(Config.IMPLICITLY_WAIT, TimeUnit.SECONDS);
        }
    }

    public boolean isElementPresent(WebElement element, String by) {
        return isElementPresent(element, By.xpath(by));
    }

    public boolean isElementAccessible(WebElement parent, By by) {
        try {
            if (!isElementPresent(parent, by)) return false;
            WebElement element = getElement(parent, by);
            return element.isDisplayed() && element.isEnabled();
        } catch (NoSuchElementException | StaleElementReferenceException | ElementNotVisibleException e) {
            return false;
        }
    }

    public boolean isElementAccessible(By by) {
        try {
            if (!isElementPresent(by)) return false;
            WebElement element = getElement(by);
            return element.isDisplayed() && element.isEnabled();
        } catch (NoSuchElementException | ElementNotVisibleException e) {
            return false;
        }
    }

    public boolean isElementAccessible(String by) {
        return isElementAccessible(By.xpath(by));
    }

    public List<WebElement> getVisibleList(List<WebElement> list) {
        return list.stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
    }

    public void pause(long value) {
        try {
            Thread.sleep(value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public WebDriverWait getWait() {
        return wait;
    }

    // iFrame
    public WebElement getElementInFrame(WebElement iFrame, By by) {
        try {
            driver.switchTo().frame(iFrame);
            return getElement(by);
        } catch (WebDriverException e) {
            throw new NoSuchElementException(String.format("Unable to find element '%s' in iFrame", by));
        } finally {
            driver.switchTo().defaultContent();
        }
    }

    public WebElement getElementInAnyFrame(By by) {
        List<WebElement> iFrames = getElements(".//iframe");
        for (WebElement iFrame : iFrames) {
            try {
                driver.switchTo().frame(iFrame);
                if (isElementPresent(by))
                    return getElement(by);
            } catch (StaleElementReferenceException ignored) {
            }
            driver.switchTo().defaultContent();
        }
        return null;
    }

    public boolean isElementPresentInAnyFrame(By by) {
        List<WebElement> iFrames = getElements(".//iframe");
        for (WebElement iFrame : iFrames) {
            driver.switchTo().frame(iFrame);
            if (isElementPresent(by)) {
                try {
                    return true;
                } finally {
                    driver.switchTo().defaultContent();
                }
            }
            driver.switchTo().defaultContent();
        }
        return false;
    }
}