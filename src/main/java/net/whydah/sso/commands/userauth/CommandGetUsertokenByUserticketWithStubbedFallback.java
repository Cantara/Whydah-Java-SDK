package net.whydah.sso.commands.userauth;


import net.whydah.sso.user.helpers.UserHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;


public class CommandGetUsertokenByUserticketWithStubbedFallback extends CommandGetUsertokenByUserticket {
    private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUserticketWithStubbedFallback.class);



    public CommandGetUsertokenByUserticketWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userticket) {
        super(tokenServiceUri, myAppTokenId, myAppTokenXml, userticket);
    }

    @Override
    protected String getFallback() {

        log.warn("CommandGetUsertokenByUserticketWithStubbedFallback - fallback - override with fallback ");
        return UserHelper.getDummyUserToken();
    }




}
