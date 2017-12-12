package utils;

import dto.TestDataError;
import dto.TestDataException;
import org.testng.*;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by kgr on 2/17/2017.
 */
//TestListenerAdapter
public class FailListener implements ITestListener, IConfigurationListener {
    public static Map<Integer, Exception> METHOD_ERROR_MAP;
    public static Set<String> METHODS_SET;
    public static int OWN_INVOCATION_COUNT;
    public static String CURRENT_METHOD_NAME = "";

    private int getInvocationCount(ITestNGMethod method) {
        return method.getInvocationCount() > 1 ? method.getCurrentInvocationCount() : OWN_INVOCATION_COUNT;
    }

    @Override
    public void onTestStart(ITestResult itr) {
        if (METHODS_SET == null) METHODS_SET = new TreeSet<>();
        METHODS_SET.add(itr.getMethod().getMethodName() + getInvocationCount(itr.getMethod()));
        if (TestDataError.isAnyTestDataExceptions()) itr.setStatus(ITestResult.FAILURE);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (TestDataError.isAnyTestDataExceptions()) {
            result.setThrowable(METHOD_ERROR_MAP.containsKey(OWN_INVOCATION_COUNT)
                    ? new TestDataException(METHOD_ERROR_MAP.get(OWN_INVOCATION_COUNT))
                    : new TestDataException(TestDataError.e));
//            METHOD_ERROR_MAP.remove(getInvocationCount(result.getMethod()));
//            result.setThrowable(new TestDataException(TestDataError.getMessage()));
//            TestDataError.clearErrors();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (TestDataError.isAnyTestDataExceptions()) {
            result.setThrowable(METHOD_ERROR_MAP.containsKey(OWN_INVOCATION_COUNT)
                    ? new TestDataException(METHOD_ERROR_MAP.get(OWN_INVOCATION_COUNT))
                    : new TestDataException(TestDataError.e));
//            METHOD_ERROR_MAP.remove(getInvocationCount(result.getMethod()));
//            result.setThrowable(new TestDataException(TestDataError.getMessage()));
//            TestDataError.clearErrors();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {

    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
    }
}
