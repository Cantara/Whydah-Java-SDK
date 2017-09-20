package net.whydah.sso.commands.appauth;

import java.net.URI;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandGetApplicationIdFromApplicationTokenId extends BaseHttpGetHystrixCommand<String> {
    private static final Logger log = LoggerFactory.getLogger(CommandGetApplicationIdFromApplicationTokenId.class);



    public CommandGetApplicationIdFromApplicationTokenId(URI tokenServiceUri, String applicationTokenId) {
        super(tokenServiceUri, "", applicationTokenId, "STSApplicationAuthGroup", 30000);

        if (tokenServiceUri == null || applicationTokenId == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri={} || applicationTokenId={}", tokenServiceUri, applicationTokenId);
        }
    }


    @Override
	protected String getTargetPath() {
		return myAppTokenId + "/get_application_id";
	}
}
