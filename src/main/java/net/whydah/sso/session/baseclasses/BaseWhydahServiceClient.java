package net.whydah.sso.session.baseclasses;


import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.commands.extras.CommandSendSms;
import net.whydah.sso.commands.userauth.CommandCreateTicketForUserTokenID;
import net.whydah.sso.commands.userauth.CommandGenerateAndSendSmsPin;
import net.whydah.sso.commands.userauth.CommandGetUserTokenByUserTicket;
import net.whydah.sso.commands.userauth.CommandGetUserTokenByUserTokenId;
import net.whydah.sso.commands.userauth.CommandLogonUserByPhoneNumberPin;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.commands.userauth.CommandReleaseUserToken;
import net.whydah.sso.commands.userauth.CommandValidateUserTokenId;
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

public class BaseWhydahServiceClient {

    protected static final Logger log = LoggerFactory.getLogger(BaseWhydahServiceClient.class);

    protected static final String TAG = BaseWhydahServiceClient.class.getName();

    private static AtomicReference<WhydahApplicationSession> whydahApplicationSessionRef = new AtomicReference<>();
    private static AtomicReference<ApplicationCredential> applicationCredentialRef = new AtomicReference<>();

    protected final URI uri_securitytoken_service;
    protected final URI uri_useradmin_service;
    protected final URI uri_crm_service;
    protected final URI uri_report_service;


    public BaseWhydahServiceClient(String securitytokenserviceurl,
                                   String useradminserviceurl,
                                   String applicationId,
                                   String applicationname,
                                   String applicationsecret) throws URISyntaxException {
        this(securitytokenserviceurl, useradminserviceurl, new ApplicationCredential(applicationId, applicationname, applicationsecret));
    }


    public BaseWhydahServiceClient(String securitytokenserviceurl,
                                   String useradminserviceurl,
                                   ApplicationCredential applicationCredential) throws URISyntaxException {

        setApplicationCredential(applicationCredential);
        this.uri_securitytoken_service = URI.create(securitytokenserviceurl);
        this.uri_useradmin_service = URI.create(useradminserviceurl);
        this.uri_crm_service = null;
        this.uri_report_service = null;

        getWAS();

    }

    public BaseWhydahServiceClient(ConstrettoConfiguration configuration) {
        if (configuration.hasValue("securitytokenservice")) {
            this.uri_securitytoken_service = URI.create(configuration.evaluateToString("securitytokenservice"));
        } else {
            this.uri_securitytoken_service = null;
        }
        if (configuration.hasValue("useradminservice")) {
            this.uri_useradmin_service = URI.create(configuration.evaluateToString("useradminservice"));
        } else {
            this.uri_useradmin_service = null;
        }
        if (configuration.hasValue("crmservice")) {
            this.uri_crm_service = URI.create(configuration.evaluateToString("crmservice"));
        } else {
            this.uri_crm_service = null;
        }
        if (configuration.hasValue("reportservice")) {
            this.uri_report_service = URI.create(configuration.evaluateToString("reportservice"));
        } else {
            this.uri_report_service = null;
        }

        String applicationid = configuration.evaluateToString("applicationid");
        String applicationname = configuration.evaluateToString("applicationname");
        String applicationsecret = configuration.evaluateToString("applicationsecret");
        ApplicationCredential myApplicationCredential = new ApplicationCredential(applicationid, applicationname, applicationsecret);
        setApplicationCredential(myApplicationCredential);

        getWAS();
    }


    public BaseWhydahServiceClient(Properties properties) {
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
        String applicationid = properties.getProperty("applicationid");
        String applicationname = properties.getProperty("applicationname");
        String applicationsecret = properties.getProperty("applicationsecret");
        ApplicationCredential myApplicationCredential = new ApplicationCredential(applicationid, applicationname, applicationsecret);
        setApplicationCredential(myApplicationCredential);

        getWAS();
    }

    //GENERAL
    public String getUri_useradmin_service() {
        String uasUrl = null;
        if (uri_useradmin_service != null) {
            uasUrl = uri_useradmin_service.toString();
            return uasUrl;

        }
        return null;
    }

    public String getUri_securitytoken_service() {
        return uri_securitytoken_service.toString();
    }

    public static void setWhydahApplicationSession(WhydahApplicationSession whydahApplicationSession) {
        whydahApplicationSessionRef.set(whydahApplicationSession);
    }


    public WhydahApplicationSession getWAS() {
        WhydahApplicationSession whydahApplicationSession = whydahApplicationSessionRef.get();
        if (whydahApplicationSession == null) {
            whydahApplicationSession = WhydahApplicationSession.getInstance(getUri_securitytoken_service(), getUri_useradmin_service(), applicationCredentialRef.get());
            setWhydahApplicationSession(whydahApplicationSession);
            whydahApplicationSession.updateApplinks(true);
        }
        return whydahApplicationSession;
    }

    public static Integer calculateTokenRemainingLifetimeInSeconds(String userTokenXml) {
        Long tokenLifespanSec = UserTokenXpathHelper.getLifespan(userTokenXml);
        Long tokenTimestampMsSinceEpoch = UserTokenXpathHelper.getTimestamp(userTokenXml);

        if (tokenLifespanSec == null || tokenTimestampMsSinceEpoch == null) {
            return null;
        }
        long endOfTokenLifeMs = tokenTimestampMsSinceEpoch + tokenLifespanSec;
        long remainingLifeMs = endOfTokenLifeMs - System.currentTimeMillis();
        return (int) (remainingLifeMs / 1000);
    }


    public Boolean isApplicationTokenIdValid(String applicationTokenId) {
        return new CommandValidateApplicationTokenId(getWAS().getSTS(), applicationTokenId).execute();
    }

    public String getUserTokenXml(String userTokenId) throws URISyntaxException {
        return new CommandGetUserTokenByUserTokenId(new URI(getWAS().getSTS()),
                getWAS().getActiveApplicationTokenId(),
                getWAS().getActiveApplicationTokenXML(),
                userTokenId).
                execute();
    }


    public String getMyAppTokenID() {
        return getWAS().getActiveApplicationTokenId();
    }

    public String getMyAppTokenXml() {
        return getWAS().getActiveApplicationTokenXML();
    }

    public String getUserTokenFromUserTokenId(String userTokenId) {
        return new CommandGetUserTokenByUserTokenId(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), userTokenId).execute();
    }

    //SSO LOGIN SERVICE

    public String getUserTokenByUserTicket(String userticket) {
        return new CommandGetUserTokenByUserTicket(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), userticket).execute();
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
        log.debug("getUserTokenByPin() - Application logon OK. applicationTokenId={}. Log on with user adminUserTokenId {}.", getMyAppTokenID(), adminUserTokenId);
        String userTokenXML = new CommandLogonUserByPhoneNumberPin(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), adminUserTokenId, phoneNo, pin, userTicket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public String getUserTokenByPin2(String adminUserTokenId, String phoneNo, String pin, String userTicket) {
        log.debug("getUserTokenByPin() called with " + "phoneNo = [" + phoneNo + "], pin = [" + pin + "], userTicket = [" + userTicket + "]");
        log.debug("getUserTokenByPin2() - Application logon OK. applicationTokenId={}. Log on with user phoneno {}.", getWAS().getActiveApplicationTokenId(), phoneNo);
        String userTokenXML = new CommandLogonUserByPhoneNumberPin(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), adminUserTokenId, phoneNo, pin, userTicket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public String getUserToken(UserCredential user, String userticket) {
        log.debug("getUserToken - Application logon OK. applicationTokenId={}. Log on with user credentials {}.", getWAS().getActiveApplicationTokenId(), user.toString());
        String userTokenXML = new CommandLogonUserByUserCredential(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), user, userticket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public boolean createTicketForUserTokenID(String userTicket, String userTokenID) {
        log.debug("createTicketForUserTokenID - apptokenid: {}", getWAS().getActiveApplicationTokenId());
        log.debug("createTicketForUserTokenID - userticket: {} userTokenID: {}", userTicket, userTokenID);
        return new CommandCreateTicketForUserTokenID(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), userTicket, userTokenID).execute();
    }

    public String getUserTokenByUserTokenID(String usertokenId) {
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
        if (phoneNo == null) {
            return false;
        }
        log.debug("sendUserSMSPin - apptokenid: {}", getWAS().getActiveApplicationTokenId());
        log.debug("sendUserSMSPin - phoneNo: {} ", phoneNo);

        return new CommandGenerateAndSendSmsPin(uri_securitytoken_service, getWAS().getActiveApplicationTokenId(), getWAS().getActiveApplicationTokenXML(), phoneNo).execute();
    }

    public boolean sendSMSMessage(String phoneNo, String msg) {
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
        return getWAS().getApplicationList();
    }

    public static void setApplicationCredential(ApplicationCredential applicationCredential) {
        BaseWhydahServiceClient.applicationCredentialRef.set(applicationCredential);
    }


}