package net.whydah.sso.commands.userauth;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.whydah.sso.user.UserCredential;
import net.whydah.sso.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Created by totto on 12/2/14.
 */
public class CommandLogonUserByUserCredential  extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandLogonUserByUserCredential.class);

    private URI tokenServiceUri;
    private String myAppTokenId;
    private String myAppTokenXml;
    private UserCredential userCredential;
    private String userticket;


    public CommandLogonUserByUserCredential(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential) {
        super(HystrixCommandGroupKey.Factory.asKey("SSOAUserAuthGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.myAppTokenId=myAppTokenId;
        this.myAppTokenXml=myAppTokenXml;
        this.userCredential=userCredential;
        this.userticket= UUID.randomUUID().toString();  // Create new UUID ticket if not provided
        if (tokenServiceUri==null || myAppTokenId==null || myAppTokenXml==null || userCredential==null || userCredential==null){
            log.error("CommandLogonUserByUserCredential initialized with null-values - will fail");
            throw new IllegalArgumentException("Missing parameters for \n" +
                    "\ttokenServiceUri ["+ tokenServiceUri + "], \n" +
                    "\tmyAppTokenId ["+ myAppTokenId + "], \n" +
                    "\tmyAppTokenXml["+myAppTokenXml + "] or \n\tuserCredential["+userCredential + "]");
        }
    }


    public CommandLogonUserByUserCredential(URI tokenServiceUri,String myAppTokenId,String myAppTokenXml ,UserCredential userCredential,String userticket) {
        this(tokenServiceUri,myAppTokenId,myAppTokenXml,userCredential);
        this.userticket=userticket;
    }

    @Override
    protected String run() {
        log.trace("CommandLogonUserByUserCredential - myAppTokenId={}",myAppTokenId);

        Client tokenServiceClient = ClientBuilder.newClient();
        WebTarget getUserToken = tokenServiceClient.target(tokenServiceUri).path("userauth/" + myAppTokenId + "/" + userticket + "/usertoken");
        Form formData = new Form();
        formData.param("apptoken", myAppTokenXml);
        formData.param("usercredential", userCredential.toXML());
        Response response = postForm(formData,getUserToken);
        if (response.getStatus() == FORBIDDEN.getStatusCode()) {
            log.warn("CommandLogonUserByUserCredential - getUserToken - User authentication failed with status code " + response.getStatus());
            return null;
        }
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.trace("CommandLogonUserByUserCredential - getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        }

        //retry once for other statuses
        log.info("CommandLogonUserByUserCredential - getUserToken - retry once for other statuses");
        response = postForm(formData,getUserToken);
        if (response.getStatus() == OK.getStatusCode()) {
            String responseXML = response.readEntity(String.class);
            log.trace("CommandLogonUserByUserCredential - getUserToken - Log on OK with response {}", responseXML);
            return responseXML;
        } else if (response.getStatus() == NOT_FOUND.getStatusCode()) {
            log.error(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - getUserToken - Auth failed - Problems connecting with TokenService", getUserToken, response));
        } else {
            log.info(ExceptionUtil.printableUrlErrorMessage("CommandLogonUserByUserCredential - getUserToken - User authentication failed", getUserToken, response));
        }
        return null;

    }

    private Response postForm(Form formData, WebTarget logonResource) {
        return logonResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
    }

    @Override
    protected String getFallback() {
        log.warn("CommandLogonUserByUserCredential - getFallback - retiurning null  ");
        return null;
    }



}