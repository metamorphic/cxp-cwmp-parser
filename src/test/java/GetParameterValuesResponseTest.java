import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cxp.ingest.model.CWMPGetParameterValuesResponse;
import cxp.ingest.model.SplunkSearchResult;
import cxp.ingest.model.TR69Response;
import cxp.ingest.util.ChunkedDataParser;
import cxp.ingest.util.ParseException;
import cxp.ingest.util.SoapParser;
import cxp.ingest.util.HttpHeaderParser;
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

public class GetParameterValuesResponseTest {

    private SoapParser soapParser;

    @Before
    public void setUp() throws Exception {
        soapParser = new SoapParser();
    }

    @Test
    public void shouldParseJson() throws IOException, ParseException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("splunk_results.json");
        List<SplunkSearchResult> searchResults = mapper.readValue(inputStream, new TypeReference<List<SplunkSearchResult>>(){});

        // Get the GetParameterValuesResponse message
        SplunkSearchResult third = searchResults.get(2);
        TR69Response response = mapper.readValue(third.getRaw(), TR69Response.class);

        InputStream stream = new ByteArrayInputStream(response.getSrcHeaders().getBytes(StandardCharsets.UTF_8));
        Map<String, String> headers = HttpHeaderParser.parseHeaders(stream);

        assertThat(headers.get("Transfer-Encoding"), is("chunked"));

        String xml = ChunkedDataParser.read(response.getSrcContent());

        // Normalise all version numbers in namespace so that multiple cwmp versions can be processed.
        // An alternative is to generate a class for each CWMP version which is not desirable.
        xml = xml.replaceAll("urn:dslforum-org:cwmp-\\d-\\d", "urn:dslforum-org:cwmp-x-x");

        CWMPGetParameterValuesResponse model = soapParser.getResult(xml, CWMPGetParameterValuesResponse.class);

        assertThat(model.getParameterValue("InternetGatewayDevice.DeviceInfo.HardwareVersion"), is("DANT-T"));

        assertThat(model.getParameterValue("InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.TotalPSKFailures"), is("5"));

        assertThat(model.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d.WLANConfiguration.\\d.TotalPSKFailures"), is("5"));

        assertThat(model.getParameterValueByPattern("InternetGatewayDevice.WANDevice.\\d.WANConnectionDevice.\\d.WANPPPConnection.\\d.Username"), is("abcde12345@bigpond.com"));

        assertThat(model.getParameterValueByPattern("InternetGatewayDevice.LANDevice.\\d.WLANConfiguration.\\d.TotalAssociations"), is("8"));
    }
}
