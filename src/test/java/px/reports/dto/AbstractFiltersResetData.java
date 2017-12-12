package px.reports.dto;

import configuration.helpers.DataHelper;
import configuration.helpers.RequestedURL;
import dto.LxpDataProvider;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import java.util.*;

import static px.reports.ReportPageLocators.GENERIC_PARAMETRIZED_FILTER;

/**
 * Created by kgr on 4/11/2017.
 */
public abstract class AbstractFiltersResetData {
    protected final Logger log = Logger.getLogger(this.getClass());
    // fields for filter reset to save once
    protected String _instanceGroup;
    protected String _url;
    protected String _sortBy;
    protected String _sortHow;
    protected String _fromPeriod;
    protected String _toPeriod;
    protected String _fromPeriodKey = "FromPeriod";
    protected String _toPeriodKey = "ToPeriod";
    protected JSONArray itemsByFiltersCombination;
    // reset filters combinations
    protected Map<String, String> filterValuesMap;
    protected String filterReset;
    // extra map for label valued filters
    protected Map<String, String> filterLabelValuesMap;

    public abstract void setFilterValuesMap();

    public abstract JSONArray getItemsByFiltersCombination();

    // default implementation, possibly split to 1) set filter to reset; 2) set  itemsByFiltersCombination
    public void resetFilter() {
        List<String> combinationFiltersList = new ArrayList<>(filterValuesMap.keySet());
        this.filterReset = DataHelper.getRandomValueFromList(combinationFiltersList);
        log.info(String.format("Reset '%s' filter", filterReset));
        // to prevent redundant request, data already exists
        if (combinationFiltersList.size() <= 2) return;
        // cause data in table supposed to be without reset filter
        Map<String, String> resetIterationMap = new HashMap<>(filterValuesMap);
        resetIterationMap.remove(filterReset);
        // keys in filter json request
        List<String> keys = new ArrayList<>(resetIterationMap.keySet());
        keys.addAll(Arrays.asList(_fromPeriodKey, _toPeriodKey));
        // values in filter json request
        List<String> values = new ArrayList<>(resetIterationMap.values());
        values.addAll(Arrays.asList(_fromPeriod, _toPeriod));
        // get rows in table after filter combination
        String requestedURL = new RequestedURL.Builder()
//                .withRelativeURL("api/" + _instanceGroup)
                .withAbsoluteURL(_url)
                .filter(keys, values)
                .sort(_sortBy, _sortHow)
                .build().getRequestedURL();
        this.itemsByFiltersCombination = new LxpDataProvider().getDataAsJSONArray(requestedURL);
    }

    public String getFilterResetKey() {
        return filterReset;
    }

    public String getFilterResetValue() {
        return filterLabelValuesMap.get(filterReset);
    }

    public String getResetFilterLocator() {
        return String.format(GENERIC_PARAMETRIZED_FILTER, filterReset);
    }

    public boolean isAnyFilterSet() {
        return !filterValuesMap.isEmpty();
    }

}
