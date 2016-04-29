package net.whydah.sso.commands.adminapi.application;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.github.kevinsawicki.http.HttpRequest;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

//TODO:make test
public class CommandCreatePinVerifiedUser extends BaseHttpPostHystrixCommand<String>{

	public CommandCreatePinVerifiedUser(URI serviceUri, String myAppTokenXml, String myAppTokenId, String adminUserToken, String userTicket, String phoneNo, String pin, String json) {
		super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup");
		this.userTicket = userTicket;
		this.pin = pin;
		this.adminUserToken = adminUserToken;
		this.cellPhone = phoneNo;
		this.json = json;
	}

	String adminUserToken;
	String userTicket;
	String pin;
	String cellPhone;
	String json;

	@Override
	protected String getTargetPath() {

		return "user/" + myAppTokenId + "/" + userTicket +  "/" + pin + "/create_pinverified_user";
	}

	@Override
	protected String dealWithFailedResponse(String responseBody, int statusCode) {
		return null;
	}


	@Override
	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		Map<String, String> formData = new HashMap<String, String>();
		formData.put("apptoken", myAppTokenXml);
		formData.put("adminUserTokenId", adminUserToken);
		formData.put("pin", pin);
		formData.put("cellPhone", cellPhone);
		formData.put("jsonuser", json);
		return super.dealWithRequestBeforeSend(request);
	}

}
