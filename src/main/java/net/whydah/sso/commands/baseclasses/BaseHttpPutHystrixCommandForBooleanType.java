package net.whydah.sso.commands.baseclasses;

import java.net.URI;

public abstract class BaseHttpPutHystrixCommandForBooleanType extends BaseHttpPutHystrixCommand<Boolean> {

	protected BaseHttpPutHystrixCommandForBooleanType(URI serviceUri,
			String myAppTokenXml, String myAppTokenId, String hystrixGroupKey,
			int hystrixExecutionTimeOut) {
		super(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey,
				hystrixExecutionTimeOut);
		// TODO Auto-generated constructor stub
	}
	
	protected BaseHttpPutHystrixCommandForBooleanType(URI serviceUri,
			String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		super(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
		return false;
	}
	
	@Override
	protected Boolean getFallback() {
		return false;
	}

   
}

