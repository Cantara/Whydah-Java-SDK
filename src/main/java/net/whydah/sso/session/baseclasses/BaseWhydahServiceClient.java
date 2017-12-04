package net.whydah.sso.session.baseclasses;


import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.commands.extras.CommandSendSms;
import net.whydah.sso.commands.userauth.*;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.types.UserCredential;
import org.constretto.ConstrettoConfiguration;
import org.constretto.exception.ConstrettoConversionException;
import org.constretto.exception.ConstrettoExpressionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

public class BaseWhydahServiceClient {

    private static volatile WhydahApplicationSession was = null;
    private static final Object lock = new Object();

    protected static Logger log;
    protected URI uri_securitytoken_service;


    protected URI uri_useradmin_service;
    //    protected URI uri_useridentitybackend_service;
    protected URI uri_crm_service;
    protected URI uri_report_service;
    protected static String TAG = "";
    private static ApplicationCredential applicationCredential;
    private static String securitytokenserviceurl;
    private static String useradminserviceurl;

    static {
        TAG = BaseWhydahServiceClient.class.getName();
        log = LoggerFactory.getLogger(TAG);

    }

    public static synchronized void setWas(WhydahApplicationSession was) {
        BaseWhydahServiceClient.was = was;
    }

    public BaseWhydahServiceClient(String securitytokenserviceurl,
                                   String useradminserviceurl,
                                   String activeApplicationId,
                                   String applicationname,
                                   String applicationsecret) throws URISyntaxException {
        this(securitytokenserviceurl, useradminserviceurl, new ApplicationCredential(activeApplicationId, applicationname, applicationsecret));
    }


    public BaseWhydahServiceClient(String securitytokenserviceurl,
                                   String useradminserviceurl,
                                   ApplicationCredential applicationCredential) throws URISyntaxException {

        this.applicationCredential = applicationCredential;
        this.securitytokenserviceurl = securitytokenserviceurl;
        this.useradminserviceurl = useradminserviceurl;

        getWAS();
//        if (was == null) {
//            setWas(WhydahApplicationSession.getInstance(securitytokenserviceurl, useradminserviceurl, applicationCredential));
//            was.updateApplinks(true);
//        }
//
//        this.uri_securitytoken_service = URI.create(securitytokenserviceurl);
//        if (useradminserviceurl != null && useradminserviceurl.length() > 8) {  // UAS is optinal
//            this.uri_useradmin_service = URI.create(useradminserviceurl);
//        }

    }

    public BaseWhydahServiceClient(ConstrettoConfiguration configuration) {
        this.TAG = this.getClass().getName();
        this.log = LoggerFactory.getLogger(TAG);
        try {
            if (configuration.hasValue("securitytokenservice")) {
                this.uri_securitytoken_service = URI.create(configuration.evaluateToString("securitytokenservice"));
                this.securitytokenserviceurl = configuration.evaluateToString("securitytokenservice");

            }
            if (configuration.hasValue("useradminservice")) {
                this.uri_useradmin_service = URI.create(configuration.evaluateToString("useradminservice"));
                this.useradminserviceurl = configuration.evaluateToString("useradminservice");
            }
            if (configuration.hasValue("crmservice")) {
                this.uri_crm_service = URI.create(configuration.evaluateToString("crmservice"));
            }
            if (configuration.hasValue("reportservice")) {
                this.uri_report_service = URI.create(configuration.evaluateToString("reportservice"));
            }


            String applicationid = configuration.evaluateToString("applicationid");
            String applicationname = configuration.evaluateToString("applicationname");
            String applicationsecret = configuration.evaluateToString("applicationsecret");
            ApplicationCredential myApplicationCredential = new ApplicationCredential(applicationid, applicationname, applicationsecret);
            this.applicationCredential = myApplicationCredential;

            getWAS();
//            String uasUrl = null;
//            if (uri_useradmin_service != null) {
//                uasUrl = uri_useradmin_service.toString();
//
//            }
//
//
//            if (was == null) {
//                setWas(WhydahApplicationSession.getInstance(uri_securitytoken_service.toString(), uasUrl, myApplicationCredential));
//            }

        } catch (ConstrettoExpressionException constrettoExpressionException) {
            log.debug("Some parameters where not found");
        } catch (ConstrettoConversionException cce) {
            log.debug("Some parameters where not found");

        } catch (Exception ex) {
            throw ex;
        }
    }


    public BaseWhydahServiceClient(Properties properties) {
        this.TAG = this.getClass().getName();
        this.log = LoggerFactory.getLogger(TAG);

        try {
            if (properties.getProperty("securitytokenservice", null) != null) {
                this.uri_securitytoken_service = URI.create(properties.getProperty("securitytokenservice"));
                this.securitytokenserviceurl = properties.getProperty("securitytokenservice");

            }
            if (properties.getProperty("useradminservice", null) != null) {
                this.uri_useradmin_service = URI.create(properties.getProperty("useradminservice"));
                this.useradminserviceurl = properties.getProperty("useradminservice");
            }
            if (properties.getProperty("crmservice", null) != null) {
                this.uri_crm_service = URI.create(properties.getProperty("crmservice"));
            }
            if (properties.getProperty("reportservice", null) != null) {
                this.uri_report_service = URI.create(properties.getProperty("reportservice"));
            }


            String applicationid = properties.getProperty("applicationid");
            String applicationname = properties.getProperty("applicationname");
            String applicationsecret = properties.getProperty("applicationsecret");
            ApplicationCredential myApplicationCredential = new ApplicationCredential(applicationid, applicationname, applicationsecret);
            this.applicationCredential = myApplicationCredential;

            getWAS();

//            String uasUrl = null;
//            if (uri_useradmin_service != null) {
//                uasUrl = uri_useradmin_service.toString();
//
//            }
//            if (was == null) {
//                setWas(WhydahApplicationSession.getInstance(uri_securitytoken_service.toString(), uasUrl, myApplicationCredential));
//            }
        } catch (Exception ex) {
            throw ex;
        }
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


    public synchronized WhydahApplicationSession getWAS() {

        if (was == null) {
            setWas(WhydahApplicationSession.getInstance(securitytokenserviceurl, getUri_useradmin_service(), applicationCredential));
            was.updateApplinks(true);
        }
        return was;
    }

    public static Integer calculateTokenRemainingLifetimeInSeconds(String userTokenXml) {
        Integer tokenLifespanSec = UserTokenXpathHelper.getLifespan(userTokenXml);
        Long tokenTimestampMsSinceEpoch = UserTokenXpathHelper.getTimestamp(userTokenXml);

        if (tokenLifespanSec == null || tokenTimestampMsSinceEpoch == null) {
            return null;
        }
        long endOfTokenLifeMs = tokenTimestampMsSinceEpoch + tokenLifespanSec;
        long remainingLifeMs = endOfTokenLifeMs - System.currentTimeMillis();
        return (int) (remainingLifeMs / 1000);
    }



    public Boolean isApplicationTokenIdValid(String applicationTokenId) {
        return new CommandValidateApplicationTokenId(was.getSTS(), applicationTokenId).execute();
    }

    public String getUserTokenXml(String userTokenId) throws URISyntaxException {
        return new CommandGetUsertokenByUsertokenId(new URI(was.getSTS()),
                was.getActiveApplicationTokenId(),
                was.getActiveApplicationTokenXML(),
                userTokenId).
                execute();
    }


    public String getMyAppTokenID() {
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        return was.getActiveApplicationTokenId();
    }

    public String getMyAppTokenXml() {

        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        return was.getActiveApplicationTokenXML();
    }

    public String getUserTokenFromUserTokenId(String userTokenId) {
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }

        return new CommandGetUsertokenByUsertokenId(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), userTokenId).execute();
    }

    //SSO LOGIN SERVICE

    public String getUserTokenByUserTicket(String userticket) {
        return new CommandGetUsertokenByUserticket(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), userticket).execute();
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
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        log.debug("getUserTokenByPin() - Application logon OK. applicationTokenId={}. Log on with user adminUserTokenId {}.", getMyAppTokenID(), adminUserTokenId);
        String userTokenXML = new CommandLogonUserByPhoneNumberPin(uri_securitytoken_service, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), adminUserTokenId, phoneNo, pin, userTicket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public String getUserTokenByPin2(String adminUserTokenId, String phoneNo, String pin, String userTicket) {
        log.debug("getUserTokenByPin() called with " + "phoneNo = [" + phoneNo + "], pin = [" + pin + "], userTicket = [" + userTicket + "]");
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        log.debug("getUserTokenByPin2() - Application logon OK. applicationTokenId={}. Log on with user phoneno {}.", was.getActiveApplicationTokenId(), phoneNo);
        String userTokenXML = new CommandLogonUserByPhoneNumberPin(uri_securitytoken_service, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), adminUserTokenId, phoneNo, pin, userTicket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public String getUserToken(UserCredential user, String userticket) {
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        log.debug("getUserToken - Application logon OK. applicationTokenId={}. Log on with user credentials {}.", was.getActiveApplicationTokenId(), user.toString());
        String userTokenXML = new CommandLogonUserByUserCredential(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), user, userticket).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public boolean createTicketForUserTokenID(String userTicket, String userTokenID) {
        log.debug("createTicketForUserTokenID - apptokenid: {}", was.getActiveApplicationTokenId());
        log.debug("createTicketForUserTokenID - userticket: {} userTokenID: {}", userTicket, userTokenID);
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        return new CommandCreateTicketForUserTokenID(uri_securitytoken_service, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), userTicket, userTokenID).execute();
    }

    public String getUserTokenByUserTokenID(String usertokenId) {
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        String userTokenXML = new CommandGetUsertokenByUsertokenId(uri_securitytoken_service, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), usertokenId).execute();
        getWAS().updateDefcon(userTokenXML);
        return userTokenXML;
    }

    public void releaseUserToken(String userTokenId) {
        log.trace("Releasing userTokenId={}", userTokenId);

        if (new CommandReleaseUserToken(uri_securitytoken_service, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), userTokenId).execute()) {
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
        return new CommandValidateUsertokenId(uri_securitytoken_service, was.getActiveApplicationTokenId(), usertokenid).execute();
    }

    public boolean sendUserSMSPin(String phoneNo) {
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        if (phoneNo == null) {
            return false;
        }
        log.debug("sendUserSMSPin - apptokenid: {}", was.getActiveApplicationTokenId());
        log.debug("sendUserSMSPin - phoneNo: {} ", phoneNo);

        return new CommandGenerateAndSendSmsPin(uri_securitytoken_service, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), phoneNo).execute();
    }

    public boolean sendSMSMessage(String phoneNo, String msg) {
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }

        if (phoneNo == null || msg == null) {
            return false;
        }
        log.debug("sendSMSMessage - apptokenid: {}", was.getActiveApplicationTokenId());
        log.debug("sendSMSMessage - phoneNo: {} msg: {}", phoneNo, msg);

        return new CommandSendSms(uri_securitytoken_service, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), phoneNo, msg).execute();
    }

    public String appendTicketToRedirectURI(String redirectURI, String userticket) {
        char paramSep = redirectURI.contains("?") ? '&' : '?';
        redirectURI += paramSep + "userticket" + '=' + userticket;
        return redirectURI;
    }


	public List<Application> getApplicationList(){
		if(was.getApplicationList()==null){
				was.updateApplinks();
		}
		return was.getApplicationList();
	}
	

}