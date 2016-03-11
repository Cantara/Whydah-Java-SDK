package net.whydah.sso.commands.appauth;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.util.HttpSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandLogonApplication extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandLogonApplication.class);
    private URI tokenServiceUri;
    private ApplicationCredential appCredential;

    public CommandLogonApplication(URI tokenServiceUri, ApplicationCredential appCredential) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("STSApplicationAdminGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.tokenServiceUri = tokenServiceUri;
        this.appCredential = appCredential;
        if (tokenServiceUri == null || appCredential == null) {
            log.error("CommandLogonApplication initialized with null-values - will fail");
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri [" + tokenServiceUri + "], \n" +
                    "\tappCredential [" + appCredential + "]");
        }
        HystrixRequestContext.initializeContext();

    }

    @Override
    protected String run() {
        String logonServiceUrl = tokenServiceUri.toString() + "logon";
        log.trace("CommandLogonApplication - uri={} appCredential={}", logonServiceUrl, ApplicationCredentialMapper.toXML(appCredential));

        Map<String, String> data = new HashMap<String, String>();
        data.put("applicationcredential", ApplicationCredentialMapper.toXML(appCredential));

        HttpRequest request = HttpRequest.post(logonServiceUrl).contentType(HttpSender.APPLICATION_FORM_URLENCODED).form(data);
        int statusCode = request.code();
        String responseBody = request.body();
        switch (statusCode) {
            case HttpSender.STATUS_OK:
                log.debug("CommandLogonApplication - Applogon ok: apptokenxml: {}", responseBody);
                String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(responseBody);
                log.trace("CommandLogonApplication - myAppTokenId: {}", myApplicationTokenID);
                return responseBody;
            default:
                log.warn("Unexpected response from STS. Response is {} content is {}", responseBody, responseBody);

        }
        throw new RuntimeException("CommandLogonApplication - Application authentication failed");

    }


    @Override
    protected String getFallback() {
        log.warn("CommandLogonApplication - fallback - uri={}", tokenServiceUri.toString());
        return null;
    }

}
