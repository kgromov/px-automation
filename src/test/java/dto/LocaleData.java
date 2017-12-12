package dto;

import configuration.helpers.I18nUtils;

import java.util.Arrays;
import java.util.List;

import static configuration.helpers.DataHelper.getRandomValueFromList;

/**
 * Created by kgr on 1/25/2017.
 */
public class LocaleData {
    private String locale;
    private List<String> alphabetList;
    private List<Character> specialCharsList = Arrays.asList('#', ';', ':', '!', '/', '<', '>', '&');
//    private List<String> localesList = Arrays.asList("UA", "RU", "TR", "TJ", "PL", "JA", "KO", "FJ", "IS"); 

    public LocaleData() {
        this.locale = getRandomValueFromList(I18nUtils.getNonLatinicLocale());
        this.alphabetList = I18nUtils.getAlphabetByLocale(locale);
        System.out.println("Data init with locale = " + locale);
    }

    public String getLocale() {
        return locale;
    }

    public List<String> getAlphabetList() {
        return alphabetList;
    }

    @Override
    public String toString() {
        return "LocaleData{" +
                "locale='" + locale + '\'' +
                ", alphabetList=" + alphabetList +
                '}';
    }
}