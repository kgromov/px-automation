package px.objects.credSets.pages;

import configuration.browser.PXDriver;
import elements.input.InputElement;
import elements.input.TextAreaElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.InstancesTestData;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static px.objects.credSets.CredSetLocators.*;

/**
 * Created by kgr on 10/30/2017.
 */
public class CreateCredSetPage extends ObjectPage implements Creatable {
    @FindBy(xpath = CRED_SET_NAME)
    private InputElement name;
    @FindBy(xpath = CRED_SET_DESCRIPTION)
    private TextAreaElement description;

    public CreateCredSetPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreateCredSetPage checkDefaultValues(InstancesTestData testData) {
        log.info("Check cloned description from credset " + testData);
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        assertThat("Check that description is cloned", description.getValue(), equalToIgnoringCase(testData.getDescription()));
        return this;
    }

    @Override
    public CreateCredSetPage createInstance(InstancesTestData testData) {
        log.info("Create new cred set " + testData);
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        name.setByText(testData.getName());
        description.setByText(testData.getDescription());
        return this;
    }

    @Override
    public CreateCredSetPage saveInstance(InstancesTestData testData) {
        saveInstance(GENERAL_CONTAINER);
        waitPageIsLoaded();
        if (testData.isPositive()) {
            checkErrorMessage();
            if (testData.isCreateMode()) helper.waitUntilToBeInvisible(GENERAL_CONTAINER);
        } else {
            checkErrorMessage(Arrays.asList(name, description));
        }
        return this;
    }
}
