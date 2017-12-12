package px.objects.filters.elements;

import elements.dropdown.FilteredDropDown;
import org.openqa.selenium.WebElement;
import px.objects.filters.nodes.FilterNode;

import static px.objects.filters.FilterManagementPageLocators.DATA_NODE_NAME;
import static px.objects.filters.FilterManagementPageLocators.OPERAND_NODE_NAME;
import static px.objects.filters.nodes.FilterNodeData.REMOVE_OPERAND;

/**
 * Created by kgr on 10/24/2017.
 */
public class OperandFilterNodeElement extends FilterNodeElement {

    public OperandFilterNodeElement(FilterNode node) {
        super(node);
    }

    public OperandFilterNodeElement(FilterNode node, WebElement element) {
        super(node, element);
    }

    @Override
    public void setName() {
        FilteredDropDown type = new FilteredDropDown(helper.getElement(element, OPERAND_NODE_NAME));
        type.setByTitle(node.getName());
    }

    @Override
    public void setCommand() {
        throw new UnsupportedOperationException("Irrelevant method - no commands for operand node");
    }

    @Override
    public void setValue() {
        throw new UnsupportedOperationException("Irrelevant method - no values for operand node");
    }

    @Override
    public String getName() {
        FilteredDropDown type = new FilteredDropDown(helper.getElement(element, OPERAND_NODE_NAME));
        return type.getValue();
    }

    @Override
    public String getValue() {
        throw new UnsupportedOperationException("Irrelevant method - no values for operand node");
    }

    @Override
    public void deleteNode() {
        FilteredDropDown type = new FilteredDropDown(helper.getElement(element, DATA_NODE_NAME));
        type.setByTitle(REMOVE_OPERAND);
    }

}
