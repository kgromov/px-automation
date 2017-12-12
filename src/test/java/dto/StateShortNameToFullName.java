package dto;

import java.util.HashMap;
import java.util.Map;

public class StateShortNameToFullName {
    private static Map<String, String> map = new HashMap<>();

    static {
        map.put("AL", "Alabama");
        map.put("AK", "Alaska");
        map.put("AZ", "Arizona");
        map.put("AR", "Arkansas");
        map.put("CA", "California");
        map.put("CO", "Colorado");
        map.put("CT", "Connecticut");
        map.put("DE", "Delaware");
        map.put("DC", "District of Columbia");
        map.put("FL", "Florida");
        map.put("GA", "Georgia");
        map.put("HI", "Hawaii");
        map.put("ID", "Idaho");
        map.put("IL", "Illinois");
        map.put("IN", "Indiana");
        map.put("IA", "Iowa");
        map.put("KS", "Kansas");
        map.put("KY", "Kentucky");
        map.put("LA", "Louisiana");
        map.put("ME", "Maine");
        map.put("MD", "Maryland");
        map.put("MA", "Massachusetts");
        map.put("MI", "Michigan");
        map.put("MN", "Minnesota");
        map.put("MS", "Mississippi");
        map.put("MO", "Missouri");
        map.put("MT", "Montana");
        map.put("NE", "Nebraska");
        map.put("NV", "Nevada");
        map.put("NH", "New Hampshire");
        map.put("NJ", "New Jersey");
        map.put("NM", "New Mexico");
        map.put("NY", "New York");
        map.put("NC", "North Carolina");
        map.put("ND", "North Dakota");
        map.put("OH", "Ohio");
        map.put("OK", "Oklahoma");
        map.put("OR", "Oregon");
        map.put("PA", "Pennsylvania");
        map.put("RI", "Rhode Island");
        map.put("SC", "South Carolina");
        map.put("SD", "South Dakota");
        map.put("TN", "Tennessee");
        map.put("TX", "Texas");
        map.put("UT", "Utah");
        map.put("VT", "Vermont");
        map.put("VA", "Virginia");
        map.put("WA", "Washington");
        map.put("WV", "West Virginia");
        map.put("WI", "Wisconsin");
        map.put("WY", "Wyoming");
    }

    public static String getFullStateName(String key) {
        return map.get(key);
    }

    public static Map<String, String> getMap() { return map; }
}
