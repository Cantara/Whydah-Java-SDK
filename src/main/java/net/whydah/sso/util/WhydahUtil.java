package net.whydah.sso.util;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.appauth.CommandRenewApplicationSession;
import net.whydah.sso.commands.userauth.CommandGetUsertokenByUsertokenId;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.commands.userauth.CommandValidateUsertokenId;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URI;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;


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

    /**
     * Logon your application to Whydah.
     *
     * @param stsURI            URI to the Security Token Service, where you do logon
     * @param applicationID     The registered ID of your application.
     * @param applicationSecret Current, updatet secret of your application.
     * @return applicationTokenXML Representing the application. In this you will find the applicationtokenId used as application session     * @param applicationSecret Current, updatet secret of your application's.
     * for further operations.
     */
    public static String logOnApplication(String stsURI, String applicationID, String applicationSecret, int timeout) {
        URI tokenServiceUri = URI.create(stsURI);
        ApplicationCredential appCredential = new ApplicationCredential(applicationID, "", applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential, timeout).execute();
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

    public static String logOnApplication(String stsURI, ApplicationCredential myAppcredential, int timeout) {
        URI tokenServiceUri = URI.create(stsURI);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, myAppcredential, timeout).execute();
        if (myAppTokenXml == null || myAppTokenXml.length() < 10) {
            log.error("logOnApplication - unable to create application session on " + stsURI + " for appCredentials: " + ApplicationCredentialMapper.toXML(myAppcredential));

        }
        return myAppTokenXml;

    }

    public static String extendApplicationSession(String stsURI, String applicationTokenId) {
        URI tokenServiceUri = URI.create(stsURI);
        String myAppTokenXml = new CommandRenewApplicationSession(tokenServiceUri, applicationTokenId).execute();
        return myAppTokenXml;

    }

    public static String extendApplicationSession(String stsURI, String applicationTokenId, int millisecondswait) {
        URI tokenServiceUri = URI.create(stsURI);
        String myAppTokenXml = new CommandRenewApplicationSession(tokenServiceUri, applicationTokenId, millisecondswait).execute();
        return myAppTokenXml;

    }

    public static String logOnApplicationAndUser(String stsURI, String applicationID, String applicationName, String applicationSecret, String username, String password) {
        URI tokenServiceUri = URI.create(stsURI);
        ApplicationCredential appCredential = new ApplicationCredential(applicationID, applicationName, applicationSecret);
        String myAppTokenXml = new CommandLogonApplication(tokenServiceUri, appCredential).execute();
        String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        UserCredential userCredential = new UserCredential(username, password);
        String userTokenXML = new CommandLogonUserByUserCredential(tokenServiceUri, myApplicationTokenID, myAppTokenXml, userCredential, UUID.randomUUID().toString()).execute();
        // getWAS().updateDefcon(userTokenXML);
        return userTokenXML;

    }


    public static String logOnUser(WhydahApplicationSession was, UserCredential userCredential) {
        URI tokenServiceUri = URI.create(was.getSTS());
        if (was.getActiveApplicationTokenId() == null || was.getActiveApplicationTokenId().length() < 8) {
            log.warn("Illegal application session from WhydahApplicationSession, applicationTokenId:" + was.getActiveApplicationTokenId());

        }
        String userTokenXML = new CommandLogonUserByUserCredential(tokenServiceUri, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), userCredential, UUID.randomUUID().toString()).execute();
        was.updateDefcon(userTokenXML);
        return userTokenXML;

    }


    public static String extendUserSession(WhydahApplicationSession was, UserCredential userCredential) {
        URI tokenServiceUri = URI.create(was.getSTS());
        String userTokenXML = new CommandLogonUserByUserCredential(tokenServiceUri, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), userCredential, UUID.randomUUID().toString()).execute();
        was.updateDefcon(userTokenXML);
        return userTokenXML;

    }


    public static String getUserTokenByUserTokenId(String stsUri, String myAppTokenId, String myAppTokenXml, String userTokenId) {
        URI tokenServiceUri = URI.create(stsUri);
        CommandGetUsertokenByUsertokenId command = new CommandGetUsertokenByUsertokenId(tokenServiceUri, myAppTokenId, myAppTokenXml, userTokenId);
        String userTokenXml = command.execute();
        return userTokenXml;
    }

    /**
     * A simple util method to add some more details to the health endpont
     */
    public static String getPrintableStatus(WhydahApplicationSession was) {

        String statusString = "Whydah session:\n" +
                " DEFCON: " + was.getDefcon() + "\"\n" +
                " - STS: " + was.getSTS() + "\"\n" +
                " - UAS: " + was.getUAS() + "\"\n" +
                " - running since: " + getRunningSince() + "\"\n" +
                " - hasApplicationToken: " + Boolean.toString(was.getActiveApplicationTokenId() != null) + "\n" +
                " - hasValidApplicationToken: " + Boolean.toString(was.checkActiveSession()) + "\n" +
                " - hasApplicationsMetadata:" + Boolean.toString(was.getApplicationList().size() > 2) + "\n";
        return statusString;

    }

    public String getWASHealthAsJson(WhydahApplicationSession was) {
        boolean hasApplicationToken = false;
        boolean hasValidApplicationToken = false;
        boolean hasApplicationsMetadata = false;
        try {
            hasApplicationToken = (was.getActiveApplicationTokenId() != null);
            hasValidApplicationToken = was.checkActiveSession();
            hasApplicationsMetadata = was.getApplicationList().size() > 2;

        } catch (Exception e) {

        }
        return "\n" +
                "  \"WhydahApplicationSession\": {\n" +
                "     \"DEFCON\": \"" + was.getDefcon() + "\",\n" +
                "     \"STS\": \"" + was.getSTS() + "\",\n" +
                "     \"UAS\": \"" + was.getUAS() + "\",\n" +
                "     \"running since\": \"" + getRunningSince() + "\",\n" +
                "     \"hasApplicationToken\": \"" + Boolean.toString(hasApplicationToken) + "\",\n" +
                "     \"hasValidApplicationToken\": \"" + Boolean.toString(hasValidApplicationToken) + "\",\n" +
                "     \"hasApplicationsMetadata\": \"" + Boolean.toString(hasApplicationsMetadata) + "\",\n" +
                "   }\n";
    }

    /**
     * A simple util method to add some more details to the health endpont
     */
    public static String getPrintableStatus(WhydahApplicationSession was, UserCredential userCredential) {

        String userticket = UUID.randomUUID().toString();
        String userToken = new CommandLogonUserByUserCredential(URI.create(was.getSTS()), was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), userCredential, userticket).execute();
        String userTokenId = UserXpathHelper.getUserTokenId(userToken);
        boolean validUser = new CommandValidateUsertokenId(URI.create(was.getSTS()), was.getActiveApplicationTokenId(), userTokenId).execute();


        String statusString = "Whydah session:\n" +
                " DEFCON: " + was.getDefcon() + "\"\n" +
                " - STS: " + was.getSTS() + "\"\n" +
                " - UAS: " + was.getUAS() + "\"\n" +
                " - running since: " + getRunningSince() + "\"\n" +
                " - hasApplicationToken: " + Boolean.toString(was.getActiveApplicationTokenId() != null) + "\n" +
                " - hasValidApplicationToken: " + Boolean.toString(was.checkActiveSession()) + "\n" +
                " - hasValidAdminUserToken: " + validUser + "\n" +
                " - hasApplicationsMetadata:" + Boolean.toString(was.getApplicationList().size() > 2) + "\n";

        return statusString;

    }

    /**
     * A simple util method to add some more details to the health endpont
     */
    public static String getPrintableStatus(WhydahApplicationSession was, String userTokenId) {

        boolean validUser = new CommandValidateUsertokenId(URI.create(was.getSTS()), was.getActiveApplicationTokenId(), userTokenId).execute();


        String statusString = "Whydah session:\n" +
                " DEFCON: " + was.getDefcon() + "\"\n" +
                " - STS: " + was.getSTS() + "\"\n" +
                " - UAS: " + was.getUAS() + "\"\n" +
                " - running since: " + getRunningSince() + "\"\n" +
                " - hasApplicationToken: " + Boolean.toString(was.getActiveApplicationTokenId() != null) + "\n" +
                " - hasValidApplicationToken: " + Boolean.toString(was.checkActiveSession()) + "\n" +
                " - hasValidAdminUserToken: " + validUser + "\n" +
                " - hasApplicationsMetadata:" + Boolean.toString(was.getApplicationList().size() > 2) + "\n";

        return statusString;

    }

    /**
     * A standard Util to be used to pring the resolved property configuration after startup
     *
     * @param properties
     */
    public static void printConfiguration(Properties properties) {
        for (Object key : properties.keySet()) {
            log.info("Using Property: {}, value: {}", key, properties.get(key));
        }
    }

    /**
     * A simple util method to add some more details to the health endpont
     */
    public static boolean hasValidAdminSession(WhydahApplicationSession was, String userTokenId) {

        boolean validUser = new CommandValidateUsertokenId(URI.create(was.getSTS()), was.getActiveApplicationTokenId(), userTokenId).execute();
        return validUser && (was.getActiveApplicationTokenId() != null) && was.checkActiveSession() && (was.getApplicationList().size() > 2);
    }

    /**
     * A simple util method to add some more details to the health endpont
     */
    public static boolean hasValidWhydahSession(WhydahApplicationSession was) {
        return (was.getActiveApplicationTokenId() != null) && was.checkActiveSession() && (was.getApplicationList().size() > 2);
    }

    private static final String ADMIN_APPLICATION_ID = "2212";
    private static final String ADMIN_APPLICATION_NAME = "Whydah-UserAdminService";
    private static final String ADMIN_ORGANIZATION_NAME = "Whydah";
    private static final String ADMIN_ROLE_NAME = "WhydahUserAdmin";
    private static final String ADMIN_ROLE_VALUE = "1";


    public static UserApplicationRoleEntry getWhydahUserAdminRole() {
        UserApplicationRoleEntry adminRole = new UserApplicationRoleEntry();
        adminRole.setApplicationId(ADMIN_APPLICATION_ID);
        adminRole.setApplicationName(ADMIN_APPLICATION_NAME);
        adminRole.setOrgName(ADMIN_ORGANIZATION_NAME);
        adminRole.setRoleName(ADMIN_ROLE_NAME);
        adminRole.setRoleValue(ADMIN_ROLE_VALUE);

        return adminRole;

    }

    public static String getRunningSince() {
        long uptimeInMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        return Instant.now().minus(uptimeInMillis, ChronoUnit.MILLIS).toString();
    }

    public static String getMyIPAddresssesString() {
        String ipAdresses = "";
        try {
            ipAdresses = InetAddress.getLocalHost().getHostAddress();
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            for (; n.hasMoreElements(); ) {
                NetworkInterface e = n.nextElement();

                Enumeration<InetAddress> a = e.getInetAddresses();
                for (; a.hasMoreElements(); ) {
                    InetAddress addr = a.nextElement();
                    ipAdresses = ipAdresses + "  " + addr.getHostAddress();
                }
            }
        } catch (Exception e) {
            ipAdresses = "Not resolved";
        }
        return ipAdresses;
    }

    public boolean hasUASAccessAdminRole(String userTokenXml) {
        UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);


        List<UserApplicationRoleEntry> roles = userToken.getRoleList();
        UserApplicationRoleEntry adminRole = WhydahUtil.getWhydahUserAdminRole();
        for (UserApplicationRoleEntry role : roles) {
            log.debug("Checking for adminrole user UID:{} roleName: {} ", userToken.getUid(), role.getRoleName());
            if (role.getApplicationId().equalsIgnoreCase(adminRole.getApplicationId())) {
                if (role.getApplicationName().equalsIgnoreCase(adminRole.getApplicationName())) {
                    if (role.getOrgName().equalsIgnoreCase(adminRole.getOrgName())) {
                        if (role.getRoleName().equalsIgnoreCase(adminRole.getRoleName())) {
                            if (role.getRoleValue().equalsIgnoreCase(adminRole.getRoleValue())) {
                                log.info("Whydah Admin user is true for name={}, uid={}", userToken.getUserName(), userToken.getUid());
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
