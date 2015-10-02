package net.whydah.sso.commands.userauth;

import java.net.URI;

public class CommandValidateUsertokenIdWithStubbedFallback extends CommandValidateUsertokenId {

    public CommandValidateUsertokenIdWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String usertokenid) {
        super(tokenServiceUri, myAppTokenId, usertokenid);
    }

    @Override
    protected Boolean getFallback() {
        return true;
    }


}
