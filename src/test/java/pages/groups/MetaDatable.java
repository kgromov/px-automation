package pages.groups;

import px.reports.dto.FieldFormatObject;

import java.util.List;

/**
 * Created by kgr on 8/15/2017.
 */
public interface MetaDatable {

    void setHeadersURL(); // -> setMetadataURL?

    void setFields();

    List<FieldFormatObject> getFields();
}