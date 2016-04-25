package net.whydah.sso.commands.extensions.crmapi;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

public class CommandVerifyEmailByToken extends BaseHttpGetHystrixCommand<Boolean> {
   
    private final String personRef;
    private final String userTokenId;
    private URI crmServiceUri;
    private String appTokenXml;
    private String emailaddress;
    private String token;

    public CommandVerifyEmailByToken(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String emailaddress, String token) {
    	super(crmServiceUri, appTokenXml, "", "CrmExtensionGroup");
        
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.emailaddress = emailaddress;
        this.token = token;
        if (this.crmServiceUri == null || this.appTokenXml == null || this.emailaddress == null || this.token == null) {
            log.error("{} initialized with null-values - will fail", TAG);
        }
    }

//    @Override
//    protected Boolean run() {
//        log.trace("{} - appTokenXml={}, ", CommandVerifyEmailByToken.class.getSimpleName(), appTokenXml);
//
//        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);
//
//        Client crmClient = ClientBuilder.newClient();
//        WebTarget sts = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);
//
//        WebTarget webResource = sts.path("verify").path("email").queryParam("token", token).queryParam("email", emailaddress);
//
//        Response response = webResource.request().get(Response.class);
//        if (response.getStatus() == 200) {
//            return Boolean.TRUE;
//        }
//        return Boolean.FALSE;
//    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
    	return true;
    }
    
    @Override
    protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
    	return false;
    }

    @Override
    protected Object[] getQueryParameters() {
    	return new String[]{"token", token,"email", emailaddress};
    }
//    @Override
//    protected Boolean getFallback() {
//        log.warn("{} - fallback - crmUri={}", CommandVerifyEmailByToken.class.getSimpleName(), crmServiceUri);
//        return Boolean.FALSE;
//    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + userTokenId + "/customer/"+personRef + "/verify/email";
	}
}
