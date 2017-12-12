package px.objects.campaigns;

/**
 * Created by kgr on 10/7/2016.
 */
public enum CampaignColumnsEnum {
    NAME("Campaign Name"),
    ID("Campaign ID"),
    VERTICAL("Vertical"),
    LEADS("Leads"),
    SPEND("Total spend"),
    QUALITY("Quality"),
    FILTERS("");

    private final String value;

    public String getValue() {
        return value;
    }

    CampaignColumnsEnum(String value) {
        this.value = value;
    }
}
