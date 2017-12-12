package pages.groups;

/**
 * Created by kgr on 8/15/2017.
 */
public abstract class MetaData {
    protected String name;
    protected String title;
    protected String type;
    protected int index;
    protected int round = -1;
    // currently for date only
    protected String inPattern;
    protected String outPattern;
    // format constants
    public static final String NUMBER_FORMAT = "number";
    public static final String DOUBLE_FORMAT = "double";
    public static final String CURRENCY_FORMAT = "currency";
    public static final String PERCENTAGE_FORMAT = "percentage";
    public static final String TIMEOUT_FORMAT = "timeout";
    public static final String NUMBER_PERCENTAGE_FORMAT = "number_percentage";
    public static final String DATE_FORMAT = "date";
    public static final String DATE_TIME_FORMAT = "datetime";

    protected MetaData() {
    }

    public MetaData(String name, String type, int round) {
        this.name = name;
        this.type = type;
        this.round = round;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public int getIndex() {
        return index;
    }

    public int getRound() {
        return round;
    }

    public String getInPattern() {
        return inPattern;
    }

    public String getOutPattern() {
        return outPattern;
    }
}