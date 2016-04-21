package net.whydah.sso.commands.userauth;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.whydah.sso.user.mappers.UserCredentialMapper;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.BaseHttpPostHystrixCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLogonUserByUserCredential  extends BaseHttpPostHystrixCommand<String> {

    private UserCredential userCredential;
    private String userticket;


    public CommandLogonUserByUserCredential(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, UserCredential userCredential) {
        super(tokenServiceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 3000);
        this.userCredential=userCredential;
        this.userticket= UUID.randomUUID().toString();  // Create new UUID ticket if not provided
        if (tokenServiceUri == null || myAppTokenId == null || userCredential == null) {
            log.error("CommandLogonUserByUserCredential initialized with null-values - will fail tokenServiceUri:{} myAppTokenId:{}, myAppTokenXml:{}, userCredential:*****", tokenServiceUri, myAppTokenId, myAppTokenXml, userCredential);
        }
    }

    public CommandLogonUserByUserCredential(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential,String userticket) {
        this(tokenServiceUri,myAppTokenId,myAppTokenXml,userCredential);
        this.userticket=userticket;
    }

    public CommandLogonUserByUserCredential(URI tokenServiceUri, String myAppTokenId, String myAppTokenXml, String userCredentialXml, String userticket) {
        this(tokenServiceUri, myAppTokenId, myAppTokenXml, UserCredentialMapper.fromXml(userCredentialXml));
        this.userticket = userticket;
    }


	@Override
	protected String getTargetPath() {
		return "user/" + myAppTokenId + "/" + userticket + "/usertoken";
	}


	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("apptoken", myAppTokenXml);
		data.put("usercredential", UserCredentialMapper.toXML(userCredential));
		return data;
	}



}