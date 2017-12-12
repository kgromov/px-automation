package pages.locators;

import org.openqa.selenium.By;

/**
 * Created by kgr on 10/7/2016.
 */
public class ElementLocators {
    // radio buttons
    public final static String RADIO_BUTTON = ".//ancestor-or-self::div[contains(@class, 'px-field-radio') or contains(@class, 'px-radio-button')]";
    // input
    public final static String INPUT_CONTAINER = ".//ancestor-or-self::div[contains(@class, 'px-field-input')]"; //div[contains(@class, 'px-field-input')]
    public final static String TEXT_AREA_CONTAINER = ".//ancestor-or-self::div[contains(@class, 'px-field-textarea')]";
    public final static String TEXT_VALUE = ".//following-sibling::*[contains(@class, 'px-value')]";
    public final static String LABEL = ".//*[contains(@class, 'px-field-title')]";
    public final static String INPUT_ELEMENT = ".//input[@type='text']";
    // table
    public final static By PROGRESS_BAR = By.id("ngProgress");
    public final static String PROGRESS_BAR_CHILD = "//*[@id='ngProgress']";
    public final static String RIGHT_PART_CONTAINER = ".//*[contains(@class, 'px-rightside')]";
    public final static String ITEMS_TABLE = ".//table[@class='px-overview-table']";
    public final static String LOADING_TABLE = ".//tr/descendant-or-self::td[contains(text(), 'Data is')]/ancestor-or-self::tr";
    public final static String AVAILABLE_DAY_RANGE_CALENDAR = ".//td/descendant-or-self::*[text()='%d' or @innerHTML='%d']/ancestor-or-self::td[not(contains(@class, 'off'))]";
    public final static String AVAILABLE_DAY_MONTH_CALENDAR = ".//td/descendant-or-self::*[text()='%d' or @innerHTML='%d']/ancestor-or-self::td[not(contains(@class, 'old'))]";
    public final static String SEARCH_FILTER = ITEMS_TABLE + "//div[contains(@class, 'px-loupe')]";
    public final static String COLUMNS_FILTER = ITEMS_TABLE + "//div[contains(@class, 'px-wheel')]";
    // pagination
    public final static String ITEMS_PER_PAGE = ".//div[contains(@class, 'px-table-counts')]";
    public final static String ITEM_PER_PAGE = ITEMS_PER_PAGE + "//button";
    public final static String ITEM_PER_PAGE_PARAMETERIZED = ITEMS_PER_PAGE + "/button/descendant-or-self::*[text()='%d']";
    public final static String ACTIVE_ITEM_PER_PAGE = ITEMS_PER_PAGE + "/button[contains(@class, 'active')]";
    public final static String PAGE_PAGINATION = ".//div[contains(@class, 'px-table-pagination')]";
    public final static String PAGES_PAGINATION = PAGE_PAGINATION + "//*[@*='page.number']/ancestor-or-self::a";
    public final static String PAGES_PAGINATION_BY_INDEX = "(" + PAGES_PAGINATION + ")[%d]";
    public final static String PAGES_PAGINATION_BY_TEXT = PAGE_PAGINATION + "//*[@*='page.number' and text()='%d']/ancestor-or-self::a";
    public final static String MORE_PAGES_PAGINATION = PAGE_PAGINATION + "//*[@*='more']/ancestor-or-self::a";
    public final static int MAX_VISIBLE_PAGES_PAGINATION = 9;
    public final static int MIN_VISIBLE_PAGES_PAGINATION = 6;
    // new pagination
    public static final By GO_TO_PAGE = By.cssSelector(".px-go-to-page");
    public static final By GO_TO_PAGE_INPUT = By.cssSelector(".px-go-to-page input");
    public static final By GO_TO_PAGE_PAGES = By.cssSelector(".px-go-to-page span.ng-binding");
    public static final By PAGINATION_PAGES_NAVIGATOR = By.cssSelector(".px-left-right");
    public static final By PAGINATION_PAGES_STAT = By.cssSelector(".px-left-right .px-table-rows-total");
    public static final By PAGINATION_PAGES_PREV = By.cssSelector(".px-left-right .px-previous-page");
    public static final By PAGINATION_PAGES_NEXT = By.cssSelector(".px-left-right .px-next-page");
    // title
    public final static String SAVE_BUTTON = ".//a[@class='px-save']";
    public final static String EDIT_BUTTON = ".//a[@class='px-edit']";
    public final static String REVERT_BUTTON = ".//a[@class='px-revert']";
    public final static String TOGGLE_BUTTON = ".//i";
    // subtitles
    public final static String CREATE_TOGGLE = "//div[contains(@class, 'px-sub-title')]//i[not(@*)]";
    public final static String CREATE_BUTTON = "//*[text()='Create']";
    public final static String UPDATE_BUTTON = "//*[text()='Update']";
    public final static String CANCEL_BUTTON = "//*[text()='Cancel']";
    // drop downs
    public final static String FILTERED_DROPDOWN = ".//div[@class='px-selectbox-dropdown show']";
    public final static String DROPDOWN_OPTION = ".//div[@class='acol']";
    public final static String FILTERED_DROPDOWN_OPTION = "//div[@style='']//div[@class='acol']";
    public final static String DROPDOWN_TABLE_CONTAINER = ".//div[@class='px-table-control px-active']//div[@class='px-table-custom-dropdown']";
    public final static String ACTIONS_CELL = ".//td[contains(@class, 'px-table-drop')]";
    public final static String MULTI_SELECT_DROPDOWN = ".//div[@class='px-configDrop checkbox-filters']";
    public final static String ACTIONS_DROPDOWN = ".//div[contains(@class, 'px-configDrop')]";
    public final static String SELECT_CONTAINER = ".//ancestor-or-self::div[contains(@class, 'px-field-select')]";
    public final static String MULTI_SELECT_CONTAINER = ".//ancestor-or-self::isteven-multi-select";
    // general title
    public final static String GENERAL_HEADER = "//div[@class='px-section-title']";
    // single checkbox
    public final static String CHECKBOX_CONTAINER = "//div[contains(@class, 'px-field-checkbox')]";
    public static final By CHECKBOX_TICK = By.cssSelector(".px-checkbox");
    public static final By CHECKBOX_INPUT = By.xpath(".//input[@type='checkbox']");
    // settings menu
    public final static String SETTING_MENU = ".//ol/..";
    // popup
    public static final By POPUP = By.cssSelector(".px-popupWin-center");
    public final static String DELETE_POPUP = ".//div[@class='px-popupWin-center']";
    public final static String POPUP_CONFIRM = ".//*[contains(@class, 'px-button') and contains(@class, 'yes')]";
    public final static String POPUP_DISMISS = ".//*[contains(@class, 'px-button') and contains(@class, 'no')]";
    public final static String POPUP_CLOSE = ".//*[contains(@class, 'px-button') and contains(@ng-show, 'close')]";
    public final static By POPUP_TEXT = By.cssSelector(".progress-bar-summary,.px-popUp-text");
    public final static By POPUP_MARKUP_CELL = By.cssSelector("px-popup-chart.ng-binding");
    public final static By POPUP_MARKUP_CELL_2 = By.cssSelector("a.ng-binding");
    public static final String CELL_AT = "tbody > tr:nth-of-type(%d) > td:nth-of-type(%d)";
    public static final String POPUP_MARKUP_CHART_AT_CELL = "tbody > tr:nth-of-type(%d) > td:nth-of-type(%d) px-popup-chart.ng-binding";
    public static final String POPUP_MARKUP_LINK_AT_CELL = "tbody > tr:nth-of-type(%d) > td:nth-of-type(%d) a.ng-binding";
    // inside popup
    public final static By POPUP_CHART = By.cssSelector(".px-popupWin-center .chart");
    public final static By POPUP_AREA_GRAPHIC = By.cssSelector(".px-popupWin-center area-chart");
    public final static By POPUP_PIE_GRAPHIC = By.cssSelector(".px-popupWin-center px-donut-chart");
    public final static By POPUP_PIE_LEGEND = By.cssSelector(".px-popupWin-center .px-legend");
    // table tooltip
    public final static By TABLE_TOOLTIP = By.xpath(".//*[@id='px-templated-tooltip']");
    public final static By HEADER_TOOLTIP_ICON = By.xpath(".//a[@px-templated-tooltip]");
    public static final String DATA_FIELD_ELEMENT = ".//div[@data-field-name]";
    public static final String DATA_FIELD_PARAMETERIZED_ELEMENT = ".//*[@data-field-name='%s']";
    public static final String DATA_FIELD_PREVIEW_ELEMENT = ".//following-sibling::*[contains(@class, 'px-value')]/ancestor-or-self::*[@data-field-name]";

}
