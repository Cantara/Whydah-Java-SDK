package net.whydah.sso.util;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandRenewApplicationSession;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUsertokenId;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.user.helpers.UserRoleXpathHelper;
import net.whydah.sso.user.mappers.UserIdentityMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;

public class WhydahUtil {
    private static final Logger log = LoggerFactory.getLogger(WhydahUtil.class);


    /**
     * Logon your application to Whydah.
     *
     * @param stsURI            URI to the Security Token Service, where you do logon
     * @param applicationID     The registered ID of your application.
     * @param applicationSecret Current, updatet secret of your application.
     * @return applicationTokenXML Representing the application. In this you will find the applicationtokenId used as application session     * @param applicationSecret Current, updatet secret of your application's.
     * for further operations.
     */
    public static String logOnApplication(String stsURI, String applicationID, String applicationSecret) {
        URI tokenServiceUri = URI.create(stsURI);
        ApplicationCredential appCredential = new ApplicationCredential(applicationID, "", applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        if (myAppTokenXml == null || myAppTokenXml.length() < 10) {
            log.error("logOnApplication - unable to create application session on " + stsURI + " for appCredentials: " + ApplicationCredentialMapper.toXML(appCredential));

        }
        return myAppTokenXml;

    }

    public static String logOnApplication(String stsURI, ApplicationCredential myAppcredential) {
        URI tokenServiceUri = URI.create(stsURI);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, myAppcredential).execute();
        if (myAppTokenXml == null || myAppTokenXml.length() < 10) {
            log.error("logOnApplication - unable to create application session on " + stsURI + " for appCredentials: " + ApplicationCredentialMapper.toXML(myAppcredential));

        }
        return myAppTokenXml;

    }

    /**
     * Extend the application's's logon expiry period.
     *
     * @param stsURI            URI to the Security Token Service, where you do logon
     * @param applicationID     The registered ID of your application's.
     * @param applicationSecret Current, updatet secret of your application's.
     * @return XML Representing the application's. In this you will find the applicationtokenId used as application's session
     * for further operations.
     * <p>
     * TODO   Use extend session not new logon...
     */
    public static String extendApplicationSession(String stsURI, String applicationID, String applicationName, String applicationSecret) {
        URI tokenServiceUri = URI.create(stsURI);
        ApplicationCredential appCredential = new ApplicationCredential(applicationID, applicationName, applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        return myAppTokenXml;

    }

    public static String extendApplicationSession(String stsURI, String applicationTokenId) {
        URI tokenServiceUri = URI.create(stsURI);
        String myAppTokenXml = new CommandRenewApplicationSession(tokenServiceUri, applicationTokenId).execute();
        return myAppTokenXml;

    }

    public static String logOnApplicationAndUser(String stsURI, String applicationID, String applicationName, String applicationSecret, String username, String password) {
        URI tokenServiceUri = URI.create(stsURI);
        ApplicationCredential appCredential = new ApplicationCredential(applicationID, applicationName, applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        UserCredential userCredential = new UserCredential(username, password);
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        return userToken;

    }


    public static String logOnUser(WhydahApplicationSession was, UserCredential userCredential) {
        URI tokenServiceUri = URI.create(was.getSTS());
        if (was.getActiveApplicationTokenId() == null || was.getActiveApplicationTokenId().length() < 8) {
            log.warn("Illegal application session from WhydahApplicationSession, applicationTokenId:" + was.getActiveApplicationTokenId());

        }
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), userCredential, UUID.randomUUID().toString()).execute();
        return userToken;

    }


    public static String extendUserSession(WhydahApplicationSession was, UserCredential userCredential) {
        URI tokenServiceUri = URI.create(was.getSTS());
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), userCredential, UUID.randomUUID().toString()).execute();
        return userToken;

    }

    /**
     * @param uasUri             URI to the User Admin Service
     * @param applicationTokenId TokenId fetched from the XML in logOnApplication
     * @param adminUserTokenId   TokenId fetched from the XML returned in logOnApplicationAndUser
     * @param userIdentity       The user identity you want to create.
     * @return UserIdentityXml
     */
    public static String addUser(String uasUri, String applicationTokenId, String adminUserTokenId, UserIdentity userIdentity) {
        String userId = null;


        WebTarget addUser = buildBaseTarget(uasUri, applicationTokenId, adminUserTokenId).path("/user");
        String userIdentityJson = UserIdentityMapper.toJson(userIdentity);
        System.out.println(userIdentityJson);
        Response response = addUser.request().accept(HttpSender.APPLICATION_JSON).post(Entity.entity(userIdentityJson, HttpSender.APPLICATION_JSON));
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
     * @param uasUri             URI to the User Admin Service
     * @param applicationTokenId TokenId fetched from the XML in logOnApplication
     * @param adminUserTokenId   TokenId fetched from the XML returned in logOnApplicationAndUser
     * @param roles              List of roles to be created.
     * @return List of the roles that has been creatd. Empty list if no roles were created.
     */
    public static List<UserApplicationRoleEntry> addRolesToUser(String uasUri, String applicationTokenId, String adminUserTokenId, List<UserApplicationRoleEntry> roles) {

        List<String> createdRolesXml = new ArrayList<>();
        List<UserApplicationRoleEntry> createdRoles = new ArrayList<>();
        WebTarget userTarget = buildBaseTarget(uasUri, applicationTokenId, adminUserTokenId).path("/user");
        Response response;
        String userName = "";
        for (UserApplicationRoleEntry role : roles) {
            String roleXml = role.toXML();
            log.trace("Try to add role {}", roleXml);
            userName = role.getUserName();
            response = userTarget.path(userName).path("/role").request().accept(HttpSender.APPLICATION_XML).post(Entity.entity(roleXml, HttpSender.APPLICATION_XML));
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
            UserApplicationRoleEntry createdUserRole = UserApplicationRoleEntry.fromXml(createdRoleXml);
            createdRoles.add(createdUserRole);
        }
        return createdRoles;
    }

    public static List<UserApplicationRoleEntry> listUserRoles(String uasUri, String adminAppTokenId, String adminUserTokenId, String applicationId, String userId) {


        List<UserApplicationRoleEntry> userRoles = new ArrayList<>();
        WebTarget userTarget = buildBaseTarget(uasUri, adminAppTokenId, adminUserTokenId).path("/user");
        Response response;
        response = userTarget.path(userId).path("roles").request().accept(HttpSender.APPLICATION_XML).get();
        if (response.getStatus() == OK.getStatusCode()) {
            String rolesXml = response.readEntity(String.class);
            log.debug("CommandListRoles - listUserRoles - Created role ok {}", rolesXml);
            userRoles = UserRoleXpathHelper.getUserRoleFromUserAggregateXml(rolesXml);  // ).rolesViaJackson
        } else {
            log.trace("Failed to find roles for user {}, response status {}", userId, response.getStatus());
        }
        return userRoles;
    }

    public static String getUserTokenByUserTokenId(String stsUri, String myAppTokenId, String myAppTokenXml ,String userTokenId) {
        URI tokenServiceUri = UriBuilder.fromUri(stsUri).build();
        CommandGetUsertokenByUsertokenId command = new CommandGetUsertokenByUsertokenId(tokenServiceUri,myAppTokenId,myAppTokenXml, userTokenId);
        String userTokenXml = command.execute();
        return userTokenXml;
    }


    @Deprecated  // Jersey has to go..
    static WebTarget buildBaseTarget(String baseUri, String applicationTokenId, String adminUserTokenId) {
        Client httpClient = ClientBuilder.newClient();
        return httpClient.target(baseUri).path(applicationTokenId + "/" + adminUserTokenId);
    }
}
