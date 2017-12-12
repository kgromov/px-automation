package px.reports;

import configuration.browser.PXDriver;
import configuration.helpers.HTMLHelper;
import pages.groups.Searchable;
import px.reports.dto.FieldFormatObject;
import px.reports.dto.SearchData;
import utils.SoftAssertionHamcrest;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;

/**
 * Created by kgr on 8/11/2017.
 */
public abstract class LookUpReportPage extends ReportsPage implements Searchable {

    public LookUpReportPage(PXDriver pxDriver) {
        super(pxDriver);
    }

    public void checkSearchFilter(String searchValue, int filteredRows, int totalRows) {
        log.info(String.format("Check Search filter by search value = '%s'", searchValue));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // set search filter with searchValue
        filterTable(searchValue);
        try {
            checkEmptyTable();
            hamcrest.append(String.format("Table is empty after search by filter '%s'", searchValue));
        } catch (AssertionError e) {
            hamcrest.assertThat(String.format("Check that table has row(s) with search value = '%s'", searchValue),
                    tableElement.isCellPresentByText(searchValue));
            hamcrest.assertThat(String.format("Check that rows number in table equals to " +
                            "lookup with search value = '%s' rows in response", searchValue),
                    tableElement.getTotalRowsCount(), equalTo(filteredRows));
            // reset search filter
            resetSearch();
            hamcrest.assertThat("Check that rows number in table after search reset equals to total rows in response",
                    tableElement.getTotalRowsCount(), equalTo(totalRows));
        }
        hamcrest.assertAll();
    }

    public void checkSearchFilter(String searchValue, SearchByEnum searchBy, int filteredRows, int totalRows) {
        log.info(String.format("Check Search filter by search value = '%s'", searchValue));
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // set search filter with searchValue
        filterTable(searchValue, searchBy.getValue());
        try {
            checkEmptyTable();
            hamcrest.append(String.format("Table is empty after search by filter '%s'", searchValue));
        } catch (AssertionError e) {
            hamcrest.assertThat(String.format("Check that table has row(s) with search value = '%s'", searchValue),
                    tableElement.isCellPresentByText(searchValue));
            hamcrest.assertThat(String.format("Check that rows number in table equals to '%s' " +
                            "lookup with search value = '%s' rows in response", searchBy.getValue(), searchValue),
                    tableElement.getTotalRowsCount(), equalTo(filteredRows));
            // check that filter works - not equals to total count
            hamcrest.assertThat(String.format("Check that rows number in table '%d' by filter '%s' " +
                            "lookup with search value = '%s' not equals to total rows", filteredRows, searchBy.getValue(), searchValue),
                    filteredRows, not(equalTo(totalRows)));
            // reset search filter
            resetSearch();
            hamcrest.assertThat("Check that rows number in table after search reset equals to total rows in response",
                    tableElement.getTotalRowsCount(), equalTo(totalRows));
        }
        hamcrest.assertAll();
    }

    public void checkSearchFilter(SearchData searchData) {
        log.info(String.format("Check Search filter by search value = '%s'", searchData.getSearchValue()));
        String searchValue = searchData.getSearchValue();
        SearchByEnum searchBy = searchData.getSearchByEnum();
        int filteredRows = searchData.getItemsSearchByCount();
        int totalRows = searchData.getTotalCount();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        // set search filter with searchValue
        filterTable(searchData.getSearchValue(), searchData.getSearchByEnum().getValue());
        try {
            checkEmptyTable();
            hamcrest.append(String.format("Table is empty after search by filter '%s'", searchValue));
        } catch (AssertionError e) {
            hamcrest.assertThat(String.format("Check that rows number in table equals to '%s' " +
                            "lookup with search value = '%s' rows in response", searchBy.getValue(), searchValue),
                    tableElement.getTotalRowsCount(), equalTo(filteredRows));
           /* // check that filter works - not equals to total count
            hamcrest.assertThat(String.format("Check that rows number in table '%d' by filter '%s' " +
                            "lookup with search value = '%s' not equals to total rows", filteredRows, searchBy.getValue(), searchValue),
                    filteredRows, not(equalTo(totalRows)));*/
            // check that all cell values are filtered correctly for seeking column
            List<String> cellValues = HTMLHelper.getTableCellsByFields_(tableElement, Collections.singletonList(searchData.getField()));
            cellValues.removeAll(Collections.singletonList(searchValue));
            cellValues.forEach(value -> {
                hamcrest.assertThat(String.format("Column '%s' contains cell with value '%s' that does not equal to search filter value '%s'",
                        searchData.getField().getName(), value, searchValue), false);
            });
            // reset search filter
            resetSearch();
            hamcrest.assertThat("Check that rows number in table after search reset equals to total rows in response",
                    tableElement.getTotalRowsCount(), equalTo(totalRows));
        }
        hamcrest.assertAll();
    }

    protected void checkFilterDoesNotChangeSearchFilter(List<FieldFormatObject> fields, String filterName, String filterValue) {
        checkFilterDoesNotChangeSearchFilter(fields, filterName, filterValue, null);
    }

    protected void checkFilterDoesNotChangeSearchFilter(List<FieldFormatObject> fields, String filterName, String filterValue, List<String> multipleFilers) {
        log.info(String.format("Check that filter '%s' with value '%s' will not change report table under search filter", filterName, filterValue));
        assertThat("Check that overview table has data after setting search filter", !tableElement.isTableEmpty());
        List<List<String>> originalCellsText = HTMLHelper.getTableCells(tableElement);
        // set filter cause there are difference between multiple and single filters
        if (multipleFilers != null && multipleFilers.contains(filterName))
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, filterName), Collections.singletonList(filterValue));
        else
            setFilter(String.format(GENERIC_PARAMETRIZED_FILTER, filterName), filterValue);
        //table size comparison
        List<List<String>> newCellsText = HTMLHelper.getTableCells(tableElement);
        int originalColumns = !originalCellsText.isEmpty() ? originalCellsText.get(0).size() : 0;
        int newColumns = !newCellsText.isEmpty() ? newCellsText.get(0).size() : 0;
        assertThat(String.format("Check that there is no difference between table rows and columns number" +
                        " after setting common filter '%s' with value '%s' after lookup." +
                        "\tOriginal table size = %d x %d, current table size = %d x %d",
                filterName, filterValue, originalCellsText.size(), originalColumns, newCellsText.size(), newColumns),
                originalCellsText.size() == newCellsText.size() & originalColumns == newColumns);
        // cells comparison
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        for (int i = 0; i < originalCellsText.size(); i++) {
            List<String> originalRow = originalCellsText.get(i);
            List<String> newRow = newCellsText.get(i);
            for (FieldFormatObject field : fields) {
                String originalValue = originalRow.get(field.getIndex());
                String newValue = newRow.get(field.getIndex());
                hamcrest.assertThat(String.format("Data in '%s' column at row '%d' is changed",
                        field.getName(), i + 1), newValue, equalTo(originalValue));
            }
        }
        // clarify with description what filter and value was set
        if (!hamcrest.toString().isEmpty())
            assertThat(String.format("Check that there is no difference between table cells after setting common filter " +
                    "'%s' with value '%s' after lookup.\n%s", filterName, filterValue, hamcrest.toString()), false);
        resetFilter(String.format(GENERIC_PARAMETRIZED_FILTER, filterName), filterValue, 0);
    }

    public abstract void checkSearchFilterWithFilters(ReportTestData testData);
}