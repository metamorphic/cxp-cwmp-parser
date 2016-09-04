package cxp.ingest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import cxp.ingest.util.HttpHeaderParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by markmo on 9/04/2016.
 */

//https://github.com/FasterXML/jackson-annotations

@JsonIgnoreProperties(ignoreUnknown = true)
public class TR69Response {

    // No field for accept. Contains something like "text/xml, text/html"

    @JsonProperty("ack_packets_in")
    private long ackPacketsIn;

    @JsonProperty("ack_packets_out")
    private long ackPacketsOut;

    private long bytes;

    @JsonProperty("bytes_in")
    private long bytesIn;

    @JsonProperty("bytes_out")
    private long bytesOut;

    @JsonProperty("c_ip")
    private String cIp;

    // client round-trip time
    @JsonProperty("client_rtt")
    private long clientRtt;

    // No field for cookie
    // Cookie seem to be also included in src_headers

    @JsonProperty("dest_content")
    private String destContent;

    @JsonProperty("dest_ip")
    private String destIp;

    @JsonProperty("dest_mac")
    private String destMac;

    @JsonProperty("dest_port")
    private int destPort;

    @JsonProperty("endtime")
    private Date endTime;

    // No field for form_data

    @JsonProperty("http_comment")
    private String httpComment;

    // No field for http_content_length. Do we need this?

    @JsonProperty("http_content_type")
    private String httpContentType;

    @JsonProperty("http_method")
    private String httpMethod;

    // No field for http_user_agent. Do we need this?

    @JsonProperty("reply_time")
    private long replyTime;

    @JsonIgnore
    private String request;

    @JsonProperty("request_time")
    private String requestTime;

    private String server;

    private String site;

    @JsonProperty("src_content")
    private String srcContent;

    @JsonProperty("src_headers")
    private String srcHeaders;

    @JsonProperty("src_ip")
    private String srcIp;

    @JsonProperty("src_mac")
    private String srcMac;

    @JsonProperty("src_port")
    private int srcPort;

    @JsonIgnore
    private int status = 0;

    private Date timestamp;

    @JsonProperty("time_taken")
    private long timeTaken;

    private String transport;

    @JsonIgnore
    private String uri;

    @JsonProperty("uri_path")
    private String uriPath;

    private String user;

    // No field for www_auth. Contains something like Basic realm="cwmp@2wire-cms".

    @JsonIgnore
    private Map<String, String> headersMemo;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public long getAckPacketsIn() {
        return ackPacketsIn;
    }

    public void setAckPacketsIn(int ackPacketsIn) {
        this.ackPacketsIn = ackPacketsIn;
    }

    public long getAckPacketsOut() {
        return ackPacketsOut;
    }

    public void setAckPacketsOut(int ackPacketsOut) {
        this.ackPacketsOut = ackPacketsOut;
    }

    public long getBytes() {
        return bytes;
    }

    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    public long getBytesIn() {
        return bytesIn;
    }

    public void setBytesIn(int bytesIn) {
        this.bytesIn = bytesIn;
    }

    public long getBytesOut() {
        return bytesOut;
    }

    public void setBytesOut(int bytesOut) {
        this.bytesOut = bytesOut;
    }

    public String getcIp() {
        return cIp;
    }

    public void setcIp(String cIp) {
        this.cIp = cIp;
    }

    public long getClientRtt() {
        return clientRtt;
    }

    public void setClientRtt(int clientRtt) {
        this.clientRtt = clientRtt;
    }

    public String getDestContent() {
        return destContent;
    }

    public void setDestContent(String destContent) {
        this.destContent = destContent;
    }

    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }

    public String getDestMac() {
        return destMac;
    }

    public void setDestMac(String destMac) {
        this.destMac = destMac;
    }

    public int getDestPort() {
        return destPort;
    }

    public void setDestPort(int destPort) {
        this.destPort = destPort;
    }

    public String getHttpComment() {
        return httpComment;
    }

    public void setHttpComment(String httpComment) {
        this.httpComment = httpComment;
    }

    public String getHttpContentType() {
        return httpContentType;
    }

    public void setHttpContentType(String httpContentType) {
        this.httpContentType = httpContentType;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public long getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(int replyTime) {
        this.replyTime = replyTime;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSrcContent() {
        return srcContent;
    }

    public void setSrcContent(String srcContent) {
        this.srcContent = srcContent;
    }

    public String getSrcHeaders() {
        return srcHeaders;
    }

    public void setSrcHeaders(String srcHeaders) {
//        headersMemo = null;
        this.srcHeaders = srcHeaders;
    }

    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTimeTaken() {
        return timeTaken;
    }

    public void setTimeTaken(int timeTaken) {
        this.timeTaken = timeTaken;
    }

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUriPath() {
        return uriPath;
    }

    public void setUriPath(String uriPath) {
        this.uriPath = uriPath;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Map<String, String> getHeaders() {
        if (headersMemo != null) return headersMemo;
        if (srcHeaders == null || srcHeaders.isEmpty()) {
            return (headersMemo = Collections.emptyMap());
        }
        InputStream stream = new ByteArrayInputStream(srcHeaders.getBytes(StandardCharsets.UTF_8));
        try {
            headersMemo = HttpHeaderParser.parseHeaders(stream);
        } catch (Exception e) {
            headersMemo = Collections.emptyMap();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                headersMemo = Collections.emptyMap();
            }
        }
        return headersMemo;
    }

    public String getHeaderValue(String key) {
        Map<String, String> headers = getHeaders();
        if (headers.containsKey(key)) {
            return headers.get(key);
        }
        return null;
    }

    public String getSubHeaderValue(String key, String subKey) {
        String headerValue = getHeaderValue(key);

        if (headerValue == null) {
            return null;
        }

        String[] subHeaders = headerValue.split(";");

        if (subHeaders.length == 0) {
            return null;
        }

        for (int i = 0; i < subHeaders.length; i++) {
            String subHeader = subHeaders[i].trim();
            if (subHeader.startsWith(subKey + "=")) {
                // e.g. JSESSIONID=33424A4B7B7318A70D455F6080FF46D9
                String[] parts = subHeader.split("=");
                if (parts.length > 1) {
                    return parts[1];
                }
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return srcHeaders;
    }
}
