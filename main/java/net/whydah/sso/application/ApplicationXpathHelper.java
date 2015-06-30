package net.whydah.sso.application;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
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
 * Created by totto on 12/3/14.
 */
public class ApplicationXpathHelper {
    private static final Logger log = LoggerFactory.getLogger(ApplicationXpathHelper.class);



    public static  String getAppTokenIdFromAppTokenXml(String appTokenXML) {
        String appTokenId = "";
        if (appTokenXML == null) {
            log.debug("roleXml was empty, so returning empty orgName.");
        } else {
            String expression = "/applicationtoken/params/applicationtokenID[1]";
            appTokenId = findValue(appTokenXML, expression);
        }
        return appTokenId;
    }

    public static  Long getExpiresFromAppTokenXml(String appTokenXML) {
        String expiresValue = "";
        Long expires = -1L;
        if (appTokenXML == null) {
            log.debug("appTokenXml was empty, so returning empty expires.");
        } else {
            String expression = "/applicationtoken/params/expires";
            expiresValue = findValue(appTokenXML, expression);
            try {
               expires = new Long(expiresValue);
            } catch (NumberFormatException nfe) {
                log.warn("Failed to parse Long value from expires {}, in AppToken {}", expiresValue, appTokenXML);
            }
        }
        return expires;
    }

    public static  String[] getApplicationNamesFromApplicationsJson(String applicationsJson) {
        if (applicationsJson == null) {
            log.debug("getApplicationNamesFromApplicationsJson was empty, so returning null.");
        } else {
            List<String>  applications = findJsonpathList(applicationsJson, "$..name");
            if (applications==null){
                log.debug("Xpath returned zero hits");
                return null;
            }
            String[] result = new String[applications.size()];
            return applications.toArray(result);
        }
        return null;
    }

    public static String[] getApplicationsFromApplicationsJson(String applicationsJson){
        if (applicationsJson == null) {
            log.debug("getApplicationNamesFromApplicationsJson was empty, so returning null.");
        } else {
            List<String>  applications = findJsonpathList(applicationsJson, "$.applications.*");
            if (applications==null){
                log.debug("Xpath returned zero hits");
                return null;
            }
            String jsonString = applications.toString().substring(1,applications.toString().lastIndexOf("]")-1);
            jsonString.replace("},{", " ,");
            String[]array = jsonString.split(" ");
            return array;
            //String[] result = new String[applications.size()];
            //return applications.toArray(result);
        }
        return null;

    }

    public static  String findApplicationNameFromApplicationId(String applicationsJson) {
        if (applicationsJson == null) {
            log.debug("findApplicationNameFromApplicationId was empty, so returning null.");
        } else {
            return  findJsonpathValue(applicationsJson,"$.applications[?(@.id=11)].name");
        }
        return null;
    }


    private static String findValue(String xmlString,  String expression) {
        String value = "";
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

    private static List<String> findJsonpathList(String jsonString,  String expression) throws PathNotFoundException {
        List<String> result=null;
        Configuration conf = Configuration.defaultConfiguration();
        try {
            result= JsonPath.using(conf).parse(jsonString).read(expression);
        } catch (Exception e) {
            log.warn("Failed to parse JSON. Expression {}, JSON {}, ", expression, jsonString, e);
        }
        return result;
    }

    private static String findJsonpathValue(String jsonString,  String expression) throws PathNotFoundException {
        String o = JsonPath.parse(jsonString).read(expression);
        return null;
    }

}
