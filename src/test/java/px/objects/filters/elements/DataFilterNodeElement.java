package px.objects.filters.elements;

import elements.dropdown.FilteredDropDown;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;
import px.objects.filters.nodes.CommandFilterNode;
import px.objects.filters.nodes.DataFilterNode;
import px.objects.filters.nodes.FilterNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static px.objects.filters.FilterManagementPageLocators.*;
import static px.objects.filters.FilterManagementTestData.RANGE_COMMANDS;

/**
 * Created by kgr on 10/24/2017.
 */
public class DataFilterNodeElement extends FilterNodeElement {
    private DataFilterNode dataNode;

    public DataFilterNodeElement(FilterNode node) {
        super(node);
        this.dataNode = (DataFilterNode) node;
    }

    public DataFilterNodeElement(FilterNode node, WebElement element) {
        super(node, element);
        this.dataNode = (DataFilterNode) node;
    }

    @Override
    public void setName() {
        this.element = helper.getElement(element, NEW_DATA_NODE);
        FilteredDropDown type = new FilteredDropDown(element);
        type.setByText(dataNode.getNameLabel());
    }

    @Override
    public void setCommand() {
        // actually it's 1 child
        node.getChildren().forEach(command -> {
            FilteredDropDown commandNode = new FilteredDropDown(helper.getElement(element, COMMAND_NODE));
            commandNode.setByTitle(((CommandFilterNode) command).getNameLabel());
        });
    }

    @Override
    public void setValue() {
        // actually it's 1 child
        node.getChildren().forEach(command -> {
            CommandFilterNode valueNode = (CommandFilterNode) command;
            WebElement valueContainer = helper.getElement(element, COMMAND_VALUE);
            if (valueNode.isSelect()) {
                // select
                log.info("Set value for select element data node");
//                FilteredDropDown value = new FilteredDropDown(helper.getElement(valueContainer, ".//isteven-multi-select"));
                FilteredDropDown value = new FilteredDropDown(valueContainer);
                if (valueNode.isMultiSelect()) {
                    // multiple
                    log.info(String.format("Set multi select element data node\tValues: %s", valueNode.getValue()));
                    value.setByTitle(Arrays.asList(valueNode.getValue().split(",")));
                } else {
                    // single
                    log.info(String.format("Set single select element data node\tValue: %s", valueNode.getValue()));
                    value.setByTitle(valueNode.getValue(), true);
                }
            } else {
                // input
                log.info("Set value for input element data node");
                if (RANGE_COMMANDS.contains(valueNode.getName())) {
                    // range - 2 inputs
                    log.info(String.format("Set input range element data node\tValues: %s", valueNode.getValue()));
                    String[] values = valueNode.getValue().split(",");
                    WebElement leftInput = helper.getElement(valueContainer, LEFT_BOUNDARY);
                    helper.type(leftInput, values[0]);
                    WebElement rightInput = helper.getElement(valueContainer, RIGHT_BOUNDARY);
                    helper.type(rightInput, values[1]);
                } else {
                    // common - 1 input
                    log.info(String.format("Set input element data node\tValue: %s", valueNode.getValue()));
                    WebElement value = helper.getElement(valueContainer, ".//input");
                    helper.type(value, valueNode.getValue());
                }
            }
        });
    }

    @Override
    public String getName() {
//        this.element = helper.getElement(element, NEW_DATA_NODE);
        FilteredDropDown type = new FilteredDropDown(element);
        return type.getValue();
    }

    public String getCommand(){
        // actually it's 1 child
        List<String> values = new ArrayList<>();
        node.getChildren().forEach(command -> {
            FilteredDropDown commandNode = new FilteredDropDown(helper.getElement(element, COMMAND_NODE));
            values.add(commandNode.getValue());
        });
        return StringUtils.join(values, ",");
    }

    @Override
    public String getValue() {
        // actually it's 1 child
        List<String> values = new ArrayList<>();
        node.getChildren().forEach(command -> {
            CommandFilterNode valueNode = (CommandFilterNode) command;
            WebElement valueContainer = helper.getElement(element, COMMAND_VALUE);
            if (valueNode.isSelect()) {
                values.add(new FilteredDropDown(valueContainer).getValue());
            } else {
                helper.getElements(valueContainer, ".//input").forEach(input -> values.add(input.getAttribute("value")));
            }
        });
        return StringUtils.join(values, ",");
    }

    @Override
    public void deleteNode() {
        log.info("Delete data filter node " + node);
        helper.getElement(element, DELETE_DATA_NODE).click();
    }

}
