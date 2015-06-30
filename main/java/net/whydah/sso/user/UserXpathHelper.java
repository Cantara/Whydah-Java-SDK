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
    private static final Logger log = LoggerFactory.getLogger(UserXpathHelper.class);


    /**
     * UserTokenXml parsers
     *
     */
    public static boolean hasRoleFromUserToken(String userTokenXml, String applicationId, String roleName) {
        String userRole = "";
        if (userTokenXml == null) {
            log.debug("userTokenXml was empty, so returning false.");
            return false;
        } else {
            String expression = "count(/usertoken/application[@ID='"+applicationId+"']/role[@name='"+roleName+"'])";
            userRole = findValue(userTokenXml, expression);
            if (userRole==null || "0".equalsIgnoreCase(userRole)){
                return false;
            }
            return true;
        }
    }

    public static String getRoleValueFromUserToken(String userTokenXml, String applicationId, String roleName) {
        String userRole = "";
        if (userTokenXml == null) {
            log.debug("userTokenXml was empty, so returning null.");
            return null;
        } else {
            String expression = "count(/usertoken/application[@ID='"+applicationId+"']/role[@name='"+roleName+"'])";
            userRole = findValue(userTokenXml, expression);
            if (userRole==null || "0".equalsIgnoreCase(userRole)){
                return null;
            }
            return findValue(userTokenXml,"/usertoken/application[@ID='"+applicationId+"']/role[@name='"+roleName+"']");
        }
    }

    public static String getUserTokenId(String userTokenXml) {
        String userTokenId = "";
        if (userTokenXml == null) {
            log.debug("userTokenXml was empty, so returning empty userTokenId.");
        } else {
            String expression = "/usertoken/@id";
            userTokenId = findValue(userTokenXml,expression);
        }
        return userTokenId;
    }
    public static String getUserIdFromUserTokenXml(String userTokenXml) {
        String userId = "";
        if (userTokenXml == null) {
            log.debug("userTokenXml was empty, so returning empty userId.");
        } else {
            String expression = "/usertoken/uid";
            userId = findValue(userTokenXml, expression);
        }
        return userId;
    }

    public static String getRealNameFromUserTokenXml(String userTokenXml){
        if (userTokenXml==null){
            log.debug("userTokenXml was empty, so returning empty realName.");
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
            log.debug("getRealNameFromUserTokenXml - usertoken" + userTokenXml + "\nvalue:" + xPathExpression.evaluate(doc) + " " + xPathExpression2.evaluate(doc));
            return (xPathExpression.evaluate(doc)+" "+xPathExpression2.evaluate(doc));
        } catch (Exception e) {
            log.error("getRealNameFromUserTokenXml - userTokenXml - getTimestampFromUserTokenXml parsing error", e);
        }
        return "";
    }


    public static Long getLifespanFromUserTokenXml(String userTokenXml) {
        if (userTokenXml == null){
            log.debug("userTokenXml was empty, so returning empty lifespan.");
            return null;
        }
        try {
            String value = findValue(userTokenXml, "/usertoken/lifespan");
            return Long.parseLong(value);
        } catch (Exception e) {
            log.error("getLifespanFromUserTokenXml - userTokenXml lifespan parsing error", e);
        }
        return 0L;
    }

    public static Long getTimestampFromUserTokenXml(String userTokenXml) {
        if (userTokenXml==null){
            log.debug("userTokenXml was empty, so returning empty timestamp.");
            return null;
        }
        try {
            String value = findValue(userTokenXml, "/usertoken/timestamp");
            return Long.parseLong(value);
        } catch (Exception e) {
            log.error("getTimestampFromUserTokenXml - userTokenXml timestamp parsing error", e);
        }
        return 0L;
    }


    /**
     * UserIdentityXml parsers
     *
     */
    public static String getUserNameFromUserIdentityXml(String userIdentityXml) {
        String userName = "";
        if (userIdentityXml == null) {
            log.debug("userTokenXml was empty, so returning empty userName.");
        } else {
            String expression = "/whydahuser/identity/username";
            userName = findValue(userIdentityXml, expression);
        }
        return userName;
    }
    public static String getUserIdFromUserIdentityXml(String userIdentityXml) {
        String userId = "";
        if (userIdentityXml == null) {
            log.debug("userTokenXml was empty, so returning empty userId.");
        } else {
            String expression = "/whydahuser/identity/UID";
            userId = findValue(userIdentityXml, expression);
        }
        return userId;
    }



    public static String findValue(String xmlString,  String expression) {
        String value = null;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(xmlString)));
            XPath xPath = XPathFactory.newInstance().newXPath();


            XPathExpression xPathExpression = xPath.compile(expression);
            value = xPathExpression.evaluate(doc);
        } catch (Exception e) {
            log.warn("Failed to parse xml. Expression {}, xml {}, ", expression, xmlString, e);
        }
        return value;
    }

    public static String getUserNameFromUserTokenXml(String userAdminTokenXml) {

        String expresseion = "usertoken/username";
        String userName = UserXpathHelper.findValue(userAdminTokenXml, expresseion);

        return userName;
    }

}
