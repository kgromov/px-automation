package dto;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

public class XmlToObject {

    public static <T> T unMarshal(Class<T> clazz, URL xml) throws JAXBException, IOException {
        JAXBContext jc = JAXBContext.newInstance(clazz);
        Unmarshaller u = jc.createUnmarshaller();
        // U can use IOUtils.toInputStream(xml) if add to deps commons-io and parse String
        // Also, it can be Node, String, etc
        return u.unmarshal(new StreamSource(xml.openStream()), clazz).getValue();
    }

    public static <T> T unMarshal(Class<T> clazz, String xml) throws JAXBException, IOException {
        try {
            JAXBContext jc = JAXBContext.newInstance(clazz);
            Unmarshaller u = jc.createUnmarshaller();
            StreamSource streamSource = new StreamSource(new StringReader(xml));
            return u.unmarshal(streamSource, clazz).getValue();
        } catch (UnmarshalException e) {
            throw new UnmarshalException("Corrupted submit XML, or data XML is not full, received xml: \n" + xml + "\n" + e.getMessage(), e);
        }
    }
}
