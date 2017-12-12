package px.reports;

/**
 * Created by kgr on 11/25/2016.
 */
public enum GraphicsEnum implements Valued {
    NONE("None"),
    BUBBLE_CHART("Bubble Chart"),
    COLUMN_CHART("Column Chart"),
    STACKED_AREA_CHART("Stacked Area Chart");

    private final String value;

    public String getValue() {
        return value;
    }

    GraphicsEnum(String value) {
        this.value = value;
    }
}
