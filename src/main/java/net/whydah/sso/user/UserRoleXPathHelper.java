package net.whydah.sso.user;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by totto on 22.06.15.
 */
public class UserRoleXPathHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleXPathHelper.class);


    public static UserRole[] getUserRoleFromUserTokenXml(String userTokenXml) {
        if (userTokenXml == null) {
            logger.debug("userTokenXml was empty, so returning empty userTokenId.");
        } else {
            String appid;
            String orgName;
            String rolename;
            String roleValue;
            String userName=findXpathValue(userTokenXml, "/usertoken/username");
            String expression = "count(/usertoken/application)";
            int noOfApps= Integer.valueOf(findXpathValue(userTokenXml,expression));
            int noOfRoles =  Integer.valueOf(findXpathValue(userTokenXml, "count(//role)"));
            UserRole[] result = new UserRole[noOfRoles];
            int roleindex=0;
            for (int n=1;n<=noOfApps;n++) {
                    appid = findXpathValue(userTokenXml, "//usertoken/application["+n+"]/@ID");
                    int noSubRoles=Integer.valueOf(findXpathValue(userTokenXml,"count(//application["+n+"]/organizationName)"));
                    for (int m=1;m<=noSubRoles;m++){
                        try {
                        orgName = findXpathValue(userTokenXml, "//application["+n+"]/organizationName["+m+"]");
                        rolename = findXpathValue(userTokenXml, "//application["+n+"]/role["+m+"]/@name");
                        roleValue = findXpathValue(userTokenXml, "//application["+n+"]/role["+m+"]/@value");
                        UserRole ur = new UserRole(userName, appid, orgName, rolename, roleValue);
                        result[roleindex++] = ur;
                        } catch (PathNotFoundException pnpe) {
                            return null;
                        }

                    }
            }
            return result;
        }
        return null;
    }

    public static List<UserRole> getUserRoleFromUserAggregateXml(String userAggregateXML) {
        List<UserRole> userRoles = new ArrayList<>();
        if (userAggregateXML == null) {
            logger.debug("userAggregateXML was empty, so returning null.");
        } else {
            String appid;
            String orgName;
            String rolename;
            String roleValue;
            String userName=findXpathValue(userAggregateXML, "/whydahuser/identity/username");
            String expression = "count(/whydahuser/applications/application)";
            int noOfRoles= Integer.valueOf(findXpathValue(userAggregateXML,expression));

            for (int n=1;n<=noOfRoles;n++) {
                try {
                    appid = findXpathValue(userAggregateXML, "/whydahuser/applications/application[" + n + "]/appId");
                    orgName = findXpathValue(userAggregateXML, "/whydahuser/applications/application[" + n + "]/orgName");
                    rolename = findXpathValue(userAggregateXML, "/whydahuser/applications/application[" + n + "]/roleName");
                    roleValue = findXpathValue(userAggregateXML, "/whydahuser/applications/application[" + n + "]/roleValue");
                    UserRole userRole = new UserRole(userName, appid, orgName, rolename, roleValue);
                    userRoles.add(userRole);
                } catch (PathNotFoundException pnpe) {
                    logger.warn("Could not parse userAggregateXml {}, reason {}", userAggregateXML,pnpe.getMessage());
//                    return null;
                }
            }

        }
        return userRoles;
    }

    public static UserRole[] getUserRoleFromUserAggregateJson(String userAggregateJson) {
        if (userAggregateJson == null) {
            logger.debug("userAggregateJson was empty, so returning null.");
        } else {
            String appid;
            String orgName;
            String rolename;
            String roleValue;
            List<String> roles=findJsonpathList(userAggregateJson,"$.roles[*]");
            String userName=findJsonpathValue(userAggregateJson, "$.username");
            UserRole[] result = new UserRole[roles.size()];
            for (int n=0;n<roles.size();n++){
                try {
                    appid = findJsonpathValue(userAggregateJson, "$.roles[" + n + "].applicationId");
                    orgName = findJsonpathValue(userAggregateJson, "$.roles[" + n + "].applicationName");
                    rolename = findJsonpathValue(userAggregateJson, "$.roles[" + n + "].applicationRoleName");
                    roleValue = findJsonpathValue(userAggregateJson, "$.roles[" + n + "].applicationRoleValue");
                    UserRole ur = new UserRole(userName,appid, orgName, rolename, roleValue);
                    result[n]=ur;
                } catch (PathNotFoundException pnpe){
                    return null;
                }

            }
            return result;
        }
        return null;
    }



    private static String findXpathValue(String xmlString,  String expression) {
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

    private static List<String> findJsonpathList(String jsonString,  String expression) throws PathNotFoundException {
        List<String> result=null;
        try {
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
            result= JsonPath.read(document, expression);

        } catch (Exception e) {
            logger.warn("Failed to parse JSON. Expression {}, JSON {}, ", expression, jsonString, e);
        }
        return result;
    }

    private static String findJsonpathValue(String jsonString,  String expression) throws PathNotFoundException {
        String value = "";
            Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
            String result= JsonPath.read(document, expression);
            value=result.toString();

        return value;
    }

    public static List<UserRole> rolesViaJackson(String rolesXml) {
        XmlMapper mapper = new XmlMapper();
        UserRolesJacksonHelper openCredentials = null;
        try {
            openCredentials = mapper.readValue(rolesXml, UserRolesJacksonHelper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(openCredentials);
        return openCredentials.getUserRoles();
    }

}
