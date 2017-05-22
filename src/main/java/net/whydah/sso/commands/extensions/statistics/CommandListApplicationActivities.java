package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

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


    @Override
    protected String getTargetPath() {
        return "observe/statistics/" + applicationid + "/usersession";
    }


}

