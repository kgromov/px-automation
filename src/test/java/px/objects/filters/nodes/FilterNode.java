package px.objects.filters.nodes;

import configuration.helpers.JSONWrapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomValueFromList;
import static px.objects.filters.nodes.FilterNode.FilterNodeTypeEnum.OPERAND;
import static px.objects.filters.nodes.FilterNodeData.OPERANDS;

/**
 * Created by kgr on 10/25/2017.
 */
public class FilterNode {
    protected String name;                          // => commandTag
    protected String guid;                // => uid; available after creation
    protected List<FilterNode> children;
    // own while create to bound with test data
    protected String type;                          // => {OPERAND; DATA; COMMAND}
    protected String value;                         // only COMMAND; if List -> join by ,

    // existed node - initialization from json
    public FilterNode(JSONObject object) {
        this.name = String.valueOf(object.get("commandTag")).replace("--", ".");
        this.guid = String.valueOf(object.get("uid"));
        this.value = object.has("value") ? String.valueOf(object.get("value")) : value;
        JSONArray objects = object.getJSONArray("commands");
        this.children = JSONWrapper.toList(objects).stream().map(FilterNode::new).collect(Collectors.toList());
    }

    // operand only
    public FilterNode() {
        this.name = getRandomValueFromList(OPERANDS);
        this.type = OPERAND.name();
    }

    public String getName() {
        return name;
    }

    public String getGuid() {
        return guid;
    }

    public List<FilterNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return children != null && !children.isEmpty();
    }

    // currently OPERAND only
    public void setChildren(List<FilterNode> children) {
        this.children = children;
    }

    // ------- build fields -------
    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FilterNode{" +
                "name='" + name + '\'' +
                ", guid='" + guid + '\'' +
                ", children=" + children +
                '}';
    }

    public enum FilterNodeTypeEnum {
        OPERAND, DATA, COMMAND
    }
}
