package net.whydah.sso.commands.appauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class CommandGetApplicationKey extends BaseHttpGetHystrixCommand<String> {
    private static final Logger log = LoggerFactory.getLogger(CommandGetApplicationIdFromApplicationTokenId.class);
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandGetApplicationKey(URI tokenServiceUri, String applicationTokenId) {
        super(tokenServiceUri, "", applicationTokenId, "STSApplicationAuthGroup", DEFAULT_TIMEOUT);

        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri={} || applicationTokenId={}", tokenServiceUri, applicationTokenId);
        }
    }
    
    public CommandGetApplicationKey(URI tokenServiceUri, String applicationTokenId, int timeout) {
        super(tokenServiceUri, "", applicationTokenId, "STSApplicationAuthGroup", timeout);

        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId)) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri={} || applicationTokenId={}", tokenServiceUri, applicationTokenId);
        }
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/get_application_key";
    }
}
