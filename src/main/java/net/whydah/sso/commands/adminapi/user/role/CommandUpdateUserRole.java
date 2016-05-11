package net.whydah.sso.commands.adminapi.user.role;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPutHystrixCommand;

import java.net.URI;


//TODO: Check, it is unfinished
public class CommandUpdateUserRole extends BaseHttpPutHystrixCommand<String> {

    private String adminUserTokenId;
    private String userRoleJson;
    private String uId;
    private String roleId;


    public CommandUpdateUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String uId, String roleId, String roleJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");

        this.adminUserTokenId = adminUserTokenId;
        this.userRoleJson = roleJson;
        this.uId = uId;
        this.roleId = roleId;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || uId == null || roleId == null || roleJson == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }


    }


    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
        return request.contentType("application/json").send(userRoleJson);
    }


    //     @Path("/{uid}/role/{roleid}")
    @Override
    protected String getTargetPath() {
        // TODO Auto-generated method stub
        return myAppTokenId + "/" + adminUserTokenId + "/user/" + uId + "/role/" + roleId;
    }


}
