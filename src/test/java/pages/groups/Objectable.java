package pages.groups;

import elements.SuperTypifiedElement;
import px.objects.InstancesTestData;

import java.util.List;

/**
 * Created by konstantin on 30.07.2017.
 */
public interface Objectable {
    Objectable checkErrorMessage();

    Objectable checkErrorMessage(List<SuperTypifiedElement> elementsWithError);

    default Objectable checkDefaultValues() {
        throw new UnsupportedOperationException();
    }

    default Objectable checkDefaultValues(InstancesTestData testData) {
        throw new UnsupportedOperationException();
    }

    Objectable saveInstance(InstancesTestData testData);

//    ObjectErrors checkErrorMessageByLocators(List<String> expectedFieldsWithErrors);
}