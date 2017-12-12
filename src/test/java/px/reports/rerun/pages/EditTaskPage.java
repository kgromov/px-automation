package px.reports.rerun.pages;

import configuration.browser.PXDriver;
import dto.TestDataException;
import elements.ElementManager;
import elements.SuperTypifiedElement;
import elements.input.TextElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import pages.DetailsPage;
import pages.groups.Editable;
import px.objects.InstancesTestData;
import px.reports.dto.FieldFormatObject;
import px.reports.rerun.FilterDetailsTestData;
import px.reports.rerun.RerunTaskTestData;
import utils.SoftAssertionHamcrest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static configuration.helpers.PXDataHelper.getMatherByMetricType;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static pages.locators.ElementLocators.*;
import static px.reports.rerun.FilterDetailsTestData.CAMPAIGNS_KEY;
import static px.reports.rerun.FilterDetailsTestData.VERTICALS_KEY;
import static px.reports.rerun.RerunPageLocators.LEAD_RERUN_TASK_CONTAINER;
import static px.reports.rerun.RerunPageLocators.SCHEDULE_BUTTON;

/**
 * Created by kgr on 10/9/2017.
 */
public class EditTaskPage extends CreateTaskPage implements Editable {

    public EditTaskPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public EditTaskPage checkFilterPreview(FilterDetailsTestData testData) {
        new FilterPreviewPage(pxDriver).checkFilterPreview(testData);
        return this;
    }

    public EditTaskPage checkFilterBeforeEdit(FilterDetailsTestData testData) {
        new FilterPreviewPage(pxDriver).checkAnyTypePreviewElements(testData);
        return this;
    }

    @Override
    public CreateTaskPage setFilterData(RerunTaskTestData testData) {
        // cause there are no 'clearAll' in multi dropdown
        FilterDetailsTestData filterData = new FilterDetailsTestData(testData.getFilter());
        // clear campaigns and verticals cause they are dependent on verticals
        Map<String, String> filterDetails = filterData.getFilterDetails();
        log.info(String.format("Filter '%s' details map\n%s", testData.getFilter(), filterDetails));
        // campaigns could be empty
        if (filterDetails.containsKey(CAMPAIGNS_KEY) && !filterDetails.get(CAMPAIGNS_KEY).isEmpty()) {
            List<String> campaignsList = Arrays.asList(filterData.getFilterDetails().get(CAMPAIGNS_KEY).split(", "));
            campaigns.setByText(campaignsList);
        }
        // verticals are mandatory
        if (filterDetails.containsKey(VERTICALS_KEY) && !filterDetails.get(VERTICALS_KEY).isEmpty()) {
            try {
                List<String> verticalsList = Arrays.asList(filterDetails.get(VERTICALS_KEY).split(", "));
                verticals.setByText(verticalsList);
            } catch (NoSuchElementException e) {
                throw new TestDataException(String.format("Verticals after edit filter '%s' do not match to existed '%s' by filter details",
                        testData.getFilter(), filterDetails.get(VERTICALS_KEY)));
            }
        }
        return super.setFilterData(testData);
    }

    @Override
    public Editable editInstance(InstancesTestData pTestData) {
        if (!(pTestData instanceof RerunTaskTestData))
            throw new IllegalArgumentException("Illegal test data class - " + pTestData.getClass().getName());
        RerunTaskTestData testData = (RerunTaskTestData) pTestData;
        log.info(String.format("Create new rerun task by name '%s'", testData.getName()));
        // task fields
        setTaskData(testData);
        // filter  fields
        setFilterData(testData);
        return this;
    }

    @Override
    public Editable editInstance(InstancesTestData oldData, InstancesTestData newData) {
        throw new UnsupportedOperationException("Irrelevanet method");
    }

    private final class FilterPreviewPage extends DetailsPage {

        FilterPreviewPage(PXDriver pxDriver) {
            super(pxDriver);
        }

        FilterPreviewPage checkFilterPreview(FilterDetailsTestData testData) {
            log.info(String.format("Check filter '%s' details", testData.getFilter()));
            helper.waitUntilDisplayed(LEAD_RERUN_TASK_CONTAINER);
            helper.waitUntilDisplayed(SCHEDULE_BUTTON);
            helper.scrollToElement(SCHEDULE_BUTTON);
            SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
            Map<String, String> filterDetailsMap = testData.getFilterDetails();
            this.missedDataMap = new HashMap<>(filterDetailsMap);
            hamcrest.append(checkPreviewElements(helper.getElement(LEAD_RERUN_TASK_CONTAINER), filterDetailsMap, testData.getFields()));
            hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
            hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
            hamcrest.assertAll();
            return this;
        }

        FilterPreviewPage checkAnyTypePreviewElements(FilterDetailsTestData testData) {
            log.info(String.format("Check filter '%s' details", testData.getFilter()));
            helper.waitUntilDisplayed(LEAD_RERUN_TASK_CONTAINER);
            SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
            Map<String, String> filterDetailsMap = testData.getFilterDetails();
            this.missedDataMap = new HashMap<>(filterDetailsMap);
            hamcrest.append(checkAnyPreviewElements(helper.getElement(LEAD_RERUN_TASK_CONTAINER), filterDetailsMap, testData.getFields()));
            hamcrest.assertThat("There are missed data fields\t" + missedDataMap, missedDataMap.isEmpty());
            hamcrest.assertThat("There are extra data fields in UI\t" + extraFieldsList, extraFieldsList.isEmpty());
            hamcrest.assertAll();
            return this;
        }

        @Override
        protected String checkPreviewElements(WebElement container, Map<String, String> detailsMap, List<FieldFormatObject> fields) {
            log.info("Check data-field elements");
            SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
            // fields with data
            List<WebElement> dataFieldsList = helper.getElements(container, DATA_FIELD_PREVIEW_ELEMENT);
            for (WebElement element : dataFieldsList) {
                TextElement textElement = new TextElement(element);
                String key = element.getAttribute("data-field-name");
                // skip element if there is no such field
                if (!missedDataMap.containsKey(key)) {
                    extraFieldsList.add(key);
                    hamcrest.assertThat(String.format("Details field '%s' with label '%s' value is empty",
                            key, textElement.getLabel()), textElement.getText(), isEmptyOrNullString());
                    continue;
                }
                FieldFormatObject field = FieldFormatObject.getFieldObjectFromListByName(fields, key);
                String expectedValue = field.hasMappedValues() ? field.getMappedValue(detailsMap.get(key)) : detailsMap.get(key);
                String actualValue = textElement.getValue();//.replaceAll("\\s+", " ");
                hamcrest.assertThat(String.format("Details field '%s' with label '%s' value equals to json",
                        key, textElement.getLabel()), actualValue, getMatherByMetricType(field, expectedValue));
                missedDataMap.remove(key);
            }
            return hamcrest.toString();
        }

        String checkAnyPreviewElements(WebElement container, Map<String, String> detailsMap, List<FieldFormatObject> fields) {
            log.info("Check data-field elements");
            SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
            // fields with data
            List<WebElement> dataFieldsList = helper.getElements(container, DATA_FIELD_ELEMENT);
            for (WebElement element : dataFieldsList) {
                String key = element.getAttribute("data-field-name");
                SuperTypifiedElement typifiedElement = ElementManager.getElement(String.format(DATA_FIELD_PARAMETERIZED_ELEMENT, key));
                // skip element if there is no such field
                if (!missedDataMap.containsKey(key)) {
                    extraFieldsList.add(key);
                    hamcrest.assertThat(String.format("Details field '%s' with label '%s' value is empty",
                            key, typifiedElement.getLabel()), typifiedElement.getValue(), isEmptyOrNullString());
                    continue;
                }
                FieldFormatObject field = FieldFormatObject.getFieldObjectFromListByName(fields, key);
                String expectedValue = field.hasMappedValues() ? field.getMappedValue(detailsMap.get(key)) : detailsMap.get(key);
                String actualValue = typifiedElement.getValue();//.replaceAll("\\s+", " ");
                hamcrest.assertThat(String.format("Details field '%s' with label '%s' value equals to json",
                        key, typifiedElement.getLabel()), actualValue, getMatherByMetricType(field, expectedValue));
                missedDataMap.remove(key);
            }
            return hamcrest.toString();
        }

    }
}
