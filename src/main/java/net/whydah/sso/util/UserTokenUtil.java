package net.whydah.sso.util;

import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * The UserToken list the userauth's identity, and the roles that are enabled on the userauth.
 *
 * You may use UserTokenUtil to:
 * - Fetch the UserToken, based by an UserTokenId.
 * - Query the content of the UserToken. Eg. ask for the RoleValue for an role that the userauth has.
 * Created by baardl on 23.06.15.
 */
public class UserTokenUtil {
    private static final Logger log = getLogger(UserTokenUtil.class);

    public static String findValidUserTokenByUserTokenId(String stsUri, String applicationTokenId, String onBehafeOfApplicationToken, String userTokenId) {
        WebTarget findUserTokenUri = buildBaseTarget(stsUri, applicationTokenId).path("get_usertoken_by_usertokenid");

        Form form = new Form();
        form.param("apptoken", onBehafeOfApplicationToken);
        form.param("usertokenid", userTokenId);
        Response response = findUserTokenUri.request().accept(MediaType.APPLICATION_XML).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED));
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.info("CommandFindUserToken - findValidUserTokenByUserTokenId - User authentication failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.debug("CommandFindUserToken - findValidUserTokenByUserTokenId - Log on OK with response {}", responseXML);
            return responseXML;
        }
        throw new IllegalArgumentException("Not found");

    }

    static WebTarget buildBaseTarget(String baseUri, String applicationTokenId) {
        Client httpClient = ClientBuilder.newClient();
        return httpClient.target(baseUri).path("/userauth" ).path(applicationTokenId);
    }
}
