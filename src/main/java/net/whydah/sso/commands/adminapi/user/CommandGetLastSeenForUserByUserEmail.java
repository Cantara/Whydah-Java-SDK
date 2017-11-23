package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.ddd.model.application.ApplicationTokenID;
import net.whydah.sso.ddd.model.user.Email;

import java.net.URI;


public class CommandGetLastSeenForUserByUserEmail extends BaseHttpGetHystrixCommand<String> {

    private String userEmail;


    public CommandGetLastSeenForUserByUserEmail(URI tokenServiceUri, String applicationTokenId, String userEmail) {
        super(tokenServiceUri, "", applicationTokenId, "STSUserQueries", 6000);

        if (tokenServiceUri == null || !ApplicationTokenID.isValid(applicationTokenId) || !Email.isValid(userEmail)) {
            log.error(TAG + " initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, userEmail:{}", tokenServiceUri, applicationTokenId, userEmail);
        }
        this.userEmail = userEmail;

    }

	@Override
	protected String getTargetPath() {
        return "user/" + myAppTokenId + "/" + userEmail + "/last_seen";
    }


}
