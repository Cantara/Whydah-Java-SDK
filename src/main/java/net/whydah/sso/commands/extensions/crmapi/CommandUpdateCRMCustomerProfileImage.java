package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPutHystrixCommand;

import java.net.URI;

public class CommandUpdateCRMCustomerProfileImage extends BaseHttpPutHystrixCommand<String> {

    private String userTokenId;
    private String personRef;
    private String contentType;
    private byte[] imageData;


    public CommandUpdateCRMCustomerProfileImage(URI crmServiceUri, String myAppTokenId, String userTokenId, String personRef, String contentType, byte[] imageData) {
    	super(crmServiceUri, "", myAppTokenId, "CrmExtensionGroup", 3000);
        
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.imageData = imageData;
        this.contentType = contentType;

        if (crmServiceUri == null || personRef == null || imageData == null || contentType == null) {
            log.error("CommandUpdateCRMCustomerProfileImage initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//        log.trace("CommandUpdateCRMCustomerProfileImage - myAppTokenId={}", myAppTokenId);
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
//        Response response = createProfileImage.request().put(Entity.entity(imageData, contentType));
//
//        log.debug("CommandUpdateCRMCustomerProfileImage - Returning status {}", response.getStatus());
//        if (response.getStatus() == ACCEPTED.getStatusCode()) {
//            String locationHeader = response.getHeaderString("location");
//            log.debug("CommandUpdateCRMCustomerProfileImage - Returning ProfileImage url {}", locationHeader);
//            return locationHeader;
//        }
//        String responseJson = response.readEntity(String.class);
//        log.debug("CommandUpdateCRMCustomerProfileImage - Returning response '{}', status {}", responseJson, response.getStatus());
//        return null;
//
//
//    }
    
    @Override
    protected String dealWithResponse(String response) {
    	return response;
    }
    
    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	if (statusCode == java.net.HttpURLConnection.HTTP_ACCEPTED) {
            String locationHeader = request.header("location");
            log.debug(TAG + " - Returning ProfileImage url {}", locationHeader);
            return locationHeader;
        }
    	return super.dealWithFailedResponse(responseBody, statusCode);
    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType(contentType).send(imageData);
    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandUpdateCRMCustomerProfileImage - fallback - whydahServiceUri={}", crmServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + userTokenId + "/customer/" + personRef + "/image";
	}


}
