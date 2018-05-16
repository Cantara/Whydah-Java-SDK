package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.Email;

import java.net.URI;


public class CommandGetLastSeenForUserByUserEmail extends BaseHttpGetHystrixCommand<String> {

	public static int DEFAULT_TIMEOUT = 6000;
    private String userEmail;
    
    public CommandGetLastSeenForUserByUserEmail(URI tokenServiceUri, String applicationTokenId, String userEmail, int timeout) {
        super(tokenServiceUri, "", applicationTokenId, "STSUserQueries", timeout);

        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !Email.isValid(userEmail)) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, userEmail:{}", tokenServiceUri, applicationTokenId, userEmail);
        }
        this.userEmail = userEmail;

    }
    
    public CommandGetLastSeenForUserByUserEmail(URI tokenServiceUri, String applicationTokenId, String userEmail) {
        this(tokenServiceUri, applicationTokenId, userEmail, DEFAULT_TIMEOUT);
    }

	@Override
	protected String getTargetPath() {
        return "user/" + myAppTokenId + "/" + userEmail + "/last_seen";
    }


}
