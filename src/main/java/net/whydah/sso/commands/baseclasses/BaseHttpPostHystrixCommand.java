package net.whydah.sso.commands.baseclasses;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.session.baseclasses.CryptoUtil;
import net.whydah.sso.util.StringConv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static net.whydah.sso.util.LoggerUtil.first50;


public abstract class BaseHttpPostHystrixCommand<R> extends HystrixCommand<R>{

	protected Logger log;
	protected URI whydahServiceUri;
	protected String myAppTokenId="";
	protected String myAppTokenXml="";
	protected String TAG="";
	protected HttpRequest request;
	private int statusCode;
	public int getStatusCode() {
		return statusCode;
	}
	
    private static HystrixThreadPoolProperties.Setter threadProperties;

    static {
        threadProperties = HystrixThreadPoolProperties.Setter();
        threadProperties.withCoreSize(10);
        threadProperties.withMaxQueueSize(1000);
        threadProperties.withMaxQueueSize(10000);
        HystrixRequestContext.initializeContext();

    }


	protected BaseHttpPostHystrixCommand(URI serviceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey, int hystrixExecutionTimeOut) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey)).
				andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(hystrixExecutionTimeOut)));
		init(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
	}

	protected BaseHttpPostHystrixCommand(URI serviceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey)));
		init(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
	}


	private void init(URI serviceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		this.whydahServiceUri = serviceUri;
		this.myAppTokenXml = myAppTokenXml;
		if(this.myAppTokenXml!=null && !this.myAppTokenXml.equals("")  &&  (myAppTokenId==null||myAppTokenId.isEmpty())){
			this.myAppTokenId= ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
		} else {
			this.myAppTokenId = myAppTokenId;
		}
		this.TAG =this.getClass().getName() + ", pool :" + hystrixGroupKey;
		this.log =  LoggerFactory.getLogger(TAG);
		HystrixRequestContext.initializeContext();
	}


	

	
	@Override
	protected R run() {
		return doPostCommand();

	}

	protected R doPostCommand() {
		try{
			String uriString = whydahServiceUri.toString();
			if(getTargetPath()!=null){
				 uriString += getTargetPath();
			}

			log.trace("TAG" + " - whydahServiceUri={} myAppTokenId={}", uriString, myAppTokenId);
		
			if(getQueryParameters()!=null && getQueryParameters().length!=0){
				request = HttpRequest.post(uriString, true, getQueryParameters());
			} else {
				request = HttpRequest.post(uriString);
			}
			request.trustAllCerts();
			request.trustAllHosts();
			
			if(getFormParameters()!=null && !getFormParameters().isEmpty()){
				request.contentType(HttpSender.APPLICATION_FORM_URLENCODED);
				request.form(getFormParameters());
			}
			
			request = dealWithRequestBeforeSend(request);
			
			responseBody = request.bytes();
            byte[] responseBodyCopy = responseBody.clone();
            statusCode = request.code();
//            String responseAsText = StringConv.UTF8(responseBodyCopy);
//            if (responseBodyCopy.length > 0) {
//                log.trace("resposeBody: {}", responseBodyCopy);
//				log.trace("StringConv: {}", StringConv.UTF8(responseBodyCopy));
//				try {
//					log.trace("responseAsText: {}", CryptoUtil.decrypt(StringConv.UTF8(responseBodyCopy)));
//					responseAsText = StringConv.UTF8(responseBodyCopy);
//					responseAsText = CryptoUtil.decrypt(StringConv.UTF8(responseBodyCopy));
//                } catch (Exception e) {
//					log.warn("Unable to decrypt - wrong cryptokey?", e.getMessage());
//				}
//            }
            String responseAsText = StringConv.UTF8(responseBodyCopy);
            if (responseBodyCopy.length > 0) {
                log.trace("resposeBody: {}", responseBodyCopy);
				log.trace("StringConv: {}", responseAsText);
				try {
                	responseAsText = CryptoUtil.decrypt(responseAsText);
                    log.trace("responseAsText: {}", responseAsText);
                } catch (Exception e) {
					log.warn("Unable to decrypt - wrong cryptokey?", e.getMessage());
				}
            }
            switch (statusCode) {
			case java.net.HttpURLConnection.HTTP_OK:
				onCompleted(responseAsText);
				return dealWithResponse(responseAsText);
			default:
				onFailed(responseAsText, statusCode);
				return dealWithFailedResponse(responseAsText, statusCode);
			}
		} catch(Exception ex){
			ex.printStackTrace();
			throw new RuntimeException("TAG" +  " - Application authentication failed to execute");
		}
	}

	protected R dealWithFailedResponse(String responseBody, int statusCode) {
		return null;
	}

	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		//CAN USE MULTIPART
		
		//JUST EXAMPLE
		
		//		HttpRequest request = HttpRequest.post("http://google.com");
		//		request.part("status[body]", "Making a multipart request");
		//		request.part("status[image]", new File("/home/kevin/Pictures/ide.png"));

		//OR SEND SOME DATA
		
		//request.send("name=huydo")
		//or something like
		//request.contentType("application/json").send(applicationJson);
		
		return request;
	}

	protected void onFailed(String responseBody, int statusCode) {
		log.debug(TAG + " - Unexpected response from {}. Status code is {} content is {} ", whydahServiceUri, String.valueOf(statusCode) + responseBody);
	}

	protected void onCompleted(String responseBody) {
		log.debug(TAG + " - ok: " + first50(responseBody));
	}


	protected abstract String getTargetPath();
	
	protected Map<String, String> getFormParameters(){
		return new HashMap<String, String>();
	}

	protected Object[] getQueryParameters(){
		return new String[]{};
	}
	
	@SuppressWarnings("unchecked")
	protected R dealWithResponse(String response){
		return (R)response;
	}

	@Override
	protected R getFallback() {
		log.warn(TAG + " - fallback - whydahServiceUri={}", whydahServiceUri.toString() + getTargetPath());
		return null;
	}
	
	private byte[] responseBody;
	public byte[] getResponseBodyAsByteArray(){
		return responseBody.clone();
	}
}

