package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;

public class CommandGetCRMCustomerProfileImage extends BaseHttpGetHystrixCommand<byte[]> {

    private String userTokenId;
    private String customerRefId;


    public CommandGetCRMCustomerProfileImage(URI crmServiceUri, String applicationTokenId, String userTokenId, String personRef) {
        super(crmServiceUri, "", applicationTokenId, "CrmExtensionGroup", 6000);

        if (crmServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId) || personRef == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
        this.userTokenId = userTokenId;
        this.customerRefId = personRef;

    }

//    @Override
//    protected byte[] run() {
//        log.trace("CommandGetCRMCustomerProfileImage - myAppTokenId={}", myAppTokenId);
//
//        Client crmClient;
//        if (!SSLTool.isCertificateCheckDisabled()) {
//            crmClient = ClientBuilder.newClient();
//        } else {
//            crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
//        }
//
//        WebTarget getProfileImage = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef).path("image");
//
//
//        try {
//            log.trace("Fetching image from path {}", getProfileImage.getUri().toURL());
//            Response response = getProfileImage.request().get();
//
//            //String contentType = response.getHeaderString("Content-Type");
//            byte[] imagedata = response.readEntity(byte[].class);
//
//            log.trace("CommandGetCRMCustomerProfileImage - returned image with length {}", imagedata == null ? 0:imagedata.length);
//            return imagedata;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
    
    @Override
    protected byte[] dealWithResponse(String response) {
    	return getResponseBodyAsByteArray();
    }
    


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + userTokenId + "/customer/" + customerRefId + "/image";
	}


}
