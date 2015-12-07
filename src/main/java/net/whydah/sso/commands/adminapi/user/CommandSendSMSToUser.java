package net.whydah.sso.commands.adminapi.user;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import net.whydah.sso.application.helpers.ApplicationHelper;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.FORBIDDEN;
import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandSendSMSToUser extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandSendSMSToUser.class);
    private String smsMessage;
    private String cellNo;
    private URI smsGWUrl;


    public CommandSendSMSToUser(URI smsGWUrl, String cellNo, String smsMessage) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CommandSendSMSToUser")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));
        this.smsMessage = smsMessage;
        this.cellNo = cellNo;
        this.smsGWUrl = smsGWUrl;

    }


    @Override
    protected String run() {

        log.trace("CommandSendSMSToUser");

        try {
            Thread.sleep(6000);  // lets us just fo a timeout
        } catch (Exception e) {
            // No Action
        }
        Client smsClient = ClientBuilder.newClient();

        WebTarget userDirectory = smsClient.target(smsGWUrl).path("sendsms").path(cellNo).path(smsMessage);

        // Works against UIB, still misisng in UAS...
        Response response = userDirectory.request().get();
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.info("CommandListUsers -  userDirectory failed with status code " + response.getStatus());
            return null;
            //throw new IllegalArgumentException("Log on failed. " + ClientResponse.Status.FORBIDDEN);
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);
            log.debug("CommandListUsers - Listing users {}", responseJson);
            return responseJson;
        }

        return null;

    }

    @Override
    protected String getFallback() {

        log.warn("CommandSendSMSToUser - getFallback - not configured ");
        return ApplicationHelper.getDummyAppllicationListJson();
    }


}