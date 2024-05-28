package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;
import net.whydah.sso.util.ExceptionUtil;


public class CommandLogonUserByPhoneNumberPinForTrustedClient extends BaseHttpPostHystrixCommand<String> {

	int retryCnt = 0;
	private String adminUserTokenId;
	private String phoneNo;
	private String pin;
	private String userticket;
	private String clientid;
	public static int DEFAULT_TIMEOUT = 6000;
	
	public CommandLogonUserByPhoneNumberPinForTrustedClient(URI serviceUri, String applicationTokenId, String myAppTokenXml, String adminUserTokenId, String phoneNo, String pin, String userTicket, String clientid) {
		super(serviceUri, myAppTokenXml, applicationTokenId, "SSOAUserAuthGroup", DEFAULT_TIMEOUT);
		
		if (serviceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(adminUserTokenId) || phoneNo == null || pin == null) {
			log.error("CommandLogonUserByPhoneNumberPin initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, ", adminUserTokenId, myAppTokenId, myAppTokenXml);
		}
		this.phoneNo = phoneNo;
		this.pin = pin;
		this.userticket = userTicket;
		this.adminUserTokenId = adminUserTokenId;
		this.clientid = clientid;
 
        if (phoneNo.length() > 16 || phoneNo.length() < 7) {
            log.warn("Attempting to access with illegal phone number: {}", phoneNo);
        }
        if (pin.length() > 7 || pin.length() < 3) {
            log.warn("Attempting to access with illegal pin code: {}", pin);
        }
        if (clientid!=null) {
            log.warn("Attempting to access with illegal clientid: {}", clientid);
        }


	}
	
	public CommandLogonUserByPhoneNumberPinForTrustedClient(URI serviceUri, String applicationTokenId, String myAppTokenXml, String adminUserTokenId, String phoneNo, String pin, String userTicket, String clientid, int timeout) {
		super(serviceUri, myAppTokenXml, applicationTokenId, "SSOAUserAuthGroup", timeout);
		

		if (serviceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(adminUserTokenId) || phoneNo == null || pin == null) {
			log.error("CommandLogonUserByPhoneNumberPin initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, ", adminUserTokenId, myAppTokenId, myAppTokenXml);
		}
		
		this.phoneNo = phoneNo;
		this.pin = pin;
		this.userticket = userTicket;
		this.adminUserTokenId = adminUserTokenId;
		this.clientid = clientid;

		if (phoneNo.length() > 16 || phoneNo.length() < 7) {
            log.warn("Attempting to access with illegal phone number: {}", phoneNo);
        }
        if (pin.length() > 7 || pin.length() < 3) {
            log.warn("Attempting to access with illegal pin code: {}", pin);
        }
        if (clientid!=null) {
            log.warn("Attempting to access with illegal clientid: {}", clientid);
        }


	}

	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/" + userticket + "/get_usertoken_by_pin_and_logon_user_for_trusted_client";
	}

	@Override
	protected Map<String, String> getFormParameters() {
	
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("adminUserTokenId", adminUserTokenId);
		data.put("pin", pin);
		data.put("phoneno", phoneNo);
		data.put("clientid", clientid);
		return data;
	}

	@Override
	protected String dealWithFailedResponse(String responseBody, int statusCode) {
		if((statusCode != java.net.HttpURLConnection.HTTP_FORBIDDEN
				)	
				&&retryCnt<1){
			//do retry
			retryCnt++;
			return doPostCommand();
		} else {
			String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User session failed", request, statusCode);
    		log.warn(authenticationFailedMessage);
			return null;
		}
	}


}