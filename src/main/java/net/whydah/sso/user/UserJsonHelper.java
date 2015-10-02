package net.whydah.sso.user;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import net.whydah.sso.user.types.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.UUID;

public class UserJsonHelper {

    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private static final Logger log = LoggerFactory.getLogger(UserJsonHelper.class);


    //String appTokenXml
    public static UserToken fromUserAggregateJson(String userAggregateXML) {
        UserToken userToken = parseUserAggregateJson(userAggregateXML);
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
            String uid = getUidFromUserAggregateJson("$.identity.uid", userAggregateJSON);
            String userName = getUidFromUserAggregateJson("$.identity.username", userAggregateJSON);
            String firstName = getUidFromUserAggregateJson("$.identity.firstName", userAggregateJSON);
            String lastName = getUidFromUserAggregateJson("$.identity.lastName", userAggregateJSON);
            String email = getUidFromUserAggregateJson("$.identity.email", userAggregateJSON);
            String cellPhone = getUidFromUserAggregateJson("$.identity.cellPhone", userAggregateJSON);
            String personRef = getUidFromUserAggregateJson("$.identity.personRef", userAggregateJSON);

            UserToken userToken = new UserToken();
            userToken.setUid(uid);
            userToken.setUserName(userName);
            userToken.setFirstName(firstName);
            userToken.setLastName(lastName);
            userToken.setEmail(email);
            userToken.setPersonRef(personRef);
            userToken.setCellPhone(cellPhone);
//            userToken.setRoleList(roleList);
            return userToken;
        } catch (Exception e) {
            log.error("Error parsing userAggregateJSON " + userAggregateJSON, e);
            return null;
        }
    }


    public static String getUidFromUserAggregateJson(String expression, String jsonString) throws PathNotFoundException {
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
