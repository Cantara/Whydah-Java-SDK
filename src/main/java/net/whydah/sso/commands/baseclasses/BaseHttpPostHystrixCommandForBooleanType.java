package net.whydah.sso.commands.baseclasses;

import java.net.URI;


public abstract class BaseHttpPostHystrixCommandForBooleanType extends BaseHttpPostHystrixCommand<Boolean>{

	protected BaseHttpPostHystrixCommandForBooleanType(URI serviceUri,
			String myAppTokenXml, String myAppTokenId, String hystrixGroupKey,
			int hystrixExecutionTimeOut) {
		super(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey,
				hystrixExecutionTimeOut);
	}
	
	protected BaseHttpPostHystrixCommandForBooleanType(URI serviceUri,
			String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		super(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey
				);
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

