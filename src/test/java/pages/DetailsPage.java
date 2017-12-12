package pages;

import configuration.browser.PXDriver;
import elements.input.InputElement;
import elements.input.TextElement;
import org.openqa.selenium.WebElement;
import px.reports.dto.FieldFormatObject;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static configuration.helpers.PXDataHelper.getMatherByMetricType;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static pages.locators.ElementLocators.DATA_FIELD_ELEMENT;
import static pages.locators.ElementLocators.TEXT_VALUE;


/**
 * Created by kgr on 5/24/2017.
 */
public abstract class DetailsPage extends ObjectPage {
    // map to get missed data elements
    protected Map<String, String> missedDataMap;
    protected Map<String, Boolean> editableFieldsMap;

    // to save extra fields from UI
    protected List<String> extraFieldsList = new ArrayList<>();

    public DetailsPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    protected String checkPreviewElements(WebElement container, Map<String, String> detailsMap, List<FieldFormatObject> fields) {
        log.info("Check data-field elements");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // fields with data
        List<WebElement> dataFieldsList = helper.getElements(container, DATA_FIELD_ELEMENT);
        for (WebElement element : dataFieldsList) {
            InputElement inputElement = new InputElement(element);
            String key = element.getAttribute("data-field-name");
            // skip element if there is no such field
            if (!missedDataMap.containsKey(key)) {
                extraFieldsList.add(key);
                hamcrest.assertThat(String.format("Details field '%s' with label '%s' value is empty",
                        key, inputElement.getLabel()), inputElement.getText(), isEmptyOrNullString());
                continue;
            }
            FieldFormatObject field = FieldFormatObject.getFieldObjectFromListByName(fields, key);
            String expectedValue = field.hasMappedValues() ? field.getMappedValue(detailsMap.get(key)) : detailsMap.get(key);
            String actualValue = inputElement.getText();//.replaceAll("\\s+", " ");
            hamcrest.assertThat(String.format("Details field '%s' with label '%s' value equals to json",
                    key, inputElement.getLabel()), actualValue, getMatherByMetricType(field, expectedValue));
            missedDataMap.remove(key);
        }
        return hamcrest.toString();
    }

    protected String checkPreviewElements(WebElement container, Map<String, String> detailsMap) {
        log.info("Check data-field elements");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // fields with data
        List<WebElement> dataFieldsList = helper.getElements(container, DATA_FIELD_ELEMENT);
        for (WebElement element : dataFieldsList) {
            TextElement previewElement = new TextElement(element);
            String key = element.getAttribute("data-field-name");
            // skip element if there is no such field
            if (!missedDataMap.containsKey(key)) {
                extraFieldsList.add(key);
                hamcrest.assertThat(String.format("Details field '%s' with label '%s' value is empty",
                        key, previewElement.getLabel()), previewElement.getValue(), isEmptyOrNullString());
                continue;
            }
            String expectedValue = detailsMap.get(key);
            String actualValue = previewElement.getValue();//.replaceAll("\\s+", " ");
            hamcrest.assertThat(String.format("Details field '%s' with label '%s' value equals to json",
                    key, previewElement.getLabel()), actualValue, equalToIgnoringCase(expectedValue));
            missedDataMap.remove(key);
        }
        return hamcrest.toString();
    }

    protected String checkPreviewElementsEditable(WebElement container, Map<String, Boolean> detailsMap) {
        log.info("Check data-field elements");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // fields with data
        List<WebElement> dataFieldsList = helper.getElements(container, DATA_FIELD_ELEMENT);
        for (WebElement element : dataFieldsList) {
            InputElement inputElement = new InputElement(element);
            String key = element.getAttribute("data-field-name");
            // skip element if there is no such field
            if (!editableFieldsMap.containsKey(key)) {
                extraFieldsList.add(key);
                hamcrest.assertThat(String.format("Details field '%s' with label '%s' value is empty",
                        key, inputElement.getLabel()), inputElement.getText(), isEmptyOrNullString());
                continue;
            }
            hamcrest.assertThat(String.format("Field '%s' with label '%s' is editable", key, inputElement.getLabel()),
                    !helper.isElementPresent(element, TEXT_VALUE), equalTo(detailsMap.get(key)));
            editableFieldsMap.remove(key);
        }
        return hamcrest.toString();
    }
}