package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;

public class CommandGetCRMCustomer extends BaseHttpGetHystrixCommand<String> {
    
    private String userTokenId;
    private String personRef;


    public CommandGetCRMCustomer(URI crmServiceUri, String applicationTokenId, String userTokenId, String personRef) {
        super(crmServiceUri, "", applicationTokenId, "CrmExtensionGroup", 6000);

        if (crmServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId) || personRef == null) {
            log.error("CommandGetCRMCustomer initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", crmServiceUri, applicationTokenId, userTokenId, personRef);
        }
        this.userTokenId = userTokenId;
        this.personRef = personRef;

    }


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + userTokenId + "/customer/" + personRef;
	}


}
