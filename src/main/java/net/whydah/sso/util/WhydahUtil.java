package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.CommandLogonApplication;
import net.whydah.sso.commands.CommandLogonUserByUserCredential;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserIdentityRepresentation;
import net.whydah.sso.user.UserRole;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 06.05.15.
 */
public class WhydahUtil {
    private static final Logger log = getLogger(WhydahUtil.class);


    /**
     * Logon your application to Whydah.
     * @param stsURI URI to the Security Token Service, where you do logon
     * @param applicationID The registered ID of your application.
     * @param applicationSecret Current, updatet secret of your application.
     * @return XML Representing the application. In this you will find the tokenId used for further operations.
     */
    public static String logOnApplication(String stsURI, String applicationID,String applicationSecret){
        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        ApplicationCredential appCredential = new ApplicationCredential(applicationID,applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        return myAppTokenXml;

    }

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
     * @param uasUri URI to the User Admin Service
     * @param applicationTokenId TokenId fetched from the XML in logOnApplication
     * @param adminUserTokenId TokenId fetched from the XML returned in logOnApplicationAndUser
     * @param userIdentity The user identity you want to create.
     * @return UserIdentityXml
     */
    public static String addUser(String uasUri, String applicationTokenId, String adminUserTokenId, UserIdentityRepresentation userIdentity) {
        String userId = null;


        WebTarget addUser = buildBaseTarget(uasUri, applicationTokenId, adminUserTokenId).path("/user");
        String userIdentityXml = userIdentity.toXML();
        Response response = addUser.request().accept(MediaType.APPLICATION_XML).post(Entity.entity(userIdentityXml,MediaType.APPLICATION_XML));
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.info("CommandAddUserIdentity - addUser - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.debug("CommandAddUserIdentity - addUser - Log on OK with response {}", responseXML);
            return responseXML;
        }
        throw new IllegalArgumentException("Not found");
    }


    /**
     *
     * @param uasUri URI to the User Admin Service
     * @param applicationTokenId TokenId fetched from the XML in logOnApplication
     * @param adminUserTokenId TokenId fetched from the XML returned in logOnApplicationAndUser
     * @param roles
     * @return
     */
    public static List<String> addRolesToUser(String uasUri, String applicationTokenId, String adminUserTokenId, List<UserRole> roles ) {

        List<String> createdRoles = new ArrayList<>();
        WebTarget userTarget = buildBaseTarget(uasUri, applicationTokenId, adminUserTokenId).path("/user");
        Response response;
        String userName = "";
        for (UserRole role : roles) {
            String roleXml = role.toXML();
            log.trace("Try to add role {}", roleXml);
            userName = role.getUserName();
            response = userTarget.path(userName).path("/role").request().accept(MediaType.APPLICATION_XML).post(Entity.entity(roleXml,MediaType.APPLICATION_XML));
            if (response.getStatus() == OK.getStatusCode()) {
                String responseXML = response.readEntity(String.class);
                log.debug("CommandAddRole - addRoles - Created role ok {}", responseXML);
                createdRoles.add(responseXML);
            } else {
                createdRoles.add("Failed to add role " + role.getRoleName() +", reason: " + response.toString());
                log.trace("Failed to add role {}, response status {}", role.toString(), response.getStatus());
            }
        }
        return createdRoles;
    }

    static WebTarget buildBaseTarget(String baseUri, String applicationTokenId, String adminUserTokenId) {
        Client httpClient = ClientBuilder.newClient();
        return httpClient.target(baseUri).path(applicationTokenId + "/" + adminUserTokenId);
    }
}
