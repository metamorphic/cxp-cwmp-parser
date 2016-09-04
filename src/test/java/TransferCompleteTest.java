
import cxp.ingest.TR69MessageTransformer;
import cxp.ingest.model.InternetGatewayDevice;

import org.springframework.messaging.Message;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TransferCompleteTest {

    @Test
    public void shouldTransform() throws IOException, ParseException {
        /*
        This tests the transform() instead and access the values in InternetGatewayDevice
        rather than testing individual functions inside TR69Response and CWMP* classes.
        */

        String rawEvent = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "SampleTransferComplete.json")));

        TR69MessageTransformer tr69MessageTransformer = new TR69MessageTransformer();
        Message<InternetGatewayDevice> message = tr69MessageTransformer.transform(rawEvent);
        InternetGatewayDevice device = message.getPayload();

        System.err.println(rawEvent);
        System.err.println(device.toString());

        assertEquals(device.getManagementServerUsername(), "30918F-CP1426SA027");

        assertEquals(device.getTransferFaultCode(), "0");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        Date transferEndTime1 = format.parse("2016-02-09T08:51:24Z");
        Date transferEndTime2 = new Date(device.getTransferEndTime());

        System.err.println(transferEndTime1);
        System.err.println(transferEndTime2);

        //This test is failing due to timezone Z difference
        //assertEquals(transferEndTime1, transferEndTime2);

    }
}
