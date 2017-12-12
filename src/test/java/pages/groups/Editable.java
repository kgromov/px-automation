package pages.groups;

import dto.TestData;
import elements.ElementsHelper;
import elements.HelperSingleton;
import elements.SectionTitleElement;
import px.objects.InstancesTestData;

/**
 * Created by kgr on 3/9/2017.
 */
public interface Editable extends Objectable {
    default Editable editInstance(TestData testData) {
        throw new UnsupportedOperationException("Implementation is required in class " + this.getClass().getName());
    }

    Editable editInstance(InstancesTestData testData);

    Editable editInstance(InstancesTestData oldData, InstancesTestData newData);

    default void editInstance(String containerLocator) {
        ElementsHelper helper = HelperSingleton.getHelper();
        helper.waitUntilDisplayed(containerLocator);
        SectionTitleElement titleElement = new SectionTitleElement(helper.getElement(containerLocator));
        // expand if collapsed
        if (titleElement.isSectionCollapsed()) titleElement.expandSection();
        titleElement.clickEdit();
//        helper.waitForAjaxComplete();
    }
}