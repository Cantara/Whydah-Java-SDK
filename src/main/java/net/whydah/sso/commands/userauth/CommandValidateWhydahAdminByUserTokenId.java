package net.whydah.sso.commands.userauth;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.WhydahUtil;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.List;

public class CommandValidateWhydahAdminByUserTokenId extends BaseHttpGetHystrixCommand<Boolean>

{

    private String usertokenid;
    private URI tokenServiceUri;
    private String myAppTokenId;
    private UserApplicationRoleEntry adminRole;


    public CommandValidateWhydahAdminByUserTokenId(URI tokenServiceUri, String myAppTokenId, String usertokenid) {
        super(tokenServiceUri, "", myAppTokenId, "SSOAdminUserAuthGroup", 6000);
        this.usertokenid = usertokenid;
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminRole = WhydahUtil.getWhydahUserAdminRole();
        if (tokenServiceUri == null || myAppTokenId == null || usertokenid == null) {
            log.error("CommandValidateWhydahAdminByUserTokenId initialized with null-values - will fail");
        }
    }


    int retryCnt = 0;

    @Override
    protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
        if (statusCode == HttpURLConnection.HTTP_CONFLICT) {
            return false;
        } else {

            if (retryCnt < 1) {
                retryCnt++;
                return doGetCommand();
            } else {
                return false;
            }

        }
    }

    @Override
    protected Boolean dealWithResponse(String response) {
        String userTokenXml = new CommandGetUsertokenByUsertokenId(tokenServiceUri, myAppTokenId, myAppTokenXml, usertokenid).execute();
        UserToken userToken = UserTokenMapper.fromUserTokenXml(userTokenXml);
        List<UserApplicationRoleEntry> roles = userToken.getRoleList();
        for (UserApplicationRoleEntry role : roles) {
            log.debug("Checking for adminrole user UID:{} roleName: {} ", userToken.getUid(), role.getRoleName());
            if (role.getApplicationId().equalsIgnoreCase(adminRole.getApplicationId())) {
                if (role.getApplicationName().equalsIgnoreCase(adminRole.getApplicationName())) {
                    if (role.getOrgName().equalsIgnoreCase(adminRole.getOrgName())) {
                        if (role.getRoleName().equalsIgnoreCase(adminRole.getRoleName())) {
                            if (role.getRoleValue().equalsIgnoreCase(adminRole.getRoleValue())) {
                                log.info("Whydah Admin user is true for uid={}", userToken.getUid());
                                return true;
                            }
                        }
                    }
                }
            }
        }
        log.info("Whydah Admin user is false for uid={}", userToken.getUid());
        return false;
    }

    @Override
    protected String getTargetPath() {
        return "user/" + myAppTokenId + "/validate_usertokenid/" + usertokenid;
    }


}
