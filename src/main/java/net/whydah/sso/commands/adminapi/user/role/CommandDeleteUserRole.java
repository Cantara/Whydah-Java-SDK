package net.whydah.sso.commands.adminapi.user.role;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpDeleteHystrixCommand;

import java.net.URI;

public class CommandDeleteUserRole extends BaseHttpDeleteHystrixCommand<String> {

    private String adminUserTokenId;
    private String userId;
    private String uId;


    /**
     * @DELETE
     * @Path("/{uid}/role/{roleid}") public Response deleteRole(@PathParam("applicationtokenid") String applicationTokenId, @PathParam("userTokenId") String userTokenId,
     * @PathParam("uid") String uid, @PathParam("roleid") String roleid) {
     */
    public CommandDeleteUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String uId, String userId) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");

        this.adminUserTokenId = adminUserTokenId;
        this.userId = userId;
        this.uId = uId;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || uId == null || userId == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }


    }


    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
        return request.contentType("application/json").send(userId);
    }


    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/" + adminUserTokenId + "/user/" + uId + "/role/" + userId;
    }


}

