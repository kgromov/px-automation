package px.objects.customRights.pages;

import configuration.browser.PXDriver;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.ObjectPage;
import pages.groups.Creatable;
import px.objects.InstancesTestData;

import java.util.Arrays;

import static px.objects.customRights.CustomRightsLocators.*;

/**
 * Created by kgr on 11/8/2017.
 */
public class CreateCustomRightPage extends ObjectPage implements Creatable {
    @FindBy(xpath = CUSTOM_RIGHT_NAME)
    private InputElement name;
    @FindBy(xpath = CUSTOM_RIGHT_DESCRIPTION)
    private InputElement description;

    public CreateCustomRightPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    @Override
    public CreateCustomRightPage createInstance(InstancesTestData testData) {
        log.info("Create new custom right " + testData);
        helper.waitUntilDisplayed(GENERAL_CONTAINER);
        name.setByText(testData.getName());
        description.setByText(testData.getDescription());
        return this;
    }

    @Override
    public CreateCustomRightPage saveInstance(InstancesTestData testData) {
        saveInstance(GENERAL_CONTAINER);
        waitPageIsLoaded();
        if (testData.isPositive()) {
            checkErrorMessage();
            helper.waitUntilToBeInvisible(GENERAL_CONTAINER);
        } else {
            checkErrorMessage(Arrays.asList(name, description));
        }
        return this;
    }
}
