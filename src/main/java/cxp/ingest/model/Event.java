package cxp.ingest.model;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by markmo on 9/04/2016.
 */
public class Event {

    private String code;
    private String commandKey;

    @XmlElement(name = "EventCode")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @XmlElement(name = "CommandKey")
    public String getCommandKey() {
        return commandKey;
    }

    public void setCommandKey(String commandKey) {
        this.commandKey = commandKey;
    }

    @Override
    public String toString() {
        return "Event{" +
                "code='" + code + '\'' +
                ", commandKey='" + commandKey + '\'' +
                '}';
    }
}
