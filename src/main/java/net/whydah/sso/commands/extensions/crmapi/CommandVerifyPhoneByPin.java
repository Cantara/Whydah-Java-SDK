package net.whydah.sso.commands.extensions.crmapi;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

public class CommandVerifyPhoneByPin extends BaseHttpGetHystrixCommand<Boolean> {

    private final String personRef;
    private final String userTokenId;
   
    private String phoneNo;
    private String pin;

    public CommandVerifyPhoneByPin(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String phoneNo, String pin) {
    	super(crmServiceUri, appTokenXml, "","CrmExtensionGroup");

        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.phoneNo = phoneNo;
        this.pin = pin;
        if (crmServiceUri == null || appTokenXml == null || this.phoneNo == null || this.pin == null) {
            log.error("{} initialized with null-values - will fail", CommandVerifyPhoneByPin.class.getSimpleName());
        }
    }
//
//    @Override
//    protected Boolean run() {
//        log.trace("{} - appTokenXml={}, ", CommandVerifyPhoneByPin.class.getSimpleName(), appTokenXml);
//
//        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);
//
//        Client crmClient = ClientBuilder.newClient();
//        WebTarget sts = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);
//
//        WebTarget webResource = sts.path("verify").path("phone").queryParam("phoneNo", phoneNo).queryParam("pin", pin);
//
//        Response response = webResource.request().get(Response.class);
//        if (response.getStatus() == 200) {
//            return Boolean.TRUE;
//        }
//        return Boolean.FALSE;
//    }

    @Override
    protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
    	return false;
    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
    	return true;
    }
    
    @Override
    protected Object[] getQueryParameters() {
    	return new String[] {"phoneNo", phoneNo, "pin", pin};
    }
    
    @Override
    protected Boolean getFallback() {
    	super.getFallback();
    	return false;
    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + userTokenId + "/customer/" + personRef + "/verify/phone";
	}
}
