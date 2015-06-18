package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.CommandLogonApplication;
import net.whydah.sso.commands.CommandLogonUserByUserCredential;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserIdentityRepresentation;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 06.05.15.
 */
public class WhydahUtil {
    private static final Logger log = getLogger(WhydahUtil.class);



    public static String logOnApplicationAndUser(String stsURI, String applicationID,String applicationSecret, String username,String password){
        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        ApplicationCredential appCredential = new ApplicationCredential(applicationID,applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppToken(myAppTokenXml);
        UserCredential userCredential = new UserCredential(username, password);
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        return userToken;

    }

    /**
     * TODO use swich case for status code.
     * @param uasUri
     * @param applicationTokenId
     * @param adminUserTokenId
     * @param userIdentity
     * @return
     */
    public static String addUser(String uasUri, String applicationTokenId, String adminUserTokenId, UserIdentityRepresentation userIdentity) {
//        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        //String userId = new CommandAddUserIdentity(tokenServiceUri, adminUserTokenId, adminUserTokenId,userIdentity.toXML()).execute();
        String userId = null;
        Client tokenServiceClient = ClientBuilder.newClient();

        WebTarget addUser = tokenServiceClient.target(uasUri).path(applicationTokenId + "/" + adminUserTokenId + "/user");
//        ClientResponse response = addUser.post(Entity.entity(userIdentityXml,MediaType.APPLICATION_XML_TYPE),ClientResponse.class);
        String userIdentityXml = userIdentity.toXML();
        Response response = addUser.request().accept(MediaType.APPLICATION_XML).post(Entity.entity(userIdentityXml,MediaType.APPLICATION_XML));
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.info("CommandLogonUserByUserCredential - addUser - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.debug("CommandLogonUserByUserCredential - addUser - Log on OK with response {}", responseXML);
            return responseXML;
        }
        throw new IllegalArgumentException("Not found");
//        log.debug("Received userId");
//        return userId;
    }
}
