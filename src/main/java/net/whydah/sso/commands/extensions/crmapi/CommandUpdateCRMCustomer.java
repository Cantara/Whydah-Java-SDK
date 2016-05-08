package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPutHystrixCommand;

import java.net.URI;

public class CommandUpdateCRMCustomer extends BaseHttpPutHystrixCommand<String> {
    
    private String userTokenId;
    private String personRef;
    private String customerJson;


    public CommandUpdateCRMCustomer(URI crmServiceUri, String myAppTokenId, String userTokenId, String personRef, String customerJson) {
    	super(crmServiceUri, "", myAppTokenId, "CrmExtensionGroup", 6000);
        
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.customerJson = customerJson;

        if (crmServiceUri == null || myAppTokenId == null || userTokenId == null || personRef == null || customerJson == null) {
            log.error("CommandUpdateCRMCustomer initialized with null-values - will fail - crmServiceUri:{} myAppTokenId:{} userTokenId:{} personRef:{}", crmServiceUri, myAppTokenId, userTokenId, personRef);
        }

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
