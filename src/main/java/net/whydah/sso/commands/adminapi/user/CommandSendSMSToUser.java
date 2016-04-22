package net.whydah.sso.commands.adminapi.user;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import com.github.kevinsawicki.http.HttpRequest;


public class CommandSendSMSToUser extends BaseHttpGetHystrixCommand<String> {
    

    private String smsMessage;
    private String cellNo;
    private String queryparam;


    public CommandSendSMSToUser(String serviceURL, String serviceAccount, String username, String password, String queryParam, String cellNo, String smsMessage) {
    	super(URI.create(serviceURL), "","", "CommandSendSMSToUser", 3000);
        
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

//    @Override
//    protected String run() {
//
//        log.trace("CommandSendSMSToUser {}, using service {} and query template {} message {}", cellNo, serviceUrl, queryparam, smsMessage);
//        HttpRequest request = HttpRequest.get(serviceUrl + "?" + queryparam).contentType(HttpSender.APPLICATION_JSON);
//        int statusCode = request.code();
//        String responseBody = request.body();
//        switch (statusCode) {
//            case HttpSender.STATUS_OK:
//                log.debug("CommandSendSMSToUser -  ok: result: {}", responseBody);
//                return responseBody;
//            default:
//                log.warn("Unexpected response from STS. Response is {} content is {}", responseBody, responseBody);
//        }
//        log.warn("CommandSendSMSToUser - failed");
//        throw new RuntimeException("CommandSendSMSToUser - failed");
//
//    }

    
	@Override
	protected String getTargetPath() {
		return "?" + queryparam;
	}


}