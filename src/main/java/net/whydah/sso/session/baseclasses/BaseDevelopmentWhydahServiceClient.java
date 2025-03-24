package net.whydah.sso.session.baseclasses;

import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.commands.extras.CommandSendSms;
import net.whydah.sso.commands.userauth.*;
import net.whydah.sso.config.ApplicationMode;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import org.constretto.ConstrettoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.Optional.ofNullable;

public class BaseDevelopmentWhydahServiceClient {

    private static final Logger log = LoggerFactory.getLogger(BaseDevelopmentWhydahServiceClient.class);

    private static final AtomicReference<WhydahApplicationSession> wasRef = new AtomicReference<>();

    protected static final String TAG = BaseDevelopmentWhydahServiceClient.class.getName();

    protected final URI uri_securitytoken_service;
    protected final URI uri_useradmin_service;
    protected final URI uri_useridentitybackend_service;
    protected final URI uri_crm_service;
    protected final URI uri_report_service;
    private final ApplicationCredential applicationCredential;

    public BaseDevelopmentWhydahServiceClient(String securitytokenserviceurl,
                                              String useradminserviceurl,
                                              String activeApplicationId,
                                              String applicationname,
                                              String applicationsecret) throws URISyntaxException {
        this(securitytokenserviceurl, useradminserviceurl, new ApplicationCredential(activeApplicationId, applicationname, applicationsecret));
    }

    public BaseDevelopmentWhydahServiceClient(String securitytokenserviceurl,
                                              String useradminserviceurl,
                                              ApplicationCredential myApplicationCredential) throws URISyntaxException {

        this.applicationCredential = myApplicationCredential;
        this.uri_securitytoken_service = URI.create(securitytokenserviceurl);
        if (useradminserviceurl != null && useradminserviceurl.length() > 8) {  // UAS is optinal
            this.uri_useradmin_service = URI.create(useradminserviceurl);
        } else {
            this.uri_useradmin_service = null;
        }
        this.uri_useridentitybackend_service = null;
        this.uri_crm_service = null;
        this.uri_report_service = null;
        getWAS();

    }

    public BaseDevelopmentWhydahServiceClient(ConstrettoConfiguration configuration) {
        this.uri_securitytoken_service = ofNullable(WhydahInternalConstrettoUtils.getStringOrDefault(configuration, "securitytokenservice", null))
                .map(URI::create)
                .orElse(null);
        this.uri_useradmin_service = ofNullable(WhydahInternalConstrettoUtils.getStringOrDefault(configuration, "useradminservice", null))
                .map(URI::create)
                .orElse(null);
        this.uri_crm_service = ofNullable(WhydahInternalConstrettoUtils.getStringOrDefault(configuration, "crmservice", null))
                .map(URI::create)
                .orElse(null);
        this.uri_report_service = ofNullable(WhydahInternalConstrettoUtils.getStringOrDefault(configuration, "reportservice", null))
                .map(URI::create)
                .orElse(null);
        this.uri_useridentitybackend_service = ofNullable(WhydahInternalConstrettoUtils.getStringOrDefault(configuration, "useridentitybackend", null))
                .map(URI::create)
                .orElse(null);
        String applicationid = WhydahInternalConstrettoUtils.getStringOrDefault(configuration, "applicationid", null);
        String applicationname = WhydahInternalConstrettoUtils.getStringOrDefault(configuration, "applicationname", null);
        String applicationsecret = WhydahInternalConstrettoUtils.getStringOrDefault(configuration, "applicationsecret", null);
        if (applicationid == null || applicationname == null || applicationsecret == null) {
            this.applicationCredential = null;
        } else {
            ApplicationCredential myApplicationCredential = new ApplicationCredential(applicationid, applicationname, applicationsecret);
            this.applicationCredential = myApplicationCredential;
            getWAS();
        }
    }


    public BaseDevelopmentWhydahServiceClient(Properties properties) {
        this.uri_securitytoken_service = ofNullable(properties.getProperty("securitytokenservice"))
                .map(URI::create)
                .orElse(null);
        this.uri_useradmin_service = ofNullable(properties.getProperty("useradminservice"))
                .map(URI::create)
                .orElse(null);
        this.uri_crm_service = ofNullable(properties.getProperty("crmservice"))
                .map(URI::create)
                .orElse(null);
        this.uri_report_service = ofNullable(properties.getProperty("reportservice"))
                .map(URI::create)
                .orElse(null);
        this.uri_useridentitybackend_service = ofNullable(properties.getProperty("useridentitybackend"))
                .map(URI::create)
                .orElse(null);
        String applicationid = properties.getProperty("applicationid");
        String applicationname = properties.getProperty("applicationname");
        String applicationsecret = properties.getProperty("applicationsecret");
        this.applicationCredential = new ApplicationCredential(applicationid, applicationname, applicationsecret);
        getWAS();
    }

    public static void setWas(WhydahApplicationSession was) {
        wasRef.set(was);
    }

    //GENERAL

    public WhydahApplicationSession getWAS() {

        WhydahApplicationSession was = wasRef.get();
        if (was == null) {
            synchronized (wasRef) {
                while (was == null) {
                    String uasUrl = null;
                    if (uri_useradmin_service != null) {
                        uasUrl = uri_useradmin_service.toString();

                    }

                    was = WhydahApplicationSession.getInstance(uri_securitytoken_service.toString(), uasUrl, applicationCredential);
                    if (wasRef.compareAndSet(null, was)) {
                        was.updateApplinks(true);
                    } else {
                        was = wasRef.get();
                    }
                }
            }
        }
        return was;
    }

    public static Integer calculateTokenRemainingLifetimeInSeconds(String userTokenXml) {
        Long tokenLifespanMs = UserTokenXpathHelper.getLifespan(userTokenXml);
        Long tokenTimestampMsSinceEpoch = UserTokenXpathHelper.getTimestamp(userTokenXml);

        if (tokenLifespanMs == null || tokenTimestampMsSinceEpoch == null) {
            return null;
        }
        long endOfTokenLifeMs = tokenTimestampMsSinceEpoch + tokenLifespanMs;
        long remainingLifeMs = endOfTokenLifeMs - System.currentTimeMillis();
        return (int) (remainingLifeMs / 1000);
    }


    public Boolean isApplicationTokenIdValid(String applicationTokenId) {
        return new CommandValidateApplicationTokenId(getWAS().getSTS(), applicationTokenId).execute();
    }

    public String getUserTokenXml(String userTokenId) throws URISyntaxException {

        String userTokenXML = new CommandGetUserTokenByUserTokenId(new URI(getWAS().getSTS()),
                getWAS().getActiveApplicationTokenId(),
                getWAS().getActiveApplicationTokenXML(),
                userTokenId).
                execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }


    public String getMyAppTokenID() {
        if (getWAS().getActiveApplicationToken() == null) {
            return null;
//            was.renewWhydahApplicationSession();
        }
        return getWAS().getActiveApplicationTokenId();
    }

    public String getMyAppTokenXml() {

        if (getWAS().getActiveApplicationToken() == null) {
            return null;
//          was.renewWhydahApplicationSession();
        }
        return getWAS().getActiveApplicationTokenXML();
    }

    public String getUserTokenFromUserTokenId(String userTokenId) {
        if (getWAS().getActiveApplicationToken() == null) {
            return null;
//            was.renewWhydahApplicationSession();
        }

        String userTokenXML = new CommandGetUserTokenByUserTokenId(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), userTokenId).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    //SSO LOGIN SERVICE

    public String getUserTokenByUserTicket(String userticket) {
        String userTokenXML = new CommandGetUserTokenByUserTicket(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), userticket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    /**
     * Logon for existing user.
     *
     * @param phoneNo    phone number entered by the user
     * @param pin        pin entered by the user
     * @param userTicket
     * @return userAggregateXML for the user represented by phoneNo
     */
    public String getUserTokenByPin(String adminUserTokenId, String phoneNo, String pin, String userTicket) {
        log.debug("getUserTokenByPin() called with " + "phoneNo = [" + phoneNo + "], pin = [" + pin + "], userTicket = [" + userTicket + "]");
        if (ApplicationMode.DEV.equals(ApplicationMode.getApplicationMode())) {
            return getDummyToken();
        }
        if (getWAS().getActiveApplicationToken() == null) {
            return null;
//            was.renewWhydahApplicationSession();
        }
        log.debug("getUserTokenByPin() - Application logon OK. applicationTokenId={}. Log on with user adminUserTokenId {}.", getMyAppTokenID(), adminUserTokenId);
        String userTokenXML = new CommandLogonUserByPhoneNumberPin(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), adminUserTokenId, phoneNo, pin, userTicket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public String getUserTokenByPin2(String adminUserTokenId, String phoneNo, String pin, String userTicket) {
        log.debug("getUserTokenByPin() called with " + "phoneNo = [" + phoneNo + "], pin = [" + pin + "], userTicket = [" + userTicket + "]");
        if (ApplicationMode.DEV.equals(ApplicationMode.getApplicationMode())) {
            return getDummyToken();
        }
        if (getWAS().getActiveApplicationToken() == null) {
            return null;
//          was.renewWhydahApplicationSession();
        }
        log.debug("getUserTokenByPin() - Application logon OK. applicationTokenId={}. Log on with user phoneno {}.", getWAS().getActiveApplicationTokenId(), phoneNo);
        return new CommandLogonUserByPhoneNumberPin(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), adminUserTokenId, phoneNo, pin, userTicket).execute();
    }

    public String getUserToken(UserCredential user, String userticket) {
        if (ApplicationMode.DEV.equals(ApplicationMode.getApplicationMode())) {
            return getDummyToken();
        }
        if (getWAS().getActiveApplicationToken() == null) {
            return null;
//            was.renewWhydahApplicationSession();
        }
        log.debug("getUserToken - Application logon OK. applicationTokenId={}. Log on with user credentials {}.", getWAS().getActiveApplicationTokenId(), user.toString());
        String userTokenXML = new CommandLogonUserByUserCredential(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), user, userticket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public boolean createTicketForUserTokenID(String userTicket, String userTokenID) {
        log.debug("createTicketForUserTokenID - apptokenid: {}", getWAS().getActiveApplicationTokenId());
        log.debug("createTicketForUserTokenID - userticket: {} userTokenID: {}", userTicket, userTokenID);
        if (getWAS().getActiveApplicationToken() == null) {
            return false;
//            was.renewWhydahApplicationSession();
        }
        return new CommandCreateTicketForUserTokenID(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), userTicket, userTokenID).execute();
    }

    public String getUserTokenByUserTokenID(String usertokenId) {
        if (ApplicationMode.DEV.equals(ApplicationMode.getApplicationMode())) {
            return getDummyToken();
        }
        if (getWAS().getActiveApplicationToken() == null) {
            return null;
//            was.renewWhydahApplicationSession();
        }
        String userTokenXML = new CommandGetUserTokenByUserTokenId(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), usertokenId).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public void releaseUserToken(String userTokenId) {
        log.trace("Releasing userTokenId={}", userTokenId);

        if (new CommandReleaseUserToken(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), userTokenId).execute()) {
            log.trace("Released userTokenId={}", userTokenId);
        } else {
            log.warn("releaseUserToken failed for userTokenId={}", userTokenId);
        }
    }

    public boolean verifyUserTokenId(String usertokenid) {
        if (usertokenid == null || usertokenid.length() < 4) {
            log.trace("verifyUserTokenId - Called with bogus usertokenid={}. return false", usertokenid);
            return false;
        }
        return new CommandValidateUserTokenId(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), usertokenid).execute();
    }

    public boolean sendUserSMSPin(String phoneNo) {
        if (getWAS().getActiveApplicationToken() == null) {
            return false;
//            was.renewWhydahApplicationSession();
        }
        if (phoneNo == null) {
            return false;
        }
        log.debug("sendUserSMSPin - apptokenid: {}", getWAS().getActiveApplicationTokenId());
        log.debug("sendUserSMSPin - phoneNo: {} ", phoneNo);

        return new CommandGenerateAndSendSmsPin(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), phoneNo).execute();
    }

    public boolean sendSMSMessage(String phoneNo, String msg) {
        if (getWAS().getActiveApplicationToken() == null) {
            return false;
//            was.renewWhydahApplicationSession();
        }

        if (phoneNo == null || msg == null) {
            return false;
        }
        log.debug("sendSMSMessage - apptokenid: {}", getWAS().getActiveApplicationTokenId());
        log.debug("sendSMSMessage - phoneNo: {} msg: {}", phoneNo, msg);

        return new CommandSendSms(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), phoneNo, msg).execute();
    }

    public String appendTicketToRedirectURI(String redirectURI, String userticket) {
        char paramSep = redirectURI.contains("?") ? '&' : '?';
        redirectURI += paramSep + "userticket" + '=' + userticket;
        return redirectURI;
    }


    public List<Application> getApplicationList() {
        if (getWAS().getApplicationList() == null) {
            getWAS().updateApplinks();
        }
        return getWAS().getApplicationList();
    }


    public static String getDummyToken() {
        return """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <usertoken xmlns:ns2="http://www.w3.org/1999/xhtml" id="759799fe-2e2f-4c8e-b096-d5796733d4d2">
                    <uid>7583278592730985723</uid>
                    <securitylevel>0</securitylevel>
                    <personRef></personRef>
                    <firstname>Olav</firstname>
                    <lastname>Nordmann</lastname>
                    <email></email>
                    <timestamp>7982374982374</timestamp>
                    <lifespan>3600000</lifespan>
                    <issuer>/iam/issuer/tokenverifier</issuer>
                    <application ID="2349785543">
                        <applicationName>MyApp</applicationName>
                        <organization ID="2349785543">
                            <organizationName>myCompany</organizationName>
                            <role name="janitor" value="Employed"/>
                            <role name="board" value="President"/>
                        </organization>
                        <organization ID="0078">
                            <organizationName>myDayJobCompany</organizationName>
                            <role name="board" value=""/>
                        </organization>
                    </application>
                    <application ID="appa">
                        <applicationName>App A</applicationName>
                        <organization ID="1078">
                            <organizationName>myFotballClub</organizationName>
                            <role name="janitor" value="Employed"/>
                        </organization>
                    </application>
                
                    <ns2:link type="application/xml" href="/" rel="self"/>
                    <hash type="MD5">7671ec2d5bac82d1e70b33c59b5c96a3</hash>
                </usertoken>""";

    }

}