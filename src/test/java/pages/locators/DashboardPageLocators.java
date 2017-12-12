package pages.locators;

import org.openqa.selenium.By;

/**
 * Created by kgr on 9/30/2016.
 */
public class DashboardPageLocators {
    public static final String LEFT_MENU = ".//px-sections-menu[@image-class='px-menu-image']";
    public static final String LEFT_MENU_MOBILE = ".//px-sections-menu[@image-class='px-menu-image']";
    // common buttons
    public static final String CREATE_INSTANCE_BUTTON = ".//a[contains(@href, '/create')]";
    public static final String CREATE_INSTANCE_PARAMETERIZED_BUTTON = ".//a[contains(@href, '%s/create') and contains(@class, 'button')]";
    public static final String SAVE_BUTTON = "//span[text()='Save']";
    // error message
    public static final By ERROR_MESSAGE = By.xpath(".//div[contains(@class, 'px-error-message')]");
    public static final By ELEMENT_ERROR_MESSAGE = By.xpath("/following-sibling::div[contains(@class, 'px-error-message')]");
    public static final By ELEMENT_WITH_ERROR_MESSAGE = By.xpath(".//div[contains(@class, 'px-error-message')]/preceding-sibling::div[@data-field-name]");
    // popups
    public static final String WELCOME_POPUP = ".//div[contains(@id, 'wm-shoutout')]";
    public static final By SUPPORT_POPUP = By.xpath(".//*[@class='jx_ui_Widget']");
    public static final By MINIMIZE_SUPPORT_POPUP = By.cssSelector(".jx_ui_Widget [title='Minimize']");
    public final static By CHART_TOOLTIP = By.cssSelector(".tooltip.fade.top");
    // footer links
    public final static String FOOTER_LINKS = ".//div[@class='px-footer-links']";
    public final static String HOME_LINK = ".//a[contains(@href, '/dashboard')]";
    public final static String PRIVACY_POLICY_LINK = ".//a[contains(@href, '/privacy-policy')]";         // www.px.com/privacy-policy/
    public final static String TERMS_AND_CONDITIONS_LINK = ".//a[contains(@href, '/terms-conditions')]"; // www.px.com/terms-conditions
    public final static String SUPPORT_LINK = ".//a[contains(@href, '/support')]";                       // www.px.com/support/
    public final static String CONTACT_LINK = ".//a[contains(@href, '/contact-us/')]";                   // www.px.com/contact-us/
    public final static String VERSION_LINK = ".//a[contains(text(), 'v.')]";
    public final static String EXCHANGE_RATE_LINK = ".//a[@px-templated-tooltip]";
    public final static String REVISION_LINK = ".//a[contains(text(), 'rev.')]";
    // exchange rate
    public static final By CURRENCY_RATE  = By.xpath(".//*[@data-ng-repeat]");
    // ============================================== Top Graphic ================================================
    public static final By STATISTICS_PANEL = By.cssSelector(".px-statistics");

    public static final By PX_STATISTICS_SEPARATED_BLOCK = By.xpath(".//div[@class='px-statistics']/div[@class='px-stat ng-scope']");
    public static final By PX_STATISTICS_SEPARATED_BLOCK_NUM = By.xpath(".//div[@class='px-statistics']/div[@class='px-stat ng-scope']/span[@class='px-stat-num ng-binding']");
    public static final By PX_STATISTICS_SEPARATED_BLOCK_TEXT = By.xpath(".//div[@class='px-statistics']/div[@class='px-stat ng-scope']/span[@class='px-stat-text ng-scope']");
    // ============================================== Daily Performance Graphic ================================================
    public static final By DAILY_PERFORMANCE = By.cssSelector("#dailyPerformance");
    public static final By CATEGORY1_FILTER = By.cssSelector("data-field-name='left'");
    public static final By CATEGORY2_FILTER = By.cssSelector("data-field-name='right'");
    // ============================================== Middle section ================================================
    public static final String GENDER_DOUGHNUT = ".//*[@insight-type='gender']";
    public static final String AGE_DOUGHNUT = ".//*[@insight-type='ageGroup']";
    public static final String STATE_PROGRESSBAR = ".//*[@insight-type='state']";
    public static final By SUMMARY_LINK = By.xpath(".//div[@class='chart-summary']/a");
    // ============================================== Offers ================================================
    public static final String OFFERS_CONTAINER = ".//div[@class='px-offer-overview']";
    // ============================================== Leads statistics ================================================
    public static final By LEAD_STATICS_CONTAINER = By.cssSelector("#shortleadslist");
    public static final By LEAD_STATICS_LIST_CHART = By.cssSelector(".list-container");
    public static final By SHOW_MORE_UP_LEADS_BUTTON = By.cssSelector("[ng-click='goUp()']"); //.px-lead-update-img.up
    public static final By SHOW_MORE_DOWN_LEADS_BUTTON = By.cssSelector("[ng-click='goDown()']"); //.px-lead-update-img

    public static final By BUYING_INFO_BLOCK_CHART = By.cssSelector(".px-buying-info");
    public static final By LEAD_COLUMN_CHART_ = By.cssSelector("config-name='lead'");
    public static final String LEAD_COLUMN_CHART = "//*[@config-name='lead']";
    public static final By CONVERSION_COLUMN_CHART_ = By.cssSelector("config-name='conversion'");
    public static final String CONVERSION_COLUMN_CHART = "//*[@config-name='conversion']";
    public static final By VIEW_MORE_BUTTON = By.cssSelector("a.px-view-repButton");
    // ============================================== Campaigns ================================================
    public static final String CAMPAIGNS_CONTAINER = ".//*[@local-storage-key='dashboard_main_campaigns']/..";
    public static final By OVERVIEW_BUTTON = By.cssSelector("a.px-overview-button");
}