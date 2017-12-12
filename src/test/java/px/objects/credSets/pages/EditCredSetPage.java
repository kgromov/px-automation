package px.objects.credSets.pages;

import configuration.browser.PXDriver;
import elements.input.InputElement;
import org.openqa.selenium.support.FindBy;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.objects.credSets.CredSetTestData;

import static px.objects.credSets.CredSetLocators.CRED_SET_GUID;
import static px.objects.credSets.CredSetLocators.GENERAL_CONTAINER;

/**
 * Created by kgr on 11/7/2017.
 */
public class EditCredSetPage extends CreateCredSetPage implements Editable {
    @FindBy(xpath = CRED_SET_GUID)
    private InputElement credSetGUID;

    public EditCredSetPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public EditCredSetPage setGUID(InstancesTestData testData) {
        log.info("Set guid");
        credSetGUID.setByText(testData.getDescription());
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof CredSetTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        CredSetTestData testData = (CredSetTestData) pTestData;
        log.info(String.format("Edit credSet '%s'", testData));
        editInstance(GENERAL_CONTAINER);
        createInstance(pTestData);
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData oldData, InstancesTestData newData) {
        throw new UnsupportedOperationException("Irrelevant method");
    }
}
