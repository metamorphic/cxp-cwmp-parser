
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import cxp.ingest.SplunkEventToRawTransformer;
import cxp.ingest.model.SplunkSearchResult;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SplunkEventToRawTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void shouldExtractRaw() throws IOException {

        //  Map the json and then extract _raw
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("splunk_result_one.json");
        List<SplunkSearchResult> searchResults = mapper.readValue(inputStream, new TypeReference<List<SplunkSearchResult>>(){});
        SplunkSearchResult first = searchResults.get(0);
        String raw_1 = first.getRaw();
        // Read the same jsong as String and then extract _raw

        String contents = new String(Files.readAllBytes(Paths.get("src", "test", "resources", "splunk_result_one.json")));
        SplunkEventToRawTransformer splunkEventToRawTransformer = new SplunkEventToRawTransformer();

        String raw_2 = splunkEventToRawTransformer.transform(contents);

        //Remove everything that has been escaped in Raw
        String raw_3 = raw_2.replace("\\\"","\"").replace("\\\\","\\").replace("\\/","/");

        System.err.println(raw_1);
        System.err.println(raw_2);
        System.err.println(raw_3);

        assertEquals(raw_1, raw_3);
    }
}
