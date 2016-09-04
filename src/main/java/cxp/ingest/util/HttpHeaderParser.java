package cxp.ingest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by markmo on 13/04/2016.
 */
public class HttpHeaderParser {

    private static Logger logger = LoggerFactory.getLogger(HttpHeaderParser.class);

    public static final String HTTP_ELEMENT_CHARSET = "US-ASCII";

    /**
     * Return byte array from an (unchunked) input stream.
     * Stop reading when "\n" terminator encountered
     * If the stream ends before the line terminator is found,
     * the last part of the string will still be returned.
     * If no input data available, null is returned
     *
     * @param inputStream the stream to read from
     * @return a byte array from the stream
     * @throws IOException if an I/O problem occurs
     */
    public static byte[] readRawLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int ch;
        while ((ch = inputStream.read()) >= 0) {
            buf.write(ch);
            if (ch == '\n') {
                break;
            }
        }
        if (buf.size() == 0) {
            return null;
        }
        return buf.toByteArray();
    }

    /**
     * Read up to "\n" from an (unchunked) input stream.
     * If the stream ends before the line terminator is found,
     * the last part of the string will still be returned.
     * If no input data available, null is returned
     *
     * @param inputStream the stream to read from
     * @return a line from the stream
     * @throws IOException if an I/O problem occurs
     */

    public static String readLine(InputStream inputStream) throws IOException {
        byte[] rawdata = readRawLine(inputStream);
        if (rawdata == null) {
            return null;
        }
        int len = rawdata.length;
        int offset = 0;
        if (len > 0) {
            if (rawdata[len - 1] == '\n') {
                offset++;
                if (len > 1) {
                    if (rawdata[len - 2] == '\r') {
                        offset++;
                    }
                }
            }
        }
        return getString(rawdata, 0, len - offset);
    }

    /**
     * Parses headers from the given stream.  Headers with the same name are not
     * combined.
     *
     * @param is the stream to read headers from
     * @return an array of headers in the order in which they were parsed
     * @throws IOException if an IO error occurs while reading from the stream
     */
    public static Map<String, String> parseHeaders(InputStream is) throws IOException {
        Map<String, String> headers = new HashMap<String, String>();
        String name = null;
        StringBuffer value = null;
        for (; ; ) {
            String line = HttpHeaderParser.readLine(is);
            if ((line == null) || (line.length() < 1)) {
                break;
            }

            // Parse the header name and value
            // Check for folded headers first
            // Detect LWS-char see HTTP/1.0 or HTTP/1.1 Section 2.2
            // discussion on folded headers
            if ((line.charAt(0) == ' ') || (line.charAt(0) == '\t')) {
                // we have continuation folded header
                // so append value
                if (value != null) {
                    value.append(' ');
                    value.append(line.trim());
                }
            } else {
                // make sure we save the previous name,value pair if present
                if (name != null) {
                    headers.put(name, value.toString());
                }

                // Otherwise we should have normal HTTP header line
                // Parse the header name and value
                int colon = line.indexOf(":");
                if (colon < 0) {
                    //logger.warn("Unable to parse header: " + line);
                    continue;
                }
                name = line.substring(0, colon).trim();
                value = new StringBuffer(line.substring(colon + 1).trim());
            }

        }

        // make sure we save the last name,value pair if present
        if (name != null) {
            headers.put(name, value.toString());
        }

        return headers;
    }

    public static String getString(final byte[] data, int offset, int len) {
        if (data == null) {
            throw new IllegalArgumentException("Parameter may not be null");
        }
        try {
            return new String(data, offset, len, HTTP_ELEMENT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Unsupported encoding: " + HTTP_ELEMENT_CHARSET + ". System default encoding used.");
            }
            return new String(data, offset, len);
        }
    }
}
