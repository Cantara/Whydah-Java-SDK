package net.whydah.sso.commands.userauth;

import java.net.URI;

/**
 * Created by totto on 24.06.15.
 */
public class CommandValidateUsertokenIdWithStubbedFallback extends CommandValidateUsertokenId {

    public CommandValidateUsertokenIdWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String usertokenid) {
        super(tokenServiceUri, myAppTokenId, usertokenid);
    }

    @Override
    protected Boolean getFallback() {
        return true;
    }


}
