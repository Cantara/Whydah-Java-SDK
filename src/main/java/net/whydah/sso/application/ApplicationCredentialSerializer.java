package net.whydah.sso.application;

import net.whydah.sso.application.types.ApplicationCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-07-01
 */
public class ApplicationCredentialSerializer {
    private static final Logger log = LoggerFactory.getLogger(ApplicationCredentialSerializer.class);

    public static String toXML(ApplicationCredential applicationCredential) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
                "<applicationcredential>\n" +
                "    <params>\n" +
                "        <applicationID>" + applicationCredential.getApplicationID() + "</applicationID>\n" +
                "        <applicationSecret>" + applicationCredential.getApplicationSecret() + "</applicationSecret>\n" +
                "    </params> \n" +
                "</applicationcredential>\n";
    }

    public static ApplicationCredential fromXml(InputStream input) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document dDoc = builder.parse(input);
            XPath xPath = XPathFactory.newInstance().newXPath();
            String applicationId = (String) xPath.evaluate("//applicationID", dDoc, XPathConstants.STRING);
            String applicationSecret = (String) xPath.evaluate("//applicationSecret", dDoc, XPathConstants.STRING);
            return new ApplicationCredential(applicationId, applicationSecret);
        } catch (SAXParseException pe) {
            String msg = "fromXml failed due to invalid xml. SAXParseException: " + pe.getMessage();
            log.debug(msg);
            throw new IllegalArgumentException(msg);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing ApplicationCredential from xml InputStream", e);
        }
    }
}
