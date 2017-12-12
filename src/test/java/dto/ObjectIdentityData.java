package dto;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static configuration.helpers.DataHelper.getRandomInt;

/**
 * Created by kgr on 2/7/2017.
 */
public class ObjectIdentityData {
    private String name;
    private String id;
    private String guid;

    public ObjectIdentityData(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            String lowerCaseKey = key.toLowerCase();
            if (lowerCaseKey.endsWith("name")) name = String.valueOf(jsonObject.get(key));
            if (lowerCaseKey.endsWith("id") && !lowerCaseKey.endsWith("guid")) id = String.valueOf(jsonObject.get(key));
            if (lowerCaseKey.endsWith("guid")) guid = String.valueOf(jsonObject.get(key));
        }
    }

    public ObjectIdentityData(String id, String name, String guid) {
        this.name = name;
        this.id = id;
        this.guid = guid;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getGuid() {
        return guid;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ObjectIdentityData)) return false;
        ObjectIdentityData that = (ObjectIdentityData) obj;
        return this.name.equals(that.name) &&
                this.id.equals(that.id) &&
                this.guid.equals(that.guid);
    }

    @Override
    public String toString() {
        return "ObjectIdentityData{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", guid='" + guid + '\'' +
                '}';
    }

    public static ObjectIdentityData getAnyObjectFromList(List<ObjectIdentityData> objectsDataList) {
        return objectsDataList.get(getRandomInt(objectsDataList.size()));
    }

    public static ObjectIdentityData getObjectFromListByName(List<ObjectIdentityData> objectsDataList, String value) {
        return objectsDataList.stream().filter(object -> object.getName().equals(value))
                .findFirst().orElse(null);
    }

    public static ObjectIdentityData getObjectFromListByID(List<ObjectIdentityData> objectsDataList, String value) {
        return objectsDataList.stream().filter(object -> object.getId().equals(value))
                .findFirst().orElse(null);
    }

    public static ObjectIdentityData getObjectFromListByGUID(List<ObjectIdentityData> objectsDataList, String value) {
        return objectsDataList.stream().filter(object -> object.getGuid().equals(value))
                .findFirst().orElse(null);
    }

    // ----- by name -----
    public static List<String> getAllNames(List<ObjectIdentityData> objectsDataList) {
        return objectsDataList.stream()
                .map(ObjectIdentityData::getName)
                .collect(Collectors.toList());
    }

    public static List<ObjectIdentityData> getObjectsByName(List<ObjectIdentityData> objectsDataList, String name) {
        return objectsDataList.stream()
                .filter(object -> object.getName().contains(name))
                .collect(Collectors.toList());
    }

    // ----- by id -----
    public static List<String> getAllIDs(List<ObjectIdentityData> objectsDataList) {
        return objectsDataList.stream()
                .map(ObjectIdentityData::getId)
                .collect(Collectors.toList());
    }

    public static List<ObjectIdentityData> getObjectByIDs(List<ObjectIdentityData> objectsDataList, List<String> iDsList) {
        return objectsDataList.stream()
                .filter(object -> iDsList.contains(object.getId()))
                .collect(Collectors.toList());
    }

    // ----- by guid -----
    public static List<String> getAllGUIDs(List<ObjectIdentityData> objectsDataList) {
        return objectsDataList.stream()
                .map(ObjectIdentityData::getGuid)
                .collect(Collectors.toList());
    }

    public static List<ObjectIdentityData> getObjectsByJSONArray(JSONArray jsonArray) {
        List<ObjectIdentityData> objectsIdentityDataList = new ArrayList<>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject.keySet().size() < 2)
                throw new TestDataException(String.format("Another JSON structure. There are less than 1 key. Check structure '%s'", jsonObject));
            ObjectIdentityData objectIdentityData = new ObjectIdentityData(jsonObject);
            // check that json structure is correct
            if (objectIdentityData.getName() == null && (objectIdentityData.getId() == null || objectIdentityData.getGuid() == null))
                throw new TestDataException(String.format("Another JSON structure. There are no Id/Guid and Name. " +
                        "Current object JSON\t%s ", jsonObject));
            objectsIdentityDataList.add(objectIdentityData);
        }
        return objectsIdentityDataList;
    }

    // ---- helper method to get unique sublist ----
    public static List<ObjectIdentityData> getRandomSubList(List<ObjectIdentityData> objects) {
        int count = getRandomInt(1, objects.size() > 10 ? 10 : objects.size());
        Set<ObjectIdentityData> temp = new HashSet<>();
        for (int i = 0; i < count; i++) {
            temp.add(objects.get(getRandomInt(objects.size())));
        }
        return new ArrayList<>(temp);
    }
}
