package dto;

/**
 * Created by kgr on 2/8/2017.
 */

import utils.FailListener;

/**
 * implement own constructor, formatter etc
 */
public class TestDataError {
    public static Exception e;
    private static String message = "";
    private static boolean isSkippedTest = false;

    public static void collect(String message) {
        TestDataError.message = message == null ? "Test data produce NullPointerException" : message;
        System.err.print("DEBUG:\tTestDataError message = " + TestDataError.message);
        TestDataError.e = new RuntimeException(message);
//        if (!FailListener.CURRENT_METHOD_NAME.isEmpty())
        FailListener.METHOD_ERROR_MAP.put(FailListener.OWN_INVOCATION_COUNT, e);
        isSkippedTest = true;
    }

    // perhaps add condition to put in map only if CURRENT_METHOD_NAME !isEmpty()
    public static void collect(Exception e) {
        TestDataError.e = e;
        TestDataError.message = e.getMessage() == null ? "Test data produce NullPointerException" : e.getMessage();
        System.err.print("DEBUG:\tTestDataError message = " + TestDataError.message);
//        if (!FailListener.CURRENT_METHOD_NAME.isEmpty())
        FailListener.METHOD_ERROR_MAP.put(FailListener.OWN_INVOCATION_COUNT, e);
        isSkippedTest = true;
    }

    public static String getMessage() {
        return message;
    }

    /* split logic for test with looped DP and common DP (with possible invocationCount > 1)
    * 1) by OWN_INVOCATION_COUNT
    * 2) or by CURRENT_METHOD_NAME
     */
    public static boolean isAnyTestDataExceptions() {
//        return !message.isEmpty();
//        return e != null || !message.isEmpty() || !FailListener.METHOD_ERROR_MAP.isEmpty();
        return (!FailListener.CURRENT_METHOD_NAME.isEmpty() && FailListener.METHOD_ERROR_MAP.containsKey(FailListener.OWN_INVOCATION_COUNT))
                || (FailListener.CURRENT_METHOD_NAME.isEmpty() && !message.isEmpty());
    }

    public static boolean isSkippedTest() {
        try {
            return (!FailListener.CURRENT_METHOD_NAME.isEmpty() && FailListener.METHOD_ERROR_MAP.containsKey(FailListener.OWN_INVOCATION_COUNT)) | isSkippedTest;
        } finally {
            isSkippedTest = false;
        }
    }

    public static void clearErrors() {
        e = null;
        message = "";
    }
}