package cxp.ingest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(namespace = "urn:dslforum-org:cwmp-x-x", name = "TransferComplete")
public class CWMPTransferComplete {

    private String commandKey;
    private FaultStruct faultStruct;
    private Date startTime;
    private Date endTime;

    @XmlElement(name = "CommandKey")
    public String getCommandKey() {
        return commandKey;
    }

    public void setCommandKey(String commandKey) {
        this.commandKey = commandKey;
    }

    @XmlElement(name = "FaultStruct")
    public FaultStruct getFaultStruct() {
        return faultStruct;
    }

    public void setFaultStruct(FaultStruct faultStruct) {
        this.faultStruct = faultStruct;
    }

    @XmlElement(name = "StartTime")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    // TODO: Raname this to completeTime if desired
    @XmlElement(name = "CompleteTime")
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    //TODO: Do we need toString() ?
}
