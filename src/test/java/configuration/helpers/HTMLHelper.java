package configuration.helpers;

import elements.ElementsHelper;
import elements.HelperSingleton;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.openqa.selenium.WebElement;
import pages.groups.MetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kgr on 3/15/2017.
 */
public class HTMLHelper {
    private static final Logger log = Logger.getLogger(HTMLHelper.class);

    // TODO: move all table methods with cells data
    public static String cleanHTML(String unsafe) {
        Whitelist whitelist = Whitelist.none();
        whitelist.addAttributes("a", "href");
        whitelist.addAttributes("span", "class");
        return StringEscapeUtils.unescapeHtml4(Jsoup.clean(unsafe, whitelist));
    }

    public static List<List<String>> getTableCells(WebElement table, boolean excludeLastColumn) {
        log.info("Start getting table cells data by Jsoup");
        long start = System.currentTimeMillis();
        try {
            String tableContent = table.getAttribute("innerHTML");
            Document tableElement = Jsoup.parse(tableContent);
            Elements rows = tableElement.select("tbody > tr");
            List<List<String>> tableList = new ArrayList<>(rows.size());
            for (Element row : rows) {
                Elements cells = row.getElementsByTag("td");
                List<String> cellList = new ArrayList<>();
                cells.forEach(cell -> cellList.add(cell.text()));
//                cells.stream().filter(cell -> !cell.attr("data-field-name").isEmpty()).forEach(cell -> cellList.add(cell.text()));
                tableList.add(cellList);
            }
            if (excludeLastColumn) tableList.forEach(list -> list.remove(list.size() - 1));
            return tableList;
        } finally {
            log.info(String.format("Get table cells data by Jsoup, time = %d", System.currentTimeMillis() - start));
        }
    }

    public static List<List<String>> getTableCells(WebElement table) {
        return getTableCells(table, true);
    }

    public static List<List<String>> getTableCells(WebElement table, List<WebElement> columns) {
        log.info("Start getting table cells data by Jsoup");
        long start = System.currentTimeMillis();
        try {
            String tableContent = table.getAttribute("innerHTML");
            Document tableElement = Jsoup.parse(tableContent);
            List<List<String>> tableList = new ArrayList<>(columns.size());
            for (WebElement column : columns) {
                Elements cells = tableElement.select(String.format("tbody > tr > td:nth-of-type(%s)",
                        column.getAttribute("cellIndex")));
                List<String> cellList = new ArrayList<>();
                cells.forEach(cell -> cellList.add(cell.text()));
                tableList.add(cellList);
            }
            return tableList;
        } finally {
            log.info(String.format("Get table cells data by Jsoup, time = %d", System.currentTimeMillis() - start));
        }
    }

    public static List<List<String>> getTableCellsByFields(WebElement table, List<MetaData> fields) {
        log.info("Start getting table cells data by Jsoup");
        long start = System.currentTimeMillis();
        try {
            String tableContent = table.getAttribute("innerHTML");
            Document tableElement = Jsoup.parse(tableContent);
            List<List<String>> tableList = new ArrayList<>(fields.size());
            for (MetaData column : fields) {
                Elements cells = tableElement.select(String.format("tbody > tr > td:eq(%s)",
                        column.getIndex()));
                List<String> cellList = new ArrayList<>();
                cells.forEach(cell -> cellList.add(cell.text()));
                tableList.add(cellList);
            }
            return tableList;
        } finally {
            log.info(String.format("Get table cells data by Jsoup, time = %d", System.currentTimeMillis() - start));
        }
    }

    public static List<String> getTableCellsByFields_(WebElement table, List<MetaData> fields) {
        log.info("Start getting table cells data by Jsoup");
        long start = System.currentTimeMillis();
        try {
            String tableContent = table.getAttribute("innerHTML");
            Document tableElement = Jsoup.parse(tableContent);
            List<String> tableList = new ArrayList<>(fields.size());
            for (MetaData column : fields) {
                Elements cells = tableElement.select(String.format("tbody > tr > td:eq(%s)",
                        column.getIndex()));
                cells.forEach(cell -> tableList.add(cell.text()));
            }
            return tableList;
        } finally {
            log.info(String.format("Get table cells data by Jsoup, time = %d", System.currentTimeMillis() - start));
        }
    }

    public static void waitPageLoaded() {
        ElementsHelper helper = HelperSingleton.getHelper();
        long startTime = System.currentTimeMillis();
        int width = 100;
        while (TimeUnit.SECONDS.convert(System.currentTimeMillis() - startTime, TimeUnit.MILLISECONDS) < 10 && width != 0) {
            Document page = Jsoup.parse(helper.getPageSource());
            Element progressBar = page.getElementById("ngProgress");
            width = Integer.parseInt(DataHelper.remainDigits(progressBar.attr("width")));
            helper.pause(500);
        }

    }
}
