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

public class CommandVerifyEmailByToken extends HystrixCommand<Response> {
    private static final Logger log = getLogger(CommandVerifyEmailByToken.class);
    private final String personRef;
    private final String userTokenId;
    private URI crmServiceUri;
    private String appTokenXml;
    private String emailaddress;
    private String token;

    public CommandVerifyEmailByToken(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String emailaddress, String token) {
        super(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup"));
        this.crmServiceUri = crmServiceUri;
        this.appTokenXml = appTokenXml;
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.emailaddress = emailaddress;
        this.token = token;
        if (this.crmServiceUri == null || this.appTokenXml == null || this.emailaddress == null || this.token == null) {
            log.error("{} initialized with null-values - will fail", CommandVerifyEmailByToken.class.getSimpleName());
        }
    }

    @Override
    protected Response run() {
        log.trace("{} - appTokenXml={}, ", CommandVerifyEmailByToken.class.getSimpleName(), appTokenXml);

        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);

        Client crmClient = ClientBuilder.newClient();
        WebTarget sts = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);

        WebTarget webResource = sts.path(myAppTokenId).path("verify").path("email");
        Form formData = new Form();
        formData.param("appTokenXml", appTokenXml);
        formData.param("emailaddress", emailaddress);
        formData.param("token", token);

        return webResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE),Response.class);
    }

    @Override
    protected Response getFallback() {
        log.warn("{} - fallback - crmUri={}", CommandVerifyEmailByToken.class.getSimpleName(), crmServiceUri);
        return null;
    }
}
