package px.logs;

import configuration.helpers.DataHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static config.Config.startDate;
import static configuration.helpers.DataHelper.*;

/**
 * Created by konstantin on 24.09.2017.
 */
public class UILogsParser {
        private static final String LOG_FOLDER = "C:\\Windows\\System32\\LogFiles\\PXUI\\";
//    private static final String LOG_FOLDER = "C:\\Users\\kgr\\Desktop\\";
    private static final String TEMPLATES_DIR = "./src/test/resources/templates/ui-logs/";
    private static final String OUTPUT_DIR = "./target/ui-logs/";
    private static final String OUTPUT_XML = "./target/ui-logs/index.xml";
    // default logs
    private static String BRANCH_NAME = "development-ui";
    private static String DATE_NAME = getDateByFormatSimple(PX_REPORT_DATE_PATTERN, new Date());
    // patters
    private final static Pattern EXCEPTION_PATTERN = Pattern.compile("Exception\\b:");
    private final static Pattern NEWlINE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}");
    // filters
    private static FileFilter BRANCHES_FILTER = branch -> branch.isDirectory() && branch.getName().equals(BRANCH_NAME);
    private static FileFilter LOG_FILES_FILTER = log -> log.isFile() && log.getName().contains(DATE_NAME);

    private static void setNames() {
        if (System.getProperty("test.date") != null && !System.getProperty("test.date").isEmpty()) {
            try {
                startDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, System.getProperty("test.date"));
            } catch (RuntimeException ignored) {
            }
        }
        DATE_NAME = startDate != null ? System.getProperty("test.date") : DATE_NAME;
        BRANCH_NAME = getSystemPropertyValue("test.branch", BRANCH_NAME);
        // BRANCH_NAME passed as url in other jobs
        if (BRANCH_NAME.startsWith("http"))
            BRANCH_NAME = BRANCH_NAME.substring(0, BRANCH_NAME.indexOf(".")).replaceAll("http://|https://", "");
    }

    // currently just 1 branch - 1 name (date)
    public static List<Record> readLogs() {
        setNames();
        File logsRoot = new File(LOG_FOLDER);
        List<Record> allRecords = new ArrayList<>();
        try {
            List<File> folders = Arrays.asList(logsRoot.listFiles(BRANCHES_FILTER));
            for (File folder : folders) {
                try {
                    List<File> logFiles = Arrays.asList(folder.listFiles(LOG_FILES_FILTER));
                    logFiles.forEach(log -> {
                        try {
                            allRecords.addAll(readLog(folder.getName(), log));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (NullPointerException e) {
                    throw new NullPointerException("No log files in folder = " + folder.getAbsolutePath());
                }
            }
        } catch (NullPointerException e) {
            throw new NullPointerException("No folders by BRANCH_NAME = " + BRANCH_NAME);
        }
        return allRecords;
    }

    public static List<Record> readLog(String branch, File log) throws IOException {
        long start = System.currentTimeMillis();
        BufferedReader reader = new BufferedReader(new FileReader(log));
        String inputLine;
        StringBuilder buffer = new StringBuilder();
        // read file - ability to operate indexes
        while ((inputLine = reader.readLine()) != null) {
            buffer.append(inputLine).append("\n");
        }
        // from buffer to list - topical records starting from yyyy-MM-dd
        List<String> lines = new ArrayList<>(Arrays.asList(buffer.toString().split("\n" + NEWlINE_PATTERN.toString().substring(1))));
        // parse list to records
        List<Record> exceptions = new ArrayList<>();
        for (int rowIndex = 0; rowIndex < lines.size(); rowIndex++) {
            String line = lines.get(rowIndex);
            // find exception
            Matcher matcher = EXCEPTION_PATTERN.matcher(line);
            if (matcher.find()) {
                // 4 or 5
                String[] fields = line.split("\\|");
//                int endMatch = matcher.end();
                try {
                    int endMatch = fields[3].indexOf(':');
                    String details = endMatch == -1 && fields.length > 6 ? fields[4] : fields[3];
                    String exception = details.substring(0, endMatch);
                    String stackTrace = line.substring(line.indexOf(exception) + exception.length() + 1);
                    String prevSteps = StringUtils.join(lines.subList(rowIndex - 4 < 0 ? 0 : rowIndex - 4, rowIndex - 1), '\n');
                    Record record = new Record.Builder()
                            .withDate(fields[0])
                            .withType(fields[1])
                            .withName(exception)
                            .withStackTrace(stackTrace)
                            .withPreviousSteps(prevSteps)
                            .build();
                    exceptions.add(record);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(String.format("Another exception format\tline = %s\nfields = %s", line, Arrays.toString(fields)));
                }
            }
        }
        // copy to output folder
        FileUtils.copyFileToDirectory(log, new File(OUTPUT_DIR));
        System.out.println(String.format("Time to parse log file %s size of %d KB = %d ms", log.getName(), log.length() / 1024, System.currentTimeMillis() - start));
        return exceptions;
    }

    public static void writeToReport(List<Record> records) {
        long start = System.currentTimeMillis();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // use factory to get an instance of document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // create instance of DOM
            Document document = db.newDocument();
            // create the root element
            Element root = document.createElement("Records");
            root.setAttribute("branch", BRANCH_NAME);
            root.setAttribute("log", DATE_NAME);
            records.forEach(item -> {
                // record element
                Element record = document.createElement("Record");
                Element time = document.createElement("Time");
                time.setTextContent(item.getTime());
                Element type = document.createElement("Type");
                type.setTextContent(item.getType());
                Element name = document.createElement("Name");
                name.setTextContent(item.getName());
                Element stackTrace = document.createElement("StackTrace");
                stackTrace.setTextContent(item.getStackTrace());
                Element prevSteps = document.createElement("PreviousSteps");
                prevSteps.setTextContent(item.getPreviousSteps());
                // append to record
                record.appendChild(time);
                record.appendChild(type);
                record.appendChild(name);
                record.appendChild(stackTrace);
                record.appendChild(prevSteps);
                // append records
                root.appendChild(record);
            });
            // append root
            document.appendChild(root);
            // add xsl
            Node pi = document.createProcessingInstruction("xml-stylesheet", "type=\"text/xsl\" href=\"index.xsl\"");
            document.insertBefore(pi, root);
            // write to file
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                // send DOM to file
                tr.transform(new DOMSource(document),
                        new StreamResult(new FileOutputStream(OUTPUT_XML)));

            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        } finally {
            System.out.println(String.format("Time to write xml with %d records = %d ms", records.size(), System.currentTimeMillis() - start));
        }
    }

    public static void copyTemplates() {
        try {
            File reporterDir = new File(OUTPUT_DIR);
            if (!reporterDir.exists()) reporterDir.mkdir();
            File[] templates = new File(TEMPLATES_DIR).listFiles();
            for (File template : templates) {
                FileUtils.copyFileToDirectory(template, reporterDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
      /*  System.setProperty("test.branch", "stage03-ui");
        System.setProperty("test.branch", "http://der-ui.stagingpx.com/");*/
        System.setProperty("test.date", "2017-11-02");
        setNames();
        copyTemplates();
        writeToReport(readLogs());
    }
}
