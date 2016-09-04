import cxp.ingest.util.ParseException;
import cxp.ingest.util.SoapParser;
import model.GetOrdersHistoryResponse;
import model.OrderStatus;
import model.Status;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by markmo on 9/04/2016.
 */
public class SoapParserTest {

    private SoapParser soapParser;

    @Before
    public void setUp() throws Exception {
        soapParser = new SoapParser();
    }

    @Test
    public void shouldGetOrdersHistoryRes() throws IOException, ParseException {
        //given
        String xml = getResponse();
        //when
        GetOrdersHistoryResponse result = soapParser.getResult(xml, GetOrdersHistoryResponse.class);

        //when
        final List<OrderStatus> orderStatuses = result.getOrderStatuses();
        assertThat(orderStatuses.size(), is(2));
        final OrderStatus first = orderStatuses.get(0);
        assertThat(first.getCustomerId(), is("customerId"));
        assertThat(first.getActionCode(), is("actionCode"));
        assertThat(first.getCreateBy(), is("2010-12-1"));
        assertThat(first.getStatus(), is(Status.None));
    }

    private String getResponse() throws IOException {
        final InputStream stream = SoapParserTest.class.getResourceAsStream("/GetOrdersHistoryResponse.xml");
        return IOUtils.toString(stream);
    }
}
