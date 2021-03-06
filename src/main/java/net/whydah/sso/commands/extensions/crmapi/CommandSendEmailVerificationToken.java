package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;

import java.net.URI;

public class CommandSendEmailVerificationToken extends BaseHttpGetHystrixCommandForBooleanType {
   
    private final String personRef;
    private final String userTokenId;
    private final String linkurl;
    private String emailaddress;
    private String ticket;
    public static int DEFAULT_TIMEOUT = 6000;
    
    public CommandSendEmailVerificationToken(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String emailaddress, String linkurl, String ticket) {
    	super(crmServiceUri, appTokenXml, "", "CrmExtensionGroup", DEFAULT_TIMEOUT);
   
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.emailaddress = emailaddress;
        this.linkurl = linkurl;
        this.ticket = ticket;
        if (crmServiceUri == null || appTokenXml == null || this.emailaddress == null || this.linkurl == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }
    
    public CommandSendEmailVerificationToken(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String emailaddress, String linkurl, String ticket, int timeout) {
    	super(crmServiceUri, appTokenXml, "", "CrmExtensionGroup", timeout);
   
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.emailaddress = emailaddress;
        this.linkurl = linkurl;
        this.ticket = ticket;
        if (crmServiceUri == null || appTokenXml == null || this.emailaddress == null || this.linkurl == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }
    }

//    @Override
//    protected Boolean run() {
//        log.trace("{} - appTokenXml={}, ", CommandSendEmailVerificationToken.class.getSimpleName(), appTokenXml);
//
//        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);
//
//        Client crmClient = ClientBuilder.newClient();
//        WebTarget sts = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);
//
//        WebTarget webResource = sts.path("verify").path("email").queryParam("email", emailaddress).queryParam("linkurl", linkurl);
//
//        Response response = webResource.request().get(Response.class);
//        if (response.getStatus() == 200) {
//            return Boolean.TRUE;
//        }
//        return Boolean.FALSE;
//    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
    	return request.ok();
    }
    
    @Override
    protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
    	return false;
    }

//    @Override
//    protected Boolean getFallback() {
//        log.warn("{} - fallback - crmUri={}", CommandSendEmailVerificationToken.class.getSimpleName(), crmServiceUri);
//        return Boolean.FALSE;
//    }

    @Override
    protected Object[] getQueryParameters() {
    	return new String[]{"email", emailaddress, "linkurl", linkurl, "userticket", ticket};
    }
	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + userTokenId + "/customer/" + personRef + "/verify/email";
	}
}
