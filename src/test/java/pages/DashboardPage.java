package pages;

import config.DashboardMenuEnum;
import configuration.browser.PXDriver;
import org.openqa.selenium.WebElement;
import px.reports.ReportsEnum;
import px.reports.dashboard.exchangeRate.ExchangeRateData;
import utils.SoftAssertionHamcrest;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static config.Config.userGroup;
import static org.hamcrest.Matchers.containsString;
import static pages.locators.DashboardPageLocators.*;
import static pages.locators.ElementLocators.TABLE_TOOLTIP;
import static pages.locators.LoginPageLocators.LOGOUT_BUTTON;
import static pages.locators.LoginPageLocators.PX_LOGO;
import static px.reports.dashboard.exchangeRate.ExchangeRateData.RATE_RECORD_PATTERN;

/**
 * Created by okru on 2/13/2016.
 */
public class DashboardPage extends OverviewPage {

    public DashboardPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void checkPage() {
        log.info("Checking that all main elements now exists...");
        log.info("Checking that 'PX logo' exists on page...");
        helper.waitUntilDisplayed(PX_LOGO);
        log.info("Checking that 'Log out' link exists on page...");
        helper.waitUntilDisplayed(LOGOUT_BUTTON);
    }

    public void checkUserAccess() {
        log.info(String.format("Menu items verification according to user role = '%s'", userGroup));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        waitMenuItems();
        log.info("Check menu items");
        List<String> expectedItems = DashboardMenuEnum.getMenuItems().stream()
                .map(DashboardMenuEnum::getValue).collect(Collectors.toList());
        List<WebElement> actualItems = menuElement.items();
        // get relative link and modify to objects object name
        List<String> itemLinks = actualItems.stream().map(item ->
                item.getAttribute("pathname").replace("/", "")).collect(Collectors.toList());
        // check menu items
        hamcrest.append(checkItems(expectedItems, itemLinks));
        // check available reports
        log.info("Check report items");
        setMenu(DashboardMenuEnum.REPORTS);
        expectedItems = ReportsEnum.getAvailableReports().stream()
                .map(ReportsEnum::getValue).collect(Collectors.toList());
        actualItems = helper.getElements(".//a[@ui-sref]");
        itemLinks = actualItems.stream().map(item ->
                item.getAttribute("pathname")).collect(Collectors.toList());
        // check report items
        hamcrest.append(checkItems(expectedItems, itemLinks));
        hamcrest.assertAll();
    }


    private String checkItems(List<String> expectedItems, List<String> itemLinks) {
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // missed grants
        expectedItems.stream().filter(itemLink -> !itemLinks.contains(itemLink)).forEach(itemLink ->
                hamcrest.assertThat(String.format("Missed menu item '%s' according to user role '%s'", itemLink, userGroup), false));
        // extra grants
        itemLinks.stream().filter(itemLink -> !expectedItems.contains(itemLink)).forEach(itemLink ->
                hamcrest.assertThat(String.format("Extra menu item '%s' according to user role '%s'", itemLink, userGroup), false));
        return hamcrest.toString();
    }

    public DashboardPage checkExchangeRate(List<ExchangeRateData> exchangeRates) {
        log.info("Check exchange rates on dashboard page");
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        helper.waitUntilDisplayed(FOOTER_LINKS + EXCHANGE_RATE_LINK.substring(1));
        // invoke tooltip
        WebElement exchangeRateLink = helper.getElement(FOOTER_LINKS, EXCHANGE_RATE_LINK);
        // fetch date verification
        hamcrest.assertThat("Exchange rate were updated date verification",
                exchangeRateLink.getText(), containsString(exchangeRates.get(0).getRateDate()));
        helper.moveToElement(exchangeRateLink);
        helper.waitUntilDisplayed(TABLE_TOOLTIP);
        // items inside tooltip verification
        WebElement tooltip = helper.getElement(TABLE_TOOLTIP);
        List<ExchangeRateData> tempRates = new ArrayList<>();
        List<WebElement> actualRates = helper.getElements(tooltip, CURRENCY_RATE);
        for (int i = 0; i < actualRates.size(); i++) {
            String actualRate = null;
            try {
                ExchangeRateData expectedRate = exchangeRates.get(i);
                actualRate = actualRates.get(i).getText();
                String pattern = String.format(RATE_RECORD_PATTERN, expectedRate.getCurrencyFrom(), expectedRate.getCurrencyTo(), expectedRate.getRoundedRate());
                log.info(String.format("DEBUG\trate=%s, rounded=%s, full rate=%s, actual=%s", expectedRate.getRate(), expectedRate.getRoundedRate(), expectedRate, actualRate));
                hamcrest.assertThat(String.format("Exchange rate does not to match to UI value:\nExpected = %s\tactual=%s", expectedRate, actualRate),
                        Pattern.compile(pattern).matcher(actualRate).matches());
                tempRates.add(expectedRate);
            } catch (IndexOutOfBoundsException e) {
                hamcrest.assertThat("Extra exchange rate - " + actualRate, false);
            }
        }
        // if anything remains in tempRates - extra rates
        exchangeRates.removeAll(tempRates);
        exchangeRates.forEach(rate -> hamcrest.assertThat("Missed exchange rate - " + rate, false));
        hamcrest.assertAll();
        return this;
    }
}
