package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.util.ExceptionUtil;

import java.net.URI;

public class CommandListApplicationActivities extends BaseHttpGetHystrixCommand<String> {

    private final String prefix;
    private String userTokenId;
    private String applicationid;


    public CommandListApplicationActivities(URI statisticsServiceUri, String myAppTokenId, String userTokenId, String applicationid) {
        super(statisticsServiceUri, "", myAppTokenId, "StatisticsExtensionGroup", 9000);

        this.userTokenId = userTokenId;
        this.applicationid = applicationid;
        this.prefix = "whydah";
        if (statisticsServiceUri == null || myAppTokenId == null || userTokenId == null || applicationid == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

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
        return "observe/statistics/" + applicationid + "/usersession";
    }

// https://whydahdev.cantara.no/reporter/observe/statistics/9999/usersession
}

