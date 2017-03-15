package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandLogonApplication extends BaseHttpPostHystrixCommand<String> {


	private URI tokenServiceUri;
	private ApplicationCredential appCredential;
    int retryCnt = 0;

	public CommandLogonApplication(URI tokenServiceUri, ApplicationCredential appCredential) {
        super(tokenServiceUri, "", "", "STSApplicationAuthGroup", 6000);
        this.appCredential = appCredential;
        this.tokenServiceUri = tokenServiceUri;
        if (tokenServiceUri == null || appCredential == null) {
            log.error(TAG + " initialized with null-values - will fail. tokenServiceUri:{}, appCredential:{} ", tokenServiceUri, appCredential);
        }

    }

    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
//        if (statusCode != HttpURLConnection.HTTP_CONFLICT && retryCnt < 1) {
//            retryCnt++;
//            return doPostCommand();
//        } else {
//            return null;
//        }
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
