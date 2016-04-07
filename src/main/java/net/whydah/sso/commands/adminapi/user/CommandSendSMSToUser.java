package net.whydah.sso.commands.adminapi.user;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import net.whydah.sso.util.HttpSender;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;


public class CommandSendSMSToUser extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandSendSMSToUser.class);
    private String serviceUrl;
    private String smsMessage;
    private String cellNo;
    private String queryparam;


    public CommandSendSMSToUser(String serviceURL, String serviceAccount, String username, String password, String queryParam, String cellNo, String smsMessage) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CommandSendSMSToUser")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));
        this.smsMessage = smsMessage;
        this.cellNo = cellNo;
        this.serviceUrl = serviceURL;
        String replacedStr = queryParam.replaceAll("smsrecepient", cellNo);
        replacedStr = replacedStr.replaceAll("serviceAccount", serviceAccount);
        replacedStr = replacedStr.replaceAll("smsserviceusername", username);
        replacedStr = replacedStr.replaceAll("smsservicepassword", password);
        this.queryparam = replacedStr.replaceAll("smscontent", smsMessage.replaceAll(" ", "%20"));
        if (this.smsMessage == null || this.cellNo == null || serviceUrl == null || queryparam == null) {
            log.error("CommandSendSMSToUser initialized with null-values - will fail - smsMessage:{}, cellNo:{}, serviceUrl:{}, queryparam:{}", smsMessage, cellNo, serviceUrl, queryparam);
        }

    }

    @Override
    protected String run() {

        log.trace("CommandSendSMSToUser {}, using service {} and query template {} message {}", cellNo, serviceUrl, queryparam, smsMessage);
        HttpRequest request = HttpRequest.get(serviceUrl + "?" + queryparam).contentType(HttpSender.APPLICATION_JSON);
        int statusCode = request.code();
        String responseBody = request.body();
        switch (statusCode) {
            case HttpSender.STATUS_OK:
                log.debug("CommandSendSMSToUser -  ok: result: {}", responseBody);
                return responseBody;
            default:
                log.warn("Unexpected response from STS. Response is {} content is {}", responseBody, responseBody);
        }
        log.warn("CommandSendSMSToUser - failed");
        throw new RuntimeException("CommandSendSMSToUser - failed");

    }


}