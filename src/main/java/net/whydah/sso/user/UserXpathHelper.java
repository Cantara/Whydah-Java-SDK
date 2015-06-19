package net.whydah.sso.user;

/**
 * Created by totto on 12/2/14.
 */

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



public class UserXpathHelper {
    private static final Logger logger = LoggerFactory.getLogger(UserXpathHelper.class);

    public static String getUserTokenId(String userTokenXml) {
        String userTokenId = "";
        if (userTokenXml == null) {
            logger.debug("userTokenXml was empty, so returning empty userTokenId.");
        } else {
            String expression = "/usertoken/@id";
            userTokenId = findValue(userTokenXml,expression);
        }
        return userTokenId;
    }
    public static String getUserId(String userTokenXml) {
        String userId = "";
        if (userTokenXml == null) {
            logger.debug("userTokenXml was empty, so returning empty userId.");
        } else {
            String expression = "/whydahuser/identity/UID";
            userId = findValue(userTokenXml, expression);
        }
        return userId;
    }
    public static String getUserName(String userTokenXml) {
        String userName = "";
        if (userTokenXml == null) {
            logger.debug("userTokenXml was empty, so returning empty userName.");
        } else {
            String expression = "/whydahuser/identity/username";
            userName = findValue(userTokenXml, expression);
        }
        return userName;
    }

    public static String getRealName(String userTokenXml){
        if (userTokenXml==null){
            logger.debug("userTokenXml was empty, so returning empty realName.");
            return "";
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/firstname";
            XPathExpression xPathExpression = xPath.compile(expression);
            String expression2 = "/usertoken/lastname";
            XPathExpression xPathExpression2 = xPath.compile(expression2);
            logger.debug("getRealName - usertoken" + userTokenXml + "\nvalue:" + xPathExpression.evaluate(doc) + " " + xPathExpression2.evaluate(doc));
            return (xPathExpression.evaluate(doc)+" "+xPathExpression2.evaluate(doc));
        } catch (Exception e) {
            logger.error("getRealName - userTokenXml - getTimestamp parsing error", e);
        }
        return "";
    }


    public static Integer getLifespan(String userTokenXml) {
        if (userTokenXml == null){
            logger.debug("userTokenXml was empty, so returning empty lifespan.");
            return null;
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/lifespan";
            XPathExpression xPathExpression = xPath.compile(expression);
            return Integer.parseInt(xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("getLifespan - userTokenXml lifespan parsing error", e);
        }
        return null;
    }

    public static Long getTimestamp(String userTokenXml) {
        if (userTokenXml==null){
            logger.debug("userTokenXml was empty, so returning empty timestamp.");
            return null;
        }
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String expression = "/usertoken/timestamp";
            XPathExpression xPathExpression = xPath.compile(expression);
            logger.debug("token" + userTokenXml + "\nvalue:" + xPathExpression.evaluate(doc));
            return Long.parseLong(xPathExpression.evaluate(doc));
        } catch (Exception e) {
            logger.error("getTimestamp - userTokenXml timestamp parsing error", e);
        }
        return null;
    }

    public static String getOrgName(String roleXml) {
        String orgName = "";
        if (roleXml == null) {
            logger.debug("roleXml was empty, so returning empty orgName.");
        } else {
            String expression = "/application/orgName";
            orgName = findValue(roleXml, expression);
        }
        return orgName;
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
