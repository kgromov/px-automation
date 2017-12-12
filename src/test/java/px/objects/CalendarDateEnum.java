package px.objects;

/**
 * Created by kgr on 10/7/2016.
 */
public enum CalendarDateEnum {
    TODAY(1),
    YESTERDAY(1),
    LAST_7_DAYS(7),
    THIS_MONTH(30),
    LAST_MONTH(30),
    LAST_30_DAYS(30);

    private final int value;

    public int getValue() {
        return value;
    }

    CalendarDateEnum(int value) {
        this.value = value;
    }
}
