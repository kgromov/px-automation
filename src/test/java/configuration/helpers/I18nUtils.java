package configuration.helpers;

import com.ibm.icu.util.LocaleData;
import com.ibm.icu.util.ULocale;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Created by kgr on 11/1/2016.
 */
public class I18nUtils {
    protected final static Logger log = Logger.getLogger(I18nUtils.class);

    public static List<String> getAlphabetByLocale(String localeName) {
        List<String> list = new ArrayList<>();
        ULocale uLocale = ULocale.forLanguageTag(localeName);
        log.info(localeName);
//        for (String s :  LocaleData.getExemplarSet(uLocale, LocaleData.ES_INDEX)) {
        for (String s : LocaleData.getExemplarSet(uLocale, LocaleData.ES_STANDARD)) {
            list.add(s);
        }
        return list;
    }

    public static List<String> getAlphabetByRandomLocale() {
        List<String> list = new ArrayList<>();
        String localeName = getRandomLocale();
        ULocale uLocale = ULocale.forLanguageTag(localeName);
        log.info(String.format("LocaleName = '%s'", localeName));
//        for (String s :  LocaleData.getExemplarSet(uLocale, LocaleData.ES_INDEX)) {
        for (String s : LocaleData.getExemplarSet(uLocale, LocaleData.ES_STANDARD)) {
            list.add(s);
        }
        return list;
    }

    public static String getRandomLocale() {
        return DataHelper.getRandomValueFromList(Locale.getISOCountries());
    }

    // extend later on not contains en alphabet
    public static List<String> getNonLatinicLocale() {
        List<String> localesList = Arrays.asList(ULocale.getISOCountries());
        return localesList.stream()
                .filter(item -> LocaleData.getExemplarSet(ULocale.forLanguageTag(item), LocaleData.ES_STANDARD).size() > 50)
                .collect(Collectors.toList());
    }
}