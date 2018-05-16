package net.whydah.sso.commands.threat;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.whydah.ThreatSignal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;


public class CommandSendThreatSignal extends BaseHttpPostHystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandSendThreatSignal.class);

    private String myAppTokenId;
    private String threatMessage;
    private static final ObjectMapper mapper = new ObjectMapper();
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandSendThreatSignal(URI tokenServiceUri, String myAppTokenId, String threatMessage) {
    	super(tokenServiceUri, "", myAppTokenId,"WhydahThreat", DEFAULT_TIMEOUT);
        this.myAppTokenId = myAppTokenId;

        try {
            this.threatMessage = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(WhydahApplicationSession.createThreat(threatMessage));
        } catch (Exception e) {
            this.threatMessage = threatMessage;
        }
        if (tokenServiceUri == null || myAppTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}", tokenServiceUri, myAppTokenId);
        }
    }
    
    public CommandSendThreatSignal(URI tokenServiceUri, String myAppTokenId, String threatMessage, int timeout) {
    	super(tokenServiceUri, "", myAppTokenId,"WhydahThreat", timeout);
        this.myAppTokenId = myAppTokenId;

        try {
            this.threatMessage = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(WhydahApplicationSession.createThreat(threatMessage));
        } catch (Exception e) {
            this.threatMessage = threatMessage;
        }
        if (tokenServiceUri == null || myAppTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}", tokenServiceUri, myAppTokenId);
        }
    }


    public CommandSendThreatSignal(URI tokenServiceUri, String myAppTokenId, ThreatSignal threatSignal) {
        super(tokenServiceUri, "", myAppTokenId, "WhydahThreat", DEFAULT_TIMEOUT);
        this.myAppTokenId = myAppTokenId;


        try {
            this.threatMessage = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(threatSignal);
        } catch (Exception e) {
            this.threatMessage = threatSignal.getText();
        }
        if (tokenServiceUri == null || myAppTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}", tokenServiceUri, myAppTokenId);
        }
    }
    
    public CommandSendThreatSignal(URI tokenServiceUri, String myAppTokenId, ThreatSignal threatSignal, int timeout) {
        super(tokenServiceUri, "", myAppTokenId, "WhydahThreat", timeout);
        this.myAppTokenId = myAppTokenId;


        try {
            this.threatMessage = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(threatSignal);
        } catch (Exception e) {
            this.threatMessage = threatSignal.getText();
        }
        if (tokenServiceUri == null || myAppTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}", tokenServiceUri, myAppTokenId);
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

