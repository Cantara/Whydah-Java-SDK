package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPutHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.PersonRef;
import net.whydah.sso.ddd.model.user.UserTokenId;

import java.net.URI;

public class CommandUpdateCRMCustomer extends BaseHttpPutHystrixCommand<String> {
    
    private String userTokenId;
    private String personRef;
    private String customerJson;
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandUpdateCRMCustomer(URI crmServiceUri, String applicationTokenId, String userTokenId, String personRef, String customerJson) {
        super(crmServiceUri, "", applicationTokenId, "CrmExtensionGroup", DEFAULT_TIMEOUT);


        if (crmServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId) || !PersonRef.isValid(personRef) || customerJson == null) {
            log.error("CommandUpdateCRMCustomer initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", crmServiceUri, applicationTokenId, userTokenId, personRef);
        }
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.customerJson = customerJson;

    }
    
    public CommandUpdateCRMCustomer(URI crmServiceUri, String applicationTokenId, String userTokenId, String personRef, String customerJson, int timeout) {
        super(crmServiceUri, "", applicationTokenId, "CrmExtensionGroup", timeout);


        if (crmServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !UserTokenId.isValid(userTokenId) || !PersonRef.isValid(personRef) || customerJson == null) {
            log.error("CommandUpdateCRMCustomer initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", crmServiceUri, applicationTokenId, userTokenId, personRef);
        }
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.customerJson = customerJson;

    }
    
    @Override
    protected String getTargetPath() {
    	return myAppTokenId + "/" + userTokenId + "/customer/" + personRef;
    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json").send(customerJson); 
    }

    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	if (statusCode == java.net.HttpURLConnection.HTTP_ACCEPTED) {
    		String locationHeader = request.header("location");
    		log.debug(TAG + " - Returning CRM location {}", locationHeader);
    		return locationHeader;
    	}
        return super.dealWithFailedResponse(responseBody, statusCode);
    }
    

}
