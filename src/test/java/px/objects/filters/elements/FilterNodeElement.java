package px.objects.filters.elements;

import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.dropdown.FilteredDropDown;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import px.objects.filters.nodes.FilterNode;

import java.util.Arrays;
import java.util.List;

import static px.objects.filters.FilterManagementPageLocators.*;
import static px.objects.filters.nodes.FilterNode.FilterNodeTypeEnum.OPERAND;

/**
 * Created by kgr on 10/24/2017.
 */
public abstract class FilterNodeElement {
    protected final Logger log = Logger.getLogger(this.getClass());
    protected final ElementsHelper helper = HelperSingleton.getHelper();
    protected FilterNode node;
    protected WebElement element;
    // possible values, no type:
    public static final String OPERAND_TYPE = "And / Or statement";
    public static final String DATA_TYPE = "Filter rule";
    List<String> TYPES = Arrays.asList("Filter rule", "And / Or statement");

    public FilterNodeElement(FilterNode node) {
        this.node = node;
        this.element = helper.getElement(NEW_NODE);
    }

    public FilterNodeElement(FilterNode node, WebElement element) {
        this.node = node;
        this.element = element;
    }

    public abstract void setName();

    public abstract void setCommand();

    public abstract void setValue();

    public abstract String getName();

    public abstract String getValue();

    // or probably move to level up
    public void addNode() {
        log.info(String.format("Set filter node with name '%s'", node.getName()));
        helper.getElement(element, ADD_CRITERIA_BUTTON).click();
        FilteredDropDown type = new FilteredDropDown(helper.getElement(element, NEW_FILTER_RULE));
        type.setByTitle(node.getType().equals(OPERAND.name()) ? OPERAND_TYPE : DATA_TYPE);
        helper.getElement(element, ADD_BUTTON).click();
    }

    public abstract void deleteNode();
}
