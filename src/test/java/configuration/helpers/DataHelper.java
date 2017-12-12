package configuration.helpers;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import px.reports.Valued;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static config.Config.LOCALE;

/**
 * Created by kgr on 10/4/2016.
 */
public class DataHelper {
    private static final Logger log = Logger.getLogger(DataHelper.class);
    public static final String DATE_TIME_REPORT_PATTERN = "yyyy-MM-dd_hh-mm-ss";
    public static final String EXPIRATION_DATE_PATTERN = "MMMM d yyyy, h:mm:ss a";
    public static final String EXPIRATION_DATE_ONLY_PATTERN = "MMMM d yyyy";
    public static final String CALENDAR_TABLE_DATE_PATTERN = "MM/dd/yyyy";
    public static final String CALENDAR_TABLE_TIME_PATTERN = "HH:mm:ss";
    public static final String CALENDAR_TABLE_TIME_PATTERN_AM = "hh:mm:ss a";
    public static final String PX_REPORT_DATE_PATTERN = "yyyy-MM-dd";
    public static final String PX_REPORT_MONTH_PATTERN = "MMM yyyy";
    public static final String PX_INBOUND_REPORT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String LEADS_REPORT_PATTERN_IN = "yyyy-MM-dd HH:mm:ss.S";
    public static final String LEADS_REPORT_PATTERN_OUT = "MMMM d yyyy, HH:mm:ss a";
    public static final String BIRTH_DATE_DATA_PATTERN = "MMM d, yyyy";
    public static final String PERIOD_MONTH_PATTERN = "MMMM yyyy";
    private static final Pattern TAG_REGEX = Pattern.compile("<w+>(.+?)</w+>");
    //  private static fina Pattern pattern = Pattern.compile("<(\\w+)( +.+)*></\\1>");
    private static final String SPECIFIC_TAG_REGEX = "<%s+>(.+?)</%s+>";

    //    private static final char FRACTION_SPLITTER = String.valueOf(new Float(0)).replaceAll("\\d", "").charAt(0);
    public static final char FRACTION_SPLITTER = DecimalFormatSymbols.getInstance(LOCALE).getDecimalSeparator();
    public static final char GROUPING_SPLITTER = DecimalFormatSymbols.getInstance(LOCALE).getGroupingSeparator();

    // ===================================== Date and Time =====================================
    public static String getCurrentDateFileFormat() {
        return new SimpleDateFormat(DATE_TIME_REPORT_PATTERN, Locale.US).format(new Date());
    }

    public static String getDateByFormatSimple(String pattern) {
        return new SimpleDateFormat(pattern, Locale.US).format(new Date());
    }

    public static String getDateByFormatSimple(String pattern, long time) {
        return new SimpleDateFormat(pattern, Locale.US).format(new Date(time));
    }

    public static String getDateByFormatSimple(String pattern, Date date) {
        return new SimpleDateFormat(pattern, Locale.US).format(date);
    }

    public static Date getDateByFormatSimple(String pattern, String source) {
        try {
            return new SimpleDateFormat(pattern, Locale.US).parse(source);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Unable to parse date from '%s' by pattern '%s'", source, pattern));
        }
    }

    // 2 march 2015 => 2nd march 2015
    public static String getFormattedDate(Date date, String outPattern) {
        switch (getDayInDate(date) % 10) {
            case 1:
                return new SimpleDateFormat("d'st' MMMM yyyy").format(date);
            case 2:
                return new SimpleDateFormat("d'nd' MMMM yyyy").format(date);
            case 3:
                return new SimpleDateFormat("d'rd' MMMM yyyy").format(date);
            default:
                return new SimpleDateFormat("d'th' MMMM yyyy").format(date);
        }
    }

    public static String getUpdatedPatternWithDayOrder(Date date, String pattern) {
        switch (getDayInDate(date) % 10) {
            case 1:
                return pattern.replace("th", "st");
            case 2:
                return pattern.replace("th", "nd");
            case 3:
                return pattern.replace("th", "rd");
            default:
                return pattern;
        }
    }

    public static Date getTimeByDate(String pattern, Date date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
            String time = dateFormat.format(date);
            return new SimpleDateFormat(pattern).parse(time);
        } catch (ParseException e) {
            throw new RuntimeException(String.format("Unable to parse date from '%s' by pattern '%s'", date, pattern));
        }
    }

    public static Date getDateByYearOffset(int offset) {
        return getDateByOffset(new Date(), Calendar.YEAR, offset);
    }

    public static Date getDateByMonthOffset(int offset) {
        return getDateByOffset(new Date(), Calendar.MONTH, offset);
    }

    public static Date getDateByDaysOffset(int offset) {
        return getDateByOffset(new Date(), Calendar.DAY_OF_WEEK, offset);
    }

    public static Date getDateByDaysOffset(Date date, int offset) {
        return getDateByOffset(date, Calendar.DAY_OF_WEEK, offset);
    }

    public static Date getDateByHourOffset(Date date, int offset) {
        return getDateByOffset(date, Calendar.HOUR, offset);
    }

    public static Date getDateByOffset(int months, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_WEEK, days);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }

    public static int getMonthOffset(Date startDate) {
        return getMonthOffset(startDate, new Date());
    }

    public static int getMonthOffset(Date startDate, Date endDate) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);
        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        return diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
    }

    public static int getLastActualDayOfMonth(int monthOffset) {
        Calendar calendar = Calendar.getInstance();
        if (monthOffset == 0)
            return calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(getDateByMonthOffset(monthOffset));
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static int getDayInDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonthInDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    public static int getYearInDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static Date setDay(final Date date, final int amount) {
        return setDate(date, Calendar.DAY_OF_MONTH, amount);
    }

    private static Date setDate(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.setTime(date);
        calendar.set(calendarField, amount);
        return calendar.getTime();
    }

    private static Date getDateByOffset(final Date date, final int calendarField, final int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.setTime(date);
        calendar.add(calendarField, amount);
        return calendar.getTime();
    }

    // ===================================== Randomize data =====================================
    public static boolean getRandBoolean() {
        return new Random().nextBoolean();
    }

    public static int getRandomInt(int max) {
        return new Random().nextInt(max);
    }

    public static int getRandomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }

    public static long getRandomInt(long min, long max, long step) {
        List<Long> list = new ArrayList<>();
        for (long i = min; i <= max; i += step) {
            list.add(i);
        }
        return list.get((new Random()).nextInt(list.size()));
    }

    public static int getRandomInt(int min, int max, int step) {
        List<Integer> list = new ArrayList<>();
        for (int i = min; i <= max; i += step) {
            list.add(i);
        }
        return list.get((new Random()).nextInt(list.size()));
    }

    public static String getRandomValueFromList(String[] array) {
        return array[new Random().nextInt(array.length)];
    }

    public static String getRandomValueFromList(List<String> list) {
        return list.get(new Random().nextInt(list.size()));
    }

    public static List<Integer> getRandUniqueListFromRange(int min, int max, int size) {
        if (size > max - min + 1)
            throw new NumberFormatException(String.format("Size '%d' of unique list could not be greater than max value '%d'", size, max));
        List<Integer> list = new ArrayList<>();
        while (list.size() < size) {
            int index = getRandomInt(min, max);
            if (list.contains(index)) continue;
            list.add(index);
        }
        Collections.sort(list);
        return list;
    }

    public static int getRandomIndexFromList(List<String> list) {
        int index = new Random().nextInt(list.size());
        return index == 0 ? 1 : index;
    }

    public static List<String> getRandomListFromList(List<String> fullList) {
        int size = getRandomIndexFromList(fullList);
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String value = getRandomValueFromList(fullList);
            if (!list.contains(value)) {
                list.add(value);
            }
        }
        return list;
    }

    public static List<String> getRandomListFromList(List<String> fullList, int size) {
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String value = getRandomValueFromList(fullList);
            if (!list.contains(value)) {
                list.add(value);
            }
        }
        return list;
    }

    public static String getRandomValueFromRange(int start, int end, int step) {
        List<Integer> list = new ArrayList<>();
        for (int i = start; i <= end; i += step) {
            list.add(i);
        }
        return Integer.toString(list.get((new Random()).nextInt(list.size())));
    }

    public static String remainDigits(String value) {
        return value.replaceAll("[^\\d]", "");
    }

    public static String getArrayAsString(List<String> list) {
        String[] tempArray = list.toString().split(", ");
        return StringUtils.join(tempArray).replaceAll("(^\\[)|(\\]$)", "");
    }

    public static List<String> getStringAsArray(String source) {
        String temp = source.replaceAll("(^\\[)|(\\]$)", "");
        return Arrays.asList(temp.split(", "));
    }

    public static String getRandomYesNo() {
        String[] list = {"Yes", "No"};
        return list[new Random().nextInt(list.length)];
    }

    // ===================================== filters =====================================
    // add in future getKeyByValue (keySet)
    public static List<String> getListByValue(Collection<String> list, String value) {
        return list.stream()
                .filter(value::equals)
                .collect(Collectors.toList());
    }

    public static List<String> getListByValue(Collection<String> list, Collection<String> inclusionList) {
        return list.stream()
                .filter(inclusionList::contains)
                .collect(Collectors.toList());
    }

    public static List<String> getListNotByValue(Collection<String> list, String value) {
        return list.stream()
                .filter(item -> !item.contains(value))
                .collect(Collectors.toList());
    }

    public static List<String> getListNotByValue(Collection<String> list, Collection<String> exclusionList) {
        return list.stream()
                .filter(item -> !exclusionList.contains(item))
                .collect(Collectors.toList());
    }

    public static Map<String, String> getMapContainsInKey(Map<String, String> map, String subKey) {
        return map.entrySet().stream()
                .filter(entry -> entry.getKey().contains(subKey))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<String, String> getMapContainsInValue(Map<String, String> map, String subValue) {
        return map.entrySet().stream()
                .filter(entry -> entry.getValue().contains(subValue))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static String getKeyByValue(Map<String, String> map, String value) {
        String result = map.entrySet().stream()
                .filter(entry -> value.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining());
        return result.isEmpty() ? null : result;
    }

    public static String getKeyByValueIgnoreCase(Map<String, String> map, String value) {
        String result = map.entrySet().stream()
                .filter(entry -> value.equalsIgnoreCase(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining());
        return result.isEmpty() ? null : result;
    }

    // ===================================== some objects specific =====================================
    public static String getSystemPropertyValue(String propertyKey, String defaultValue) {
        Properties properties = System.getProperties();
        String value = properties.getProperty(propertyKey);
        return value != null && !value.isEmpty() ? value : defaultValue;
    }

    public static String getYesNo(String value) {
        try {
            return Integer.parseInt(value) == 1 ? "Yes" : "No";
        } catch (NumberFormatException e) {
            return value;
        }
    }

    public static String getRoundedFloat(String value) {
        return getRoundedFloat(value, 0);
    }

    public static String getSplittedByComma(String value, int accuracy) {
        try {
//            return String.format("%" + GROUPING_SPLITTER + "." + accuracy + "f", Float.parseFloat(getRoundedFloat(value, accuracy)));
            return String.format("%" + GROUPING_SPLITTER + "." + accuracy + "f", new BigDecimal(getRoundedFloat(value, accuracy)).doubleValue());
        } catch (UnknownFormatConversionException | NumberFormatException e) {
            return value;
        }
    }

    public static String getSplittedNumber(String value) {
        try {
            return DecimalFormat.getInstance().format(new BigDecimal(value).longValue());
        } catch (ArithmeticException | NumberFormatException e) {
            return value;
        }
    }

    public static String getRoundedFloat(String value, int accuracy) {
        try {
           /* log.info(String.format("Round %s, FRACTION_SPLITTER = '%s'", value, FRACTION_SPLITTER));
            log.info(String.format("Round %s, GROUPING_SPLITTER = '%s'", value, GROUPING_SPLITTER));*/
            String nan = value.replaceAll("(\\d*[.,]*\\d*)", "");
            String floatString = value.replace(nan, "");
            return new BigDecimal(floatString.replaceAll("[^\\d]", String.valueOf(FRACTION_SPLITTER))).setScale(accuracy, BigDecimal.ROUND_HALF_UP).toString();
//            return String.format("%." + accuracy + "f", aFloat);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    public static String getRoundedFloatToPatten(String expectedValue) {
        try {
            return String.valueOf(Integer.parseInt(expectedValue));
        } catch (NumberFormatException e) {
            return String.format("[^\\d]{0,2}%s[^\\d]{0,4}", expectedValue.replaceAll("[^\\d]", "[^\\\\d]"));
        }
    }

    public static String getSplittedNumberToPattern(String value) {
        try {
            return getSplittedNumber(value).replaceAll("[^\\d]", "[^\\d]?");
        } catch (ArithmeticException | NumberFormatException e) {
            return value;
        }
    }

    public static double getFloat(String value) {
        return new BigDecimal(value.replaceAll("[^\\d,.-]", "").replaceAll("[.,]", String.valueOf(FRACTION_SPLITTER))).doubleValue();
    }

    public static float getRoundedFloat(float value, int accuracy) {
//        return new BigDecimal(value).setScale(accuracy, RoundingMode.HALF_EVEN).floatValue();
        return new BigDecimal(value).setScale(accuracy, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static List<Integer> getListFromRange(int min, int max) {
        List<Integer> list = new ArrayList<>();
        while (min <= max) {
            list.add(min);
            ++min;
        }
        return list;
    }

    public static List<String> getFilteredEnumValues(Valued[] enums) {
        List<Valued> enumList = new ArrayList<>(Arrays.asList(enums));
        return enumList.stream().map(Valued::getValue).collect(Collectors.toList());
    }

    public static List<String> getFilteredEnumValues(Valued[] enums, String exclude) {
        List<Valued> enumList = new ArrayList<>(Arrays.asList(enums));
        return enumList.stream()
                .filter(anEnum -> !anEnum.name().contains(exclude))
                .map(Valued::getValue).collect(Collectors.toList());
    }

    public static List<String> getFilteredEnumValues(Valued[] enums, List<String> exclusions) {
        List<Valued> enumList = new ArrayList<>(Arrays.asList(enums));
        return enumList.stream()
                .filter(anEnum -> !exclusions.contains(anEnum.name()))
                .map(Valued::getValue).collect(Collectors.toList());
    }

    public static String getTagValue(final String str) {
        try {
            final Matcher matcher = TAG_REGEX.matcher(str);
            if (matcher.find())
//            tagValues.add(matcher.group(1));
                return matcher.group(1);
        } catch (NullPointerException ignored) {
        }
        return null;
    }

    public static String getTagValue(String tag, String input) {
        try {
            final Matcher matcher = Pattern.compile(String.format(SPECIFIC_TAG_REGEX, tag, tag)).matcher(input);
            if (matcher.find())
                return matcher.group(1);
        } catch (NullPointerException ignored) {
        }
        return null;
    }

    public static boolean hasTagValue(final String str) {
        try {
            return TAG_REGEX.matcher(str).find();
        } catch (NullPointerException e) {
            return false;
        }
    }

    public static boolean hasTagValue(String tag, String input) {
        try {
            return Pattern.compile(String.format(SPECIFIC_TAG_REGEX, tag, tag)).matcher(input).find();
        } catch (NullPointerException e) {
            return false;
        }
    }

    // ===================================== url parsers =====================================
    // ! URL CORRECTNESS IS REQUIRED {? or &, duplications etc}
    public static Map<String, String> getParametersMapFromUrl(String url, boolean decode) {
        Map<String, String> map = null;
        if (url.contains("?")) {
            map = new HashMap<>();
            String params = url.substring(url.indexOf("?") + 1).replaceAll("\\#\\w{4,8}\\/\\d{1,2}", "");
            for (String urlParameter : params.split("&")) {
                int indexOfSeparation = urlParameter.indexOf('=');
                String value = urlParameter.substring(indexOfSeparation + 1);
                try {
                    if (decode) {
                        map.put(URLDecoder.decode(urlParameter.substring(0, indexOfSeparation), "UTF-8"),
                                URLDecoder.decode(value, "UTF-8"));
                    } else {
                        map.put(urlParameter.substring(0, indexOfSeparation), value);
                    }
                } catch (UnsupportedEncodingException e) {
                    log.warn("Unable to decode parameter from url '" + urlParameter + "'");
                }
            }
        }
        return map;
    }

    public static String getParameterValueFromUrl(String url, String parameter, boolean decode) {
        Map<String, String> parametersMap = getParametersMapFromUrl(url, decode);
        if (!parametersMap.containsKey(parameter)) {
            log.warn("There is no " + parameter + " parameter in url");
        }
        return parametersMap.get(parameter);
    }

    public static String getUrlParams(String key, String value) {
        return getUrlParams(null, Collections.singletonList(key), Collections.singletonList(value));
    }

    public static String getUrlParams(List<String> keys, List<String> values) {
        return getUrlParams(null, keys, values);
    }

    public static String getUrlParams(String url, List<String> keys, List<String> values) {
        int keysLength = keys.size();
        int valuesLength = values.size();
        if (keysLength != valuesLength)
            throw new Error(String.format("Key-value arrays sizes are different '%d' by filter '%d'", keysLength, valuesLength));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keysLength; i++) {
            builder.append(keys.get(i)).append("=").append(values.get(i));
            if (i < keysLength - 1) builder.append("&");
        }
        return builder.toString();
    }

    public static String getCorrectURL(String url) {
        while (StringUtils.countMatches(url, "?") > 1) {
            String begin = url.substring(0, url.lastIndexOf("?"));
            String end = url.substring(url.lastIndexOf("?") + 1, url.length());
            url = begin + "&" + end;
        }
        if (StringUtils.countMatches(url, "//") == 1) return url;
        if (url.contains("http://") || url.contains("https://")) {
            int siteStart = url.indexOf("://") + 3;
            String protocol = url.substring(0, siteStart);
            return protocol + url.substring(siteStart).replaceAll("/+", "\\/");
        }
        return url.replaceAll("/+", "\\/");
    }

    // ===================================== json =====================================
    public static boolean hasJSONValue(JSONObject jsonObject, String value) {
        for (Map.Entry<String, Object> entry : jsonObject.toMap().entrySet()) {
            if (entry.getValue().equals(value)) return true;
        }
        return false;
    }

    public static String getJoinedValue(JSONObject jsonObject, String... keys) {
        List<String> valuesJSON = new ArrayList<>(keys.length);
        for (String key : keys) {
            valuesJSON.add(String.valueOf(jsonObject.get(key)));
        }
        return StringUtils.join(valuesJSON, " - ");
    }

    public static String getParameterJSONFromURL(String url, String... keys) {
        JSONObject jsonObject = new JSONObject(getParameterValueFromUrl(url, "query", true));
        for (String key : keys) {
            Object result = jsonObject.get(key);
            if (result instanceof JSONObject) {
                jsonObject = jsonObject.getJSONObject(key);
            } else return String.valueOf(result);
        }
        throw new IllegalArgumentException(String.format("Incorrect chain of keys %s\tOriginal JSON:\n%s",
                Arrays.asList(keys), (new JSONObject(getParameterValueFromUrl(url, "query", true)).toString())));
    }

    public static List<String> getValuesJSONFromURL(String url, String groupKey, String... keys) {
        JSONObject jsonObject = new JSONObject(getParameterValueFromUrl(url, "query", true));
        JSONObject group = jsonObject.getJSONObject(groupKey);
        List<String> values = new ArrayList<>(keys.length);
        for (String key : keys)
            values.add(String.valueOf(group.get(key)));
        return values;
    }

    public static Set<String> getSetFromJSONArrayByKey(JSONArray jsonArray, String key) {
        return new HashSet<>(getListFromJSONArrayByKey(jsonArray, key, true));
    }

    public static List<String> getListFromJSONArrayByKey(JSONArray jsonArray, String key) {
        return getListFromJSONArrayByKey(jsonArray, key, true);
    }

    public static List<String> getListFromJSONArrayByKey(JSONArray jsonArray, String key, boolean isRemoveEmpty) {
        List<String> list = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!jsonObject.has(key)) {
//                throw new IllegalArgumentException(String.format(
                log.error(String.format(
                        "Incorrect json\nKey '%s' is missed in JSON element '%s'", key, jsonObject.toString()));
                continue;
            }
            list.add(String.valueOf(jsonObject.get(key)));
        }
        // remove empty keys
        if (isRemoveEmpty) list.remove("");
        return list;
    }

    public static JSONObject getJSONFromJSONArray(JSONArray jsonArray, String key) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.has(key)) {
                return jsonObject.getJSONObject(key);
            }
        }
        return null;
      /*  throw new IllegalArgumentException(String.format(
                "Incorrect json\nKey '%s' is missed in JSON element '%s'", key, jsonArray.toString()));*/
    }

    public static JSONObject getJSONFromJSONArrayByCondition(JSONArray jsonArray, String key, String value) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!jsonObject.has(key)) {
//                throw new IllegalArgumentException(String.format(
                log.error(String.format(
                        "Incorrect json\nKey '%s' is missed in JSON element '%s'", key, jsonObject.toString()));
                continue;
            }
            if (String.valueOf(jsonObject.get(key)).equals(value))
                return jsonObject;
        }
        return null;
    }

    public static List<String> getListFromJSONArrayByKeyByCondition(JSONArray jsonArray, String key, String conditionKey, String conditionValue) {
        List<String> list = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (!jsonObject.has(key)) {
//                throw new IllegalArgumentException(String.format(
                log.error(String.format(
                        "Incorrect json\nKey '%s' is missed in JSON element '%s'", key, jsonObject.toString()));
                continue;
            }
            if (jsonObject.has(conditionKey) && String.valueOf(jsonObject.get(conditionKey)).equals(conditionValue))
                list.add(String.valueOf(jsonObject.get(key)));
        }
        // remove empty keys
        list.remove("");
        return list;
    }

}