package net.whydah.sso.commands.adminapi.user.role;

import com.github.kevinsawicki.http.HttpRequest;

import net.whydah.sso.commands.baseclasses.BaseHttpDeleteHystrixCommand;

import java.net.URI;

public class CommandDeleteUserRole extends BaseHttpDeleteHystrixCommand<Boolean> {

    private String adminUserTokenId;
    private String roleId;
    private String uId;


    /**
     * @DELETE
     * @Path("/{uid}/role/{roleid}") public Response deleteRole(@PathParam("applicationtokenid") String applicationTokenId, @PathParam("userTokenId") String userTokenId,
     * @PathParam("uid") String uid, @PathParam("roleid") String roleid) {
     */
    public CommandDeleteUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String uId, String roleId) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup",3000);

        this.adminUserTokenId = adminUserTokenId;
        this.roleId = roleId;
        this.uId = uId;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || uId == null || roleId == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }


    }

//
//    @Override
//    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
//        return request.contentType("application/json").send(roleId);
//    }

    @Override
    protected Boolean dealWithFailedResponse(String responseBody, int statusCode) {
    	return statusCode==java.net.HttpURLConnection.HTTP_NO_CONTENT;//should receive 204
    }
    
    @Override
    protected Boolean dealWithResponse(String response) {
    	return false; //never happens
    }

    @Override
    protected String getTargetPath() {
        return myAppTokenId + "/" + adminUserTokenId + "/user/" + uId + "/role/" + roleId;
    }


}

