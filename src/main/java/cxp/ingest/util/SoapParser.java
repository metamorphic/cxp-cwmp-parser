package cxp.ingest.util;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created by markmo on 9/04/2016.
 */
public class SoapParser {

    public <T> T getResult(String xml, Class<T> type) throws ParseException {
        final Node soapBody = getSoapBody(xml);
        return getInstance(soapBody, type);
    }

    private Node getSoapBody(String xml) throws ParseException {
        SOAPMessage message = getSoapMessage(xml);
        return getFirstElement(message);
    }

    private SOAPMessage getSoapMessage(String xml) throws ParseException {
        try {
            MessageFactory factory = MessageFactory.newInstance();
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            return factory.createMessage(new MimeHeaders(), byteArrayInputStream);
        } catch (SOAPException | IOException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    private Node getFirstElement(SOAPMessage message) throws ParseException {
        final NodeList childNodes;
        try {
            childNodes = message.getSOAPBody().getChildNodes();
        } catch (SOAPException e) {
            throw new ParseException(e.getMessage(), e);
        }
        Node firstElement = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            if (childNodes.item(i) instanceof Element) {
                firstElement = childNodes.item(i);
                break;
            }
        }
        return firstElement;
    }

    @SuppressWarnings("unchecked")
    private <T> T getInstance(Node body, Class<T> type) throws ParseException {
        try {
            JAXBContext jc = JAXBContext.newInstance(type);
            Unmarshaller u = jc.createUnmarshaller();
            return (T) u.unmarshal(body);
        } catch (JAXBException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }
}
