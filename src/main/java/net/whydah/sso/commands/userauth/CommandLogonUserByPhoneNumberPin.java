package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.util.ExceptionUtil;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

//TODO:make test
public class CommandLogonUserByPhoneNumberPin extends BaseHttpPostHystrixCommand<String> {

	int retryCnt = 0;
	private String adminUserTokenId;
	private String phoneNo;
	private String pin;
	private String userticket;

	public CommandLogonUserByPhoneNumberPin(URI serviceUri, String myAppTokenId, String myAppTokenXml, String adminUserTokenId, String phoneNo, String pin, String userTicket) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup");
		this.phoneNo = phoneNo;
		this.pin = pin;
		this.userticket = userTicket;
		this.adminUserTokenId = adminUserTokenId;
		if (serviceUri == null || myAppTokenId == null || adminUserTokenId == null || phoneNo == null || pin == null) {
			log.error("CommandLogonUserByPhoneNumberPin initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, ", adminUserTokenId, myAppTokenId, myAppTokenXml);
		}

	}

	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/" + userticket + "/get_usertoken_by_pin_and_logon_user";
	}

	@Override
	protected Map<String, String> getFormParameters() {
		//                                                              @FormParam("adminUserTokenId") String adminUserTokenId,

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