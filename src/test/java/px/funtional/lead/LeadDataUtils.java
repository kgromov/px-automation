package px.funtional.lead;

import configuration.helpers.HtppClient;
import configuration.helpers.RequestedURL;
import dto.LeadResponse;
import dto.TestDataException;
import dto.XmlToObject;
import okhttp3.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import px.objects.users.ContactTestData;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import static config.Config.*;

/**
 * Created by kgr on 11/8/2017.
 */
public class LeadDataUtils {
    private static final Logger log = Logger.getLogger(LeadDataUtils.class);
    private static final OkHttpClient client = HtppClient.getHtppClient(60, 60, 60);

    // ====================== Data preparation ===========================
    public static Document stringToXml(String xmlData) {
//        String xml = xmlData.replace("\\r", "").replace("\\t", "").replace("\\t", "").replaceAll("\\s{2,}", "");
        String xml = StringEscapeUtils.unescapeJava(xmlData);
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes("utf-8"))));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException("Unable to parse xml cause\t" + e.getMessage(), e);
        }
    }

    public static String xmlToString(Document doc) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
//            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            return writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch (javax.xml.transform.TransformerException e) {
            throw new RuntimeException("Unable to read xml");
        }
    }

    public static Document updateLeadData(Document doc, String emailValue) {
        // update lead data to be unique
        Element leadData = (Element) doc.getElementsByTagName("LeadData").item(0);
        leadData.setAttribute("Partner", user);
        leadData.setAttribute("Password", password);
        leadData.setAttribute("TransactionId", "");
        // update lead data to be unique
        Element affiliateData = (Element) doc.getElementsByTagName("LeadData").item(0);
        if (affiliateData.hasAttribute("TransactionId"))
            affiliateData.setAttribute("TransactionId", "");
        // update email with unique value
        Node email = doc.getElementsByTagName("EmailAddress").item(0);
        email.setTextContent(emailValue);
        return doc;
    }

    public static Document updateLeadData(Document doc, ContactTestData testData) {
        // update lead data to be unique
        Element leadData = (Element) doc.getElementsByTagName("LeadData").item(0);
        leadData.setAttribute("Partner", user);
        leadData.setAttribute("Password", password);
        leadData.setAttribute("TransactionId", "");
        // update contact data
        testData.toMap().entrySet().forEach(field -> {
            NodeList nodes = doc.getElementsByTagName(field.getKey());
            if (nodes.getLength() > 0) {
                Node node = nodes.item(0);
                node.setTextContent(field.getValue());
            }
        });
        return doc;
    }

    public static boolean isHttpPost(String inboundData) {
        return inboundData.contains("command=HTTPPost");
    }

    // ============================= Operations ===============================
    public static LeadResponse insertLead(String xml) {
        try {
            log.info("Insert lead with data:\n" + xml);
            String requestedURL = new RequestedURL.Builder()
                    .withAbsoluteURL(apiURL + "px")
                    .withParams("command", "XMLPost")
                    .build().getRequestedURL();
            MediaType mediaType = MediaType.parse(ContentType.APPLICATION_XML.getMimeType());
            RequestBody body = RequestBody.create(mediaType, xml);
            Request request = new Request.Builder()
                    .url(requestedURL)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            String responseText = response.body().string();
            System.out.println(responseText);
            return XmlToObject.unMarshal(LeadResponse.class, responseText);
        } catch (IOException e) {
            throw new TestDataException("Unable to insert lead cause of\n" + e.getMessage(), e);
        } catch (JAXBException e2) {
            throw new RuntimeException("Unable to deserialize to lead response object of\n" + e2.getMessage(), e2);
        }
    }

    // ================================= Wrappers =================================
    public static LeadResponse insertLeadXmlWithEmail(ContactTestData testData, String xmlData) {
        ContactTestData contactData = new ContactTestData();
        log.info("Insert lead with updated email = " + contactData.getInternalEmail());
        testData.setEmail(contactData.getInternalEmail());
        Document document = stringToXml(xmlData);
        document = updateLeadData(document, testData.getEmail());
        return insertLead(xmlToString(document));
    }
}
