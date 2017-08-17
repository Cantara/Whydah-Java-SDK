package net.whydah.sso.commands.threat;


import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class CommandSendThreatSignal extends BaseHttpPostHystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandSendThreatSignal.class);

    private URI tokenServiceUri;
    private String myAppTokenId;
    private String threatMessage;


    public CommandSendThreatSignal(URI tokenServiceUri, String myAppTokenId, String threatMessage) {
    	super(tokenServiceUri, "", myAppTokenId,"WhydahThreat",1000);
        this.myAppTokenId = myAppTokenId;
        
        
        this.threatMessage = threatMessage;
        if (tokenServiceUri == null || myAppTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}", tokenServiceUri.toString(), myAppTokenId);
        }
    }

    @Override
    protected String dealWithResponse(String response) {
    	return "";
    }
    
    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    	return "";
    }

	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("signal", threatMessage);
		return data;
	}


    @Override
	protected String getTargetPath() {
		return "threat/" + myAppTokenId + "/signal";
	}


}

