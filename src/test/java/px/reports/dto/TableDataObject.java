package px.reports.dto;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by kgr on 3/15/2017.
 */
public class TableDataObject {
    private JSONArray dataList;
    private List<List<String>> tableCellsText;

    public TableDataObject(JSONArray dataList, List<List<String>> tableCellsText) {
        this.dataList = dataList;
        this.tableCellsText = tableCellsText;
    }

    public JSONArray getDataList() {
        return dataList;
    }

    public List<List<String>> getTableCellsText() {
        return tableCellsText;
    }

    public boolean isEmptyTable() {
        return dataList.length() == 0;
    }
}
