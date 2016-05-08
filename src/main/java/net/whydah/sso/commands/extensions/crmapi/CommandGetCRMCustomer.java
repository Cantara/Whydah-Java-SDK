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
            log.error("CommandGetCRMCustomer initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", crmServiceUri, myAppTokenId, userTokenId, personRef);
        }

    }


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + userTokenId + "/customer/" + personRef;
	}


}
