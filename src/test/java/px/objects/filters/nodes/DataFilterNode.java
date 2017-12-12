package px.objects.filters.nodes;

import org.apache.commons.lang3.StringUtils;

import java.util.Collections;

import static px.objects.filters.FilterManagementTestData.FILTER_COMMANDS_MAP;
import static px.objects.filters.FilterManagementTestData.FILTER_FIELDS_MAP;
import static px.objects.filters.nodes.FilterNode.FilterNodeTypeEnum.COMMAND;
import static px.objects.filters.nodes.FilterNode.FilterNodeTypeEnum.DATA;

/**
 * Created by kgr on 10/25/2017.
 */
public class DataFilterNode extends FilterNode {
    private String group;
    protected String nameLabel;

    public DataFilterNode() {
    }

    public DataFilterNode(String name) {
        this.name = name;
        this.type = COMMAND.name();
        this.nameLabel = FILTER_COMMANDS_MAP.containsKey(name) ? FILTER_COMMANDS_MAP.get(name) : name;
    }

    // FilterNodeData contains of DATA node and it's COMMAND child
    public DataFilterNode(FilterNodeData fieldData) {
        this.name = fieldData.getName();
        this.type = DATA.name();
        // parse to more representative
        this.group = name.substring(0, name.lastIndexOf("."));
        this.group = FILTER_FIELDS_MAP.containsKey(name) ? FILTER_FIELDS_MAP.get(name) : group;
        this.nameLabel = group + " - " + StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name.substring(name.lastIndexOf(".") + 1)), " ");
        // child node - command
//        children = Collections.singletonList(new DataFilterNode(fieldData.getCommand()));
        this.children = Collections.singletonList(new CommandFilterNode(fieldData));
    }

    public String getGroup() {
        return group;
    }

    public String getNameLabel() {
        return nameLabel;
    }

    @Override
    public String toString() {
        return super.toString() +
                "group='" + group + '\'' +
                ", nameLabel='" + nameLabel + '\'' +
                '}';
    }
}
