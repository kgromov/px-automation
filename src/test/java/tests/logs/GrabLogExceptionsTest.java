package tests.logs;

import org.testng.annotations.Test;

import static px.logs.UILogsParser.copyTemplates;
import static px.logs.UILogsParser.readLogs;
import static px.logs.UILogsParser.writeToReport;

/**
 * Created by kgr on 10/12/2017.
 */
public class GrabLogExceptionsTest {

    @Test
    public void grabExceptions(){
//        System.setProperty("test.date", "2017-09-17");
        copyTemplates();
        writeToReport(readLogs());
    }
}
