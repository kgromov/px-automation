package dto;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;

@XmlRootElement(name = "Response")
@XmlAccessorType(XmlAccessType.FIELD)
public class LeadResponse {
    @XmlElement(name = "Result")
    private Result result;

    public LeadResponse() {
    }

    public LeadResponse(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public boolean isSucceed() {
        return result != null && result.getValue() != null && result.getValue().equals("BaeOK");
    }

    @Override
    public String toString() {
        return "LeadResponse{" +
                "result=" + result +
                '}';
    }

    // ==========================================
//    @XmlRootElement(name = "Result")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Result {

        @XmlAttribute(name = "Value")
        private String value;
        @XmlElement(name = "TransactionId")
        private String transactionId;
        @XmlElement(name = "Error")
        private Error error;

        public Result() {
        }

        public Result(String value) {
            this.value = value;
        }

        public Result(String value, Error error) {
            this.value = value;
            this.error = error;
        }

        public Result(String value, String transactionId, Error error) {
            this.value = value;
            this.transactionId = transactionId;
            this.error = error;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        public Error getError() {
            return error;
        }

        public void setError(Error error) {
            this.error = error;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "value='" + value + '\'' +
                    ", error=" + error +
                    '}';
        }
    }

    // ==========================================
//    @XmlRootElement(name = "Error")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class Error {

        @XmlElement(name = "Reason")
        private String reason;

        @XmlElement(name = "Param")
        private String param;

        @XmlElement(name = "ExtraInfo")
        private String extraInfo;

        public Error() {
        }

        public Error(String reason, String param, String extraInfo) {
            this.reason = reason;
            this.param = param;
            this.extraInfo = extraInfo;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public String getParam() {
            return param;
        }

        public void setParam(String param) {
            this.param = param;
        }

        public String getExtraInfo() {
            return extraInfo;
        }

        public void setExtraInfo(String extraInfo) {
            this.extraInfo = extraInfo;
        }

        @Override
        public String toString() {
            return "Error{" +
                    "reason='" + reason + '\'' +
                    ", param='" + param + '\'' +
                    ", extraInfo='" + extraInfo + '\'' +
                    '}';
        }
    }

    public static void main(String[] args) {
        Error error = new Error("reason", "param", "param is incorrect");
        Result result = new Result("BaeOK", error);
        LeadResponse response = new LeadResponse(result);
        try {

            File file = new File("D:\\file.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(LeadResponse.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(response, file);
            jaxbMarshaller.marshal(response, System.out);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            LeadResponse response1 = (LeadResponse) jaxbUnmarshaller.unmarshal(file);
            System.out.println(response1);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }
}
