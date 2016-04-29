package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.util.ExceptionUtil;

//TODO:make test
public class CommandLogonUserByPhoneNumberPin extends BaseHttpPostHystrixCommand<String> {

	private String phoneNo;
	private String pin;
	private String userticket;

	public CommandLogonUserByPhoneNumberPin(URI serviceUri, String myAppTokenId, String myAppTokenXml, String phoneNo, String pin, String userTicket) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup");
		this.phoneNo = phoneNo;
		this.pin = pin;
		this.userticket = userTicket;
	}

	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/" + userticket + "/get_usertoken_by_pin_and_logon_user";
	}


	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("pin", pin);
		data.put("phoneno", phoneNo);
		
		return data;
	}

	int retryCnt=0;
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