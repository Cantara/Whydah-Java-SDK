package net.whydah.sso.commands.userauth;


import java.net.URI;

import net.whydah.sso.user.helpers.UserHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandGetUsertokenByUsertokenIdWithStubbedFallback extends CommandGetUsertokenByUsertokenId {
    private static final Logger log = LoggerFactory.getLogger(CommandGetUsertokenByUsertokenIdWithStubbedFallback.class);



    public CommandGetUsertokenByUsertokenIdWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userticket) {
        super(tokenServiceUri, myAppTokenId, myAppTokenXml, userticket);
    }

    @Override
    protected String getFallback() {
        log.warn("CommandGetUsertokenByUsertokenIdWithStubbedFallback - getFallback - override with fallback ");
        return UserHelper.getDummyUserToken();
    }
}
