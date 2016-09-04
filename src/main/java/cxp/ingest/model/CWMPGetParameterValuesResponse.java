package cxp.ingest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by markmo on 14/04/2016.
 */

 // urn:dslforum-org:cwmp-1-2

@XmlRootElement(namespace = "urn:dslforum-org:cwmp-x-x", name = "GetParameterValuesResponse")
public class CWMPGetParameterValuesResponse extends CWMPParameterValuesType{

    @Override
    @XmlElement(name = "ParameterValueStruct")
    @XmlElementWrapper(name = "ParameterList")
    public List<ParameterValue> getParameterValues() {
        return parameterValues;
    }

    @Override
    public void setParameterValues(List<ParameterValue> parameterValues) {
        this.parameterValues = parameterValues;
    }

    //TODO: Do we need toString() ?
}
