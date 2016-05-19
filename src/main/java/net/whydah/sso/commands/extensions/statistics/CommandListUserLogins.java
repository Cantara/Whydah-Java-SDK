package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandListUserLogins extends BaseHttpGetHystrixCommand<String> {
    
    private final String prefix;
    private String adminUserTokenId;
    private String userid;


    public CommandListUserLogins(URI statisticsServiceUri, String myAppTokenId, String adminUserTokenId, String userid) {
    	super(statisticsServiceUri, "", myAppTokenId, "StatisticsExtensionGroup", 3000);
        
        this.adminUserTokenId = adminUserTokenId;
        this.userid = userid;
        this.prefix = "whydah";
        if (statisticsServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userid == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }


	@Override
	protected String getTargetPath() {
		return "observe/activities/"+ prefix + "/logon/user/" + userid;
	}


}
