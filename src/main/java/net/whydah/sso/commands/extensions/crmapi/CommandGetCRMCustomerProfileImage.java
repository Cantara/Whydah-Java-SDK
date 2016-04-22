package net.whydah.sso.commands.extensions.crmapi;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

public class CommandGetCRMCustomerProfileImage extends BaseHttpGetHystrixCommand<byte[]> {

    private String userTokenId;
    private String personRef;


    public CommandGetCRMCustomerProfileImage(URI crmServiceUri, String myAppTokenId, String userTokenId, String personRef) {
    	super(crmServiceUri, "", myAppTokenId, "CrmExtensionGroup", 3000);
        
        this.userTokenId = userTokenId;
        this.personRef = personRef;

        if (crmServiceUri == null || personRef == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

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
    	return request.bytes();
    }
    
   

//    @Override
//    protected byte[] getFallback() {
//        log.warn("CommandGetCRMCustomerProfileImage - fallback - uri={}", crmServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + userTokenId + "/customer/" + personRef + "/image";
	}


}
