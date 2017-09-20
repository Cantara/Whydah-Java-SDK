package net.whydah.sso.commands.baseclasses;

import java.net.URI;

public abstract class BaseHttpDeleteHystrixCommandForBooleanType extends BaseHttpDeleteHystrixCommand<Boolean> {

	protected BaseHttpDeleteHystrixCommandForBooleanType(URI serviceUri,
			String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		super(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
		// TODO Auto-generated constructor stub
	}
	

	protected BaseHttpDeleteHystrixCommandForBooleanType(URI serviceUri,
			String myAppTokenXml, String myAppTokenId, String hystrixGroupKey,
			int hystrixExecutionTimeOut) {
		super(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey,
				hystrixExecutionTimeOut);
	}
	
	
	protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
		return false;
	}
	
	
	protected Boolean getFallback() {
		return false;
	}

}

