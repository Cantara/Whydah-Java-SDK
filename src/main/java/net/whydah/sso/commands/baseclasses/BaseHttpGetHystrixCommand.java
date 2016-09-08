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

public abstract class BaseHttpGetHystrixCommand<R> extends HystrixCommand<R>{

	protected Logger log;
	protected URI whydahServiceUri;
	protected String myAppTokenId="";
	protected String myAppTokenXml="";
	protected String TAG="";
	protected HttpRequest request;
	
	protected BaseHttpGetHystrixCommand(URI serviceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey, int hystrixExecutionTimeOut) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey)).
				andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(hystrixExecutionTimeOut)));
		init(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
	}

	protected BaseHttpGetHystrixCommand(URI tokenServiceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey)));
		init(tokenServiceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
	}


	private void init(URI serviceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		this.whydahServiceUri = serviceUri;
		this.myAppTokenXml = myAppTokenXml;
		if(this.myAppTokenXml!=null && !this.myAppTokenXml.equals("")  &&  (myAppTokenId==null||myAppTokenId.isEmpty())){
			this.myAppTokenId= ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
		} else {
			this.myAppTokenId = myAppTokenId;
		}
		this.TAG =this.getClass().getSimpleName() + ", pool: " + hystrixGroupKey;
		this.log =  LoggerFactory.getLogger(TAG);
		HystrixRequestContext.initializeContext();
	}


	

	
	@Override
	protected R run() {
		return doGetCommand();

	}

	protected R doGetCommand() {
		try{
			String uriString = whydahServiceUri.toString();
			if(getTargetPath()!=null){
				 uriString += getTargetPath();
			}

			log.trace("TAG" + " - whydahServiceUri={} myAppTokenId={}", uriString, myAppTokenId);
		
			
			
			if(getQueryParameters()!=null && getQueryParameters().length!=0){
				request = HttpRequest.get(uriString, true, getQueryParameters());
			} else {
				request = HttpRequest.get(uriString);
			}
			
			if(getAcceptHeaderRequestValue()!=null && !getAcceptHeaderRequestValue().equals("")){
				request = request.accept(getAcceptHeaderRequestValue());
			}
			
			request.trustAllCerts();
			request.trustAllHosts();
			
			if(getFormParameters()!=null && !getFormParameters().isEmpty()){
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
		
		//HttpRequest.post("http://google.com").send("name=kevin")
		
		return request;
	}

	private void onFailed(String responseBody, int statusCode) {
		log.trace(TAG + " - Unexpected response from {}. Status code is {} content is {} ", whydahServiceUri, String.valueOf(statusCode) + responseBody);
	}


	private void onCompleted(String responseBody) {
		log.debug(TAG + " - ok: " + responseBody);
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
		// TODO - this should return false for Boolean commands
		return null;
	}


	protected String getAcceptHeaderRequestValue(){
		//CAN RETURN JSON (can be used in derived class)
		//return "application/json";
		return "";
		
	}
	
	private byte[] responseBody;
	public byte[] getResponseBodyAsByteArray(){
		return responseBody;
	}
}
