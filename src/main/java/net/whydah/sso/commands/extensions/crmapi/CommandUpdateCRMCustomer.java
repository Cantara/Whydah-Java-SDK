package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

import static javax.ws.rs.core.Response.Status.ACCEPTED;

public class CommandUpdateCRMCustomer extends BaseHttpPostHystrixCommand<String> {
    
    private String userTokenId;
    private String personRef;
    private String customerJson;


    public CommandUpdateCRMCustomer(URI crmServiceUri, String myAppTokenId, String userTokenId, String personRef, String customerJson) {
    	super(crmServiceUri, "", myAppTokenId, "CrmExtensionGroup", 3000);
        
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.customerJson = customerJson;

        if (crmServiceUri == null || myAppTokenId == null || userTokenId == null || personRef == null || customerJson == null) {
            log.error("CommandUpdateCRMCustomer initialized with null-values - will fail");
        }

    }
    
    @Override
    protected String getTargetPath() {
    	return myAppTokenId + "/" + userTokenId + "/customer/" + personRef;
    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json").send(customerJson); 
    }

    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	if (statusCode == ACCEPTED.getStatusCode()) {
    		String locationHeader = request.header("location");
    		log.debug(TAG + " - Returning CRM location {}", locationHeader);
    		return locationHeader;
    	}
    	return super.dealWithFailedResponse(responseBody, statusCode); //null
    }
    
//    @Override
//    protected String run() {
//        log.trace("CommandUpdateCRMCustomer - myAppTokenId={}", myAppTokenId);
//
//        Client crmClient;
//        if (!SSLTool.isCertificateCheckDisabled()) {
//            crmClient = ClientBuilder.newClient();
//        } else {
//            crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
//        }
//
//        WebTarget updateCustomer = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);
//
//        Response response = updateCustomer.request().put(Entity.entity(customerJson, MediaType.APPLICATION_JSON_TYPE));
//
//        log.debug("CommandUpdateCRMCustomer - Returning CRM location {}", response.getStatus());
//        if (response.getStatus() == ACCEPTED.getStatusCode()) {
//            String locationHeader = response.getHeaderString("location");
//            log.debug("CommandUpdateCRMCustomer - Returning CRM location {}", locationHeader);
//            return locationHeader;
//        }
//        String responseJson = response.readEntity(String.class);
//        log.debug("CommandUpdateCRMCustomer - Returning CRM location '{}', status {}", responseJson, response.getStatus());
//        return null;
//
//
//    }
//
//    @Override
//    protected String getFallback() {
//        log.warn("CommandUpdateCRMCustomer - fallback - whydahServiceUri={}", crmServiceUri.toString());
//        return null;
//    }


}
