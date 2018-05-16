package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;

public class CommandGetCRMCustomer extends BaseHttpGetHystrixCommand<String> {
    
    private String userTokenId;
    private String personRef;
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandGetCRMCustomer(URI crmServiceUri, String applicationTokenId, String userTokenId, String personRef) {
        super(crmServiceUri, "", applicationTokenId, "CrmExtensionGroup", DEFAULT_TIMEOUT);

        if (crmServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId) || personRef == null) {
            log.error("CommandGetCRMCustomer initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", crmServiceUri, applicationTokenId, userTokenId, personRef);
        }
        this.userTokenId = userTokenId;
        this.personRef = personRef;

    }
    
    public CommandGetCRMCustomer(URI crmServiceUri, String applicationTokenId, String userTokenId, String personRef, int timeout) {
        super(crmServiceUri, "", applicationTokenId, "CrmExtensionGroup", timeout);

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
