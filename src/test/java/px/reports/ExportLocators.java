package px.reports;

import org.openqa.selenium.By;

/**
 * Created by kgr on 4/19/2017.
 */
public class ExportLocators {
    // common
//    public final static By EXPORT_BUTTON = By.cssSelector(".px-overview-button");
    public final static By EXPORT_BUTTON = By.xpath(".//a[contains(@ng-click, 'showExportModal')]");
    public final static By EXPORT_DIALOGUE = By.xpath(".//div[@class='px-popupWin-table']");
    // CREATE_TOGGLE - to collapse/expand
    public final static By SEPARATOR_SELECT = By.xpath(".//div[@data-field-name='csvSeparator']");
    // leads report
    public final static By EXPORT_ATTRIBUTES_RADIO = By.xpath(".//div[@data-field-name='DoAnswers']");
    public final static By VERTICAL_SELECT = By.xpath(".//div[@data-field-name='Vertical']");
    public final static By COUNTRY_SELECT = By.xpath(".//div[@data-field-name='Country']");
    // tooltip
    public final static By QUEUED_TOOLTIP = By.tagName("px-toast");
    public final static By QUEUED_TOOLTIP_MESSAGE = By.cssSelector("[id*=toastmessage]");
}
