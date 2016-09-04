import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cxp.ingest.model.CWMPInform;
import cxp.ingest.model.SplunkSearchResult;
import cxp.ingest.model.TR69Response;
import cxp.ingest.util.*;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;

/**
 * Created by markmo on 9/04/2016.
 */
public class JsonParseTest {

    private SoapParser soapParser;

    private DeviceSummaryParser summaryParser;

    @Before
    public void setUp() throws Exception {
        soapParser = new SoapParser();
        summaryParser = new DeviceSummaryParser();
    }

    @Test
    public void shouldParseJson() throws ParseException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("splunk_results.json");
        List<SplunkSearchResult> searchResults = mapper.readValue(inputStream, new TypeReference<List<SplunkSearchResult>>(){});

        SplunkSearchResult first = searchResults.get(0);
        assertThat(first.getCode(), is("231:5646873"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(first.getTime());
        assertThat(cal.get(Calendar.DAY_OF_MONTH), is(8));

        TR69Response response = mapper.readValue(first.getRaw(), TR69Response.class);

        cal.setTime(response.getEndTime());
        assertThat(cal.get(Calendar.DAY_OF_MONTH), is(8));
        assertThat(response.getBytes(), is(3695L));
        assertThat(response.getSite(), containsString("bigpond"));


        InputStream stream = new ByteArrayInputStream(response.getSrcHeaders().getBytes(StandardCharsets.UTF_8));
        Map<String, String> headers = HttpHeaderParser.parseHeaders(stream);

//        for (Map.Entry<String, String> header : headers.entrySet()) {
//            System.out.println(header.getKey() + ": " + header.getValue());
//        }

        assertThat(headers.get("Transfer-Encoding"), is("chunked"));

        String xml = ChunkedDataParser.read(response.getSrcContent());

        // Normalise all version numbers in namespace so that multiple cwmp versions can be processed.
        // An alternative is to generate a class for each CWMP version which is not desirable.
        xml = xml.replaceAll("urn:dslforum-org:cwmp-\\d-\\d", "urn:dslforum-org:cwmp-x-x");

        //System.out.println(xml);

        CWMPInform model = soapParser.getResult(xml, CWMPInform.class);

        //System.out.println(model);

        assertThat(model.getParameterValue("InternetGatewayDevice.DeviceInfo.HardwareVersion"), is("DANT-Y"));

        //TODO: Use List<Map> instead of DeviceSummary Object
        //DeviceSummaries summaries = summaryParser.parse(model.getParameterValue("InternetGatewayDevice.DeviceSummary"));
        //assertThat(summaries.getSummary("InternetGatewayDevice").getComponent("EthernetLAN").getStatus(), is(1));
    }
}
