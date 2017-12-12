package dto;

/**
 * Created by kgr on 2/8/2017.
 */

/**
 * implement own constructor, formatter etc
 */
public class TestDataException extends RuntimeException {

    public TestDataException(String message) {
        super(message);
        TestDataError.collect(message);
    }

    public TestDataException(String message, Exception cause) {
        super(message, cause, true, true);
        TestDataError.collect(cause);
    }

    public TestDataException(Exception cause) {
        super(cause.getMessage(), cause, true, true);
        TestDataError.collect(cause);
    }
}
