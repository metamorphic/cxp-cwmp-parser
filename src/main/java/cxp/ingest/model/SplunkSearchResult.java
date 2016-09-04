package cxp.ingest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by markmo on 9/04/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SplunkSearchResult {

    @JsonProperty("_bkt")
    private String bucket;

    @JsonProperty("_cd")
    private String code;

    @JsonProperty("_indextime")
    private String indexTime;

    @JsonProperty("_raw")
    private String raw;

    @JsonProperty("_serial")
    private int serial;

    @JsonProperty("_si")
    private String si;

    @JsonProperty("_subsecond")
    private String subsecond;

    @JsonProperty("_time")
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date time;

    private String host;
    private String index;

    @JsonProperty("linecount")
    private int lineCount;

    private String source;

    @JsonProperty("sourcetype")
    private String sourceType;

    @JsonProperty("splunk_server")
    private String splunkServer;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIndexTime() {
        return indexTime;
    }

    public void setIndexTime(String indexTime) {
        this.indexTime = indexTime;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public int getSerial() {
        return serial;
    }

    public void setSerial(int serial) {
        this.serial = serial;
    }

    public String getSi() {
        return si;
    }

    public void setSi(String si) {
        this.si = si;
    }

    public String getSubsecond() {
        return subsecond;
    }

    public void setSubsecond(String subsecond) {
        this.subsecond = subsecond;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public int getLineCount() {
        return lineCount;
    }

    public void setLineCount(int lineCount) {
        this.lineCount = lineCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getSplunkServer() {
        return splunkServer;
    }

    public void setSplunkServer(String splunkServer) {
        this.splunkServer = splunkServer;
    }
}
