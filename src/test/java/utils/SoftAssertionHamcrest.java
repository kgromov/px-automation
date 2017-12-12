package utils;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

/**
 * Created by kgr on 10/19/2015.
 */
public class SoftAssertionHamcrest {
    private StringBuilder sb = new StringBuilder();

    public <T> void assertThat(T actual, Matcher<? super T> matcher) {
        assertThat("", actual, matcher);
    }

    public <T> void assertThat(String reason, T actual, Matcher<? super T> matcher) {
        boolean result = matcher.matches(actual);
        if (!result) {
            Description description = new StringDescription();
            description.appendText(reason)
                    .appendText("\nExpected: ")
                    .appendDescriptionOf(matcher)
                    .appendText("\n     but: ");
            matcher.describeMismatch(actual, description);

            sb.append(description.toString()).append("\n");
        }
    }

    public void assertThat(String reason, boolean assertion) {
        if (!assertion) {
            sb.append(reason).append("\n");
        }
    }

    public void append(String assertionError) {
        if (!assertionError.isEmpty()) {
            sb.append(assertionError).append("\n");
        }
    }

    public void assertAll() {
        try {
            if (sb.length() > 0) {
                throw new AssertionError(sb.toString());
            }
        } finally {
            sb.setLength(0);
        }
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}