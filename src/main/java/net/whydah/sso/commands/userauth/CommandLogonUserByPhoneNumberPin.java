package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.util.ExceptionUtil;


public class CommandLogonUserByPhoneNumberPin extends BaseHttpPostHystrixCommand<String> {

	int retryCnt = 0;
	private String adminUserTokenId;
	private String phoneNo;
	private String pin;
	private String userticket;

	public CommandLogonUserByPhoneNumberPin(URI serviceUri, String myAppTokenId, String myAppTokenXml, String adminUserTokenId, String phoneNo, String pin, String userTicket) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 8000);
		this.phoneNo = phoneNo;
		this.pin = pin;
		this.userticket = userTicket;
		this.adminUserTokenId = adminUserTokenId;
		if (serviceUri == null || myAppTokenId == null || adminUserTokenId == null || phoneNo == null || pin == null) {
			log.error("CommandLogonUserByPhoneNumberPin initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, ", adminUserTokenId, myAppTokenId, myAppTokenXml);
		}
        if (phoneNo.length() > 16 || phoneNo.length() < 7) {
            log.warn("Attempting to access with illegal phone number: {}", phoneNo);
        }
        if (pin.length() > 7 || phoneNo.length() < 3) {
            log.warn("Attempting to access with illegal pin code: {}", phoneNo);
        }


	}

	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/" + userticket + "/get_usertoken_by_pin_and_logon_user";
	}

	@Override
	protected Map<String, String> getFormParameters() {
	
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("adminUserTokenId", adminUserTokenId);
		data.put("pin", pin);
		data.put("phoneno", phoneNo);

		return data;
	}

	@Override
	protected String dealWithFailedResponse(String responseBody, int statusCode) {
		if(statusCode != java.net.HttpURLConnection.HTTP_FORBIDDEN &&retryCnt<1){
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