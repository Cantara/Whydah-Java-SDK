package net.whydah.sso.session.baseclasses;


import net.whydah.sso.application.types.Application;
import net.whydah.sso.commands.adminapi.user.CommandCreatePinVerifiedUser;
import net.whydah.sso.commands.adminapi.user.CommandResetUserPassword;
import net.whydah.sso.commands.adminapi.user.role.CommandAddUserRole;
import net.whydah.sso.commands.adminapi.user.role.CommandGetUserRoles;
import net.whydah.sso.commands.adminapi.user.role.CommandUpdateUserRole;
import net.whydah.sso.commands.appauth.CommandValidateApplicationTokenId;
import net.whydah.sso.commands.extras.CommandSendSms;
import net.whydah.sso.commands.userauth.*;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import net.whydah.sso.user.mappers.UserRoleMapper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.user.types.UserToken;
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

    static WhydahApplicationSession was = null;
    protected Logger log;
    protected URI uri_securitytoken_service;
    protected URI uri_useradmin_service;
    protected URI uri_useridentitybackend_service;
    protected URI uri_crm_service;
    protected URI uri_report_service;
    protected String TAG = "";


    public BaseWhydahServiceClient(String securitytokenserviceurl,
                                   String useradminserviceurl,
                                   String activeApplicationId,
                                   String applicationname,
                                   String applicationsecret) throws URISyntaxException {

        if (was == null) {
            was = WhydahApplicationSession.getInstance(securitytokenserviceurl, useradminserviceurl, activeApplicationId, applicationname, applicationsecret);
        }
        
        this.uri_securitytoken_service = URI.create(securitytokenserviceurl);
        if (useradminserviceurl != null && useradminserviceurl.length() > 8) {  // UAS is optinal
            this.uri_useradmin_service = URI.create(useradminserviceurl);
        }

        this.TAG = this.getClass().getName();
        this.log = LoggerFactory.getLogger(TAG);
    }

    public BaseWhydahServiceClient(ConstrettoConfiguration configuration) {
        this.TAG = this.getClass().getName();
        this.log = LoggerFactory.getLogger(TAG);
        try {
            if (configuration.hasValue("securitytokenservice")) {
                this.uri_securitytoken_service = URI.create(configuration.evaluateToString("securitytokenservice"));
            }
            if (configuration.hasValue("useradminservice")) {
                this.uri_useradmin_service = URI.create(configuration.evaluateToString("useradminservice"));
            }
            if (configuration.hasValue("crmservice")) {
                this.uri_crm_service = URI.create(configuration.evaluateToString("crmservice"));
            }
            if (configuration.hasValue("reportservice")) {
                this.uri_report_service = URI.create(configuration.evaluateToString("reportservice"));
            }
            if (configuration.hasValue("useridentitybackend")) {
                this.uri_useridentitybackend_service = URI.create(configuration.evaluateToString("useridentitybackend"));
            }


            String applicationid = configuration.evaluateToString("applicationid");
            String applicationname = configuration.evaluateToString("applicationname");
            String applicationsecret = configuration.evaluateToString("applicationsecret");
            String uasUrl = null;
            if (uri_useradmin_service != null) {
                uasUrl = uri_useradmin_service.toString();

            }
            if (was == null) {
                was = WhydahApplicationSession.getInstance(uri_securitytoken_service.toString(), uasUrl, applicationid, applicationname, applicationsecret);
            }

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
            }
            if (properties.getProperty("useradminservice", null) != null) {
                this.uri_useradmin_service = URI.create(properties.getProperty("useradminservice"));
            }
            if (properties.getProperty("crmservice", null) != null) {
                this.uri_crm_service = URI.create(properties.getProperty("crmservice"));
            }
            if (properties.getProperty("reportservice", null) != null) {
                this.uri_report_service = URI.create(properties.getProperty("reportservice"));
            }
            if (properties.getProperty("useridentitybackend", null) != null) {
                this.uri_useridentitybackend_service = URI.create(properties.getProperty("useridentitybackend"));
            }


            String applicationid = properties.getProperty("applicationid");
            String applicationname = properties.getProperty("applicationname");
            String applicationsecret = properties.getProperty("applicationsecret");
            String uasUrl = null;
            if (uri_useradmin_service != null) {
                uasUrl = uri_useradmin_service.toString();

            }
            if (was == null) {
                was = WhydahApplicationSession.getInstance(uri_securitytoken_service.toString(), uasUrl, applicationid, applicationname, applicationsecret);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    //GENERAL
    
    public WhydahApplicationSession getWAS(){
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

    public String createPinVerifiedUser(String adminUserTokenXml, String userTicket, String phoneNo, String pin, String userIdentityJson) {
        if (was.getActiveApplicationToken() == null) {
            was.renewWhydahApplicationSession();
        }
        String result = new CommandCreatePinVerifiedUser(uri_securitytoken_service, was.getActiveApplicationTokenId(), was.getActiveApplicationTokenXML(), adminUserTokenXml, userTicket, phoneNo, pin, userIdentityJson).execute();
        String userName = phoneNo;

        new CommandResetUserPassword(uri_useradmin_service, was.getActiveApplicationTokenId(), userName, "NewUserPasswordResetEmail.ftl").execute();
        return result;
    }

	public List<Application> getApplicationList(){
		if(was.getApplicationList()==null){
				was.updateApplinks();
		}
		return was.getApplicationList();
	}
	
	//UPDATE OR CREATE ROLE ENTRY
	public boolean updateOrCreateUserApplicationRoleEntry(String applicationId, String applicationName, String organization, String roleName, String roleValue, String userTokenXml) {

		boolean result = false;
		try {
			//    	a) find the correct application/website the customer shall return to (redirectURI/from view)
			//		b) lookup and find the userRole the user have for this application with roleName="INNData" (UAS)
			//		c) update the roleValue for this particular role (UAS)
			//		d) call the "non-existing" updateUserToken method in STS

			//implement
			//step a -> find correct app
			UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);
			List<Application> apps = getApplicationList();
            if (apps == null || apps.size() == 0) {
                log.warn("No APPS initialized - trying to refrest application list");
                was.updateApplinks(true);
				apps = getApplicationList();
			} 
			log.debug("application_list's size: {} apps", apps.size());
			Application appFound=null;
			for(Application app:apps){
				if(app.getId().equalsIgnoreCase(applicationId) || app.getName().equalsIgnoreCase(applicationName)){
					appFound = app;
					break;
				}
			}
			
			if(appFound==null){
				log.debug("find app: app is not found, appId {} or appName {}", applicationId, applicationName);
				result = false;
			} else {
				String rolesJson = new CommandGetUserRoles(uri_useradmin_service, getMyAppTokenID(), userToken.getTokenid(), userToken.getUid()).execute();
				//step b -> find userRole
				List<UserApplicationRoleEntry> appRoleEntryList = UserRoleMapper.fromJsonAsList(rolesJson);
				UserApplicationRoleEntry selectApplicationEntry = null;
				for (UserApplicationRoleEntry appRoleEntry : appRoleEntryList) {
					if (appFound.getId().equals(appRoleEntry.getApplicationId())){
						if (appRoleEntry.getRoleName().equalsIgnoreCase(roleName)) {
							selectApplicationEntry = appRoleEntry;
							break;
						}
					}
				}
				
				//step c
				if (selectApplicationEntry == null) {
					//create new application, this command is already tested
					UserApplicationRoleEntry userRole = new UserApplicationRoleEntry(userToken.getTokenid(), appFound.getId(), appFound.getName(), organization, roleName, roleValue);
					String userAddRoleResult = new CommandAddUserRole(uri_useradmin_service, getMyAppTokenID(), userToken.getTokenid(), userToken.getUid(), userRole.toJson()).execute();
					log.debug("new: userAddRoleResult:{}", userAddRoleResult);
				} else {

					selectApplicationEntry.setRoleValue(roleValue);
					String editedUserRoleResult = new CommandUpdateUserRole(uri_useradmin_service, getMyAppTokenID(), userToken.getTokenid(), userToken.getUid(), selectApplicationEntry.getId(), selectApplicationEntry.toJson()).execute();
					log.debug("update: userUpdateRoleResult:{}", editedUserRoleResult);
				}
				
				Thread.sleep(1000);
				
				//step d
				//call the "non-existing" updateUserToken method in STS
				String updatedUserTokenXML = (new CommandRefreshUserToken(uri_securitytoken_service, getMyAppTokenID(), getMyAppTokenXml(), userToken.getTokenid()).execute());
				log.debug("Updated UserToken: {}", updatedUserTokenXML);
				if (updatedUserTokenXML != null && updatedUserTokenXML.length() > 10) {
					result = true;
				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			log.error("updateOrCreateUserApplicationRoleEntry failed: " + ex.getMessage());
			result = false;
		}
		
		return result;
	}


}