package px.reports.dto;

import dto.LxpDataProvider;
import org.json.JSONArray;
import pages.groups.Searchable;
import px.reports.ReportTestData;
import px.reports.SearchByEnum;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by kgr on 8/10/2017.
 */
public abstract class SearchData implements Searchable {
    protected final LxpDataProvider dataProvider = new LxpDataProvider();
    protected FieldFormatObject field;
    protected String searchValue;
    protected String searchBy;
    protected String searchByRegExp;
    protected SearchByEnum searchByEnum;
    protected JSONArray itemsSearchBy;
    protected int itemsSearchByCount;
    protected int totalCount;
    // label mapping cause of disability to add data-field-name attribute to search items
    public static final Set<String> LOOKUPS_REG_EXP = Stream.of("transaction.*", "email.*", "phone.*", "lead.*").collect(Collectors.toSet());

    public SearchData(ReportTestData testData, FieldFormatObject field) {
        this.field = field;
        this.totalCount = testData.getItemsTotalCount();
        this.searchValue = getAnyValueFromColumn(testData.getAllRowsArray(), field.getName());
        this.searchBy = field.getSearchByFilter();
        this.searchByRegExp =
                LOOKUPS_REG_EXP.stream().filter(lookup ->
                        Pattern.compile(lookup, Pattern.CASE_INSENSITIVE).matcher(searchBy).matches()
                ).findFirst().orElseThrow(() -> new IllegalArgumentException("Illegal or unknown search by - " + searchBy));
        this.searchByEnum = Stream.of(SearchByEnum.values()).filter(lookUp ->
                Pattern.compile(searchByRegExp, Pattern.CASE_INSENSITIVE).matcher(lookUp.getValue()).matches()
        ).findFirst().orElseThrow(() -> new IllegalArgumentException("Illegal or unknown search by - " + searchBy));
    }

    public String getSearchValue() {
        return searchValue;
    }

    public String getSearchBy() {
        return searchBy;
    }

    public JSONArray getItemsSearchBy() {
        return itemsSearchBy;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getItemsSearchByCount() {
        return itemsSearchByCount;
    }

    public SearchByEnum getSearchByEnum() {
        return searchByEnum;
    }

    public FieldFormatObject getField() {
        return field;
    }

    @Override
    public String toString() {
        return "SearchData{" +
                "searchValue='" + searchValue + '\'' +
                ", searchBy='" + searchBy + '\'' +
                '}';
    }
}