package px.objects.filters;

import org.openqa.selenium.By;

/**
 * Created by kgr on 10/18/2016.
 */
public class FilterManagementPageLocators {
    // ================================== Campaigns page ==================================
    public static final String FILTER_PREVIEW_STAT = ".//div[@class='px-filter-leads-number']";
    public static final String FILTER_PREVIEW_CONTAINER = ".//div[@class='px-filter-text-view']";
    public static final String FILTER_PREVIEW_ITEM = ".//li"; // could have nested - .//ul[@class='px-tree-view']//li; .//px-filter-preview; class contains hasChildren
    public static final String SAVE_BUTTON = ".//a[text()='Save']";
    public static final String REVERT_BUTTON = ".//a[text()='Revert']";
    // tree
    public static final By FILTER_TREE = By.cssSelector("#filterConfig");
    // add new
    public static final By NEW_FILTER_RULE = By.cssSelector(".rule-type");      // select {rule;operand}
    public static final String ADD_BUTTON = ".//a[text()='Add']";
    public static final String CANCEL_BUTTON = ".//a[text()='Cancel']";
    // new node
    public static final By OPERAND_NODE = By.cssSelector(".content");
    public static final By ADD_CRITERIA_BUTTON = By.cssSelector(".new-rule");
    public static final By NEW_NODE = By.xpath("(.//*[@class='content'])[last()]");
    public static final By NEW_DATA_NODE = By.xpath("(.//*[@class='existing-rule'])[last()]");
    // operand
    public static final By OPERAND_NODE_NAME = By.cssSelector(".type");            // select
    // data node
    public static final By DATA_NODE = By.cssSelector(".existing-rule");
    public static final By DATA_NODE_NAME = By.cssSelector(".command-tag");         // select
    public static final By COMMAND_NODE = By.cssSelector(".check-type");
    public static final By COMMAND_VALUE = By.xpath(".//px-filter-edit-value");
    // range only (between 2 values):
    public static final By LEFT_BOUNDARY = By.xpath(".//input[contains(@ng-model, 'leftBoundary')]");
    public static final By RIGHT_BOUNDARY = By.xpath(".//input[contains(@ng-model, 'rightBoundary')]");
    public static final By DELETE_DATA_NODE = By.cssSelector("a.px-delete");         // delete rule
}