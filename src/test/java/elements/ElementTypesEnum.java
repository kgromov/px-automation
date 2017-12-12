package elements;

/**
 * Created by konstantin on 19.07.2017.
 */
public enum ElementTypesEnum {
    INPUT("px-field-input"),
    TEXT_AREA("px-field-textarea"),
    TEXT("px-field-preview"),
    SELECT("px-field-select"),
    CHECKBOX("px-field-checkbox"),
    RADIO_BUTTON("px-field-radio");

    private final String value;

    public String getValue() {
        return value;
    }

    ElementTypesEnum(String value) {
        this.value = value;
    }
}
