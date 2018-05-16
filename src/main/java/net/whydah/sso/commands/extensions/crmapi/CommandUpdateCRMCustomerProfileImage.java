package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPutHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.PersonRef;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;

public class CommandUpdateCRMCustomerProfileImage extends BaseHttpPutHystrixCommand<String> {

    private String userTokenId;
    private String personRef;
    private String contentType;
    private byte[] imageData;
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandUpdateCRMCustomerProfileImage(URI crmServiceUri, String applicationTokenId, String userTokenId, String personRef, String contentType, byte[] imageData) {
        super(crmServiceUri, "", applicationTokenId, "CrmExtensionGroup", DEFAULT_TIMEOUT);


        if (crmServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId) || !PersonRef.isValid(personRef) || imageData == null || contentType == null) {
            log.error("CommandUpdateCRMCustomerProfileImage initialized with null-values - will fail");
        }

        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.imageData = imageData;
        this.contentType = contentType;
    }
    
    public CommandUpdateCRMCustomerProfileImage(URI crmServiceUri, String applicationTokenId, String userTokenId, String personRef, String contentType, byte[] imageData, int timeout) {
        super(crmServiceUri, "", applicationTokenId, "CrmExtensionGroup", timeout);


        if (crmServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId) || !PersonRef.isValid(personRef) || imageData == null || contentType == null) {
            log.error("CommandUpdateCRMCustomerProfileImage initialized with null-values - will fail");
        }

        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.imageData = imageData;
        this.contentType = contentType;
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
