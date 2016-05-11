package net.whydah.sso.commands.adminapi.user.role;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;


public class CommandAddUserRole extends BaseHttpPostHystrixCommand<String> {

    private String adminUserTokenId;
    private String userRoleJson;
    private String uId;


    public CommandAddUserRole(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String uId,String roleJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
        
        this.adminUserTokenId = adminUserTokenId;
        this.userRoleJson = roleJson;
        this.uId=uId;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || uId == null || roleJson == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }

        
    }

    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType("application/json").send(userRoleJson);
    }


	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/" + adminUserTokenId + "/user/" +  uId + "/role";
	}


}

