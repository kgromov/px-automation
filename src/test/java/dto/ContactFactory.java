package dto;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kgr on 11/4/2015.
 */
public class ContactFactory {
    public static final String xmlContactData = "./src/test/resources/testdata/leads_data_1000.xml";
    public static final String xmlContactData_Dummy = "./src/test/resources/testdata/leads_data_1000_dummy.xml";

    private static <T extends RandomObject> Object unMarshalToObject(String xml_file_name, Class<T> clazz) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            T data = (T) jaxbUnmarshaller.unmarshal(new File(xml_file_name));
            return data.getRandom();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Contact> getContactList(int desiredSize) {
        List<Contact> contactList = new ArrayList<>(desiredSize);
        for (int i = 0; i < desiredSize; i++) {
            contactList.add((Contact) unMarshalToObject(xmlContactData, Contacts.class));
        }
        return contactList;
    }

    public static Contact getContact(){
        return (Contact) unMarshalToObject(xmlContactData, Contacts.class);
    }
}
