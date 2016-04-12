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

public class CommandSendEmailVerificationToken extends HystrixCommand<Boolean> {
    private static final Logger log = getLogger(CommandSendEmailVerificationToken.class);
    private final String personRef;
    private final String userTokenId;
    private final String linkurl;
    private URI crmServiceUri;
    private String appTokenXml;
    private String emailaddress;

    public CommandSendEmailVerificationToken(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String emailaddress, String linkurl) {
        super(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup"));
        this.crmServiceUri = crmServiceUri;
        this.appTokenXml = appTokenXml;
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.emailaddress = emailaddress;
        this.linkurl = linkurl;
        if (this.crmServiceUri == null || this.appTokenXml == null || this.emailaddress == null || this.linkurl == null) {
            log.error("{} initialized with null-values - will fail", CommandSendEmailVerificationToken.class.getSimpleName());
        }
    }

    @Override
    protected Boolean run() {
        log.trace("{} - appTokenXml={}, ", CommandSendEmailVerificationToken.class.getSimpleName(), appTokenXml);

        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);

        Client crmClient = ClientBuilder.newClient();
        WebTarget sts = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);

        WebTarget webResource = sts.path("verify").path("email");
        Form formData = new Form();
        formData.param("appTokenXml", appTokenXml);
        formData.param("email", emailaddress);
        formData.param("linkurl", linkurl);

        Response response = webResource.request().post(Entity.entity(formData, MediaType.APPLICATION_FORM_URLENCODED_TYPE), Response.class);
        if (response.getStatus() == 200) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    protected Boolean getFallback() {
        log.warn("{} - fallback - crmUri={}", CommandSendEmailVerificationToken.class.getSimpleName(), crmServiceUri);
        return Boolean.FALSE;
    }
}
