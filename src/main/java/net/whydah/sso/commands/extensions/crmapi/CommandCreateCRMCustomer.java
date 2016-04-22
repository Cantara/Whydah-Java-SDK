package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

public class CommandCreateCRMCustomer extends BaseHttpPostHystrixCommand<String> {


	private String userTokenId;
	private String personRef;
	private String customerJson;


	public CommandCreateCRMCustomer(URI crmServiceUri, String myAppTokenId, String userTokenId, String personRef, String customerJson) {
		super(crmServiceUri, "", myAppTokenId, "CrmExtensionGroup", 3000);

		this.userTokenId = userTokenId;
		this.personRef = personRef;
		this.customerJson = customerJson;

		if (crmServiceUri == null || myAppTokenId == null || userTokenId == null || personRef == null) {
			log.error(TAG + " initialized with null-values - will fail");
		}

	}

//	@Override
//	protected String run() {
//		log.trace("CommandCreateCRMCustomer - myAppTokenId={}", myAppTokenId);
//
//		Client crmClient;
//		if (!SSLTool.isCertificateCheckDisabled()) {
//			crmClient = ClientBuilder.newClient();
//		} else {
//			crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
//		}
//
//		WebTarget createCustomer = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer");
//
//		if (personRef != null) {
//			createCustomer = createCustomer.path(personRef);
//		}
//
//		Response response = createCustomer.request().post(Entity.entity(customerJson, MediaType.APPLICATION_JSON_TYPE));
//		
//		log.debug("CommandCreateCRMCustomer - Returning status {}", response.getStatus());
//		if (response.getStatus() == CREATED.getStatusCode()) {
//			String locationHeader = response.getHeaderString("location");
//			String crmCustomerId = locationHeader.substring(locationHeader.lastIndexOf("/")+1);
//			log.debug("CommandCreateCRMCustomer - Returning CRM Id {}", crmCustomerId);
//			return crmCustomerId;
//		}
//		String responseJson = response.readEntity(String.class);
//		log.debug("CommandCreateCRMCustomer - Returning response '{}', status {}", responseJson, response.getStatus());
//		return null;
//
//
//	}
	
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

	//    @Override
	//    protected String getFallback() {
	//        log.warn("CommandCreateCRMCustomer - fallback - whydahServiceUri={}", crmServiceUri.toString());
	//        return null;
	//    }

	@Override
	protected String getTargetPath() {

		String path = myAppTokenId + "/" + userTokenId + "/customer";
		if (personRef != null) {
			path += personRef;
		}
		return path;
	}


}
