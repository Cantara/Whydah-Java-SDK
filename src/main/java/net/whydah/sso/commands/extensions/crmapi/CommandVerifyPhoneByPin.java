package net.whydah.sso.commands.extensions.crmapi;

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

public class CommandVerifyPhoneByPin extends HystrixCommand<Response> {
    private static final Logger log = getLogger(CommandVerifyPhoneByPin.class);
    private final String personRef;
    private final String userTokenId;
    private URI crmServiceUri;
    private String appTokenXml;
    private String phoneNo;
    private String pin;

    public CommandVerifyPhoneByPin(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String phoneNo, String pin) {
        super(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup"));
        this.crmServiceUri = crmServiceUri;
        this.appTokenXml = appTokenXml;
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.phoneNo = phoneNo;
        this.pin = pin;
        if (this.crmServiceUri == null || this.appTokenXml == null || this.phoneNo == null || this.pin == null) {
            log.error("{} initialized with null-values - will fail", CommandVerifyPhoneByPin.class.getSimpleName());
        }
    }

    @Override
    protected Response run() {
        log.trace("{} - appTokenXml={}, ", CommandVerifyPhoneByPin.class.getSimpleName(), appTokenXml);

        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);

        Client crmClient = ClientBuilder.newClient();
        WebTarget sts = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);

        WebTarget webResource = sts.path(myAppTokenId).path("verify").path("phone");
        Form formData = new Form();
        formData.param("appTokenXml", appTokenXml);
        formData.param("phoneNo", phoneNo);
        formData.param("smsPin", pin);

        return webResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
    }

    @Override
    protected Response getFallback() {
        log.warn("{} - fallback - crmUri={}", CommandVerifyPhoneByPin.class.getSimpleName(), crmServiceUri);
        return null;
    }
}
