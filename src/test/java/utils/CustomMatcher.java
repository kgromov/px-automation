package utils;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.text.IsEqualIgnoringCase;

import java.util.regex.Pattern;

/**
 * Created by kgr on 2/24/2017.
 */
public class CustomMatcher {

    public static org.hamcrest.Matcher<String> containsStringToIgnoreCase(String substring) {
        return ContainsStringToIgnoreCase.containsStringToIgnoreCase(substring);
    }

    public static org.hamcrest.Matcher<String> equalsIgnoreBlankets(String expectedString) {
        return EqualsIgnoreBlankets.equalsIgnoreBlankets(expectedString);
    }

    public static org.hamcrest.Matcher<String> equalsIgnoreBlanketsAndNewLines(String expectedString) {
        return EqualsIgnoreBlanketsAndNewLines.equalsIgnoreBlanketsAndNewLines(expectedString);
    }

    public static org.hamcrest.Matcher<String> containsStringToIgnoreCaseAndBlanketInAnyOrder(String expectedString) {
        return ContainsStringToIgnoreCaseAndBlanketInAnyOrder.containsStringToIgnoreCaseAndBlanketInAnyOrder(expectedString);
    }

    public static org.hamcrest.Matcher<String> matchesPattern(String pattern) {
        return MatchesPattern.matchesPattern(pattern);
    }

    public static org.hamcrest.Matcher<String> matchesPattern(String expectedString, String pattern) {
        return MatchesPattern.matchesPattern(expectedString, pattern);
    }

    public static org.hamcrest.Matcher<String> equalsToEscapeSpace(String expectedString) {
        return EqualsToEscapeSpace.equalsToEscapeSpace(expectedString);
    }

    private static class ContainsStringToIgnoreCase extends IsEqualIgnoringCase {
        private final String string;

        ContainsStringToIgnoreCase(String string) {
            super(string);
            this.string = string;
        }

        @Override
        public boolean matchesSafely(String item) {
            return string.toLowerCase().contains(item.toLowerCase());
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("containsStringToIgnoreCase(")
                    .appendValue(string)
                    .appendText(")");
        }

        @Factory
        static Matcher<String> containsStringToIgnoreCase(String expectedString) {
            return new ContainsStringToIgnoreCase(expectedString);
        }
    }

    private static class ContainsStringToIgnoreCaseAndBlanketInAnyOrder extends IsEqualIgnoringCase {
        private final String string;

        ContainsStringToIgnoreCaseAndBlanketInAnyOrder(String string) {
            super(string);
            this.string = string;
        }

        @Override
        public boolean matchesSafely(String item) {
            String str1 = string.toLowerCase().replaceAll("\\s", "");
            String str2 = item.toLowerCase().replaceAll("\\s", "");
            return str1.contains(str2) || str2.contains(str1);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("containsStringToIgnoreCaseAndBlanket(")
                    .appendValue(string)
                    .appendText(")");
        }

        @Factory
        static Matcher<String> containsStringToIgnoreCaseAndBlanketInAnyOrder(String expectedString) {
            return new ContainsStringToIgnoreCaseAndBlanketInAnyOrder(expectedString);
        }
    }

    private static class EqualsIgnoreBlankets extends IsEqualIgnoringCase {
        private final String string;

        EqualsIgnoreBlankets(String string) {
            super(string);
            this.string = string;
        }

        @Override
        public boolean matchesSafely(String item) {
            return string.replaceAll("\\s", "").equalsIgnoreCase(item.replaceAll("\\s", ""));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("equalsIgnoreBlankets(")
                    .appendValue(string)
                    .appendText(")");
        }

        @Factory
        static Matcher<String> equalsIgnoreBlankets(String expectedString) {
            return new EqualsIgnoreBlankets(expectedString);
        }
    }

    private static class EqualsIgnoreBlanketsAndNewLines extends IsEqualIgnoringCase {
        private final String string;

        EqualsIgnoreBlanketsAndNewLines(String string) {
            super(string);
            this.string = string;
        }

        @Override
        public boolean matchesSafely(String item) {
            return string.replaceAll("\\s|\n|\t|\r", "").equalsIgnoreCase(item.replaceAll("\\s|\n|\t|\r", ""));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("equalsIgnoreBlankets(")
                    .appendValue(string)
                    .appendText(")");
        }

        @Factory
        static Matcher<String> equalsIgnoreBlanketsAndNewLines(String expectedString) {
            return new EqualsIgnoreBlanketsAndNewLines(expectedString);
        }
    }

    private static class EqualsToEscapeSpace extends IsEqualIgnoringCase {
        private final String string;

        EqualsToEscapeSpace(String string) {
            super(string);
            this.string = string;
        }

        @Override
        public boolean matchesSafely(String item) {
            return string.replaceAll("\\s{2,}|\t|\u00A0", " ")
                    .equalsIgnoreCase(item.replaceAll("\\s{2,}|\t|\u00A0", " "));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("equalsToEscapeSpace(")
                    .appendValue(string)
                    .appendText(")");
        }

        @Factory
        static Matcher<String> equalsToEscapeSpace(String expectedString) {
            return new EqualsToEscapeSpace(expectedString);
        }
    }

    private static class MatchesPattern extends IsEqualIgnoringCase {
        private String expectedString;
        private final Pattern pattern;

        MatchesPattern(String string, String pattern) {
            super(string);
            this.expectedString = string;
            this.pattern = Pattern.compile(pattern);
        }

        MatchesPattern(String string) {
            super(string);
            this.pattern = Pattern.compile(string);
        }

        @Override
        public boolean matchesSafely(String item) {
            return pattern.matcher(item).matches();
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(expectedString != null
                    ? expectedString + " matchesPattern("
                    : "" + "matchesPattern(")
                    .appendValue(pattern.toString())
                    .appendText(")");
        }

        @Factory
        static Matcher<String> matchesPattern(String pattern) {
            return new MatchesPattern(pattern);
        }

        @Factory
        static Matcher<String> matchesPattern(String expectedString, String pattern) {
            return new MatchesPattern(expectedString, pattern);
        }
    }

}
