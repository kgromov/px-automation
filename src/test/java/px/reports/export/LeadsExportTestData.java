package px.reports.export;

import config.Config;
import configuration.helpers.DataHelper;
import px.reports.ReportTestData;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by kgr on 4/21/2017.
 */
public class LeadsExportTestData extends ReportTestData {
    private String vertical;
    private String country;
    private String exportAttributes;

    public LeadsExportTestData() {
        super.startMonthOffset = 3;
        super.durationDays = 1;
        setDateRanges();
        Map<String, String> possibleValuesMap = dataProvider.getPossibleValueFromJSON("Verticals" + (Config.isBetaEnvironment() ? "_beta" : ""));
        this.vertical = DataHelper.getRandomValueFromList(new ArrayList<>(possibleValuesMap.keySet()));
        possibleValuesMap = dataProvider.getPossibleValueFromJSON("Countries");
        this.country = DataHelper.getRandomValueFromList(new ArrayList<>(possibleValuesMap.keySet()));
        this.exportAttributes = DataHelper.getRandomYesNo();
    }

    public String getVertical() {
        return vertical;
    }

    public String getCountry() {
        return country;
    }

    public String getExportAttributes() {
        return exportAttributes;
    }

    @Override
    public String toString() {
        return "LeadsExportTestData{" +
                "fromPeriod='" + fromPeriod + '\'' +
                ", toPeriod='" + toPeriod + '\'' +
                ", vertical='" + vertical + '\'' +
                ", country='" + country + '\'' +
                ", exportAttributes='" + exportAttributes + '\'' +
                '}';
    }
}