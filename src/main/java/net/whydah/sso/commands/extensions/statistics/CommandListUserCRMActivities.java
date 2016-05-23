package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;

public class CommandListUserCRMActivities extends BaseHttpGetHystrixCommand<String> {

    private final String prefix;
    private String userTokenId;
    private String userid;


    public CommandListUserCRMActivities(URI statisticsServiceUri, String myAppTokenId, String userTokenId, String userid) {
        super(statisticsServiceUri, "", myAppTokenId, "StatisticsExtensionGroup", 9000);

        this.userTokenId = userTokenId;
        this.userid = userid;
        this.prefix = "whydah";
//        if (statisticsServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userid == null) {
        if (statisticsServiceUri == null || userid == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

    }


    @Override
    protected String getTargetPath() {
        return "observe/statistics/" + userid + "/crmsession";
    }


}
