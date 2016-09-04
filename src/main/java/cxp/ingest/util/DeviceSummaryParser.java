package cxp.ingest.util;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses content with the following format:
 *
 * InternetGatewayDevice:1.3[](Baseline:1, EthernetLAN:1, ADSLWAN:1, EthernetWAN:1, Bridging:1, Time:1, DeviceAssociation:1, WiFiLAN:1, QoS:1, IPPing:1), VoiceService:1.0[1](SIPEndpoint:1, TAEndpoint:1)
 *
 * Created by markmo on 10/04/2016.
 */
public class DeviceSummaryParser {

    public static List<Map<String, String>> parse(String input) {

        if (input == null)
            return null;

        List<Map<String, String>> summaries = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+):(\\d+\\.\\d+)\\[(\\d+)?\\]\\(((\\w+:\\d+(,\\s)?)*)\\)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String name = matcher.group(1);
            String version = matcher.group(2);
            String compStr = matcher.group(4);
            String[] comps = compStr.split(", ");

            Map<String, String> summary = new HashMap<String, String>();
            for (String comp : comps) {
                String[] parts = comp.split(":");
                summary.put(parts[0], parts[1]);   // Component:Status
            }

            summary.put("Name", name);
            summary.put("Version", version);
            summaries.add(summary);
        }
        return summaries;
    }
}
