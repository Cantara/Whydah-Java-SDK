package net.whydah.sso.commands.adminapi.user;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import com.github.kevinsawicki.http.HttpRequest;


public class CommandSendSMSToUser extends BaseHttpGetHystrixCommand<String> {
    
	public static int DEFAULT_TIMEOUT = 6000;
	
    private String smsMessage;
    private String cellNo;
    private String queryparam;


    public CommandSendSMSToUser(String serviceURL, String serviceAccount, String username, String password, String queryParam, String cellNo, String smsMessage) {
    	this(serviceURL, serviceAccount, username, password, queryParam, cellNo, smsMessage, DEFAULT_TIMEOUT);
    }
    
    public CommandSendSMSToUser(String serviceURL, String serviceAccount, String username, String password, String queryParam, String cellNo, String smsMessage, int timeout) {
    	super(URI.create(serviceURL), "","", "CommandSendSMSToUser", timeout);
        
        this.smsMessage = smsMessage;
        this.cellNo = cellNo;
        String replacedStr = queryParam.replaceAll("smsrecepient", cellNo);
        replacedStr = replacedStr.replaceAll("serviceAccount", serviceAccount);
        replacedStr = replacedStr.replaceAll("smsserviceusername", username);
        replacedStr = replacedStr.replaceAll("smsservicepassword", password);
        this.queryparam = replacedStr.replaceAll("smscontent", smsMessage.replaceAll(" ", "%20"));
        if (this.smsMessage == null || this.cellNo == null || serviceURL == null || queryparam == null) {
            log.error(TAG + " initialized with null-values - will fail - smsMessage:{}, cellNo:{}, serviceUrl:{}, queryparam:{}", smsMessage, cellNo, serviceURL, queryparam);
        }

    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json");
    }


    
	@Override
	protected String getTargetPath() {
		return "?" + queryparam;
	}


}