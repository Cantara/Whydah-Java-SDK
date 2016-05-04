package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandGetCRMCustomer extends BaseHttpGetHystrixCommand<String> {
    
    private String userTokenId;
    private String personRef;


    public CommandGetCRMCustomer(URI crmServiceUri, String myAppTokenId, String userTokenId, String personRef) {
    	super(crmServiceUri, "", myAppTokenId, "CrmExtensionGroup",6000);
        
    	this.userTokenId = userTokenId;
        this.personRef = personRef;
        if (crmServiceUri == null || myAppTokenId == null || userTokenId == null || personRef == null) {
            log.error("CommandGetCRMCustomer initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//        log.trace("CommandGetCRMCustomer - myAppTokenId={}", myAppTokenId);
//
////        Client crmClient = ClientBuilder.newClient();
//        Client crmClient;
//        if (!SSLTool.isCertificateCheckDisabled()) {
//            crmClient = ClientBuilder.newClient();
//        } else {
//            crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
//        }
//
//        WebTarget getCustomer = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);
//
//        Response response = getCustomer.request().get();
//        log.debug("CommandGetCRMCustomer - Returning CRM customer {}", response.getStatus());
//        if (response.getStatus() == OK.getStatusCode()) {
//            String responseJson = response.readEntity(String.class);
//            log.debug("CommandGetCRMCustomer - Returning CRM customer {}", responseJson);
//            return responseJson;
//        }
//        String responseJson = response.readEntity(String.class);
//        log.debug("CommandGetCRMCustomer - Returning CRM customer {}", responseJson);
//        return null;
//
//
//    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandGetCRMCustomer - fallback - whydahServiceUri={}", crmServiceUri.toString());
//        return null;
//    }

	@Override
	protected String getTargetPath() {
		// TODO Auto-generated method stub
		return myAppTokenId + "/" + userTokenId + "/customer/" + personRef;
	}


}
