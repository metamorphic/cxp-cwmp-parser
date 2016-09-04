package cxp.ingest.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by markmo on 9/04/2016.
 */
public class DeviceId {

    private String manufacturer;
    private String oui;
    private String productClass;
    private String serialNumber;

    @XmlElement(name = "Manufacturer")
    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    @XmlElement(name = "OUI")
    public String getOui() {
        return oui;
    }

    public void setOui(String oui) {
        this.oui = oui;
    }

    @XmlElement(name = "ProductClass")
    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    @XmlElement(name = "SerialNumber")
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public String toString() {
        return "DeviceId{" +
                "manufacturer='" + manufacturer + '\'' +
                ", oui='" + oui + '\'' +
                ", productClass='" + productClass + '\'' +
                ", serialNumber='" + serialNumber + '\'' +
                '}';
    }
}
