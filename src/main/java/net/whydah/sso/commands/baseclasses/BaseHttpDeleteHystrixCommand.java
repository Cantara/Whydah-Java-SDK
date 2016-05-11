package net.whydah.sso.commands.baseclasses;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.util.StringConv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseHttpDeleteHystrixCommand<R> extends HystrixCommand<R> {

    protected Logger log;
    protected URI whydahServiceUri;
    protected String myAppTokenId = "";
    protected String myAppTokenXml = "";
    protected String TAG = "";
    protected HttpRequest request;
    private byte[] responseBody;

    protected BaseHttpDeleteHystrixCommand(URI serviceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey, int hystrixExecutionTimeOut) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey)).
                andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                        .withExecutionTimeoutInMilliseconds(hystrixExecutionTimeOut)));
        init(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
    }


    protected BaseHttpDeleteHystrixCommand(URI serviceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey)));
        init(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
    }

    private void init(URI serviceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
        this.whydahServiceUri = serviceUri;
        this.myAppTokenXml = myAppTokenXml;
        if (this.myAppTokenXml != null && !this.myAppTokenXml.equals("") && (myAppTokenId == null || myAppTokenId.isEmpty())) {
            this.myAppTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
        } else {
            this.myAppTokenId = myAppTokenId;
        }
        this.TAG = this.getClass().getName() + ", pool :" + hystrixGroupKey;
        this.log = LoggerFactory.getLogger(TAG);
        HystrixRequestContext.initializeContext();
    }

    @Override
    protected R run() {
        return doDeleteCommand();

    }

    protected R doDeleteCommand() {
        try {
            String uriString = whydahServiceUri.toString();
            if (getTargetPath() != null) {
                uriString += getTargetPath();
            }

            log.debug("TAG" + " - whydahServiceUri={} myAppTokenId={}", uriString, myAppTokenId);

            if (getQueryParameters() != null && getQueryParameters().length != 0) {
                request = HttpRequest.delete(uriString, true, getQueryParameters());
            } else {
                request = HttpRequest.delete(uriString);
            }
            request.trustAllCerts();
            request.trustAllHosts();

            if (getFormParameters() != null && !getFormParameters().isEmpty()) {
                request.contentType(HttpSender.APPLICATION_FORM_URLENCODED);
                request.form(getFormParameters());
            }

            request = dealWithRequestBeforeSend(request);

            responseBody = request.bytes();
            int statusCode = request.code();
            String responseAsText = StringConv.UTF8(responseBody);

            switch (statusCode) {
                case java.net.HttpURLConnection.HTTP_OK:
                    onCompleted(responseAsText);
                    return dealWithResponse(responseAsText);
                default:
                    onFailed(responseAsText, statusCode);
                    return dealWithFailedResponse(responseAsText, statusCode);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("TAG" + " - Application authentication failed to execute");
        }
    }

    protected R dealWithFailedResponse(String responseBody, int statusCode) {
        return null;
    }

    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {

        return request;
    }

    protected void onFailed(String responseBody, int statusCode) {
        log.debug(TAG + " - Unexpected response from {}. Status code is {} content is {} ", whydahServiceUri, String.valueOf(statusCode) + responseBody);
    }

    protected void onCompleted(String responseBody) {
        log.debug(TAG + " - ok: " + responseBody);
    }

    protected abstract String getTargetPath();

    protected Map<String, String> getFormParameters() {
        return new HashMap<String, String>();
    }

    protected Object[] getQueryParameters() {
        return new String[]{};
    }

    @SuppressWarnings("unchecked")
    protected R dealWithResponse(String response) {
        return (R) response;
    }

    @Override
    protected R getFallback() {
        log.warn(TAG + " - fallback - whydahServiceUri={}", whydahServiceUri.toString() + getTargetPath());
        return null;
    }

    public byte[] getResponseBodyAsByteArray() {
        return responseBody;
    }
}
