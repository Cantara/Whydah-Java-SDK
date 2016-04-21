package net.whydah.sso.util;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;

public abstract class BaseHttpPostHystrixCommand<R> extends HystrixCommand<R>{

	protected Logger log;
	protected URI tokenServiceUri ;
	protected String myAppTokenId="";
	protected String myAppTokenXml="";
	protected String TAG="";

	protected BaseHttpPostHystrixCommand(URI tokenServiceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey, int hystrixExecutionTimeOut) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey)).
				andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
						.withExecutionTimeoutInMilliseconds(hystrixExecutionTimeOut)));
		init(tokenServiceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
	}

	protected BaseHttpPostHystrixCommand(URI tokenServiceUri, String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(hystrixGroupKey)));
		init(tokenServiceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
	}


	private void init(URI tokenServiceUri, String myAppTokenXml,String myAppTokenId, String hystrixGroupKey) {
		this.tokenServiceUri = tokenServiceUri;
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
		try{
			String logonServiceUrl = tokenServiceUri.toString();
			if(getTargetPath()!=null){
				 logonServiceUrl += getTargetPath();
			} 
			
			log.trace("TAG" + " - uri={} myAppTokenId={}", tokenServiceUri.toString(), myAppTokenId);
			HttpRequest request = HttpRequest.post(logonServiceUrl);
			request.trustAllCerts();
			request.trustAllHosts();
			
			if(getFormParameters()!=null && !getFormParameters().isEmpty()){
				request.contentType(HttpSender.APPLICATION_FORM_URLENCODED);
				request.form(getFormParameters());
			}
			
			request = dealWithRequestBeforeSend(request);
			
			String responseBody = request.body();
			int statusCode = request.code();
			

			switch (statusCode) {
			case HttpSender.STATUS_OK:
				onCompleted(responseBody);
				return dealWithResponse(responseBody);
			default:
				onFailed(responseBody, statusCode);
				
			}
		} catch(Exception ex){
			ex.printStackTrace();
			throw new RuntimeException("TAG" +  " - Application authentication failed to execute");
		}

		return null;
	}

	protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
		return request;
	}

	private void onFailed(String responseBody, int statusCode) {
		log.debug(TAG + " - Unexpected response from STS. Status code is {} content is {} ", String.valueOf(statusCode) + responseBody);
	}


	private void onCompleted(String responseBody) {
		log.debug(TAG + " - ok: " + responseBody);
	}


	protected abstract String getTargetPath();
	protected Map<String, String> getFormParameters(){
		return new HashMap<String, String>();
	}
	


	@SuppressWarnings("unchecked")
	protected R dealWithResponse(String response){
		return (R)response;
	}

	@Override
	protected R getFallback() {
		log.warn( TAG + " - fallback - uri={}", tokenServiceUri.toString() + getTargetPath());
		return null;
	}
}
