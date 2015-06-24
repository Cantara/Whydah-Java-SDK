package net.whydah.sso.commands.userauth;

import net.whydah.sso.user.UserHelper;

import java.net.URI;

/**
 * Created by totto on 24.06.15.
 */
public class CommandGetUsertokenByUserticketWithStubbedFallback extends CommandGetUsertokenByUserticket {


    public CommandGetUsertokenByUserticketWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userticket) {
        super(tokenServiceUri, myAppTokenId, myAppTokenXml, userticket);
    }

    @Override
    protected String getFallback() {
        return UserHelper.getDummyToken();
    }




}
