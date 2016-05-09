package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

public class CommandCreateCRMCustomer extends BaseHttpPostHystrixCommand<String> {


	private String userTokenId;
	private String customerRefId;
	private String customerJson;


	public CommandCreateCRMCustomer(URI crmServiceUri, String myAppTokenId, String userTokenId, String customerRefId, String customerJson) {
		super(crmServiceUri, "", myAppTokenId, "CrmExtensionGroup", 6000);

		this.userTokenId = userTokenId;
		this.customerRefId = customerRefId;
		this.customerJson = customerJson;

		if (crmServiceUri == null || myAppTokenId == null || userTokenId == null) {
			log.error(TAG + " initialized with null-values - will fail");
			log.error("CommandCreateCRMCustomer initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", crmServiceUri, myAppTokenId, userTokenId, customerRefId);

		}

	}


	@Override
	protected String dealWithFailedResponse(String responseBody, int statusCode) {
		if(statusCode == 201){//CREATED.getStatusCode()
			String locationHeader = request.header("location");
			String crmCustomerId = locationHeader.substring(locationHeader.lastIndexOf("/")+1);
			log.debug(TAG + " - Returning CRM Id {}", crmCustomerId);
			return crmCustomerId;
		}
		return super.dealWithFailedResponse(responseBody, statusCode); //null here
	}
	
	@Override
	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		return request.contentType("application/json").send(customerJson);
	}


	@Override
	protected String getTargetPath() {

		String path = myAppTokenId + "/" + userTokenId + "/customer";
		if (customerRefId != null) {
			path += "/" + customerRefId;
		}
		return path;
	}


}
