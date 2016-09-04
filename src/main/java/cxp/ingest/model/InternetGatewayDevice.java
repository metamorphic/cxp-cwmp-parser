package cxp.ingest.model;

import java.util.List;
import java.util.ArrayList;

import java.util.Map;
import java.util.HashMap;

import org.apache.avro.reflect.Nullable;

/**
 * This is all fairly flat based on suggested fields from Alcatel.
 *
 * As different devices return different information, include nested
 * structure to capture variable information.
 *
 * Can the hdfs-dataset module of Spring XD handle nested structures?
 * The hdfs-dataset module uses the Kite SDK kite-data functionality
 * for this. (Ans: Not supported yet.)
 *
 * Created by markmo on 4/04/2016.
 */
public class InternetGatewayDevice {

    // Populated by TR60 Message Transformer
    private @Nullable String cwmpMessageType;

    // All key-value pairs from JSON is preserved here
    private @Nullable Map<String, String> rawTR69Response;

    // Fields from src_header
    private @Nullable Map<String, String> httpHeaders;
    private @Nullable String sessionId;

    // Fields from Inform + GetParameterValuesResponse Messages
    private @Nullable long eventTime;                       //<CurrentTime> in epochs
    private @Nullable String manufacturer;                  //DeviceId or InternetGatewayDevice.DeviceInfo.Manufacturer
    private @Nullable String manufacturerOUI;               //DeviceId or InternetGatewayDevice.DeviceInfo.ManufacturerOUI
    private @Nullable String productClass;                  //DeviceId or InternetGatewayDevice.DeviceInfo.ProductClass
    private @Nullable String serialNumber;                  //DeviceId or InternetGatewayDevice.DeviceInfo.SerialNumber

    // According to TR-069 spec, the ACS does not place any significance on the order of events
    private @Nullable Map<String, String> event;            //<EventStruct> from Inform
    private @Nullable int retryCount;                       //<RetryCount> from Inform
    private @Nullable Map<String, String> parameterList;    //<ParameterValueStruct>

    // Fields from TransferComplete Message
    private @Nullable String transferCommandKey;            //<CommandKey>
    private @Nullable String transferFaultCode;             //<FaultCode>
    private @Nullable String transferFaultString;           //<FaultString>
    private @Nullable long transferStartTime;               //<StartTime> in epochs
    private @Nullable long transferEndTime;                 //<CompleteTime> in epochs

    // Fields inside ParameterList
    private @Nullable List<Map<String, String>> deviceSummaries;    //InternetGatewayDevice.DeviceSummary

    // Fields inside ParameterList
    private @Nullable String wanPPPConnectionUsername;          //InternetGatewayDevice.WANDevice.%.WANConnectionDevice.%.WANPPPConnection.%.Username
    private @Nullable String hardwareVersion;                   //InternetGatewayDevice.DeviceInfo.HardwareVersion
    private @Nullable String softwareVersion;                   //InternetGatewayDevice.DeviceInfo.SoftwareVersion
    private @Nullable String modemFirmwareVersion;              //InternetGatewayDevice.DeviceInfo.ModemFirmwareVersion
    private @Nullable String managementServerURL;               //InternetGatewayDevice.ManagementServer.ConnectionRequestURL
    private @Nullable String managementServerUsername;          //InternetGatewayDevice.ManagementServer.Username
    private @Nullable long upTime;                               //InternetGatewayDevice.DeviceInfo.UpTime
    private @Nullable long lanDeviceTotalAssociations;           //InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.TotalAssociations
    private @Nullable long lanDeviceTotalPskFailures;            //InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.TotalPSKFailures
    private @Nullable boolean lanDeviceAutoChannelEnable;       //InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.AutoChannelEnable

    // InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.Stats.{i}.ErrorsReceived
    // InternetGatewayDevice.LANDevice.1.LANEthernetInterfaceConfig.1.Stats.ErrorsReceived
    private @Nullable long lanDeviceErrorsReceived;

    // InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.Stats.{i}.ErrorsSent
    // InternetGatewayDevice.LANDevice.1.LANEthernetInterfaceConfig.1.Stats.ErrorsSent
    private @Nullable long lanDeviceErrorsSent;

    // InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.CRCError
    // Note: this field is not yet seen in test data so far
    private @Nullable boolean lanDeviceCrcError;

    // InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.NumofFailedFrames
    // Note: this field is not yet seen in test data so far
    private @Nullable long lanDeviceNumberFailedFrames;

    private @Nullable long lanDevicePacketsErrored;              //InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.xxxxx_PacketsErrored
    private @Nullable long lanDevicePacketsDropped;              //InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.xxxxx_PacketsDropped
    private @Nullable long lanDeviceTotalBytesReceived;          //InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.TotalBytesReceived
    private @Nullable long lanDeviceTotalBytesSent;              //InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.TotalBytesSent
    private @Nullable long lanDeviceTotalPacketsReceived;        //InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.TotalPacketsReceived
    private @Nullable long lanDeviceTotalPacketsSent;            //InternetGatewayDevice.LANDevice.{i}.WLANConfiguration.{i}.TotalPacketsSent
    private @Nullable long wanDeviceTotalBytesReceived;          //InternetGatewayDevice.WANDevice.{i}.WANEthernetInterfaceConfig.Stats.BytesReceived
    private @Nullable long wanDeviceTotalBytesSent;              //InternetGatewayDevice.WANDevice.{i}.WANEthernetInterfaceConfig.Stats.BytesSent
    private @Nullable long wanDeviceTotalPacketsReceived;        //InternetGatewayDevice.WANDevice.{i}.WANEthernetInterfaceConfig.Stats.PacketsReceived
    private @Nullable long wanDeviceTotalPacketsSent;            //InternetGatewayDevice.WANDevice.{i}.WANEthernetInterfaceConfig.Stats.PacketsSent

    private @Nullable String lanDeviceStandard;                 //InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.Standard
    private @Nullable String lanDeviceBasicEncryptionModes;     //InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.BasicEncryptionModes
    private @Nullable String lanDeviceWpaEncryptionModes;       //InternetGatewayDevice.LANDevice.%.WLANConfiguration.%.WPAEncryptionModes
    private @Nullable int totalMemory;                          //InternetGatewayDevice.DeviceInfo.MemoryStatus.Total
    private @Nullable int freeMemory;                           //InternetGatewayDevice.DeviceInfo.MemoryStatus.Free
    private @Nullable double cpuUsage;                          //InternetGatewayDevice.DeviceInfo.ProcessStatus.CPUUsage
    private @Nullable long processNumberEntries;                 //InternetGatewayDevice.DeviceInfo.ProcessStatus.ProcessNumberOfEntries
    private @Nullable String wanDeviceUpstreamAttenuation;      //InternetGatewayDevice.WANDevice.{i}.WAN-DSLInterfaceConfig.UpstreamAttenuation
    private @Nullable String wanDeviceDownstreamAttenuation;    //InternetGatewayDevice.WANDevice.{i}.WAN-DSLInterfaceConfig.DownstreamAttenuation
    private @Nullable boolean lanDeviceHostActive;                  //status.  InternetGatewayDevice.LANDevice.%.Hosts.Host.%.Active
    private @Nullable String lanDeviceAssociatedDeviceMacAddress;   //src_mac. Alternatvely InternetGatewayDevice.LANDevice.{i}.WLAN-Configuration.{i}.AssociatedDevice.{i}.AssociatedDeviceMACAddress (not used at the moment)
    private @Nullable String lanDeviceAssociatedDeviceIpAddress;    //src_ip.  Alternatively InternetGatewayDevice.LANDevice.{i}.WLAN-Configuration.{i}.AssociatedDevice.{i}.AssociatedDeviceIPAddress (not used at the moment)
    private @Nullable String lanDeviceAssociatedDeviceAuthenticationState;  //InternetGatewayDevice.LANDevice.{i}.WLAN-Configuration.{i}.AssociatedDevice.{i}.AssociatedDeviceAuthenticationState
    private @Nullable String lanDeviceAssociatedDeviceType;         //site (why?)

    /* Some (not all) other keys found in the data, which may be brought up as a top level field if required
        InternetGatewayDevice.WANDevice.1.WANDSLInterfaceConfig.Stats.CurrentDay.CRCErrors
        "InternetGatewayDevice.DeviceInfo.ProvisioningCode"
        "InternetGatewayDevice.DeviceInfo.SpecVersion"
        "InternetGatewayDevice.ManagementServer.ParameterKey"
        "InternetGatewayDevice.Services.X_TELSTRA_IWIFI.1.ExtendedStatus"
    */

    public String getCwmpMessageType() {
        return cwmpMessageType;
    }

    public void setCwmpMessageType(String cwmpMessageType) {
        this.cwmpMessageType = cwmpMessageType;
    }

    public Map<String, String> getRawTR69Response() {
        return rawTR69Response;
    }

    public void setRawTR69Response(Map<String, String> rawTR69Response) {
        this.rawTR69Response = rawTR69Response;
    }

    public Map<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    public void setHttpHeaders(Map<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getManufacturerOUI() {
        return manufacturerOUI;
    }

    public void setManufacturerOUI(String manufacturerOUI) {
        this.manufacturerOUI = manufacturerOUI;
    }

    public String getProductClass() {
        return productClass;
    }

    public void setProductClass(String productClass) {
        this.productClass = productClass;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /* Fields within Parameter List */

    public String getWanPPPConnectionUsername() {
        return wanPPPConnectionUsername;
    }

    public void setWanPPPConnectionUsername(String wanPPPConnectionUsername) {
        this.wanPPPConnectionUsername = wanPPPConnectionUsername;
    }

    public String getHardwareVersion() {
        return hardwareVersion;
    }

    public void setHardwareVersion(String hardwareVersion) {
        this.hardwareVersion = hardwareVersion;
    }

    public String getSoftwareVersion() {
        return softwareVersion;
    }

    public void setSoftwareVersion(String softwareVersion) {
        this.softwareVersion = softwareVersion;
    }

    public String getModemFirmwareVersion() {
        return modemFirmwareVersion;
    }

    public void setModemFirmwareVersion(String modemFirmwareVersion) {
        this.modemFirmwareVersion = modemFirmwareVersion;
    }

    public String getManagementServerURL() {
        return managementServerURL;
    }

    public void setManagementServerURL(String managementServerURL) {
        this.managementServerURL = managementServerURL;
    }

    public String getManagementServerUsername() {
        return managementServerUsername;
    }

    public void setManagementServerUsername(String managementServerUsername) {
        this.managementServerUsername = managementServerUsername;
    }

    public long getUpTime() {
        return upTime;
    }

    public void setUpTime(long upTime) {
        this.upTime = upTime;
    }

    public long getLanDeviceTotalAssociations() {
        return lanDeviceTotalAssociations;
    }

    public void setLanDeviceTotalAssociations(long lanDeviceTotalAssociations) {
        this.lanDeviceTotalAssociations = lanDeviceTotalAssociations;
    }

    public long getLanDeviceTotalPskFailures() {
        return lanDeviceTotalPskFailures;
    }

    public void setLanDeviceTotalPskFailures(long lanDeviceTotalPskFailures) {
        this.lanDeviceTotalPskFailures = lanDeviceTotalPskFailures;
    }

    public boolean isLanDeviceAutoChannelEnable() {
        return lanDeviceAutoChannelEnable;
    }

    public void setLanDeviceAutoChannelEnable(boolean lanDeviceAutoChannelEnable) {
        this.lanDeviceAutoChannelEnable = lanDeviceAutoChannelEnable;
    }

    public long getLanDeviceErrorsReceived() {
        return lanDeviceErrorsReceived;
    }

    public void setLanDeviceErrorsReceived(long lanDeviceErrorsReceived) {
        this.lanDeviceErrorsReceived = lanDeviceErrorsReceived;
    }

    public long getLanDeviceErrorsSent() {
        return lanDeviceErrorsSent;
    }

    public void setLanDeviceErrorsSent(long lanDeviceErrorsSent) {
        this.lanDeviceErrorsSent = lanDeviceErrorsSent;
    }

    public boolean isLanDeviceCrcError() {
        return lanDeviceCrcError;
    }

    public void setLanDeviceCrcError(boolean lanDeviceCrcError) {
        this.lanDeviceCrcError = lanDeviceCrcError;
    }

    public long getLanDeviceNumberFailedFrames() {
        return lanDeviceNumberFailedFrames;
    }

    public void setLanDeviceNumberFailedFrames(long lanDeviceNumberFailedFrames) {
        this.lanDeviceNumberFailedFrames = lanDeviceNumberFailedFrames;
    }

    public long getLanDevicePacketsErrored() {
        return lanDevicePacketsErrored;
    }

    public void setLanDevicePacketsErrored(long lanDevicePacketsErrored) {
        this.lanDevicePacketsErrored = lanDevicePacketsErrored;
    }

    public long getLanDevicePacketsDropped() {
        return lanDevicePacketsDropped;
    }

    public void setLanDevicePacketsDropped(long lanDevicePacketsDropped) {
        this.lanDevicePacketsDropped = lanDevicePacketsDropped;
    }

    public long getLanDeviceTotalBytesReceived() {
        return lanDeviceTotalBytesReceived;
    }

    public void setLanDeviceTotalBytesReceived(long lanDeviceTotalBytesReceived) {
        this.lanDeviceTotalBytesReceived = lanDeviceTotalBytesReceived;
    }

    public long getLanDeviceTotalBytesSent() {
        return lanDeviceTotalBytesSent;
    }

    public void setLanDeviceTotalBytesSent(long lanDeviceTotalBytesSent) {
        this.lanDeviceTotalBytesSent = lanDeviceTotalBytesSent;
    }

    public long getLanDeviceTotalPacketsReceived() {
        return lanDeviceTotalPacketsReceived;
    }

    public void setLanDeviceTotalPacketsReceived(long lanDeviceTotalPacketsReceived) {
        this.lanDeviceTotalPacketsReceived = lanDeviceTotalPacketsReceived;
    }

    public long getLanDeviceTotalPacketsSent() {
        return lanDeviceTotalPacketsSent;
    }

    public void setLanDeviceTotalPacketsSent(long lanDeviceTotalPacketsSent) {
        this.lanDeviceTotalPacketsSent = lanDeviceTotalPacketsSent;
    }

    public long getWanDeviceTotalBytesReceived() {
        return wanDeviceTotalBytesReceived;
    }

    public void setWanDeviceTotalBytesReceived(long wanDeviceTotalBytesReceived) {
        this.wanDeviceTotalBytesReceived = wanDeviceTotalBytesReceived;
    }

    public long getWanDeviceTotalBytesSent() {
        return wanDeviceTotalBytesSent;
    }

    public void setWanDeviceTotalBytesSent(long wanDeviceTotalBytesSent) {
        this.wanDeviceTotalBytesSent = wanDeviceTotalBytesSent;
    }

    public long getWanDeviceTotalPacketsReceived() {
        return wanDeviceTotalPacketsReceived;
    }

    public void setWanDeviceTotalPacketsReceived(long wanDeviceTotalPacketsReceived) {
        this.wanDeviceTotalPacketsReceived = wanDeviceTotalPacketsReceived;
    }

    public long getWanDeviceTotalPacketsSent() {
        return wanDeviceTotalPacketsSent;
    }

    public void setWanDeviceTotalPacketsSent(long wanDeviceTotalPacketsSent) {
        this.wanDeviceTotalPacketsSent = wanDeviceTotalPacketsSent;
    }

    public String getLanDeviceStandard() {
        return lanDeviceStandard;
    }

    public void setLanDeviceStandard(String lanDeviceStandard) {
        this.lanDeviceStandard = lanDeviceStandard;
    }

    public String getLanDeviceBasicEncryptionModes() {
        return lanDeviceBasicEncryptionModes;
    }

    public void setLanDeviceBasicEncryptionModes(String lanDeviceBasicEncryptionModes) {
        this.lanDeviceBasicEncryptionModes = lanDeviceBasicEncryptionModes;
    }

    public String getLanDeviceWpaEncryptionModes() {
        return lanDeviceWpaEncryptionModes;
    }

    public void setLanDeviceWpaEncryptionModes(String lanDeviceWpaEncryptionModes) {
        this.lanDeviceWpaEncryptionModes = lanDeviceWpaEncryptionModes;
    }

    public int getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(int totalMemory) {
        this.totalMemory = totalMemory;
    }

    public int getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(int freeMemory) {
        this.freeMemory = freeMemory;
    }

    public double getCpuUsage() {
        return cpuUsage;
    }

    public void setCpuUsage(double cpuUsage) {
        this.cpuUsage = cpuUsage;
    }

    public long getProcessNumberEntries() {
        return processNumberEntries;
    }

    public void setProcessNumberEntries(long processNumberEntries) {
        this.processNumberEntries = processNumberEntries;
    }

    public String getWanDeviceUpstreamAttenuation() {
        return wanDeviceUpstreamAttenuation;
    }

    public void setWanDeviceUpstreamAttenuation(String wanDeviceUpstreamAttenuation) {
        this.wanDeviceUpstreamAttenuation = wanDeviceUpstreamAttenuation;
    }

    public String getWanDeviceDownstreamAttenuation() {
        return wanDeviceDownstreamAttenuation;
    }

    public void setWanDeviceDownstreamAttenuation(String wanDeviceDownstreamAttenuation) {
        this.wanDeviceDownstreamAttenuation = wanDeviceDownstreamAttenuation;
    }

    public boolean isLanDeviceHostActive() {
        return lanDeviceHostActive;
    }

    public void setLanDeviceHostActive(boolean lanDeviceHostActive) {
        this.lanDeviceHostActive = lanDeviceHostActive;
    }

    public String getLanDeviceAssociatedDeviceMacAddress() {
        return lanDeviceAssociatedDeviceMacAddress;
    }

    public void setLanDeviceAssociatedDeviceMacAddress(String lanDeviceAssociatedDeviceMacAddress) {
        this.lanDeviceAssociatedDeviceMacAddress = lanDeviceAssociatedDeviceMacAddress;
    }

    public String getLanDeviceAssociatedDeviceIpAddress() {
        return lanDeviceAssociatedDeviceIpAddress;
    }

    public void setLanDeviceAssociatedDeviceIpAddress(String lanDeviceAssociatedDeviceIpAddress) {
        this.lanDeviceAssociatedDeviceIpAddress = lanDeviceAssociatedDeviceIpAddress;
    }

    public String getLanDeviceAssociatedDeviceAuthenticationState() {
        return lanDeviceAssociatedDeviceAuthenticationState;
    }

    public void setLanDeviceAssociatedDeviceAuthenticationState(String lanDeviceAssociatedDeviceAuthenticationState) {
        this.lanDeviceAssociatedDeviceAuthenticationState = lanDeviceAssociatedDeviceAuthenticationState;
    }

    public String getLanDeviceAssociatedDeviceType() {
        return lanDeviceAssociatedDeviceType;
    }

    public void setLanDeviceAssociatedDeviceType(String lanDeviceAssociatedDeviceType) {
        this.lanDeviceAssociatedDeviceType = lanDeviceAssociatedDeviceType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public Map<String,String> getParameterList() {
        return parameterList;
    }

    public void setParameterList(Map<String,String> parameterList) {
        this.parameterList = parameterList;
    }

    public String getTransferCommandKey() {
        return transferCommandKey;
    }

    public void setTransferCommandKey(String transferCommandKey) {
        this.transferCommandKey = transferCommandKey;
    }

    public String getTransferFaultCode() {
        return transferFaultCode;
    }

    public void setTransferFaultCode(String transferFaultCode) {
        this.transferFaultCode = transferFaultCode;
    }

    public String getTransferFaultString() {
        return transferFaultString;
    }

    public void setTransferFaultString(String transferFaultString) {
        this.transferFaultString = transferFaultString;
    }

    public long getTransferStartTime() {
        return transferStartTime;
    }

    public void setTransferStartTime(long transferStartTime) {
        this.transferStartTime= transferStartTime;
    }

    public long getTransferEndTime() {
        return transferEndTime;
    }

    public void setTransferEndTime(long transferEndTime) {
        this.transferEndTime= transferEndTime;
    }

    public Map<String, String> getEvent() {
        return event;
    }

    public void setEvent(Map<String, String> event) {
        this.event = event;
    }

    public List<Map<String,String>> getDeviceSummaries() {
        return deviceSummaries;
    }

    public void setDeviceSummaries(List<Map<String,String>> deviceSummaries) {
        this.deviceSummaries = deviceSummaries;
    }

    public String toString() {

        return "InternetGatewayDevice{" +
                "cwmpMessageType=" + cwmpMessageType +
                ", manufacturer=" + manufacturer +
                ", manufacturerOUI=" + manufacturer +
                ", productClass=" + productClass +
                ", serialNumber=" + serialNumber +
                ", (other fields omitted)" +
                '}';
    }
}
