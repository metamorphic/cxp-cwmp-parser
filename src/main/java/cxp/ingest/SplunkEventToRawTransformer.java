
package cxp.ingest;

public class SplunkEventToRawTransformer {

    public String transform(String splunkEvent) {

        /*
        The input String would be equivalent to SplunkSearchResult/SplunkEvent.
        However due to unescaped double quotes in _raw value, object mapper fails to parse the JSON.
        This module takes the JSON as string and parse out the _raw field without the use of object mapper.
        The output string is also another JSON, passed to the next module.
        */

        String[] dataIn = splunkEvent.split("_raw\"\\s*:\\s*\"");

        if (dataIn.length < 1)
            return "";

        String temp = dataIn[1];
        int level = 0;
        StringBuilder raw = new StringBuilder();

        //The end of the _raw string is identified by scanning a number of open and close brackets {}
        for (int i = 0; i < temp.length(); i++) {
            char c = temp.charAt(i);
            if (c == '{')
                level++;
            else if (c == '}')
                level--;
            raw.append(c);
            if (level == 0)
                break;
        }
        return raw.toString();
    }
}
