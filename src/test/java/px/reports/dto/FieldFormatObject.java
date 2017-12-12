package px.reports.dto;

import configuration.helpers.DataHelper;
import configuration.helpers.JSONWrapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import pages.groups.MetaData;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static configuration.helpers.PXDataHelper.getValueByType;

/**
 * Created by kgr on 3/15/2017.
 */
public class FieldFormatObject extends MetaData {
    private String searchByFilter;
    private PopupData popupData;
    private String headerTooltip;
    private String tooltipSource;
    private FormulaObject formula;
    private boolean hasHeaderTooltip;
    private boolean hasTooltip;
    private boolean hasPopup;
    private boolean isSortable;
    private boolean isGrouping;
    private boolean isNotAField;
    // values mapper
    private Map<String, String> valuesMap;
    // tooltips mapper -> probably later on all mapping to json
    private JSONObject tooltipsMap;
    // depends on filter
    private List<String> dependentFilters;
    private boolean isFilterSatisfied;

    public FieldFormatObject(JSONObject jsonObject) {
        this.title = jsonObject.has("title") ? jsonObject.getString("title") : null;
        this.isNotAField = !jsonObject.has("field");
        this.name = !isNotAField ? jsonObject.getString("field") : null;
        // usually for report page
        this.type = jsonObject.has("metricType") ? jsonObject.getString("metricType") : "String";
        // usually for preview page - 2nd chance
        this.type = !jsonObject.has("metricType") && jsonObject.has("viewType") ? jsonObject.getString("viewType") : type;
        this.searchByFilter = jsonObject.has("searchable") ? jsonObject.getString("searchable") : null;
        // tooltip
        this.hasTooltip = jsonObject.has("tooltipTemplate") || jsonObject.has("tooltip-theme");
        this.hasHeaderTooltip = jsonObject.has("headerTooltip") && jsonObject.getBoolean("headerTooltip");
        this.tooltipSource = jsonObject.has("tooltipTemplate") ? jsonObject.getString("tooltipTemplate") : null;
        // rest
        this.isSortable = jsonObject.has("sortable") && jsonObject.getBoolean("sortable");
        this.isGrouping = jsonObject.has("grouping");
        this.hasPopup = jsonObject.has("template") && PopupData.POPUP_PATTERN.matcher(String.valueOf(jsonObject.get("template"))).find();
        this.popupData = hasPopup ? new PopupData(String.valueOf(jsonObject.get("template"))) : null;
        this.dependentFilters = jsonObject.has("filtersDependent") ? DataHelper.getStringAsArray(
                String.valueOf(jsonObject.get("filtersDependent")).replaceAll("\"", "")) : null;
    }

    public FieldFormatObject(JSONObject jsonObject, Map<String, String> metricsMap, int index) {
        this(jsonObject);
        this.index = index;
        // override from string to proper format from map
        this.type = !isNotAField && metricsMap.containsKey(name) ? metricsMap.get(name) : type;
        // set in-out patterns for Date type
        setPatterns(jsonObject);
    }

    public PopupData getPopupData() {
        return popupData;
    }

    public String getHeaderTooltip() {
        return headerTooltip;
    }

    public boolean hasHeaderTooltip() {
        return hasHeaderTooltip;
    }

    public boolean hasTooltip() {
        return hasTooltip;
    }

    public boolean hasPopup() {
        return hasPopup;
    }

    public boolean isSortable() {
        return isSortable;
    }

    public boolean isGrouping() {
        return isGrouping;
    }

    // search
    public String getSearchByFilter() {
        return searchByFilter;
    }

    public boolean isSearchable() {
        return searchByFilter != null;
    }

    // dependent on filters
    public boolean isFilterSatisfied() {
        return isFilterSatisfied;
    }

    public boolean hasDependentFilters() {
        return dependentFilters != null && !dependentFilters.isEmpty();
    }

    public void setIndex(int index) {
        this.index = index;
    }

    // data mapping
    public void setValuesMap(Map<String, String> valuesMap) {
        this.valuesMap = valuesMap;
    }

    public String getMappedValue(String value) {
        return valuesMap.containsKey(value) ? valuesMap.get(value) : value;
    }

    public boolean hasMappedValues() {
        return valuesMap != null && !valuesMap.isEmpty();
    }

    // tooltip data mapping
    public FieldFormatObject withMapping(ReportDataMapping mapping) {
        if (mapping.hasFieldHeaderMapping(name))
            this.headerTooltip = mapping.getFieldHeaderMapping(name);
        if (mapping.hasFieldTooltipMapping(name)) {
            this.tooltipsMap = mapping.getFieldTooltipMapping(name);
//            this.formula = tooltipsMap.has("formula") ? new FormulaObject(tooltipsMap.getString("formula")) : null;
            this.formula = tooltipsMap.has("formula") ? new FormulaObject(tooltipSource) : null;
        }
        return this;
    }

    public String getMappedTooltipValue(String value) {
        return tooltipsMap.has(value) ? tooltipsMap.getString(value) : hasFormula() ? formula.getResult() : value;
    }

    public boolean hasMappedHeaderTooltip() {
        return headerTooltip != null;
    }

    public boolean hasMappedTooltipValues() {
        return tooltipsMap != null;
    }

    public FormulaObject getFormula() {
        return formula;
    }

    public boolean hasFormula() {
        return formula != null;
    }

    // Date
    private void setPatterns(JSONObject jsonObject) {
        if (type.equalsIgnoreCase(DATE_FORMAT) | type.equalsIgnoreCase(DATE_TIME_FORMAT) && jsonObject.has("template")) {
            String template = String.valueOf(jsonObject.get("template"));
            // in case of complex value with span elements
            Elements elements = Jsoup.parse(template).getElementsByTag("span");
            Element span = elements.stream().filter(element -> Pattern.compile(":\\s*'.*'").matcher(element.text()).find()).findFirst().orElse(null);
            template = span != null ? span.text() : template;
            java.util.regex.Matcher matcher = Pattern.compile(":\\s*'.*'").matcher(template);
            try {
                String[] patterns = matcher.find() ? matcher.group().split("\\s?:\\s?'") : null;
                this.inPattern = patterns != null ? patterns[1].replace("'", "") : null;
                this.outPattern = patterns != null ? patterns[2].replace("'", "") : null;
                // from js -> java Date patterns
                inPattern = inPattern != null ? inPattern.replaceAll("Y", "y").replaceAll("D", "d").replaceAll("T", " ") : null;
                outPattern = outPattern != null ? outPattern.replaceAll("Y", "y").replaceAll("D", "d").replaceAll("T", " ") : null;
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }

    private void applyCondition(String filterName) {
        this.isFilterSatisfied = dependentFilters != null && dependentFilters.contains(filterName);
    }

    public static FieldFormatObject getFieldObjectFromListByName(List<FieldFormatObject> fields, String name) {
        return fields.stream().filter(field -> field.getName().equals(name))
                .findFirst().orElse(null);
    }

    public static List<FieldFormatObject> withoutNonFields(List<FieldFormatObject> fields) {
        return fields.stream().filter(field -> !field.isNotAField).collect(Collectors.toList());
    }

    public static List<String> getFieldNames(List<FieldFormatObject> fields) {
        return withoutNonFields(fields).stream().
                map(FieldFormatObject::getName)
                .collect(Collectors.toList());
    }

    public static List<String> getGroupingFieldNames(List<FieldFormatObject> fields) {
        return fields.stream()
                .filter(FieldFormatObject::isGrouping)
                .map(FieldFormatObject::getName)
                .collect(Collectors.toList());
    }

    // dependent filters
    public static List<FieldFormatObject> withDependenciesFiltersFields(List<FieldFormatObject> fields) {
        return fields.stream().filter(field -> !field.hasDependentFilters()).collect(Collectors.toList());
    }

    public static List<FieldFormatObject> withRelevantFields(List<FieldFormatObject> fields) {
        List<FieldFormatObject> tempFields = new ArrayList<>();
        fields.stream().filter(field -> !field.isNotAField).forEach(field -> {
            if (field.hasDependentFilters()) {
                if (field.isFilterSatisfied())
                    tempFields.add(field);
            } else tempFields.add(field);
        });
        return tempFields;
    }

    public static void applyCondition(List<FieldFormatObject> fields, String filterName) {
        withoutNonFields(fields).stream().filter(FieldFormatObject::hasDependentFilters).forEach(field -> field.applyCondition(filterName));
    }

    public static void resetCondition(List<FieldFormatObject> fields) {
        withoutNonFields(fields).forEach(field -> field.isFilterSatisfied = false);
    }

    // search
    public static List<FieldFormatObject> searchableFields(List<FieldFormatObject> fields) {
        return fields.stream().filter(FieldFormatObject::isSearchable).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "FieldFormatObject{" +
                "title='" + title + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", hasTooltip=" + hasTooltip +
                ", isSortable=" + isSortable +
                ", popupData=" + (hasPopup ? popupData.toString() : "") +
                ", dependentFilters=" + (hasDependentFilters() ? dependentFilters.toString() : "") +
                '}';
    }

    public final class FormulaObject {
        private String source;
        private String result;
        private Set<String> names;
        private Set<MetaData> fields;

        public FormulaObject(String source) {
            /*this.source = source;
            this.names = new HashSet<>();
            Pattern pattern = Pattern.compile("<\\w+>");
            java.util.regex.Matcher matcher = pattern.matcher(source);
            while (matcher.find()) {
                names.add(matcher.group().replaceAll("[><]", ""));
            }*/
            parseToFields(source);
        }

        private void parseToFields(String source) {
            Set<MetaData> fields = new LinkedHashSet<>();
            Pattern pattern = Pattern.compile("\\{\\{\\w+\\.*\\w+\\s?\\|\\s?\\w+(:\\d+)*\\}\\}");
            Element document = Jsoup.parse(source);
            Elements elements = document.getElementsByAttribute("ng-if");
            Element formulaElement = elements.stream().filter(element -> pattern.matcher(element.text()).find()).findFirst().orElse(null);
            if (formulaElement == null) return;
            source = formulaElement.text();
            java.util.regex.Matcher matcher = pattern.matcher(source);
            // to fields
            while (matcher.find()) {
                String temp = matcher.group();
                String[] fieldData = temp.replaceAll("[}{]", "").split("\\s?\\|\\s?");
                String name = fieldData[0].replace("data.", "");
                String type = fieldData[1].replace("px", "");
                int round = -1;
                if (type.contains(":")) {
                    try {
                        round = Integer.parseInt(type.substring(type.indexOf(':') + 1));
                    } catch (NumberFormatException ignored) {
                    }
                    type = type.substring(0, type.indexOf(':'));
                }
                MetaData field = new MetaData(name, type, round) {
                };
                source = source.replace(temp, field.getName());
                fields.add(field);
            }
            this.source = source;
            this.fields = fields;
            this.names = fields.stream().map(MetaData::getName)
                    .collect(Collectors.toSet());
        }

        public String getSource() {
            return source;
        }

        public String getResult() {
            return result;
        }

        public Set<String> getNames() {
            return names;
        }

        public void calculateFormula(List<FieldFormatObject> fields, JSONObject expectedData) {
            this.result = source;
            List<FieldFormatObject> formulaFields = fields.stream()
                    .filter(field -> names.contains(field.getName()))
                    .collect(Collectors.toList());
            for (FieldFormatObject field : formulaFields) {
                try {
                    String value = JSONWrapper.getString(expectedData, field.getName());
                    // currently without calculation
                    // get regExp by field type or expected value like in ChartMetaData
                    result = result.replace("<" + field.getName() + ">", getValueByType(field, value));
                } catch (JSONException ignored) {
                }
            }
        }

        public void calculateFormula(JSONObject expectedData) {
            this.result = source;
            for (MetaData field : fields) {
                try {
                    String value = JSONWrapper.getString(expectedData, field.getName());
                    // currently without calculation
                    // get regExp by field type or expected value like in ChartMetaData
                    result = result.replace(field.getName(), getValueByType(field, value));
                } catch (JSONException ignored) {
                }
            }
        }

        @Override
        public String toString() {
            return "FormulaObject{" +
                    "source='" + source + '\'' +
                    '}';
        }
    }
}