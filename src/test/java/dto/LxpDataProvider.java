package dto;

import config.PXFieldsEnum;
import configuration.helpers.*;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static pages.groups.Pagination.MAX_ROWS_LIMIT;
import static pages.groups.Pagination.MAX_ROWS_PER_PAGE;

/**
 * Created by kgr on 10/18/2016.
 */
public class LxpDataProvider {
    private static final String testDataRootFolder = "./src/test/resources/testdata/";
    private RequestHelper requestHelper;
    private int currentTotal;

    public LxpDataProvider() {
        this.requestHelper = new RequestHelper();
    }

    public LxpDataProvider(SessionToken sessionToken) {
        this.requestHelper = sessionToken;
    }

    /**
     * @return total count of the last response which contains 'total' and 'data' keys
     * Also used to collect BigData
     */
    public int getCurrentTotal() {
        return currentTotal;
    }

    /**
     * @param name objects enum
     * @return map description - number of objects enums
     * Get data from saved {@link testdata} responses in xml form
     */
    public Map<String, String> getPossibleValueFromXML(String name) {
        String xmlPath = testDataRootFolder + "xml/" + name + ".xml";
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(xmlPath);

            NodeList keyList = doc.getElementsByTagName("d3p1:EnumDescription");
            NodeList valueList = doc.getElementsByTagName("d3p1:EnumSequenceNumber");
            if (keyList.getLength() != valueList.getLength())
                throw new TestDataException(String.format("Unable to read data from '%s'", xmlPath));
            Map<String, String> dataMap = new LinkedHashMap<>(keyList.getLength());
            for (int i = 0; i < keyList.getLength(); i++) {
                dataMap.put(keyList.item(i).getTextContent(), valueList.item(i).getTextContent());
            }
            return dataMap;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new TestDataException(String.format("Unable to read data from '%s'", xmlPath) + e.getMessage());
        }
    }

    /**
     * @param name objects enum
     * @return map description - number of objects enums
     * Get data from saved {@link testdata} responses in json form
     * Usage example: choose some random value from possible list in objects test data
     * Prevent waiting extra time and possible exceptions while requesting to lxp
     */
    public Map<String, String> getPossibleValueFromJSON(String name) {
        return getPossibleValueFromJSON(name, "enumSequenceNumber");
    }

    /**
     * @param name     objects enum
     * @param valueKey number or index
     * @return map description - number or index of objects enums
     * Extended version of getPossibleValueFromJSON cause depends on data  both
     * 'enumSequenceNumber' or 'enumSequenceIndex' could be used
     */
    public Map<String, String> getPossibleValueFromJSON(String name, String valueKey) {
        File file = new File(testDataRootFolder + "json/" + name + ".json");
        try {
            Map<String, String> dataMap = new LinkedHashMap<>();
            Reader reader = new InputStreamReader(new FileInputStream(file));
            JSONObject jsonObject = new JSONObject(new JSONTokener(reader));
            if (jsonObject.keySet().size() > 1)
                throw new TestDataException(String.format("Another JSON structure. Check file '%s'", file.getAbsoluteFile()));
            for (Object o : jsonObject.keySet()) {
                name = (String) o;
            }
            for (Object enumName : jsonObject.getJSONArray(name)) {
                JSONObject enumObject = (JSONObject) enumName;
                String key = enumObject.has("enumFullDescription")
                        ? (String) enumObject.get("enumFullDescription")
                        : (String) enumObject.get("enumDescription");
                String value = (String) enumObject.get(valueKey);
                dataMap.put(key, value);
            }
            dataMap.remove("None");
            return dataMap;
        } catch (JSONException | IOException e) {
            throw new TestDataException(String.format("Unable to read data from '%s'", file.getAbsoluteFile()) + e.getMessage());
        }
    }

    // ================================= Actual getters =================================

    /**
     * @param requestedURL is url to get response by
     * @return response as a string
     * The most generic response getter, use GET HTTP method by default
     */
    public String getDataAsString(String requestedURL) {
        String response = requestHelper.getResponseText(requestedURL);
        if (response == null)
            throw new TestDataException(String.format("Unable to get response by request '%s'", requestedURL));
        return response;
    }

    /**
     * @param requestedURL is url to get response by
     * @param methodsEnum  HTTP method
     * @return response as a string
     * Method is used for some specific requests with specific HTTP method type
     */
    public String getDataAsString(String requestedURL, HttpMethodsEnum methodsEnum) {
        String response = requestHelper.getResponseTextByHTTPMethod(requestedURL, methodsEnum);
        if (response == null)
            throw new TestDataException(String.format("Unable to get response by request '%s'", requestedURL));
        return response;
    }

    /**
     * @param requestedURL is url to get response by
     * @return first element of JSON array supposed to be unique
     * Usage example: get object by guid
     */
    public org.json.JSONObject getDataAsJSON(String requestedURL) {
        String response = requestHelper.getResponseText(requestedURL);
        if (response == null)
            throw new TestDataException(String.format("Unable to get response by request '%s'", requestedURL));
        try {
            return new JSONWrapper(response).getJSON().getJSONArray("data").getJSONObject(0);
        } catch (JSONException | IndexOutOfBoundsException e) {
            throw new TestDataException(String.format("Unable to parse data\n%s cause '%s'", response, e.getMessage()), e);
        }
    }

    /**
     * @param requestedURL is url to get response by
     * @return JSON array of data
     * Method has logic to understand whether it BigData or not
     */
    public org.json.JSONArray getDataAsJSONArray(String requestedURL, boolean isForceAll) {
        String response = requestHelper.getResponseText(requestedURL);
        if (response == null)
            throw new TestDataException(String.format("Unable to get response by request '%s'", requestedURL));
        try {
            org.json.JSONObject jsonObject = new JSONWrapper(response).getJSON();
            org.json.JSONArray jsonArray = jsonObject.getJSONArray("data");
            int totalCount = jsonObject.getInt("total");
            // used in LxpDataProvider in the following verifications
            this.currentTotal = totalCount;
            boolean isBigData = totalCount > MAX_ROWS_LIMIT;
            boolean includeTotal = jsonArray.length() > 0 && DataHelper.hasJSONValue(jsonArray.getJSONObject(0), "Total");
            return (!isBigData && totalCount > (includeTotal ? MAX_ROWS_PER_PAGE + 1 : MAX_ROWS_PER_PAGE) && isForceAll)
                    ? getJSONArrayBigData(jsonArray, requestedURL, totalCount) : jsonArray;
        } catch (JSONException | IndexOutOfBoundsException e) {
            throw new TestDataException(String.format("Unable to parse data\n%s cause '%s'", response, e.getMessage()));
        }
    }

    public org.json.JSONArray getDataAsJSONArray(String requestedURL) {
        return getDataAsJSONArray(requestedURL, false);
    }

    /**
     * @param initialArray   first portion of data to accumulate
     * @param requestedURL   is url to get response by
     * @param totalRowsCount indicate how many requests remains to collect the rest data
     * @return accumulated JSON array of data, usually object
     * Method is used to accumulate data by portions
     * In PX it's some sort of limitation to improve performance not to get all data at once
     * E.g. there are 1000 created publishers, by 1 request only 100 could be received in response
     * To collect all them all call getJSONArrayBigData
     */
    private org.json.JSONArray getJSONArrayBigData(org.json.JSONArray initialArray, String requestedURL, int totalRowsCount) {
        StringBuilder builder = new StringBuilder(initialArray.toString().replaceAll("\\}\\]", "}"));
        int rowsPerPage = DataHelper.hasJSONValue(initialArray.getJSONObject(0), "Total") ? MAX_ROWS_PER_PAGE + 1 : MAX_ROWS_PER_PAGE;
        int pageCount = new BigDecimal((double) totalRowsCount / rowsPerPage).setScale(0, RoundingMode.UP).intValue();
        for (int i = 2; i <= pageCount; i++) {
            String response = requestHelper.getResponseText(requestedURL.replace("&page=1", "&page=" + i));
            if (response == null)
                throw new TestDataException(String.format("Unable to get response by request '%s'", requestedURL));
            try {
                // follow json syntax - separate json element with comma
                builder.append(",");
                builder.append(new org.json.JSONObject(response).getJSONArray("data").toString().replaceAll("(\\[\\{)|(\\}\\])", ""));
            } catch (JSONException | IndexOutOfBoundsException e) {
                throw new TestDataException(String.format("Unable to parse data\n%s cause '%s'", response, e.getMessage()));
            }
        }
        builder.append("]");
        return new JSONWrapper(builder.toString()).getJSONArray();
    }

    // ======================================== In parallel ========================================

    /**
     * @param urls is List of requested urls to be processed concurrently
     * @return {@link Responses} object which contains map of responses by requests
     * Usage example: reports, big data where each response takes too long time
     */
    public Responses getResponsesInParallel(List<String> urls) {
        Map<String, String> responsesMap = urls.parallelStream()
                .collect(Collectors.toMap(request -> request, this::getDataAsString));
        return new Responses(responsesMap);
    }

    // ===================== helper methods that repeats in specific test data =====================

    /**
     * @param requestedURL is url to get response by
     * @return any random JSON object JSON array
     * Differs to getAnyJSONObjectFromArray that does not take array by response key 'data'
     * Usage example: lightModel requests
     */
    public org.json.JSONObject getAnyJSONObjectFromString(String requestedURL) {
        org.json.JSONArray jsonArray = new JSONWrapper(getDataAsString(requestedURL)).getJSONArray();
        return jsonArray.getJSONObject(DataHelper.getRandomInt(jsonArray.length()));
    }

    /**
     * @param requestedURL is url to get response by
     * @return any random JSON object JSON array
     * Usage example: select any publisher in Pixel Data
     */
    public org.json.JSONObject getAnyJSONObjectFromArray(String requestedURL) {
        org.json.JSONArray jsonArray = getDataAsJSONArray(requestedURL);
        return jsonArray.getJSONObject(DataHelper.getRandomInt(jsonArray.length()));
    }

    /**
     * @param requestedURL is url to get response by
     * @param filterBy     is used to filter all JSON array by some key
     * @param filterHow    with value
     * @return any random JSON object from filtered JSON array
     * Usage example: filter all instances by created by automation tests
     */
    public org.json.JSONObject getAnyJSONObjectFromArray(String requestedURL, String filterBy, String filterHow) {
        org.json.JSONArray jsonArray = getDataAsJSONArray(requestedURL);
        List<String> allItems = DataHelper.getListFromJSONArrayByKey(jsonArray, filterBy);
        List<String> seekingList = allItems.stream()
                .filter(item -> item.contains(filterHow))
                .collect(Collectors.toList());
        return jsonArray.getJSONObject(DataHelper.getRandomInt(seekingList.size()));
    }

    /**
     * @param instanceName is objects objects e.g. offers, publishers etc
     * @return List of {@link  ObjectIdentityData} which contains id, name and guid of each created object
     * @throws TestDataException if could not get response or parse it
     */
    public List<ObjectIdentityData> getCreatedInstancesData(String instanceName) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/" + instanceName)
                .build().getRequestedURL();
        return getCreatedInstancesDataByURL(requestedURL);
    }

    public List<ObjectIdentityData> getCreatedInstancesData(String instanceName, List<String> paramKeys, List<String> paramValus) {
        String requestedURL = new RequestedURL.Builder()
                .withRelativeURL("api/lightModel/" + instanceName)
                .withParams(paramKeys, paramValus)
                .build().getRequestedURL();
        return getCreatedInstancesDataByURL(requestedURL);
    }

    private List<ObjectIdentityData> getCreatedInstancesDataByURL(String requestedURL) {
        String response = requestHelper.getResponseText(requestedURL);
        if (response == null)
            throw new TestDataException(String.format("Unable to get response by url '%s'", requestedURL));
        try {
            org.json.JSONArray jsonArray = new JSONWrapper(response).getJSONArray();
            List<ObjectIdentityData> objectsIdentityDataList = new ArrayList<>(jsonArray.length());
            for (int i = 0; i < jsonArray.length(); i++) {
                org.json.JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.keySet().size() < 2)
                    throw new TestDataException(String.format("Another JSON structure. There are less than 1 key. Check structure '%s'", response));
                ObjectIdentityData objectIdentityData = new ObjectIdentityData(jsonObject);
                // check that json structure is correct
                if (objectIdentityData.getName() == null && (objectIdentityData.getId() == null || objectIdentityData.getGuid() == null))
                    throw new TestDataException(String.format("Another JSON structure. There are no Id/Guid and Name. " +
                            "Current object JSON\n %s Check structure '%s'", jsonObject.toString(), response));
                objectsIdentityDataList.add(objectIdentityData);
            }
            return objectsIdentityDataList;
        } catch (JSONException e) {
            throw new TestDataException(String.format("Unable to parse data\n%s cause '%s'", response, e.getMessage()));
        }
    }

    /**
     * update {@link testdata} files to use as possible enum values
     * instead of request each time
     * Use {@link Formatter} class to save in user friendly format
     * Common format of such file: description - index
     *
     * @throws TestDataException if unable to get response or parse it
     */
    public void updateTestDataFiles() {
        for (PXFieldsEnum enumName : PXFieldsEnum.values()) {
            try {
                FileUtils.writeStringToFile(new File(testDataRootFolder + "json/" + enumName.name() + ".json"),
                        Formatter.prettyJSONFormat(requestHelper.getEnumTestDataByName(enumName.name(), false)));
                FileUtils.writeStringToFile(new File(testDataRootFolder + "xml/" + enumName.name() + ".xml"),
                        Formatter.prettyXMLFormat(requestHelper.getEnumTestDataByName(enumName.name(), true)));
            } catch (IOException e) {
                throw new TestDataException("Unable to update test data file\n Cause of" + e.getMessage());
            }
        }
    }

    public void addNewEnum(String enumName) {
        try {
            FileUtils.writeStringToFile(new File(testDataRootFolder + "json/" + enumName + ".json"),
                    Formatter.prettyJSONFormat(requestHelper.getEnumTestDataByName(enumName, false)));
            FileUtils.writeStringToFile(new File(testDataRootFolder + "xml/" + enumName + ".xml"),
                    Formatter.prettyXMLFormat(requestHelper.getEnumTestDataByName(enumName, true)));
        } catch (IOException e) {
            throw new TestDataException("Unable to update test data file\n Cause of" + e.getMessage());
        }
    }
}