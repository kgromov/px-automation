package pages.groups;

import configuration.helpers.DataHelper;
import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.dropdown.SelectElement;
import org.openqa.selenium.NoSuchElementException;

import java.util.List;

import static pages.locators.ElementLocators.CREATE_TOGGLE;
import static pages.locators.ElementLocators.POPUP_CONFIRM;
import static px.reports.ExportLocators.*;


/**
 * Created by kgr on 4/19/2017.
 */
public interface Exportable {

    default void checkExport() {
        System.out.println("Start 'Export report' feature verification");
        ElementsHelper helper = HelperSingleton.getHelper();
        // if 'Export' button is absent - verification fails
        if (!helper.isElementPresent(EXPORT_BUTTON, 3))
            throw new NoSuchElementException("'Export' button is not present");
        // click on 'Export' button
        helper.waitUntilDisplayed(EXPORT_BUTTON);
//        helper.click(POPUP_CONFIRM);
        helper.getElement(EXPORT_BUTTON).click();
        // wait till popup
        helper.waitUntilDisplayed(EXPORT_DIALOGUE);
        // expand if required
        if (!helper.isElementAccessible(SEPARATOR_SELECT)) {
            helper.getElement(CREATE_TOGGLE).click();
            helper.waitUntilDisplayed(SEPARATOR_SELECT);
        }
        // select random item separator
        SelectElement separatorSelect = new SelectElement(helper.getElement(SEPARATOR_SELECT));
        separatorSelect.expand();
        List<String> separators = separatorSelect.getItems();
        separatorSelect.expand();
        String separator = DataHelper.getRandomValueFromList(separators);
        System.out.println(String.format("Select '%s' csv separator", separator));
        // proceed
        separatorSelect.setByText(separator);
        helper.click(POPUP_CONFIRM);
        helper.waitUntilToBeInvisible(EXPORT_DIALOGUE);
        // wait till confirmation tooltip
        helper.waitUntilDisplayed(QUEUED_TOOLTIP);
        System.out.println(String.format("Confirmation tooltip message\n%s",
                helper.getElement(QUEUED_TOOLTIP_MESSAGE).getText()));
        System.out.println("Finished 'Export report' feature verification");
    }
}
