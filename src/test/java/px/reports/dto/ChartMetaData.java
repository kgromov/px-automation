package px.reports.dto;

import org.json.JSONArray;
import org.json.JSONObject;
import pages.groups.MetaData;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static config.Config.LOCALE;
import static configuration.helpers.DataHelper.getDateByFormatSimple;
import static configuration.helpers.DataHelper.getUpdatedPatternWithDayOrder;

/**
 * Created by kgr on 8/28/2017.
 */
public class ChartMetaData extends MetaData {
    private String locator;
    // for rounding
    private int round;
    // for total
    private String sortBy;
    private String totalBy;

    public ChartMetaData(JSONObject jsonObject) {
        this.name = jsonObject.getString("name");
        this.locator = jsonObject.has("locator") ? jsonObject.getString("locator") : null; // locator is not mandatory for tooltipData
        this.title = jsonObject.has("title") ? jsonObject.getString("title") : null;
        this.sortBy = jsonObject.has("sortBy") ? jsonObject.getString("sortBy") : null;
        this.totalBy = jsonObject.has("totalBy") ? jsonObject.getString("totalBy") : null;
        this.type = jsonObject.has("type") ? jsonObject.getString("type") : "String";
        if (type.equals(NUMBER_FORMAT)) {
            this.round = jsonObject.getInt("round");
        } else if (type.equals(DATE_TIME_FORMAT)) {
            this.inPattern = jsonObject.getString("inPattern");
            this.outPattern = jsonObject.getString("outPattern");
        }
    }

    public String getLocator() {
        return locator;
    }

    public String getSortBy() {
        return sortBy;
    }

    public String getTotalBy() {
        return totalBy;
    }

    public void setInPattern(String inPattern) {
        this.inPattern = inPattern;
    }

    public void setOutPattern(String outPattern) {
        this.outPattern = outPattern;
    }

    public ChartMetaData withIndex(int index) {
        this.index = index;
        return this;
    }

    public ChartMetaData updateLocatorWithIndex(int index) {
        // update locator
        if (locator.contains("%d")) locator = String.format(locator, index + 1);
        locator = locator.replaceAll("\\[\\d+\\]", String.format("[%d]", index + 1));
        return this;
    }

    // get formatted data
    public String getValue(String value) {
        try {
            switch (type) {
                case NUMBER_FORMAT:
                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(LOCALE);
                    DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
                    symbols.setGroupingSeparator(' ');  // make parameterized
                    symbols.setDecimalSeparator(',');   // make parameterized
                    formatter.setDecimalFormatSymbols(symbols);
                    formatter.setMinimumFractionDigits(round);
                    formatter.setMaximumFractionDigits(round);
                    formatter.setRoundingMode(RoundingMode.HALF_UP);
                    return formatter.format(Double.parseDouble(value));
//                    return String.valueOf(new BigDecimal(value).setScale(round, BigDecimal.ROUND_HALF_UP).doubleValue());
                case DATE_TIME_FORMAT:
                    Date temp = getDateByFormatSimple(inPattern, value.replace("T", " "));
                    // in case of day of Month
                    return getDateByFormatSimple(outPattern.contains("'th'")
                            ? getUpdatedPatternWithDayOrder(temp, outPattern) : outPattern, temp)
                            .replace("AM", "am").replace("PM", "pm");
                default:
                    return value;
            }
        } catch (RuntimeException e) {
            return value;
        }
    }

    public static List<ChartMetaData> getObjectsByJSONArray(JSONArray objects) {
        List<ChartMetaData> fields = new ArrayList<>(objects.length());
        for (int i = 0; i < objects.length(); i++) {
            fields.add(new ChartMetaData(objects.getJSONObject(i)));
        }
        return fields;
    }

    public static ChartMetaData getFieldObjectFromListByName(List<ChartMetaData> fields, String name) {
        return fields.stream().filter(field -> field.getName().equals(name))
                .findFirst().orElse(null);
    }

    public static List<ChartMetaData> getFieldsWithoutNames(List<ChartMetaData> fields, String... name) {
        List<String> names = Arrays.asList(name);
        return fields.stream().filter(field -> !names.contains(field.getName()))
                .collect(Collectors.toList());
    }

    public static List<String> getFieldNames(List<ChartMetaData> fields) {
        return fields.stream().map(ChartMetaData::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "ChartMetaData{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", locator='" + locator + '\'' +
                ", inPattern='" + inPattern + '\'' +
                ", outPattern='" + outPattern + '\'' +
                ", sortBy='" + sortBy + '\'' +
                ", totalBy='" + totalBy + '\'' +
                '}';
    }
}