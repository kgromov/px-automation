package px.reports.dashboard.charts;

import org.openqa.selenium.Dimension;

import java.util.Comparator;

/**
 * Created by kgr on 9/1/2017.
 */
public class ChartItem {
    private String value;
    private int width;
    private int height;
    // comparators
    public static final Comparator<ChartItem> VALUE_COMPARATOR = (ChartItem item1, ChartItem item2) -> {
        try {
            double value1 = Double.parseDouble(item1.value);
            double value2 = Double.parseDouble(item2.value);
            return value1 > value2 ? 1 : value1 == value2 ? 0 : -1;
        } catch (NumberFormatException e) {
            return item1.value.compareTo(item2.value);
        }
    };
    public static final Comparator<ChartItem> WIDTH_COMPARATOR = (ChartItem item1, ChartItem item2) -> item1.width - item2.width;
    public static final Comparator<ChartItem> HEIGHT_COMPARATOR = (ChartItem item1, ChartItem item2) -> item1.height - item2.height;


    public ChartItem(String value, Dimension dimension) {
        this.value = value;
        this.width = dimension.getWidth();
        this.height = dimension.getHeight();
    }

    public ChartItem(String value, int width, int height) {
        this.value = value;
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChartItem)) return false;
        ChartItem that = (ChartItem) obj;
        return this.width == that.width &&
                this.height == that.height &&
                this.value.equals(that.value);
    }

    @Override
    public String toString() {
        return "ChartItem{" +
                "value='" + value + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
