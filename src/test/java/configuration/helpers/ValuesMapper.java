package configuration.helpers;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by kgr on 7/4/2017.
 */
public class ValuesMapper {
    private String fieldName;
    private Map<String, String> map;

    public ValuesMapper(String fieldName, Map<String, String> map) {
        this.fieldName = fieldName;
        this.map = map;
    }

    public String getMappedValue(String value) {
        return map.containsKey(value) ? map.get(value) : value;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public static ValuesMapper getMappedValues(List<ValuesMapper> fields, String fieldName) {
        return fields.stream().filter(field -> field.fieldName.equals(fieldName)).findFirst().orElse(null);
    }

    public static boolean hasMappedValues(List<ValuesMapper> fields, String fieldName) {
        return fields.stream().map(field -> field.fieldName).collect(Collectors.toList()).contains(fieldName);
    }
}