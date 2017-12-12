package px.objects.filters.nodes;

import configuration.helpers.JSONWrapper;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import px.objects.users.ContactTestData;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomListFromList;
import static configuration.helpers.DataHelper.getRandomValueFromList;
import static px.objects.filters.FilterManagementTestData.*;

/**
 * Created by kgr on 10/23/2017.
 */
public class FilterNodeData {
    private String name;
    private String type;
    private String value;
    private String command;
    private List<String> possibleValues;
    // not transparent usage 
    private boolean isMandatory;
    private boolean isMultiSelect;
    // auxiliary to bound to formats: phone -> digits, city -> chars etc
    private static final Map<String, String> CONTACT_DATA_MAP = new ContactTestData().toFilterMap();
    // static data
    public static final List<String> OPERANDS = Arrays.asList("Or", "And");
    public static final String REMOVE_OPERAND = "Remove";

    public FilterNodeData(JSONObject object, List<String> commands) {
        this.name = String.valueOf(object.get("field"));
        this.type = String.valueOf(object.get("valueType"));
        if (type.equals("ValueList")) this.possibleValues = JSONWrapper.toList(object, "valuesList");
        this.isMandatory = object.has("mandatory") && object.getBoolean("mandatory");
        // commands - also some limitations and mapping
        commands = !isRangeAllowed(name) ? commands.stream().filter(command ->
                !RANGE_COMMANDS.contains(command)).collect(Collectors.toList()) : commands;
        this.command = getRandomValueFromList(hasPossibleValues() ? getMultiCommands() : commands);
        // -------------------- value initialization --------------------
        // when letters when numbers or bound to contact data
        this.value = CONTACT_DATA_MAP.get(name);
        // select
        this.isMultiSelect = hasPossibleValues() && MULTI_SELECT_COMMANDS.contains(command);
        // if select element: if multi - up to 10, else - 1
        int valuesCount = isMultiSelect ? possibleValues.size() > 10 ? 10 : possibleValues.size() : 1;
        List<String> temp = hasPossibleValues() ? getRandomListFromList(possibleValues, valuesCount) : null;
        // final value
        this.value = temp != null ? StringUtils.join(temp, ",") : value;
        // change value according to command -> isRangeCommand 2 boundaries
        if (RANGE_COMMANDS.contains(command)) {
            String rightBoundaryValue = new ContactTestData().toFilterMap().get(name);
            value = String.format("%s,%s", value, rightBoundaryValue);
        }
        // probably add assertion for null value
       /* if (value == null)
            throw new TestDataException("Null value for node - " + this.toString());*/
    }

    public FilterNodeData(JSONObject object) {
        this.name = String.valueOf(object.get("field"));
        this.type = String.valueOf(object.get("valueType"));
        if (type.equals("valuesList")) this.possibleValues = JSONWrapper.toList(object, "valuesList");
        this.isMandatory = object.has("mandatory") && object.getBoolean("mandatory");
        // parse to more representative       
        this.name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name.substring(name.lastIndexOf(".") + 1)), " ");
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCommand() {
        return command;
    }

    public String getValue() {
        return value;
    }

    public List<String> getPossibleValues() {
        return possibleValues;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public boolean hasPossibleValues() {
        return possibleValues != null && !possibleValues.isEmpty();
    }

    @Override
    public String toString() {
        return "FilterNodeData{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", value='" + value + '\'' +
                ", command='" + command + '\'' +
                '}';
    }
}
