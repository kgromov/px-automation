package px.reports.dashboard.exchangeRate;

import configuration.helpers.JSONWrapper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import org.json.JSONArray;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by kgr on 10/20/2017.
 */
public class ExchangeRatesData {
    private List<ExchangeRateData> rates;

    public ExchangeRatesData() {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/currency/exchangerates")
                .build().getRequestedURL();
        JSONArray jsonArray = new JSONWrapper(new LxpDataProvider().getDataAsString(requestedURL)).getJSONArray();
        this.rates = JSONWrapper.toList(jsonArray).stream().map(ExchangeRateData::new).collect(Collectors.toList());
    }

    public List<ExchangeRateData> getRates() {
        return rates;
    }

    @Override
    public String toString() {
        return "ExchangeRatesData{" +
                "rates=" + rates +
                '}';
    }
}
