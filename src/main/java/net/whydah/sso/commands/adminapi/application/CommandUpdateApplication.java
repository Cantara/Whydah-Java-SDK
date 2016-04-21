package net.whydah.sso.commands.adminapi.application;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;

// TODO:  wait for https://github.com/Cantara/Whydah-UserAdminService/issues/35

public class CommandUpdateApplication extends BaseHttpPostHystrixCommand<String> {

  
    private String myAppTokenId;
    private String adminUserTokenId;
    private String applicationJson;


    public CommandUpdateApplication(URI userAdminServiceUri, String myAppTokenId, String adminUserTokenId, String applicationJson) {
        super(userAdminServiceUri, "", myAppTokenId, "UASUserAdminGroup");
        
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.applicationJson = applicationJson;
        if (userAdminServiceUri == null || myAppTokenId == null || adminUserTokenId == null || applicationJson == null) {
            log.error("CommandUpdateApplication initialized with null-values - will fail");
        }

    }

//    @Override
//    protected String run() {
//        log.trace("CommandUpdateApplication - myAppTokenId={}", myAppTokenId);
//
//        Client uasClient = ClientBuilder.newClient();
//
//        WebTarget updateApplication = uasClient.target(userAdminServiceUri).path(myAppTokenId + "/" + adminUserTokenId + "/xxx");
//        Response response = updateApplication.request().post(Entity.json(applicationJson));
//        throw new UnsupportedOperationException();
//        //return null;
//
//    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandUpdateApplication - fallback - uri={}", userAdminServiceUri.toString());
//        return null;
//    }
    
    @Override
    protected String getTargetPath() {
    	return myAppTokenId + "/" + adminUserTokenId + "/xxx";
    }


}
