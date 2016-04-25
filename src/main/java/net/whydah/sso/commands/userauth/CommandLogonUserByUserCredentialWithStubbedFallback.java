package net.whydah.sso.commands.userauth;

import net.whydah.sso.user.helpers.UserHelper;
import net.whydah.sso.user.types.UserCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class CommandLogonUserByUserCredentialWithStubbedFallback extends CommandLogonUserByUserCredential {

   // private static final Logger log = LoggerFactory.getLogger(CommandLogonUserByUserCredentialWithStubbedFallback.class);


    public CommandLogonUserByUserCredentialWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, UserCredential userCredential) {
        super(tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential);
    }

    public CommandLogonUserByUserCredentialWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, UserCredential userCredential,String userticket) {
        super(tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential,userticket);
    }


    @Override
    protected String getFallback() {
        log.warn("CommandLogonUserByUserCredential - getFallback - User authentication override with fallback ");
        return UserHelper.getDummyUserToken();
    }


}
