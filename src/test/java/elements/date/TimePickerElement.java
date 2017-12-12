package elements.date;

import configuration.helpers.DataHelper;
import elements.HelperSingleton;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.Calendar;
import java.util.Date;

import static configuration.helpers.DataHelper.CALENDAR_TABLE_TIME_PATTERN;
import static configuration.helpers.DataHelper.CALENDAR_TABLE_TIME_PATTERN_AM;

/**
 * Created by kgr on 10/11/2016.
 */
public class TimePickerElement extends DateTimePicker {
    private static final String ampm = ".//*[@data-action='togglePeriod']";
    private boolean hasAmPm;

    @FindBy(xpath = ".//*[@data-action='incrementHours']")
    private WebElement upHour;

    @FindBy(xpath = ".//*[@data-action='incrementMinutes']")
    private WebElement upMinute;

    @FindBy(xpath = ".//*[@data-action='incrementSeconds']")
    private WebElement upSecond;

    @FindBy(xpath = ".//*[@data-action='decrementHours']")
    private WebElement lowHour;

    @FindBy(xpath = ".//*[@data-action='decrementMinutes']")
    private WebElement lowMinute;

    @FindBy(xpath = ".//*[@data-action='decrementSeconds']")
    private WebElement lowSecond;

    @FindBy(xpath = ampm)
    private WebElement period;

    // time values
    @FindBy(xpath = ".//*[@data-action='showHours']")
    private WebElement hourText;

    @FindBy(xpath = ".//*[@data-action='showMinutes']")
    private WebElement minutesText;

    @FindBy(xpath = ".//*[@data-action='showSeconds']")
    private WebElement secondsText;

    @Override
    public void setDate(Date date) {
        hasAmPm = HelperSingleton.getHelper().isElementAccessible(ampm);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DataHelper.getDateByFormatSimple(hasAmPm ? CALENDAR_TABLE_TIME_PATTERN_AM : CALENDAR_TABLE_TIME_PATTERN, getValue()));
        int hours = calendar.get(Calendar.HOUR);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        // negative or positive
//        boolean sign = DataHelper.getTimeByDate(CALENDAR_TABLE_TIME_PATTERN, date).getTime() - calendar.getTimeInMillis() > 0;
        // test data
        calendar.setTime(date);
        int hourClickCount = calendar.get(Calendar.HOUR) - hours;
        int minuteClickCount = calendar.get(Calendar.MINUTE) - minutes;
        int secondClickCount = calendar.get(Calendar.SECOND) - seconds;
        // set hours
        clickOnPicker(upHour, lowHour, hourClickCount);
        // set minutes
        clickOnPicker(upMinute, lowMinute, minuteClickCount);
        // set seconds
        clickOnPicker(upSecond, lowSecond, secondClickCount);
    }

    @Override
    public String getValue() {
        return hourText.getText() + ":" + minutesText.getText() + ":" + secondsText.getText() +
                (hasAmPm ? " " + period.getText() : "");
    }
}
