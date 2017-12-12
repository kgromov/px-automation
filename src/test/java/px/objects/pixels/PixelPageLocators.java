package px.objects.pixels;

import org.openqa.selenium.By;

/**
 * Created by kgr on 5/3/2017.
 */
public class PixelPageLocators {
    // menu 'Publisher settings'
    public final static String GENERAL_INFO_ITEM = "General";
    public final static String TEST_ITEM = "Tracking data";
    public final static By GENERAL_CONTAINER = By.cssSelector("#general");
    // ---------------------------- Test section ----------------------------
    public final static By TEST_CONTAINER = By.cssSelector("#test");
    public final static By PUBLISHER_INPUT = By.xpath(".//*[@data-field-name='publisherId']");
    public final static By OFFER_INPUT = By.xpath(".//*[@data-field-name='offerId']");
    public final static By PUBLISHER_URL_INPUT = By.xpath(".//*[@data-field-name='publisherLink']");
    public final static By OPEN_BUTTON = By.xpath(".//*[text()='Open']");
    public final static By CONVERSION_URL_INPUT = By.xpath(".//*[@data-field-name='conversionUrl']");
    public final static By TEST_BUTTON = By.xpath(".//*[text()='Test']");
    // ---------------------------- Offer Pixels section ----------------------------
    public static final String PREVIEW_LINK = ".//a[text()='Preview']";
    public static final String TEST_LINK = ".//a[text()='Test']";
    public static final String DELETE_LINK = ".//a[text()='Delete']";
    public static final String LOGIN_AS_LINK = ".//a[contains(text(), 'Log')]";
}
