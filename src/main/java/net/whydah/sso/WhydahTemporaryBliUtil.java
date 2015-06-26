package net.whydah.sso;

import net.whydah.sso.user.UserCredential;
import net.whydah.sso.util.ExceptionUtil;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Stable direct connection util. 
 * Will be removed when Commands are stable.
 * Created by baardl on 26.06.15.
 */
public class WhydahTemporaryBliUtil {
    private static final Logger log = getLogger(WhydahTemporaryBliUtil.class);
    
    public static String logOnUser(String tokenServiceUri, String myAppTokenId,String myAppTokenXml, String userName, String password) {
        UserCredential userCredential = new UserCredential(userName,password);
       String userticket= UUID.randomUUID().toString();
        Client tokenServiceClient = ClientBuilder.newClient();
        WebTarget getUserToken = tokenServiceClient.target(tokenServiceUri).path("user/" + myAppTokenId + "/" + userticket + "/usertoken");
        Form formData = new Form();
        formData.param("apptoken", myAppTokenXml);
        formData.param("usercredential", userCredential.toXML());
        Response response = postForm(formData,getUserToken);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.warn("BLI-Logon - getUserToken - User authentication failed with status code " + response.getStatus());
            return null;
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.trace("BLI-Logon - getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        }

        //retry once for other statuses
        log.info("BLI-Logon - getUserToken - retry once for other statuses");
        response = postForm(formData,getUserToken);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.trace("BLI-Logon - getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        } else if (response.getStatus() == NOT_FOUND.getStatusCode()) {
            log.error(ExceptionUtil.printableUrlErrorMessage("BLI-Logon - getUserToken - Auth failed - Problems connecting with TokenService", getUserToken, response));
        } else {
            log.info(ExceptionUtil.printableUrlErrorMessage("BLI-Logon - getUserToken - User authentication failed", getUserToken, response));
        }
        return null;
    }

    private static Response postForm(Form formData, WebTarget logonResource) {
        return logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
    }
}
