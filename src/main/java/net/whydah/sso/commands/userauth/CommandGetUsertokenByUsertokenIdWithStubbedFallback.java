package net.whydah.sso.commands.userauth;


import net.whydah.sso.user.helpers.UserHelper;

import java.net.URI;

/**
 * Created by totto on 25.06.15.
 */
public class CommandGetUsertokenByUsertokenIdWithStubbedFallback extends CommandGetUsertokenByUsertokenId {


    public CommandGetUsertokenByUsertokenIdWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userticket) {
        super(tokenServiceUri, myAppTokenId, myAppTokenXml, userticket);
    }

    @Override
    protected String getFallback() {
        return UserHelper.getDummyUserToken();
    }
}
