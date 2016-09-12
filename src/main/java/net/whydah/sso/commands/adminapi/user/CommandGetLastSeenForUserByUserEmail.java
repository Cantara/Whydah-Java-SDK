package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;


public class CommandGetLastSeenForUserByUserEmail extends BaseHttpGetHystrixCommand<String> {

    private String userEmail;


    public CommandGetLastSeenForUserByUserEmail(URI tokenServiceUri, String myAppTokenId, String userEmail) {
    	super(tokenServiceUri, "", myAppTokenId, "STSUserQueries", 6000);
        
        this.userEmail = userEmail;
        if (tokenServiceUri == null || myAppTokenId == null || userEmail == null) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, userEmail:{}", tokenServiceUri, myAppTokenId, userEmail);
        }

    }

	@Override
	protected String getTargetPath() {
        return "user/" + myAppTokenId + "/" + userEmail + "/last_seen";
    }


}
