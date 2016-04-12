package net.whydah.sso.commands.userauth;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandSendSmsPin extends HystrixCommand<Response> {
    private static final Logger log = getLogger(CommandSendSmsPin.class);
    private URI tokenServiceUri;
    private String appTokenXml;
    private String phoneNo;
    private String pin;

    public CommandSendSmsPin(URI tokenServiceUri, String appTokenXml, String phoneNo, String pin) {
        super(HystrixCommandGroupKey.Factory.asKey("SSOAUserAuthGroup"));
        this.tokenServiceUri = tokenServiceUri;
        this.appTokenXml = appTokenXml;
        this.phoneNo = phoneNo;
        this.pin = pin;
        if (this.tokenServiceUri == null || this.appTokenXml == null || this.phoneNo == null || this.pin == null) {
            log.error("{} initialized with null-values - will fail", CommandSendSmsPin.class.getSimpleName());
        }
    }

    @Override
    protected Response run() {
        log.trace("{} - appTokenXml={}, ", CommandSendSmsPin.class.getSimpleName(), appTokenXml);

        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);

        Client client = ClientBuilder.newClient();
        WebTarget sts = client.target(tokenServiceUri);

        WebTarget webResource = sts.path(myAppTokenId).path("send_sms_pin");
        Form formData = new Form();
        formData.param("phoneNo", phoneNo);
        formData.param("smsPin", pin);

        return webResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
    }

    @Override
    protected Response getFallback() {
        log.warn("{} - fallback - uibUri={}", CommandSendSmsPin.class.getSimpleName(), tokenServiceUri);
        return null;
    }
}