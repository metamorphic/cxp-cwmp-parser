package cxp.ingest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * Created by markmo on 9/04/2016.
 */
@XmlRootElement(namespace = "urn:dslforum-org:cwmp-x-x", name = "Inform")
public class CWMPInform extends CWMPParameterValuesType {

    private DeviceId deviceId;
    private List<Event> events;
    private int maxEnvelopes;
    private Date currentTime;
    private int retryCount = 0;
    //private List<ParameterValue> parameterValues;

    @XmlElement(name = "DeviceId")
    public DeviceId getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(DeviceId deviceId) {
        this.deviceId = deviceId;
    }

    @XmlElement(name = "EventStruct")
    @XmlElementWrapper(name = "Event")
    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    @XmlElement(name = "MaxEnvelopes")
    public int getMaxEnvelopes() {
        return maxEnvelopes;
    }

    public void setMaxEnvelopes(int maxEnvelopes) {
        this.maxEnvelopes = maxEnvelopes;
    }

    @XmlElement(name = "CurrentTime")
    public Date getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Date currentTime) {
        this.currentTime = currentTime;
    }

    @XmlElement(name = "RetryCount")
    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

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



    @Override
    public String toString() {
        return "CWMPInform{" +
                "deviceId=" + deviceId +
                ", events=" + events +
                ", maxEnvelopes=" + maxEnvelopes +
                ", currentTime=" + currentTime +
                ", retryCount=" + retryCount +
                ", parameterValues=" + parameterValues +
                '}';
    }
}
