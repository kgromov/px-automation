package tests.reports.dashboard;

import org.testng.annotations.Test;
import pages.DashboardPage;
import px.reports.dashboard.DashboardReportPage;
import px.reports.dashboard.exchangeRate.ExchangeRatesData;
import tests.LoginTest;

/**
 * Created by konstantin on 23.08.2017.
 */
public class ExchangeRatesTest extends LoginTest {

    @Test
    public void checkExchangeRates() {
        ExchangeRatesData testData = new ExchangeRatesData();
        new DashboardReportPage(pxDriver).navigateToPage();
        // test exchange rates
        new DashboardPage(pxDriver).checkExchangeRate(testData.getRates());
    }
}
