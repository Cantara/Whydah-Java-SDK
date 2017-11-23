package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommandForBooleanType;
import net.whydah.sso.ddd.model.user.PersonRef;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;

public class CommandVerifyEmailByToken extends BaseHttpGetHystrixCommandForBooleanType {
   
    private final String personRef;
    private final String userTokenId;
    private String emailaddress;
    private String emailverificationtoken;

    public CommandVerifyEmailByToken(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String emailaddress, String emailverificationtoken) {
        super(crmServiceUri, appTokenXml, "", "CrmExtensionGroup");

        if (crmServiceUri == null || appTokenXml == null || !UserTokenId.isValid(userTokenId) || !PersonRef.isValid(personRef) || emailaddress == null || emailverificationtoken == null) {
            log.error("{} initialized with null-values - will fail", TAG);
        }
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.emailaddress = emailaddress;
        this.emailverificationtoken = emailverificationtoken;
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
//        WebTarget webResource = sts.path("verify").path("email").queryParam("emailverificationtoken", emailverificationtoken).queryParam("email", emailaddress);
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
        return new String[]{"emailverificationtoken", emailverificationtoken, "email", emailaddress};
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
