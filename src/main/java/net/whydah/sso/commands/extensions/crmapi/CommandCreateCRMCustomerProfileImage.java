package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

public class CommandCreateCRMCustomerProfileImage extends BaseHttpPostHystrixCommand<String> {
    
    
    private String userTokenId;
    private String customerRefId;
    private String contentType;
    private byte[] imageData;


    public CommandCreateCRMCustomerProfileImage(URI crmServiceUri, String myAppTokenId, String userTokenId, String customerRefId, String contentType, byte[] imageData) {
    	super(crmServiceUri, "", myAppTokenId, "CrmExtensionGroup", 6000);
        
        this.userTokenId = userTokenId;
        this.customerRefId = customerRefId;
        this.imageData = imageData;
        this.contentType = contentType;

        if (crmServiceUri == null || customerRefId == null || imageData == null || contentType == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }
    

//    @Override
//    protected String run() {
//        log.trace("CommandCreateCRMCustomerProfileImage - myAppTokenId={}", myAppTokenId);
//
//        Client crmClient;
//        if (!SSLTool.isCertificateCheckDisabled()) {
//            crmClient = ClientBuilder.newClient();
//        } else {
//            crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
//        }
//
//        WebTarget createProfileImage = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef).path("image");
//
//        Response response = createProfileImage.request().post(Entity.entity(imageData, contentType));
//
//        log.debug("CommandCreateCRMCustomerProfileImage - Returning status {}", response.getStatus());
//        if (response.getStatus() == CREATED.getStatusCode()) {
//            String locationHeader = response.getHeaderString("location");
//            log.debug("CommandCreateCRMCustomerProfileImage - Returning ProfileImage url {}", locationHeader);
//            return locationHeader;
//        }
//        String responseJson = response.readEntity(String.class);
//        log.debug("CommandCreateCRMCustomerProfileImage - Returning response '{}', status {}", responseJson, response.getStatus());
//        return null;
//
//
//    }
    
    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	if (statusCode == java.net.HttpURLConnection.HTTP_CREATED) {
    		String locationHeader = request.header("location");
    		log.debug(TAG + " - Returning ProfileImage url {}", locationHeader);
    		return locationHeader;
    	}
    	return super.dealWithFailedResponse(responseBody, statusCode);//null
    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType(contentType).send(imageData);
    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandCreateCRMCustomerProfileImage - fallback - whydahServiceUri={}", crmServiceUri.toString());
//        return null;
//    }


	@Override
	protected String getTargetPath() {
        return myAppTokenId + "/" + userTokenId + "/customer/" + customerRefId + "/image";
    }


}
