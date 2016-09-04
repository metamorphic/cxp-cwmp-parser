package cxp.ingest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cxp.ingest.model.*;
import cxp.ingest.util.ChunkedDataParser;
import cxp.ingest.util.DeviceSummaryParser;
import cxp.ingest.util.ParseException;
import cxp.ingest.util.SoapParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by markmo on 2/04/2016.
 */
public class TR69MessageTransformer {

    private static Logger logger = LoggerFactory.getLogger(TR69MessageTransformer.class);

    private static ObjectMapper mapper;
    private static SoapParser soapParser;
    private static DeviceSummaryParser summaryParser;

    static {
        mapper = new ObjectMapper();
        soapParser = new SoapParser();
        summaryParser = new DeviceSummaryParser();
    }

    private TR69Response response;
    private InternetGatewayDevice device;

    // Error info below is transferred to the next module as header fields
    // ErrorCode 0 = No errors, 1 = New XML Tag, 2 = Errors
    private int ingestErrorCode;
    private String ingestErrorMessage;
    private String ingestErrorDump;

    /**
     * Looks like some of the required information must be aggregated over multiple events,
     * e.g. upTime and lanDeviceErrorsReceived.
     * <p/>
     * We could either maintain state in the streaming processor (Spring XD), or just write
     * events to HDFS and aggregate there using Spark.
     * <p/>
     * Requests (in one event) must be matched with responses (in another event) using
     * JSESSIONID. Rather than match in the stream, write as individual records to HDFS,
     * then use Spark to join.
     * <p/>
     * ? what to do about device cwmps with no JSESSIONID?
     * If no JSESSIONID, then request and response appear together.
     *
     * @param rawEvent String
     * @return Message<InternetGatewayDevice>
     */
    public Message<InternetGatewayDevice> transform(String rawEvent) {
        ingestErrorCode = 0;
        ingestErrorMessage = "";
        ingestErrorDump = "";

        // Convert JSON String to Object
        try {
            response = mapper.readValue(rawEvent, TR69Response.class);
        } catch (JsonParseException e) {
            ingestErrorCode = 2;
            ingestErrorMessage = "Invalid JSON input: " + e.getMessage();
            ingestErrorDump = rawEvent;

        } catch (IOException e) {
            // probably an encoding issue, e.g. UTF-16 for chinese language
            ingestErrorCode = 2;
            ingestErrorMessage = "IO Exception : " + e.toString();
            ingestErrorDump = rawEvent;
        }
        device = new InternetGatewayDevice();

        try {
            // Ingest "response" and save to "device"
            extractMetaFields();
            extractSrcContent();
            extractRawTR69Response();
        } catch (Exception e) {
            ingestErrorCode = 2;
            ingestErrorMessage = "General Error: " + e.toString();
            ingestErrorDump = rawEvent;
        }

//        logger.info(ingestErrorMessage);
//        if (logger.isDebugEnabled()) {
//            logger.debug(ingestErrorDump);
//        }

        // Error details are passed in the headers
        return MessageBuilder
                .withPayload(device)
                .setHeader("TR69IngestErrorCode", String.valueOf(ingestErrorCode))
                .setHeader("TR69IngestErrorMessage", ingestErrorMessage)
                .setHeader("TR69IngestErrorDump", ingestErrorDump)
                .setHeader("TR69RawResponse", rawEvent)
                .build();
    }

    /**
     * Extracts src_content field from the input, which contains CWMP XML.
     */
    private void extractSrcContent() {
        String srcContent = response.getSrcContent();

        if (srcContent == null || srcContent.isEmpty()) {
            return;
        }

        String xml = ChunkedDataParser.read(srcContent);

        if (xml == null) {
            ingestErrorCode = 2;
            ingestErrorMessage = "Cannot parse src_content";
            return;
        }

        if (!(xml.contains("<soapenv:Envelope") && xml.contains("</soapenv:Envelope>")) &&
                !(xml.contains("<SOAP-ENV:Envelope") && xml.contains("</SOAP-ENV:Envelope>"))) {
            ingestErrorCode = 2;
            ingestErrorMessage = "Invalid/truncated SOAP Message";
            ingestErrorDump = xml;
            return;
        }

        // Normalise all version numbers in namespace so that multiple CWMP versions can be processed.
        // An alternative is to generate a class for each CWMP version, which is not desirable.
        xml = xml.replaceAll("urn:dslforum-org:cwmp-\\d+-\\d+", "urn:dslforum-org:cwmp-x-x");

        // Check XML tags and parse XML into object
        try {
            if (xml.contains("cwmp:Inform")) {
                ingestErrorMessage = "cwmp:Inform: ";
                mapCWMPInform(soapParser.getResult(xml, CWMPInform.class));
                ingestErrorMessage += "OK";

            } else if (xml.contains("cwmp:GetParameterValuesResponse")) {
                ingestErrorMessage = "cwmp:GetParameterValuesResponse: ";
                mapCWMPGetParameterValuesResponse(soapParser.getResult(xml, CWMPGetParameterValuesResponse.class));
                ingestErrorMessage += "OK";

            } else if (xml.contains("cwmp:TransferComplete")) {
                ingestErrorMessage = "cwmp:TransferComplete: ";
                mapCWMPTransferComplete(soapParser.getResult(xml, CWMPTransferComplete.class));
                ingestErrorMessage += "OK";

            } else {
                Pattern p = Pattern.compile("<cwmp:\\w+>");
                Matcher m = p.matcher(xml);
                if (m.find()) {
                    ingestErrorCode = 1;
                    ingestErrorMessage = "Unknown CWMP Command";
                    ingestErrorDump = m.group();
                } else {
                    ingestErrorCode = 2;
                    ingestErrorMessage = "Unknown XML - not a CWMP Command";
                    ingestErrorDump = xml;
                }
            }
        } catch (ParseException e) {
            ingestErrorDump = xml;
            ingestErrorMessage += "FAILED";
            //logger.warn(e.getMessage(), e);
        }
    }

    /**
     * Maps fields from CWMP Inform XML to InternetGatewayDevice
     */
    private void mapCWMPInform(CWMPInform cwmp) {
        if (cwmp == null) {
            return;
        }
        device.setCwmpMessageType("Inform");

        if (cwmp.getCurrentTime() != null) {
            device.setEventTime(cwmp.getCurrentTime().getTime());
        }
        device.setRetryCount(cwmp.getRetryCount());

        if (cwmp.getDeviceId() != null) {
            device.setManufacturer(cwmp.getDeviceId().getManufacturer());
            device.setManufacturerOUI(cwmp.getDeviceId().getOui());
            device.setProductClass(cwmp.getDeviceId().getProductClass());
            device.setSerialNumber(cwmp.getDeviceId().getSerialNumber());
        }

        // Store all events in EventStruct
        Map<String, String> event = new HashMap<String, String>();
        if (cwmp.getEvents() != null && !cwmp.getEvents().isEmpty()) {
            for (Event e : cwmp.getEvents()) {
                if (!isNullOrEmpty(e.getCode()) && !isNullOrEmpty(e.getCommandKey())) {
                    event.put(e.getCode(), e.getCommandKey());
                }
            }
        }
        device.setEvent(event);

        mapParameterValuesType(cwmp);
    }

    /**
     * Maps fields from CWMP GetParameterValuesResponse XML to InternetGatewayDevice
     */
    private void mapCWMPGetParameterValuesResponse(CWMPGetParameterValuesResponse cwmp) {
        if (cwmp == null) {
            return;
        }
        device.setCwmpMessageType("GetParameterValuesResponse");
        mapParameterValuesType(cwmp);
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * This mapping module is shared by CWMP Inform and CWMP GetParameterValuesResponse
     */
    private void mapParameterValuesType(CWMPParameterValuesType cwmp) {
        // Check first if the following 4 fields are already set with values from JSON
        if (isNullOrEmpty(device.getManufacturer()))
            device.setManufacturer(cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.Manufacturer"));

        if (isNullOrEmpty(device.getProductClass()))
            device.setProductClass(cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.ProductClass"));

        if (isNullOrEmpty(device.getSerialNumber()))
            device.setManufacturerOUI(cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.SerialNumber"));

        if (isNullOrEmpty(device.getManufacturerOUI()))
            device.setManufacturerOUI(cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.ManufacturerOUI"));

        device.setHardwareVersion(cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.HardwareVersion"));
        device.setSoftwareVersion(cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.SoftwareVersion"));
        device.setModemFirmwareVersion(cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.ModemFirmwareVersion"));

        // Check first if value already set from JSON
        if (isNullOrEmpty(device.getManagementServerUsername()))
            device.setManagementServerUsername(cwmp.getParameterValue("InternetGatewayDevice.ManagementServer.Username"));

        device.setManagementServerURL(cwmp.getParameterValue("InternetGatewayDevice.ManagementServer.ConnectionRequestURL"));

        String upTime = cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.UpTime");
        if (!isNullOrEmpty(upTime))
            device.setUpTime(Long.parseLong(upTime));

        String cpuUsage = cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.ProcessStatus.CPUUsage");
        if (!isNullOrEmpty(cpuUsage))
            device.setCpuUsage(Double.parseDouble(cpuUsage));

        String freeMemory = cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.MemoryStatus.Free");
        if (!isNullOrEmpty(freeMemory))
            device.setFreeMemory(Integer.parseInt(freeMemory));

        String totalMemory = cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.MemoryStatus.Total");
        if (!isNullOrEmpty(totalMemory))
            device.setTotalMemory(Integer.parseInt(totalMemory));

        String processNumberOfEntries = cwmp.getParameterValue("InternetGatewayDevice.DeviceInfo.ProcessStatus.ProcessNumberOfEntries");
        if (!isNullOrEmpty(processNumberOfEntries))
            device.setProcessNumberEntries(Integer.parseInt(processNumberOfEntries));

        device.setWanPPPConnectionUsername(cwmp.getParameterValueByPattern("InternetGatewayDevice.WANDevice.\\d+.WANConnectionDevice.\\d+.WANPPPConnection.\\d+.Username"));

        String lanDeviceTotalAssociations = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.TotalAssociations");
        if (!isNullOrEmpty(lanDeviceTotalAssociations))
            device.setLanDeviceTotalAssociations(Integer.parseInt(lanDeviceTotalAssociations));

        String lanDeviceTotalPskFailures = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.TotalPSKFailures");
        if (!isNullOrEmpty(lanDeviceTotalPskFailures))
            device.setLanDeviceTotalPskFailures(Integer.parseInt(lanDeviceTotalPskFailures));

        String lanDeviceAutoChannelEnable = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.AutoChannelEnable");
        if (!isNullOrEmpty(lanDeviceAutoChannelEnable))
            device.setLanDeviceAutoChannelEnable(lanDeviceAutoChannelEnable.equalsIgnoreCase("1"));

        String lanDeviceTotalBytesReceived = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.TotalBytesReceived");
        if (!isNullOrEmpty(lanDeviceTotalBytesReceived))
            device.setLanDeviceTotalBytesReceived(Long.parseLong(lanDeviceTotalBytesReceived));

        String lanDeviceTotalBytesSent = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.TotalBytesSent");
        if (!isNullOrEmpty(lanDeviceTotalBytesSent))
            device.setLanDeviceTotalBytesSent(Long.parseLong(lanDeviceTotalBytesReceived));

        String lanDeviceTotalPacketsReceived = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.TotalPacketsReceived");
        if (!isNullOrEmpty(lanDeviceTotalPacketsReceived))
            device.setLanDeviceTotalPacketsReceived(Long.parseLong(lanDeviceTotalPacketsReceived));

        String lanDeviceTotalPacketsSent = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.TotalPacketsSent");
        if (!isNullOrEmpty(lanDeviceTotalPacketsSent))
            device.setLanDeviceTotalPacketsSent(Long.parseLong(lanDeviceTotalPacketsSent));

        //  Sometimes there may be two (or more) possible fields where we can source the value
        String lanDeviceErrorsReceived = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.Stats.\\d+.ErrorsReceived");
        //if (isNullOrEmpty(lanDeviceErrorsReceived))
        //    lanDeviceErrorsReceived = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.LANEthernetInterfaceConfig.\\d+.Stats.ErrorsReceived");

        if (!isNullOrEmpty(lanDeviceErrorsReceived))
            device.setLanDeviceErrorsReceived(Long.parseLong(lanDeviceErrorsReceived));

        String lanDeviceErrorsSent = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.Stats.\\d+.ErrorsSent");
        //if (isNullOrEmpty(lanDeviceErrorsSent))
        //    lanDeviceErrorsSent = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.LANEthernetInterfaceConfig.\\d+.Stats.ErrorsSent");
        if (!isNullOrEmpty(lanDeviceErrorsSent))
            device.setLanDeviceErrorsSent(Long.parseLong(lanDeviceErrorsSent));

        String lanDeviceCrcError = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.CRCError");
        if (!isNullOrEmpty(lanDeviceCrcError))
            device.setLanDeviceCrcError(lanDeviceCrcError.equalsIgnoreCase("1"));

        String lanDeviceNumberFailedFrames = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.NumofFailedFrames");
        if (!isNullOrEmpty(lanDeviceNumberFailedFrames))
            device.setLanDeviceNumberFailedFrames(Long.parseLong(lanDeviceNumberFailedFrames));

        String lanDevicePacketsErrored = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.\\w+_PacketsErrored");
        if (!isNullOrEmpty(lanDevicePacketsErrored))
            device.setLanDevicePacketsErrored(Long.parseLong(lanDevicePacketsErrored));

        String lanDevicePacketsDropped = cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d+.WLANConfiguration.\\d+.\\w+_PacketsDropped");
        if (!isNullOrEmpty(lanDevicePacketsDropped))
            device.setLanDevicePacketsDropped(Long.parseLong(lanDevicePacketsDropped));

        /* Using a more generic regex such as w+ is more likely to bring more matches than matching individual digits such as d+
            but it also increases the chances of matching something not intended.
            We do not have full list of InternetGatewayDevice parameter names easily accessible
            so being too strict in regex may not populate enough data. */

        device.setLanDeviceStandard(cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\w+.Standard"));
        device.setLanDeviceBasicEncryptionModes(cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\w+.BasicEncryptionModes"));
        device.setLanDeviceWpaEncryptionModes(cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\w+.WPAEncryptionModes"));
        device.setLanDeviceAssociatedDeviceAuthenticationState(cwmp.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\w+.AssociatedDeviceAuthenticationState"));

        device.setWanDeviceUpstreamAttenuation(cwmp.getParameterValueByPattern("InternetGatewayDevice.WANDevice.\\w+.UpstreamAttenuation"));
        device.setWanDeviceDownstreamAttenuation(cwmp.getParameterValueByPattern("InternetGatewayDevice.WANDevice.\\w+.DownstreamAttenuation"));

        // Store every field in maps or lists so that we don't lose any info
        Map<String, String> parameterList = new HashMap<String, String>();
        if (cwmp.getParameterValues() != null && !cwmp.getParameterValues().isEmpty()) {
            for (ParameterValue p : cwmp.getParameterValues()) {
                parameterList.put(p.getName(), p.getValue());
            }
        }
        device.setParameterList(parameterList);

        // Device Summaries are stored as Arrays of Maps.
        // Each Map contains summary for a device.
        List<Map<String, String>> deviceSummaries = DeviceSummaryParser.parse(cwmp.getParameterValue("InternetGatewayDevice.DeviceSummary"));
        device.setDeviceSummaries(deviceSummaries);
    }

    /**
     * Maps fields from CWMP TransferComplete XML to InternetGatewayDevice
     */
    private void mapCWMPTransferComplete(CWMPTransferComplete cwmp) {
        device.setCwmpMessageType("TransferComplete");
        device.setTransferCommandKey(cwmp.getCommandKey());

        if (cwmp.getFaultStruct() != null) {
            device.setTransferFaultCode(cwmp.getFaultStruct().getFaultCode());
            device.setTransferFaultString(cwmp.getFaultStruct().getFaultString());
        }

        Date startTime = cwmp.getStartTime();
        if (startTime != null)
            device.setTransferStartTime(startTime.getTime());

        Date endTime = cwmp.getEndTime();
        if (endTime != null)
            device.setTransferEndTime(endTime.getTime());
    }

    private void extractMetaFields() {
        // response.getUser() gives you a serial number such as 0876FF-CP1048TT4C2,
        // appears the same value as InternetGatewayDevice.ManagementServer.Username
        device.setManagementServerUsername(response.getUser());

        /*
        The values in ParameterList seems to be bigger and more realistic
        so the approach to use the values from JSON has been disabled.

        device.setLanDeviceTotalBytesReceived(response.getBytesIn());
        device.setLanDeviceTotalBytesSent(response.getBytesOut());
        device.setLanDeviceTotalPacketsReceived(response.getAckPacketsIn());
        device.setLanDeviceTotalPacketsSent(response.getAckPacketsOut());
        */

        if (response.getTimestamp() != null) {
            device.setEventTime(response.getTimestamp().getTime());
        }
        // This will be overwritten later if the message is Inform.

        device.setLanDeviceHostActive(response.getStatus() == 200);
        device.setLanDeviceAssociatedDeviceIpAddress(response.getSrcIp()); // appears the same as response.cip
        device.setLanDeviceAssociatedDeviceMacAddress(response.getSrcMac());
        device.setLanDeviceAssociatedDeviceType(response.getSite()); // ??

        Map<String, String> httpHeaders = response.getHeaders();
        device.setHttpHeaders(httpHeaders);

        device.setSessionId(response.getSubHeaderValue("Cookie", "JSESSIONID"));
    }

    /**
     * Saves all JSON key-values into a map object
     */
    private void extractRawTR69Response() {
        Map<String, Object> rawTR69Response = new HashMap<String, Object>();

        rawTR69Response.put("ack_packets_in", response.getAckPacketsIn());
        rawTR69Response.put("ack_packets_device", response.getAckPacketsOut());
        rawTR69Response.put("bytes_in", response.getBytesIn());
        rawTR69Response.put("bytes", response.getBytes());
        rawTR69Response.put("bytes_device", response.getBytesOut());
        rawTR69Response.put("c_ip", response.getcIp());

        // Disabled to exclude lengthy XML data
        //rawTR69Response.put("dest_content", response.getDestContent());

        rawTR69Response.put("dest_ip", response.getDestIp());
        rawTR69Response.put("dest_port", response.getDestPort());
        rawTR69Response.put("endtime", response.getEndTime());
        rawTR69Response.put("http_comment", response.getHttpComment());
        rawTR69Response.put("http_content_type", response.getHttpContentType());
        rawTR69Response.put("http_method", response.getHttpMethod());
        rawTR69Response.put("reply_time", response.getReplyTime());
        rawTR69Response.put("request_time", response.getRequestTime());
        rawTR69Response.put("server", response.getServer());
        rawTR69Response.put("site", response.getSite());

        // Disabled to exclude lengthy XML data
        //rawTR69Response.put("src_content", response.getSrcContent());

        rawTR69Response.put("src_headers", response.getSrcHeaders());
        rawTR69Response.put("src_ip", response.getSrcIp());
        rawTR69Response.put("src_mac", response.getSrcMac());
        rawTR69Response.put("src_port", response.getSrcPort());
        rawTR69Response.put("status", response.getStatus());
        rawTR69Response.put("timestamp", response.getTimestamp());
        rawTR69Response.put("time_taken", response.getTimeTaken());
        rawTR69Response.put("transport", response.getTransport());
        rawTR69Response.put("uri", response.getUri());
        rawTR69Response.put("uri_path", response.getUriPath());
        rawTR69Response.put("user", response.getUser());

        // Throw away null-valued fields and convert all other objects to String
        // as we cannot write other object types to parquet file
        Map<String, String> rawTR69Response2 = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : rawTR69Response.entrySet()) {
            String k = entry.getKey();
            Object v = entry.getValue();
            if (v == null)
                continue;
            rawTR69Response2.put(k, v.toString());
        }

        device.setRawTR69Response(rawTR69Response2);
    }
}
