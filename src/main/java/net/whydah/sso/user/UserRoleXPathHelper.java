package net.whydah.sso.user;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
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
import java.util.List;

/**
 * Created by totto on 22.06.15.
 */
public class UserRoleXPathHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleXPathHelper.class);


    public static UserRole[] getUserRoleFromUserToken(String userTokenXml) {
        String userTokenId = "";
        if (userTokenXml == null) {
            logger.debug("userTokenXml was empty, so returning empty userTokenId.");
        } else {
            String expression = "/usertoken/application";
            System.out.println("Result"+expression);
        }
        return null;
    }

    public static UserRole[] getUserRoleFromUserAggregateXML(String userAggregateXML) {
        String userTokenId = "";
        if (userAggregateXML == null) {
            logger.debug("userAggregateXML was empty, so returning null.");
        } else {
            String expression = "/whydahuser/applications/*";
            System.out.println("Result"+findXpathValue(userAggregateXML,expression));
        }
        return null;
    }

    public static UserRole[] getUserRoleFromUserAggregateJSON(String userAggregateJson) {
        String userTokenId = "";
        if (userAggregateJson == null) {
            logger.debug("userAggregateJson was empty, so returning null.");
        } else {
            String expression = "$.roles[*]";
            System.out.println("Result"+findJsonpathValue(userAggregateJson, expression));
        }
        return null;
    }



    public static String findXpathValue(String xmlString,  String expression) {
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

    public static String findJsonpathValue(String jsonString,  String expression) {
        String value = "";
        try {
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
            List<String> result= JsonPath.read(document, expression);
            value=result.toString();
        } catch (Exception e) {
            logger.warn("Failed to parse JSON. Expression {}, JSON {}, ", expression, jsonString, e);
        }
        return value;
    }

}
