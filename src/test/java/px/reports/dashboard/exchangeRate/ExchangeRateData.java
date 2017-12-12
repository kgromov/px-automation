package px.reports.dashboard.exchangeRate;

import dto.TestDataException;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Date;

import static config.Config.LOCALE;
import static configuration.helpers.DataHelper.getDateByFormatSimple;
import static configuration.helpers.DataHelper.getDateByHourOffset;

/**
 * Created by kgr on 10/20/2017.
 */
public class ExchangeRateData {
    // patterns
    public static final String IN_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String OUT_PATTERN = "MMMM d yyyy, h:mm:ss a";
    //    public static final String OUT_PATTERN = "MMMM d yyyy, h:mm:ss a '(UTC'Z)";
    // + format.setTimeZone(TimeZone.getTimeZone("UTC"));
//    public static String RATE_RECORD_PATTERN = "%s.*%s.*\\d{1,2}[,.]\\d{3}";
    public static String RATE_RECORD_PATTERN = "%s.*%s.*%s";
    // data
    private String currencyFrom;
    private String currencyTo;
    private String rate;
    private String roundedRate;
    private Date rateDate;

    public ExchangeRateData(JSONObject object) {
        try {
            this.currencyFrom = object.getString("from");
            this.currencyTo = object.getString("to");
            this.rate = String.valueOf(object.get("rate"));
            setRoundedRate(rate);
            String date = String.valueOf(object.get("fetchTime"));
            try {
                this.rateDate = getDateByFormatSimple(IN_PATTERN, date);
            } catch (RuntimeException e) {
                throw new TestDataException("Unable to parse rate date\tDetails: " + e.getMessage());
            }
        } catch (JSONException e) {
            throw new TestDataException("Unable to parse exchange rate\tResponse: " + object);
        }
    }

    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public String getRate() {
        return rate;
    }

    public String getRateDate() {
        return getDateByFormatSimple(OUT_PATTERN, getDateByHourOffset(rateDate, 5)).replace("AM", "am").replace("PM", "pm");
    }

    public String getRoundedRate() {
        return roundedRate;
    }

    private void setRoundedRate(String rate) {
        /*MetaData metaData = new MetaData("Rate", DOUBLE_FORMAT, 3) {
        };
        return PXDataHelper.getValueByType(metaData, rate);*/
        try {
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(LOCALE);
            DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
            symbols.setGroupingSeparator(' ');  // make parameterized
            symbols.setDecimalSeparator(',');   // make parameterized
            formatter.setDecimalFormatSymbols(symbols);
            formatter.applyPattern("###,##0.00");
            formatter.setMinimumFractionDigits(3);
            formatter.setMaximumFractionDigits(3);
            double roundedRate = new BigDecimal(rate).setScale(3, RoundingMode.HALF_UP).doubleValue();
//            return formatter.format(new BigDecimal(rate).doubleValue());
            this.roundedRate = formatter.format(roundedRate);
        } catch (RuntimeException e) {
            this.roundedRate = rate;
        }
    }

    @Override
    public String toString() {
        return "ExchangeRateData{" +
                "currencyFrom='" + currencyFrom + '\'' +
                ", currencyTo='" + currencyTo + '\'' +
                ", rate=" + rate +
                ", roundedRate=" + roundedRate +
                ", rateDate=" + rateDate +
                '}';
    }
}