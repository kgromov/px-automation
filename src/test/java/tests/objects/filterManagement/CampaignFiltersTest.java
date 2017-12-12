package tests.objects.filterManagement;

import config.Config;
import dto.LxpDataProvider;
import org.testng.annotations.Test;
import px.objects.filters.nodes.DataFilterNode;
import px.objects.filters.FilterManagementTestData;
import px.objects.filters.nodes.FilterNode;
import px.objects.filters.nodes.FilterNodeData;
import utils.SoftAssertionHamcrest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by kgr on 10/27/2017.
 */
public class CampaignFiltersTest {

    @Test (enabled = false)
    // USAGE: check that all contact fields are valid names and have mapped value (!=null)
    public void checkContactDataFields() {
        Config.setAdminUser();
        SoftAssertionHamcrest hamcrest = new SoftAssertionHamcrest();
        Set<String> unMappedFields = new TreeSet<>();
        LxpDataProvider dataProvider = new LxpDataProvider();
        // loop through all country-verticals
        Map<String, String> verticalCountries = dataProvider.getPossibleValueFromJSON("VerticalAndCountry");
        verticalCountries.values().forEach(item -> {
            String[] verticalCountry = item.split("-");
            String country = verticalCountry[0].toUpperCase();
            String vertical = verticalCountry[1].toUpperCase();
            // look through all fields to save exceptions
            FilterManagementTestData filtersData = new FilterManagementTestData(country, vertical);
            filtersData.getFields().forEach(field -> {
                String fieldInfo = String.format("name=%s, type=%s, valuesList=%s", field.getName(), field.getType(), field.getPossibleValues());
                if (field.getValue() == null) {
                    unMappedFields.add(field.getName());
                    hamcrest.append(String.format("Field %s has null-value\tCountry = %s, Vertical = %s", fieldInfo, country, vertical));
                }
            });
        });
        // grab all exception fields
        if (!hamcrest.toString().isEmpty()) hamcrest.append("Unmapped field names:\n" + unMappedFields);
        hamcrest.assertAll();
    }

    public static void main(String[] args) {
        Config.setAdminUser();
        FilterManagementTestData.checkFilterFile("350");
        FilterManagementTestData filtersData = new FilterManagementTestData("US", "AUTO");
        List<FilterNodeData> data = filtersData.getFields();

        FilterNode root = new FilterNode();
        List<FilterNode> leaves = data.stream().map(DataFilterNode::new).collect(Collectors.toList());
        root.setChildren(leaves);
    }
}
