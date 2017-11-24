package net.whydah.sso.commands.baseclasses;

import java.net.URI;

public abstract class BaseHttpGetHystrixCommandForBooleanType extends BaseHttpGetHystrixCommand<Boolean> {


    public BaseHttpGetHystrixCommandForBooleanType(URI tokenServiceUri,
			String myAppTokenXml, String myAppTokenId, String hystrixGroupKey) {
		super(tokenServiceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey);
	}
	
	public BaseHttpGetHystrixCommandForBooleanType(URI serviceUri,
			String myAppTokenXml, String myAppTokenId, String hystrixGroupKey,
			int hystrixExecutionTimeOut) {
		super(serviceUri, myAppTokenXml, myAppTokenId, hystrixGroupKey,
				hystrixExecutionTimeOut);
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
