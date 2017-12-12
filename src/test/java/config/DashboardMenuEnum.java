package config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static config.Config.userGroup;

/**
 * Created by konstantin on 05.10.2016.
 */
public enum DashboardMenuEnum {
    DASHBOARD("dashboard"),
    OFFERS("offers"),
    LEADS("leads"),
    CAMPAIGNS("campaigns"),
    REPORTS("reports"),
    BILLING("billing"),
    ADMIN("admin");

    private final String value;

    public String getValue() {
        return value;
    }

    DashboardMenuEnum(String value) {
        this.value = value;
    }

    public static List<DashboardMenuEnum> getMenuItems() {
        List<DashboardMenuEnum> items = new ArrayList<>(Arrays.asList(DashboardMenuEnum.values()));
        // different left menu items set on beta
        if (Config.isBetaEnvironment()) items.remove(DashboardMenuEnum.BILLING);
        // switch by user role
        switch (UserRoleEnum.valueOf(userGroup)) {
            case ADMIN:
                return items;
            case BUYER:
                items.remove(DashboardMenuEnum.ADMIN);
                items.remove(DashboardMenuEnum.OFFERS);
                return items;
            case PUBLISHER:
                items.remove(DashboardMenuEnum.ADMIN);
                items.remove(DashboardMenuEnum.CAMPAIGNS);
                items.remove(DashboardMenuEnum.LEADS);
                return items;
            default:
                throw new IllegalArgumentException("Unknown user role - " + userGroup);
        }
    }
}
