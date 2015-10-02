package net.whydah.sso.user;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.whydah.sso.user.types.ApplicationRoleEntry;
import net.whydah.sso.user.types.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserTokenMapper {

    public static final Logger log = LoggerFactory.getLogger(UserTokenMapper.class);
    public static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();


    public static UserToken fromUserTokenXml(String userTokenXml) {
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(userTokenXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();

            String uid = (String) xPath.evaluate("/usertoken/uid", doc, XPathConstants.STRING);
            String personRef = (String) xPath.evaluate("/usertoken/personref", doc, XPathConstants.STRING);
            String userName = (String) xPath.evaluate("/usertoken/username", doc, XPathConstants.STRING);
            String firstName = (String) xPath.evaluate("/usertoken/firstname", doc, XPathConstants.STRING);
            String lastName = (String) xPath.evaluate("/usertoken/lastname", doc, XPathConstants.STRING);
            String email = (String) xPath.evaluate("/usertoken/email", doc, XPathConstants.STRING);
            String cellPhone = (String) xPath.evaluate("/usertoken/cellphone", doc, XPathConstants.STRING);
            String securityLevel = (String) xPath.evaluate("/usertoken/securitylevel", doc, XPathConstants.STRING);

            String tokenId = (String) xPath.evaluate("/usertoken/@id", doc, XPathConstants.STRING);
            String timestamp = (String) xPath.evaluate("/usertoken/timestamp", doc, XPathConstants.STRING);
            String lastSeen = (String) xPath.evaluate("/usertoken/lastseen", doc, XPathConstants.STRING);

            String defcon = (String) xPath.evaluate("/usertoken/DEFCON", doc, XPathConstants.STRING);
            String lifespan = (String) xPath.evaluate("/usertoken/lifespan", doc, XPathConstants.STRING);
            String issuer = (String) xPath.evaluate("/usertoken/issuer", doc, XPathConstants.STRING);


            List<ApplicationRoleEntry> roleList = new ArrayList<>();
            NodeList applicationNodes = (NodeList) xPath.evaluate("//application", doc, XPathConstants.NODESET);
            for (int i = 0; i < applicationNodes.getLength(); i++) {
                Node appNode = applicationNodes.item(i);
                String appId = (String) xPath.evaluate("@ID", appNode, XPathConstants.STRING);
                String appName = (String) xPath.evaluate("./applicationName", appNode, XPathConstants.STRING);
                String organizationName = (String) xPath.evaluate("./organizationName", appNode, XPathConstants.STRING);
                NodeList roles = (NodeList) xPath.evaluate("./role", appNode, XPathConstants.NODESET);

                for (int k = 0; k < roles.getLength(); k++) {
                    Node roleNode = roles.item(k);
                    String roleName = (String) xPath.evaluate("@name", roleNode, XPathConstants.STRING);
                    String roleValue = (String) xPath.evaluate("@value", roleNode, XPathConstants.STRING);

                    ApplicationRoleEntry role = new ApplicationRoleEntry();
                    role.setApplicationId(appId);
                    role.setApplicationRoleName(appName);
                    role.setOrganizationName(organizationName);
                    role.setRoleName(roleName);
                    role.setRoleValue(roleValue);
                    roleList.add(role);
                }
            }


            UserToken userToken = new UserToken();
            userToken.setUid(uid);
            userToken.setUserName(userName);
            userToken.setFirstName(firstName);
            userToken.setLastName(lastName);
            userToken.setEmail(email);
            userToken.setCellPhone(cellPhone);
            userToken.setPersonRef(personRef);
            userToken.setSecurityLevel(securityLevel);
            userToken.setRoleList(roleList);

            userToken.setTokenid(tokenId);
            userToken.setTimestamp(timestamp);
            userToken.setLastSeen(lastSeen);
            UserToken.setDefcon(defcon);
            userToken.setLifespan(lifespan);
            userToken.setIssuer(issuer);
            return userToken;
        } catch (Exception e) {
            log.error("Error parsing userTokenXml " + userTokenXml, e);
            return null;
        }
    }

    public static UserToken fromUserAggregateXml(String userAggregateXML) {
        try {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document doc = documentBuilder.parse(new InputSource(new StringReader(userAggregateXML)));
            XPath xPath = XPathFactory.newInstance().newXPath();
            String uid = (String) xPath.evaluate("//UID", doc, XPathConstants.STRING);
            String userName = (String) xPath.evaluate("//identity/username", doc, XPathConstants.STRING);
            String firstName = (String) xPath.evaluate("//identity/firstname", doc, XPathConstants.STRING);
            String lastName = (String) xPath.evaluate("//identity/lastname", doc, XPathConstants.STRING);
            String email = (String) xPath.evaluate("//identity/email", doc, XPathConstants.STRING);
            String cellPhone = (String) xPath.evaluate("//identity/cellPhone", doc, XPathConstants.STRING);
            String personRef = (String) xPath.evaluate("//identity/personref", doc, XPathConstants.STRING);


            List<ApplicationRoleEntry> roleList = new ArrayList<>();
            NodeList applicationNodes = (NodeList) xPath.evaluate("/whydahuser/applications/application/appId", doc, XPathConstants.NODESET);
            for (int i = 1; i < applicationNodes.getLength() + 1; i++) {
                ApplicationRoleEntry role = new ApplicationRoleEntry();
                role.setApplicationId((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/appId", doc, XPathConstants.STRING));
                role.setApplicationRoleName((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/applicationName", doc, XPathConstants.STRING));
                role.setOrganizationName((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/orgName", doc, XPathConstants.STRING));
                role.setRoleName((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/roleName", doc, XPathConstants.STRING));
                role.setRoleValue((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/roleValue", doc, XPathConstants.STRING));
                roleList.add(role);
            }
            UserToken userToken = new UserToken();
            userToken.setUid(uid);
            userToken.setUserName(userName);
            userToken.setFirstName(firstName);
            userToken.setLastName(lastName);
            userToken.setEmail(email);
            userToken.setPersonRef(personRef);
            userToken.setCellPhone(cellPhone);
            userToken.setRoleList(roleList);
            return userToken;
        } catch (Exception e) {
            log.error("Error parsing userAggregateXML " + userAggregateXML, e);
            return null;
        }
    }

    //String appTokenXml
    public static UserToken fromUserAggregateJson(String userAggregateJson) {
        UserToken userToken = parseUserAggregateJson(userAggregateJson);
        userToken.setTokenid(generateID());
        userToken.setTimestamp(String.valueOf(System.currentTimeMillis()));
        String securityLevel = "1"; //UserIdentity as source = securitylevel=0
        userToken.setSecurityLevel(securityLevel);

        //userToken.setDefcon(defcon);
        //String issuer = extractIssuer(appTokenXml);
        //userToken.setIssuer(TOKEN_ISSUER);
        //userToken.setLifespan(lifespanMs);
        return userToken;
    }

    private static UserToken parseUserAggregateJson(String userAggregateJSON) {
        try {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            String uid = getStringFromJsonpathExpression("$.identity.uid", userAggregateJSON);
            String userName = getStringFromJsonpathExpression("$.identity.username", userAggregateJSON);
            String firstName = getStringFromJsonpathExpression("$.identity.firstName", userAggregateJSON);
            String lastName = getStringFromJsonpathExpression("$.identity.lastName", userAggregateJSON);
            String email = getStringFromJsonpathExpression("$.identity.email", userAggregateJSON);
            String cellPhone = getStringFromJsonpathExpression("$.identity.cellPhone", userAggregateJSON);
            String personRef = getStringFromJsonpathExpression("$.identity.personRef", userAggregateJSON);

            // TODO  add rolemapping
            List<ApplicationRoleEntry> roleList = new ArrayList<>();
            /**
             NodeList applicationNodes = (NodeList) xPath.evaluate("/whydahuser/applications/application/appId", doc, XPathConstants.NODESET);
             for (int i = 1; i < applicationNodes.getLength() + 1; i++) {
             ApplicationRoleEntry role = new ApplicationRoleEntry();
             role.setApplicationId((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/appId", doc, XPathConstants.STRING));
             role.setApplicationRoleName((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/applicationName", doc, XPathConstants.STRING));
             role.setOrganizationName((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/orgName", doc, XPathConstants.STRING));
             role.setRoleName((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/roleName", doc, XPathConstants.STRING));
             role.setRoleValue((String) xPath.evaluate("/whydahuser/applications/application[" + i + "]/roleValue", doc, XPathConstants.STRING));
             roleList.add(role);
             }
             */

            UserToken userToken = new UserToken();
            userToken.setUid(uid);
            userToken.setUserName(userName);
            userToken.setFirstName(firstName);
            userToken.setLastName(lastName);
            userToken.setEmail(email);
            userToken.setPersonRef(personRef);
            userToken.setCellPhone(cellPhone);
            userToken.setRoleList(roleList);
            return userToken;
        } catch (Exception e) {
            log.error("Error parsing userAggregateJSON " + userAggregateJSON, e);
            return null;
        }
    }


    public static String getStringFromJsonpathExpression(String expression, String jsonString) throws PathNotFoundException {
        //String expression = "$.identity.uid";
        String value = "";
        Object document = Configuration.defaultConfiguration().jsonProvider().parse(jsonString);
        String result = JsonPath.read(document, expression);
        value = result.toString();

        return value;
    }


    private static String generateID() {
        return UUID.randomUUID().toString();
    }


}
