package net.whydah.sso.commands.adminapi.user;

import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;
import net.whydah.sso.user.types.UserCredential;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class CommandCreatePinVerifiedUser extends BaseHttpPostHystrixCommand<String>{

    String myAppTokenId;
    String myAppTokenXml;
    String adminUserTokenId;
    String userTicket;
    String pin;
    String cellPhone;
    String json;


    public CommandCreatePinVerifiedUser(URI serviceUri, String myAppTokenId, String myAppTokenXml, String adminUserToken, String userTicket, String phoneNo, String pin, String userIdentityJson) {
        super(serviceUri, myAppTokenXml, myAppTokenId, "SSOAUserAuthGroup", 6000);  // 600 ms as this is a slow command
        this.myAppTokenId = myAppTokenId;
        this.myAppTokenXml = myAppTokenXml;
        this.adminUserTokenId = adminUserToken;
        this.userTicket = userTicket;
		this.pin = pin;
		this.cellPhone = phoneNo;
        this.json = userIdentityJson;
        if (serviceUri == null || myAppTokenId == null || myAppTokenXml == null || adminUserTokenId == null || userTicket == null || pin == null || cellPhone == null || userIdentityJson == null) {
            log.error(TAG + " initialized with null-values - will fail - userAdminServiceUri:{}, myAppTokenId:{}, adminUserTokenId:{}, userIdentityJson:{}", serviceUri, myAppTokenId, adminUserTokenId, userIdentityJson);
        }

    }


	@Override
	protected String getTargetPath() {

		return "user/" + myAppTokenId + "/" + userTicket +  "/" + pin + "/create_pinverified_user";
	}

	@Override
	protected String dealWithFailedResponse(String responseBody, int statusCode) {
		return null;
	}


    @Override
    protected Map<String, String> getFormParameters() {
        Map<String, String> formData = new HashMap<String, String>();
        formData.put("apptoken", myAppTokenXml);
        formData.put("adminUserTokenId", adminUserTokenId);
        formData.put("cellPhone", cellPhone);
        UserCredential userCredential = new UserCredential(cellPhone, userTicket);
        formData.put("usercredential", userCredential.toXML());
        formData.put("jsonuser", json);
        return formData;
    }
    /**
     *     @Path("/{applicationtokenid}/{userticket}/{pin}/create_pinverified_user")
     @POST
     @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
     @Produces(MediaType.APPLICATION_XML) public Response createAndLogOnPinUser(@PathParam("applicationtokenid") String applicationtokenid,
     @PathParam("userticket") String userticket,
     @PathParam("pin") String pin,
     @FormParam("apptoken") String appTokenXml,
     @FormParam("adminUserTokenId") String adminUserTokenId,
     @FormParam("usercredential") String userCredentialXml,
     @FormParam("cellPhone") String cellPhone,
     @FormParam("jsonuser") String newUserjson) {

     */

}
