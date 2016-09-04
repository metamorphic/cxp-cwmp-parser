package cxp.ingest.model;

import javax.xml.bind.annotation.XmlElement;

public class FaultStruct{

    private String faultCode;
    private String faultString;

    @XmlElement(name = "FaultCode")
    public String getFaultCode() {
        return faultCode;
    }

    public void setFaultCode(String faultCode) {
        this.faultCode = faultCode;
    }

    @XmlElement(name = "FaultString")
    public String getFaultString() {
        return faultString;
    }

    public void setFaultString(String faultString) {
        this.faultString = faultString;
    }
}
