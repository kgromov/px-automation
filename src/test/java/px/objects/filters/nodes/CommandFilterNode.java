package px.objects.filters.nodes;

import static px.objects.filters.FilterManagementTestData.FILTER_COMMANDS_MAP;
import static px.objects.filters.nodes.FilterNode.FilterNodeTypeEnum.COMMAND;

/**
 * Created by kgr on 10/25/2017.
 */
public class CommandFilterNode extends DataFilterNode {
    private boolean isSelect;
    private boolean isMultiSelect;

    public CommandFilterNode(FilterNodeData fieldData) {
        super();
        this.type = COMMAND.name();
        this.name = fieldData.getCommand();
        this.value = fieldData.getValue();
        this.isSelect = fieldData.hasPossibleValues();
        this.isMultiSelect = isSelect && fieldData.isMultiSelect();
        this.nameLabel = FILTER_COMMANDS_MAP.containsKey(name) ? FILTER_COMMANDS_MAP.get(name) : name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

}
