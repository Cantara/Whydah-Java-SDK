package net.whydah.sso.commands.userauth;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandValidateUsertokenIdWithStubbedFallback extends CommandValidateUsertokenId {
    private static final Logger log = LoggerFactory.getLogger(CommandValidateUsertokenIdWithStubbedFallback.class);


    public CommandValidateUsertokenIdWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String usertokenid) {
        super(tokenServiceUri, myAppTokenId, usertokenid);
    }

    @Override
    protected Boolean getFallback() {

        log.warn("CommandValidateUsertokenIdWithStubbedFallback - getFallback - override with fallback ");
        return true;
    }


}
