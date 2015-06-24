package net.whydah.sso.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

/**
 * Created by totto on 12/3/14.
 */
public class ApplicationXpathHelper {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationXpathHelper.class);



    public static  String getAppTokenIdFromAppToken(String appTokenXML) {
        String appTokenId = "";
        if (appTokenXML == null) {
            logger.debug("roleXml was empty, so returning empty orgName.");
        } else {
            String expression = "/applicationtoken/params/applicationtokenID[1]";
            appTokenId = findValue(appTokenXML, expression);
        }
        return appTokenId;
    }

    public static  String getExpiresFromAppToken(String appTokenXML) {
        String expires = "";
        if (appTokenXML == null) {
            logger.debug("roleXml was empty, so returning empty orgName.");
        } else {
            String expression = "/expires";
            expires = findValue(appTokenXML, expression);
        }
        return expires;
    }


    public static String findValue(String xmlString,  String expression) {
        String value = "";
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xmlString)));
            XPath xPath = XPathFactory.newInstance().newXPath();


            XPathExpression xPathExpression = xPath.compile(expression);
            value = xPathExpression.evaluate(doc);
        } catch (Exception e) {
            logger.warn("Failed to parse xml. Expression {}, xml {}, ", expression, xmlString, e);
        }
        return value;
    }

}
