package px.objects.filters;

import configuration.browser.PXDriver;
import org.openqa.selenium.WebElement;
import pages.ObjectPage;
import pages.groups.Actions;
import px.objects.filters.elements.DataFilterNodeElement;
import px.objects.filters.elements.FilterNodeElement;
import px.objects.filters.elements.OperandFilterNodeElement;
import px.objects.filters.nodes.CommandFilterNode;
import px.objects.filters.nodes.DataFilterNode;
import px.objects.filters.nodes.FilterNode;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static px.objects.filters.FilterManagementPageLocators.*;

/**
 * Created by kgr on 10/24/2017.
 */
public class FilterManagementPage extends ObjectPage implements Actions {

    public FilterManagementPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public FilterManagementPage createContactDataFilter(FilterNode root) {
        log.info("Create filter with ContactData fields with height 2");
        FilterNodeElement rootNode = new OperandFilterNodeElement(root);
        rootNode.addNode();
        rootNode.setName();
        // all contact data nodes
        root.getChildren().forEach(dataNode -> {
            FilterNodeElement node = new DataFilterNodeElement(dataNode);
            // data node itself
            node.addNode();
            node.setName();
            // command node
            node.setCommand();
            node.setValue();
        });
        return this;
    }

    public FilterManagementPage checkPreview() {
        return this;
    }

    public FilterManagementPage checkByResponse() {
        return this;
    }

    public FilterManagementPage checkContactDataFilter(FilterNode root) {
        log.info("Check values of filter with ContactData fields with height 2");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        FilterNodeElement rootNode = new OperandFilterNodeElement(root, helper.getElement(OPERAND_NODE));
        hamcrest.assertThat("Root node name verification", rootNode.getName(), equalTo(root.getName()));
        // all contact data nodes
        List<FilterNode> contactData = root.getChildren();
        List<WebElement> dataNodeElements = helper.getElements(DATA_NODE);
        // calculate once to use later
        int expectedNodes = contactData.size();
        int actualNodes = dataNodeElements.size();
        for (int i = 0; i < expectedNodes; i++) {
            DataFilterNode dataNode = (DataFilterNode) contactData.get(i);
            try {
                DataFilterNodeElement nodeElement = new DataFilterNodeElement(dataNode, dataNodeElements.get(i));
                // data node name
                hamcrest.assertThat("Data node name verification", nodeElement.getName(), equalToIgnoringCase(dataNode.getNameLabel()));
                // command node
                dataNode.getChildren().forEach(command -> {
                    CommandFilterNode valueNode = (CommandFilterNode) command;
                    // command and value
                    // TODO: multi select when there are more than 3 values?
                    hamcrest.assertThat("Data node command verification", nodeElement.getCommand(), equalTo(valueNode.getNameLabel()));
                    hamcrest.assertThat("Data node value verification", nodeElement.getValue(), equalTo(valueNode.getValue()));
                });
            } catch (IndexOutOfBoundsException e) {
                hamcrest.assertThat(String.format("Missed data node by index %d\tExpected name = %s", i + 1, dataNode.getName()), false);
            }
        }
        // extra
        List<WebElement> extraNodes = actualNodes > expectedNodes ? dataNodeElements.subList(expectedNodes, actualNodes) : new ArrayList<>();
        extraNodes.forEach(extraNode -> {
            DataFilterNodeElement node = new DataFilterNodeElement(null, extraNode);
            hamcrest.assertThat(String.format("There is extra node %s in UI", node.getName()), false);
        });
        hamcrest.assertAll();
        return this;
    }

    public FilterManagementPage saveFilter() {
        log.info("Save campaign's filter changes");
        helper.click(SAVE_BUTTON);
        pxDriver.waitForAjaxComplete();
        confirm(true);
        checkErrorMessage();
        return this;
    }
}
