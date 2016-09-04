package cxp.ingest.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by markmo on 9/04/2016.
 */
public class ParameterValue {

    private String name;
    private String value;

    @XmlElement(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "Value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ParameterValue{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
