package net.whydah.sso.commands.userauth;

import java.net.URI;


//TODO:make test
public class CommandSendSmsPin extends CommandSendSms {
    

    public CommandSendSmsPin(URI tokenServiceUri, String appTokenId, String appTokenXml, String phoneNo, String pin) {
    	super(tokenServiceUri, appTokenId, appTokenXml, phoneNo, pin);
    }

	@Override
	protected String getTargetPath() {
		return myAppTokenId + "/send_sms_pin";
	}
}
