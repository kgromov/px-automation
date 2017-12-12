package elements.date;

import elements.SuperElement;
import org.openqa.selenium.WebElement;

import java.util.Date;

/**
 * Created by kgr on 10/11/2016.
 */
abstract class DateTimePicker extends SuperElement {

    public abstract void setDate(Date date);

    public abstract String getValue();

    void clickOnPicker(WebElement upPicker, WebElement lowPicker, int clickCount) {
        log.info(String.format("Click count = '%d'", clickCount));
        while (clickCount != 0) {
            log.info(String.format("Click on picker clickCount = '%d'", clickCount));
            if (clickCount > 0) {
                upPicker.click();
                --clickCount;
            } else {
                lowPicker.click();
                ++clickCount;
            }
        }
    }
}
