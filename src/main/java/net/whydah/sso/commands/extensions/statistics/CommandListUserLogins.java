package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.util.ExceptionUtil;

import java.net.URI;

//still work but please use CommandGetUserLogon instead
@Deprecated
public class CommandListUserLogins extends BaseHttpGetHystrixCommand<String> {
    
    private final String prefix;
    private String userid;
    public static int DEFAULT_TIMEOUT = 6000;

    public CommandListUserLogins(URI statisticsServiceUri, String prefix, String userid) {
    	super(statisticsServiceUri, null, null, "StatisticsExtensionGroup", DEFAULT_TIMEOUT);       
        this.userid = userid;
        this.prefix = prefix;
    }
    
    public CommandListUserLogins(URI statisticsServiceUri, String prefix, String userid, int timeout) {
    	super(statisticsServiceUri, null, null, "StatisticsExtensionGroup", timeout);       
        this.userid = userid;
        this.prefix = prefix;
    }

    int retryCnt = 0;

    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
        if (statusCode != java.net.HttpURLConnection.HTTP_FORBIDDEN && retryCnt < 1) {
            //do retry
            retryCnt++;
            return doGetCommand();
        } else {
            String authenticationFailedMessage = ExceptionUtil.printableUrlErrorMessage("User session failed", request, statusCode);
            log.warn(authenticationFailedMessage);
            return null;
        }
    }


    @Override
    protected String getTargetPath() {
		return "observe/activities/"+ this.prefix + "/logon/user/" + userid;
	}


}
