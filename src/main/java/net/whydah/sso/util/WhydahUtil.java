package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserIdentityRepresentation;
import net.whydah.sso.user.UserRole;
import net.whydah.sso.user.UserRoleXpathHelper;
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
 *
 * TODO  add init to embedd sts/uas and renewal of application and user sessions
 *
 */
public class WhydahUtil {
    private static final Logger log = getLogger(WhydahUtil.class);


    /**
     * Logon your application to Whydah.
     * @param stsURI URI to the Security Token Service, where you do logon
     * @param applicationID The registered ID of your application.
     * @param applicationSecret Current, updatet secret of your application.
     * @return XML Representing the application. In this you will find the applicationtokenId used as application session
     * for further operations.
     */
    public static String logOnApplication(String stsURI, String applicationID,String applicationSecret){
        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        ApplicationCredential appCredential = new ApplicationCredential(applicationID,applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        return myAppTokenXml;

    }

    /**
     * Extend the application's logon expiry period.
     * @param stsURI URI to the Security Token Service, where you do logon
     * @param applicationID The registered ID of your application.
     * @param applicationSecret Current, updatet secret of your application.
     * @return XML Representing the application. In this you will find the applicationtokenId used as application session
     * for further operations.
     *
     * TODO   Use extend session not new logon...
     *
     */
    public static String extendApplicationSession(String stsURI, String applicationID,String applicationSecret){
        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        ApplicationCredential appCredential = new ApplicationCredential(applicationID,applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        return myAppTokenXml;

    }


    public static String logOnApplicationAndUser(String stsURI, String applicationID,String applicationSecret, String username,String password){
        URI tokenServiceUri = UriBuilder.fromUri(stsURI).build();
        ApplicationCredential appCredential = new ApplicationCredential(applicationID,applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        UserCredential userCredential = new UserCredential(username, password);
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        return userToken;

    }



    public static String logOnUser(WhydahApplicationSession was, UserCredential userCredential){
        URI tokenServiceUri = UriBuilder.fromUri(was.getSTS()).build();
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, was.getActiveApplicationTokenId(), was.getActiveApplicationToken(), userCredential, UUID.randomUUID().toString()).execute();
        return userToken;

    }


    public static String extendUserSession(WhydahApplicationSession was, UserCredential userCredential){
        URI tokenServiceUri = UriBuilder.fromUri(was.getSTS()).build();
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, was.getActiveApplicationTokenId(), was.getActiveApplicationToken(), userCredential, UUID.randomUUID().toString()).execute();
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
            log.info("CommandAddUser - addUser - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.debug("CommandAddUser - addUser - Log on OK with response {}", responseXML);
            return responseXML;
        }
        throw new IllegalArgumentException("Not found");
    }


    /**
     *
     * @param uasUri URI to the User Admin Service
     * @param applicationTokenId TokenId fetched from the XML in logOnApplication
     * @param adminUserTokenId TokenId fetched from the XML returned in logOnApplicationAndUser
     * @param roles List of roles to be created.
     * @return List of the roles that has been creatd. Empty list if no roles were created.
     */
    public static List<UserRole> addRolesToUser(String uasUri, String applicationTokenId, String adminUserTokenId, List<UserRole> roles) {

        List<String> createdRolesXml = new ArrayList<>();
        List<UserRole> createdRoles = new ArrayList<>();
        WebTarget userTarget = buildBaseTarget(uasUri, applicationTokenId, adminUserTokenId).path("/user");
        Response response;
        String userName = "";
        for (UserRole role : roles) {
            String roleXml = role.toXML();
            log.trace("Try to add role {}", roleXml);
            userName = role.getUserName();
            response = userTarget.path(userName).path("/role").request().accept(MediaType.APPLICATION_XML).post(Entity.entity(roleXml, MediaType.APPLICATION_XML));
            if (response.getStatus() == OK.getStatusCode()) {
                String responseXML = response.readEntity(String.class);
                log.debug("CommandAddRole - addRoles - Created role ok {}", responseXML);
                createdRolesXml.add(responseXML);
            } else {
                //createdRolesXml.add("Failed to add role " + role.getRoleName() + ", reason: " + response.toString());
                log.trace("Failed to add role {}, response status {}", role.toString(), response.getStatus());
            }
        }
        for (String createdRoleXml : createdRolesXml) {
            UserRole createdUserRole = UserRole.fromXml(createdRoleXml);
            createdRoles.add(createdUserRole);
        }
        return createdRoles;
    }

    public static List<UserRole> listUserRoles(String uasUri, String adminAppTokenId, String adminUserTokenId, String applicationId, String userId ) {

        List<UserRole> userRoles = new ArrayList<>();
        WebTarget userTarget = buildBaseTarget(uasUri, adminAppTokenId, adminUserTokenId).path("/user");
        Response response;
        response = userTarget.path(userId).path("roles").request().accept(MediaType.APPLICATION_XML).get();
        if (response.getStatus() == OK.getStatusCode()) {
            String rolesXml = response.readEntity(String.class);
            log.debug("CommandListRoles - listUserRoles - Created role ok {}", rolesXml);
           userRoles = UserRoleXpathHelper.rolesViaJackson(rolesXml);
        } else {
            log.trace("Failed to find roles for user {}, response status {}", userId, response.getStatus());
        }
        return userRoles;
    }

    static WebTarget buildBaseTarget(String baseUri, String applicationTokenId, String adminUserTokenId) {
        Client httpClient = ClientBuilder.newClient();
        return httpClient.target(baseUri).path(applicationTokenId + "/" + adminUserTokenId);
    }
}
