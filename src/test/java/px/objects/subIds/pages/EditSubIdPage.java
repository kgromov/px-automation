package px.objects.subIds.pages;

import configuration.browser.PXDriver;
import elements.RadioButtonElement;
import elements.SuperTypifiedElement;
import elements.dropdown.DropDownElement;
import elements.input.InputElement;
import org.json.JSONException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import px.objects.publishers.pages.EditPublishersPage;
import px.objects.subIds.SubIdTestData;
import px.reports.dto.FieldFormatObject;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static configuration.helpers.PXDataHelper.getMatherByMetricType;
import static pages.locators.ElementLocators.DATA_FIELD_PARAMETERIZED_ELEMENT;
import static px.objects.publishers.PublishersPageLocators.*;

/**
 * Created by kgr on 7/14/2017.
 */
public class EditSubIdPage extends EditPublishersPage {
    @FindBy(xpath = SUB_ID_NAME_INPUT)
    private InputElement subIdName;

    public EditSubIdPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public EditSubIdPage editGeneralInfo(SubIdTestData testData) {
        log.info("Edit 'General' data");
        editInstance(GENERAL_INFO_CONTAINER);
        // to get all field names
//        helper.getElements(GENERAL_INFO_CONTAINER + "//*[@data-field-name]").forEach(el -> log.info(el.getAttribute("data-field-name")));
        // filling in
        super.filInGeneralInfo(testData);
        WebElement parentContainer = helper.getElement(GENERAL_INFO_CONTAINER);
        subIdName.setByText(testData.getName());
        spending.setByText(testData.getSpending());
        qualityScore.setByText(testData.getQualityScore());
//        leadPercentage.setByText(testData.getLeadPercentage());
        // should be like in publisher:
        new DropDownElement(helper.getElement(parentContainer, LEAD_PERCENTAGE_INPUT)).setByTitle(testData.getLeadPercentageType());
        helper.type(helper.getElement(LEAD_PERCENTAGE_INPUT, ".//input"), testData.getLeadPercentage());
        fraudPercentage.setByText(testData.getFraudPercentage());
        status.setByText(testData.getStatus());
        new RadioButtonElement(helper.getElement(parentContainer, PARENT_INHERITANCE_RADIO)).setByText(testData.getNoParentInherit());
        // save filled data
        saveInstance(GENERAL_INFO_CONTAINER);
        listMenuElement.setByIndex(1);
        helper.pause(2000);
        // set error containers list
        if (!testData.isPositive()) {
            List<SuperTypifiedElement> elementsWithError = new ArrayList<>(Arrays.asList(marginInput, minPriceInput,
                    checkPercentageInput, startBalanceInput, freeTestsInput,
                    subIdName, spending, qualityScore, leadPercentage, fraudPercentage,
                    bidFloor, outbidPercentage, outbidPercentagePerc
            ));
            checkErrorMessage(elementsWithError);
        } else checkErrorMessage();
        return this;
    }

    public EditSubIdPage checkUpdatedSubIdGeneralInfo(SubIdTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<FieldFormatObject> fields = testData.getFields();
        listMenuElement.setByIndex(1);
        // payout details data
        testData.generalInfoAsMap().forEach((fieldName, expectedValue) -> {
            InputElement inputElement = new InputElement(helper.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, fieldName)));
            try {
                FieldFormatObject header = FieldFormatObject.getFieldObjectFromListByName(fields, fieldName);
                hamcrest.assertThat(String.format("%s value is updated", inputElement.getLabel()),
                        inputElement.getText(), getMatherByMetricType(header, expectedValue));
            } catch (JSONException e) {
                log.info(String.format("ERROR\tKey '%s' is missed in subId details\t%s", fieldName, testData.asMap().keySet()));
            } catch (NullPointerException e2) {
                log.info(String.format("ERROR\tKey '%s' is missed in subId fields\t%s", fieldName, FieldFormatObject.getFieldNames(fields)));
            }
        });
        hamcrest.assertAll();
        return this;
    }

    public EditSubIdPage checkUpdatedSubIdContactInfo(SubIdTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<FieldFormatObject> fields = testData.getFields();
        listMenuElement.setByIndex(2);
        // payout details data
        testData.contactInfoAsMap().forEach((fieldName, expectedValue) -> {
            InputElement inputElement = new InputElement(helper.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, fieldName)));
            try {
                FieldFormatObject header = FieldFormatObject.getFieldObjectFromListByName(fields, fieldName);
                hamcrest.assertThat(String.format("%s value is updated", inputElement.getLabel()),
                        inputElement.getText(), getMatherByMetricType(header, expectedValue));
            } catch (JSONException e) {
                log.info(String.format("ERROR\tKey '%s' is missed in subId details\t%s", fieldName, testData.asMap().keySet()));
            } catch (NullPointerException e2) {
                log.info(String.format("ERROR\tKey '%s' is missed in subId fields\t%s", fieldName, FieldFormatObject.getFieldNames(fields)));
            }
        });
        hamcrest.assertAll();
        return this;
    }

    public EditSubIdPage checkUpdatedSubIdAddressInfo(SubIdTestData testData) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        List<FieldFormatObject> fields = testData.getFields();
        listMenuElement.setByIndex(3);
        // payout details data
        testData.addressInfoAsMap().forEach((fieldName, expectedValue) -> {
            InputElement inputElement = new InputElement(helper.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, fieldName)));
            try {
                FieldFormatObject header = FieldFormatObject.getFieldObjectFromListByName(fields, fieldName);
                hamcrest.assertThat(String.format("%s value is updated", inputElement.getLabel()),
                        inputElement.getText(), getMatherByMetricType(header, expectedValue));
            } catch (JSONException e) {
                log.info(String.format("ERROR\tKey '%s' is missed in subId details\t%s", fieldName, testData.asMap().keySet()));
            } catch (NullPointerException e2) {
                log.info(String.format("ERROR\tKey '%s' is missed in subId fields\t%s", fieldName, FieldFormatObject.getFieldNames(fields)));
            }
        });
        hamcrest.assertAll();
        return this;
    }
}
