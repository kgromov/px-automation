package config;

import configuration.helpers.DataHelper;
import utils.FailListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TreeSet;
import java.util.regex.Pattern;

import static configuration.helpers.DataHelper.getSystemPropertyValue;

/**
 * Created by kgr on 9/29/2016.
 */
public class Config {
    public static final Locale LOCALE = new Locale(System.getProperty("user.language"), System.getProperty("user.country"));
    // browser's settings
    public static final int MOBILE_WIDTH = 390;
    public static final int MOBILE_HEIGHT = 756;
    public static final int DESKTOP_WIDTH = 1040;
    public static final int DESKTOP_HEIGHT = 784;
    public static final int IMPLICITLY_WAIT = 10;
    public static final int DEFAULT_TIMEOUT = 10;
    public static final int DEFAULT_SCRIPT_TIMEOUT = 20;
    // test environment settings
    public static String testUrl;
    public static String user;
    public static String password;
    public static String userGroup;
    // lead insert
    public static String apiURL; // set the same as testURL
    // report start date
    public static Date startDate;
    public static int daysRange = -1;
    // html reports
    public static final String PATH_TO_REPORTS = "target/surefire-reports/html/";
    public static final String WD_SCREEN = "screens-wd/";
    public static final String FULL_SCREEN = "screens-full/";
    public static final String PATH_TO_SCREENS_WD = PATH_TO_REPORTS + WD_SCREEN;
    public static final String PATH_TO_SCREENS_FULL = PATH_TO_REPORTS + FULL_SCREEN;

    public static void setTestURL(String url) {
//        System.setProperty("test.branch", "http://sprint_116-ui.stagingpx.com/");
        // set methods - errors map
        FailListener.METHOD_ERROR_MAP = new HashMap<>();
        FailListener.METHODS_SET = new TreeSet<>();
        // system properties and global test parameters
        System.out.println("Locale\t" + LOCALE);
        System.out.println("\tCredentials:"
                + "\npx.user.group = " + System.getProperty("px.user.group")
                + "\npx.custom.user.name = " + System.getProperty("px.custom.user.name")
                + "\npx.custom.user.password = " + System.getProperty("px.custom.user.password")
                + "\n\tEnvironment:"
                + "\ntest.environment = " + System.getProperty("test.environment")
                + "\napi.environment = " + System.getProperty("api.environment")
                + "\ntest.branch = " + System.getProperty("test.branch")
                + "\n\tReport variables:"
                + "\ntest.date = " + System.getProperty("test.date")
                + "\ntest.range = " + System.getProperty("test.range")
        );
        // set start date for reports from job parameter
        if (System.getProperty("test.date") != null && !System.getProperty("test.date").isEmpty()) {
            try {
                startDate = DataHelper.getDateByFormatSimple(DataHelper.PX_REPORT_DATE_PATTERN, System.getProperty("test.date"));
            } catch (RuntimeException ignored) {
            }
        }
        // set maximum days range
        if (System.getProperty("test.range") != null && !System.getProperty("test.range").isEmpty()) {
            try {
                daysRange = Pattern.compile("(?!.*(^0))\\d{0,2}").matcher(System.getProperty("test.range")).matches() ? Integer.parseInt(System.getProperty("test.range")) : -1;
            } catch (NumberFormatException ignored) {
            }
        }
        /* test branch - highest priority
         * than url from suite
         * than test environment url
         */
        testUrl = getSystemPropertyValue("test.branch", (url != null && !url.isEmpty()) ? url : System.getProperty("test.environment"));
        apiURL = getSystemPropertyValue("test.api.environment", System.getProperty("api.environment"));
        if (!testUrl.endsWith("/")) testUrl += "/";
        System.out.println("Test environment\ttestUrl = " + testUrl);
        // override credentials by Jenkins ones if exist
        user = getSystemPropertyValue("px.custom.user.name", System.getProperty("px.user.name"));
        password = getSystemPropertyValue("px.custom.user.password", System.getProperty("px.user.password"));
        userGroup = getSystemPropertyValue("px.user.group", UserRoleEnum.ADMIN.name()).toUpperCase();
        System.out.println(String.format("Credentials:\tlogin/password = %s/%s", user, password));
//        setBuyerUser();
    }

    public static boolean isBetaEnvironment() {
        return testUrl.contains("beta.") || testUrl.contains("demo.");
    }

    public static boolean isNewStagingEnvironment() {
        return testUrl.contains("stagingpx.");
    }

    // for convenience
    private static void setTestEnvironment(){
        if(System.getProperty("test.environment") == null || System.getProperty("test.environment").isEmpty())
            System.setProperty("test.environment", "http://development-ui.stagingpx.com/");
        if(System.getProperty("api.environment") == null || System.getProperty("api.environment").isEmpty())
            System.setProperty("api.environment", "http://api.stage02.stagingpx.com/");
    }

    public static void setAdminUser() {
        setTestEnvironment();
        userGroup = UserRoleEnum.ADMIN.name();
        user = "automation@qa.px";
        password = "123456";
        testUrl = getSystemPropertyValue("test.branch", System.getProperty("test.environment"));
        apiURL = getSystemPropertyValue("test.api.environment", System.getProperty("api.environment"));
        if (!testUrl.endsWith("/")) testUrl += "/";
    }

    public static void setBuyerUser() {
        setTestEnvironment();
        userGroup = UserRoleEnum.BUYER.name();
        user = "clemens@stagingbuyer.com";
        password = "clemens";
        testUrl = getSystemPropertyValue("test.branch", System.getProperty("test.environment"));
        apiURL = getSystemPropertyValue("test.api.environment", System.getProperty("api.environment"));
        if (!testUrl.endsWith("/")) testUrl += "/";
    }

    public static void setPublisherUser() {
        setTestEnvironment();
        userGroup = UserRoleEnum.PUBLISHER.name();
        user = "autumn@stagingpublisher.com";
        password = "autumn";
        user = "auto@stagingpublisher.com";
        password = "123456";
        testUrl = getSystemPropertyValue("test.branch", System.getProperty("test.environment"));
        apiURL = getSystemPropertyValue("test.api.environment", System.getProperty("api.environment"));
        if (!testUrl.endsWith("/")) testUrl += "/";
    }

    public static boolean isAdmin() {
        return UserRoleEnum.ADMIN.name().equals(userGroup.toUpperCase());
    }

    public static boolean isBuyer() {
        return UserRoleEnum.BUYER.name().equals(userGroup.toUpperCase());
    }

    public static boolean isPublisher() {
        return UserRoleEnum.PUBLISHER.name().equals(userGroup.toUpperCase());
    }

    public static String userToString() {
        return String.format("User = '%s' with '%s' grants", user, userGroup);
    }
}