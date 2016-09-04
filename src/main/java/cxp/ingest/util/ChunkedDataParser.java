package cxp.ingest.util;

import com.google.common.io.ByteStreams;
import sun.net.www.http.ChunkedInputStream;
import sun.net.www.http.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Reads a String using Chunked transfer encoding.
 *
 * In the following example, three chunks of length 4, 5 and 14 are shown.
 * The chunk size is transferred as a hexadecimal number followed by \r\n
 * as a line separator, following a chunk of data of the given size.
 *
 * <pre>
 *     4\r\n
 *     Wiki\r\n
 *     5\r\n
 *     pedia\r\n
 *     E\r\n
 *      in\r\n
 *     \r\n
 *     chunks.\r\n
 *     0\r\n
 *     \r\n
 * </pre>
 *
 * Decoded data:
 * <pre>
 *     Wikipedia in
 *
 *     chunks
 * </pre>
 *
 * See https://en.wikipedia.org/wiki/Chunked_transfer_encoding
 *
 * Created by markmo on 14/04/2016.
 */
public class ChunkedDataParser {

    public static String read(String input) {
        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            ChunkedInputStream cis = new ChunkedInputStream(stream, new HttpClient() {}, null);
            return readFully(cis);
        } catch (IOException e) {
            return null;
        }
    }

    public static String readFully(InputStream stream) throws IOException {
        return new String(ByteStreams.toByteArray(stream));
    }

}
