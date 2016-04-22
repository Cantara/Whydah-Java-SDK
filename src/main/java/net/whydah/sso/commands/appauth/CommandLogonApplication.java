package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandLogonApplication extends BaseHttpPostHystrixCommand<String> {


	private URI tokenServiceUri;
	private ApplicationCredential appCredential;

	public CommandLogonApplication(URI tokenServiceUri, ApplicationCredential appCredential) {
		super(tokenServiceUri, "", "", "STSApplicationAdminGroup", 3000);
		this.appCredential = appCredential;
	}

	@Override
	protected String getFallback() {
		log.warn("CommandLogonApplication - fallback - whydahServiceUri={}", tokenServiceUri.toString());
		return null;
	}



	@Override
	protected String getTargetPath() {
		return "logon";
	}

	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("applicationcredential", ApplicationCredentialMapper.toXML(appCredential));
		return data;
	}

}
