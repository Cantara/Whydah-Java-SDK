package net.whydah.sso.commands.userauth;

import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Created by totto on 24.06.15.
 */
public class CommandLogonUserByUserCredentialWithStubbedFallback extends CommandLogonUserByUserCredential {

    private static final Logger log = LoggerFactory.getLogger(CommandLogonUserByUserCredentialWithStubbedFallback.class);


    public CommandLogonUserByUserCredentialWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, UserCredential userCredential) {
        super(tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential);
    }

    public CommandLogonUserByUserCredentialWithStubbedFallback(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, UserCredential userCredential,String userticket) {
        super(tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential,userticket);
    }


    @Override
    protected String getFallback() {
        log.warn("CommandLogonUserByUserCredential - getFallback - User authentication override with fallback ");
        return UserHelper.getDummyToken();
    }


}
