package px.reports.dto;

import dto.TestDataException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.regex.Pattern;

/**
 * Created by kgr on 6/19/2017.
 */
public class PopupData {
    // popup template regexp
    static final Pattern POPUP_PATTERN = Pattern.compile("(ng-click='vm.show.*\\(.*\\)')|(px-popup-chart)");
    static final Pattern POPUP_VALUED_PATTERN = Pattern.compile("(ng-click='vm.show.*\\(.*\\)')");
    // graphic types inside popup
    public static final String AREA_GRAPHIC = "Area";
    public static final String PIE_GRAPHIC = "Pie";
    private String title;
    private String type;
    private boolean hasValueInTitle;

    public PopupData(String source) {
        this.hasValueInTitle = POPUP_VALUED_PATTERN.matcher(source).find();
        if (!hasValueInTitle) {
            Document document = Jsoup.parse(source);
            try {
                Element popup = document.getElementsByTag("px-popup-chart").get(0);
                this.type = popup.attr("type").replaceAll("\\\\|\"", "");
            } catch (IndexOutOfBoundsException e) {
                throw new TestDataException("Unknown popup data\t" + source);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public boolean hasValueInTitle() {
        return hasValueInTitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "PopupData{" +
                "title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", hasValueInTitle=" + hasValueInTitle +
                '}';
    }
}