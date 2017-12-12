package px.objects.brokers;

/**
 * Created by kgr on 10/18/2016.
 */
public class BrokersPageLocators {
    // ================================== Buyers page ==================================
    public final static String CREATE_BROKER_BUTTON = ".//a[@href='/admin/brokers/create']";
    // Action items
    public final static String DETAILS_ITEM = "Details";
    public final static String BROKER_INSTANCES_ITEM = "Broker instances";
    public final static String DELETE_ITEM = "Delete";
    // =============================== Create Broker page ===============================
    public final static String GENERAL_CONTAINER = ".//div[@id='general']";
    // inputs
    public final static String BROKER_NAME_INPUT = ".//*[@data-field-name='brokerName']";
    public final static String DESCRIPTION_INPUT = ".//*[@data-field-name='description']";
    // for crated one - while editing
    public final static String DAILY_CAP_INPUT = ".//*[@data-field-name='dailyCap']";
    // selects
    public final static String BROKER_TYPE_SELECT = ".//*[@data-field-name='brokerType']";
}