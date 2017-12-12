package configuration.helpers;

import org.hamcrest.Matcher;
import pages.groups.MetaData;
import utils.CustomMatcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;

import static config.Config.LOCALE;
import static configuration.helpers.DataHelper.getDateByFormatSimple;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static px.reports.dto.FieldFormatObject.*;

/**
 * Created by kgr on 9/22/2017.
 */
public class PXDataHelper {

    public static String getValueByType(String value, String type) {
        try {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(LOCALE);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');  // make parameterized
            symbols.setDecimalSeparator(',');   // make parameterized
            symbols.setCurrency(formatter.getCurrency()); // make parameterized
            formatter.setDecimalFormatSymbols(symbols);
            formatter.setRoundingMode(RoundingMode.HALF_UP);
            switch (type) {
                case NUMBER_FORMAT:
                    formatter.applyPattern("###,###");
                    return formatter.format(new BigDecimal(value).longValue());
                case DOUBLE_FORMAT:
                    formatter.applyPattern("###,###");
                    return formatter.format(new BigDecimal(value).doubleValue());
                case PERCENTAGE_FORMAT:
                    formatter.applyPattern("###,##0.00'%'");
                    return formatter.format(Double.parseDouble(value));
                case CURRENCY_FORMAT:
                case TIMEOUT_FORMAT:
                    char ending = type.equals(TIMEOUT_FORMAT) ? 's' : '$';
                    formatter.applyPattern("###,##0.00 " + String.valueOf(ending));
                    return formatter.format(Double.parseDouble(value));
                default:
                    return value;
            }
        } catch (RuntimeException e) {
            return value;
        }
    }

    public static String getValueByType(MetaData metaData, String value) {
        try {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(LOCALE);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');  // make parameterized
            symbols.setDecimalSeparator(',');   // make parameterized
            symbols.setCurrency(formatter.getCurrency()); // make parameterized
            formatter.setDecimalFormatSymbols(symbols);
            formatter.setRoundingMode(RoundingMode.HALF_UP);
          /*  formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);*/
            BigDecimal decimal = new BigDecimal(0);
            if (!metaData.getType().toLowerCase().equals(DATE_FORMAT) && !metaData.getType().toLowerCase().equals(DATE_TIME_FORMAT)) {
                decimal = new BigDecimal(value).setScale(metaData.getRound() != -1 ? metaData.getRound() : 2, RoundingMode.HALF_UP);
                if (metaData.getRound() != -1) {
                    formatter.setMinimumFractionDigits(metaData.getRound());
                    formatter.setMaximumFractionDigits(metaData.getRound());
                }
            }
            switch (metaData.getType().toLowerCase()) {
                case NUMBER_FORMAT:
                    formatter.applyPattern(metaData.getRound() > 0 ? "###,##0.00" : "###,###");
                    return formatter.format(metaData.getRound() != -1 ? decimal.doubleValue() : decimal.longValue());
                case DOUBLE_FORMAT:
                    formatter.applyPattern("###,##0.00");
                    return formatter.format(decimal.doubleValue());
                case PERCENTAGE_FORMAT:
                    formatter.applyPattern("###,##0.00'%'");
                    return formatter.format(decimal.doubleValue());
                case CURRENCY_FORMAT:
                case TIMEOUT_FORMAT:
                    char ending = metaData.getType().equals(TIMEOUT_FORMAT) ? 's' : '$';
//                    formatter = new DecimalFormat("###,###.## $", symbols); // formatter.getCurrency().getSymbol()
                    formatter.applyPattern("###,##0.00 " + String.valueOf(ending));
                    return formatter.format(decimal.doubleValue());
                case DATE_FORMAT:
                case DATE_TIME_FORMAT:
                    if (metaData.getInPattern() != null && metaData.getOutPattern() != null) {
                        try {
                            Date temp = getDateByFormatSimple(metaData.getInPattern(), value.replace("T", " "));
                            return getDateByFormatSimple(metaData.getOutPattern(), temp);
                        } catch (RuntimeException ignored) {
                            return value;
                        }
                    }
                default:
                    return value;
            }
        } catch (RuntimeException e) {
            return value;
        }
    }

    public static Matcher<String> getMatherByMetricType(MetaData metaData, String expectedValue) {
        switch (metaData.getType().toLowerCase()) {
            case NUMBER_FORMAT:
                expectedValue = DataHelper.getSplittedNumber(expectedValue);
                return CustomMatcher.matchesPattern(expectedValue, DataHelper.getSplittedNumberToPattern(expectedValue));
            case CURRENCY_FORMAT:
                expectedValue = DataHelper.getSplittedByComma(expectedValue, 2);
                return CustomMatcher.matchesPattern(expectedValue, DataHelper.getRoundedFloatToPatten(expectedValue));
            case PERCENTAGE_FORMAT:
//                expectedValue = DataHelper.getRoundedFloat(expectedValue, 2);
                expectedValue = DataHelper.getSplittedByComma(expectedValue, 2);
                return CustomMatcher.matchesPattern(expectedValue, DataHelper.getRoundedFloatToPatten(expectedValue));
            // custom, not from objects, could be \d+([^\d]?\d*)?[^\d]{0,2}
            case NUMBER_PERCENTAGE_FORMAT:
            case TIMEOUT_FORMAT:
                expectedValue = DataHelper.getSplittedNumber(expectedValue);
                return CustomMatcher.matchesPattern(expectedValue, expectedValue + "[^\\d]{0,2}");
            case DATE_FORMAT:
            case DATE_TIME_FORMAT:
                if (metaData.getInPattern() != null && metaData.getOutPattern() != null) {
                    try {
                        Date temp = DataHelper.getDateByFormatSimple(metaData.getInPattern(), expectedValue.replace("T", " "));
                        return equalToIgnoringCase(DataHelper.getDateByFormatSimple(metaData.getOutPattern(), temp));
                    } catch (RuntimeException ignored) {
                    }
                }
                return equalToIgnoringCase(expectedValue);
            default:
                return equalToIgnoringCase(expectedValue);
        }
    }

    public static String getValueByPattern(String expectedValue, String type) {
        switch (type) {
            case NUMBER_FORMAT:
                return DataHelper.getSplittedNumber(expectedValue);
            case CURRENCY_FORMAT:
                return DataHelper.getSplittedByComma(expectedValue, 2);
            case PERCENTAGE_FORMAT:
                return DataHelper.getSplittedByComma(expectedValue, 2);
            // custom, not from objects, could be \d+([^\d]?\d*)?[^\d]{0,2}
            case NUMBER_PERCENTAGE_FORMAT:
                return DataHelper.getSplittedNumber(expectedValue) + "[^\\d]{0,2}";
        }
        return expectedValue;
    }
}