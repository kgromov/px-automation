package pages.groups;

import dto.TestData;
import px.objects.InstancesTestData;

/**
 * Created by kgr on 3/9/2017.
 */
public interface Creatable extends Objectable {
    default Creatable createInstance(TestData testData) {
        throw new UnsupportedOperationException("Implementation is required in class " + this.getClass().getName());
    }

    Creatable createInstance(InstancesTestData testData);
}