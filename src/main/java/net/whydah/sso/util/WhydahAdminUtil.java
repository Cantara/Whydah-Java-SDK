package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.adminapi.CommandListApplications;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.UserXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import org.slf4j.Logger;

import java.net.URI;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by totto on 24.06.15.
 */
public class WhydahAdminUtil {
    private static final Logger log = getLogger(WhydahAdminUtil.class);


    /**
     * List registered whydah applications
     *
     * @param tokenServiceUri URI to the Security Token Service, where you do logon
     * @param userAdminServiceUri URI to the User Admin  Service, for user directory access
     * @param appCredential Application access credentials.
     * @param userCredential User access credentials
     * @return Json Representing the applications.
     */
    public static String listRegisteredWhydahApplications(URI tokenServiceUri, URI userAdminServiceUri, ApplicationCredential appCredential, UserCredential userCredential){
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        String userticket = UUID.randomUUID().toString();
        String userToken = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);

        return new CommandListApplications(userAdminServiceUri, myApplicationTokenID,userTokenId,"").execute();
    }

}
