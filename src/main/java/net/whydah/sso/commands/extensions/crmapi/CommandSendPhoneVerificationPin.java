package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;

import java.net.URI;

public class CommandSendPhoneVerificationPin extends BaseHttpGetHystrixCommandForBooleanType {

    private final String personRef;
    private final String userTokenId;
    private String phoneNo;
    public static int DEFAULT_TIMEOUT = 6000;
    
    public CommandSendPhoneVerificationPin(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String phoneNo) {
        super(crmServiceUri, appTokenXml, "", "CrmExtensionGroup", DEFAULT_TIMEOUT);
        
      
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.phoneNo = phoneNo;
        if (crmServiceUri == null || appTokenXml == null || this.phoneNo == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }
    
    public CommandSendPhoneVerificationPin(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String phoneNo, int timeout) {
        super(crmServiceUri, appTokenXml, "", "CrmExtensionGroup", timeout);
        
      
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.phoneNo = phoneNo;
        if (crmServiceUri == null || appTokenXml == null || this.phoneNo == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }


//    @Override
//    protected Boolean run() {
//        log.trace("{} - appTokenXml={}, ", CommandSendPhoneVerificationPin.class.getSimpleName(), appTokenXml);
//
//        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);
//
//        Client crmClient = ClientBuilder.newClient();
//        WebTarget sts = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);
//
//        WebTarget webResource = sts.path("verify").path("phone").queryParam("phoneNo", phoneNo);
//
//        Response response = webResource.request().get(Response.class);
//        if (response.getStatus() == 200) {
//            return Boolean.TRUE;
//        }
//        return Boolean.FALSE;
//    }

//    @Override
//    protected Boolean getFallback() {
//        log.warn("{} - fallback - crmUri={}", CommandSendPhoneVerificationPin.class.getSimpleName(), crmServiceUri);
//        return Boolean.FALSE;
//    }
    
    @Override
    protected Object[] getQueryParameters() {
    	return new String[]{"phoneNo", phoneNo};
    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
    	return true;
    }
    
    @Override
    protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
    	return false;
    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + userTokenId + "/customer/" + personRef + "/verify/phone";
	}
}
