package net.whydah.sso.commands.extensions.crmapi;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import net.whydah.sso.user.helpers.UserTokenXpathHelper;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandSendPhoneVerificationPin extends HystrixCommand<Boolean> {
    private static final Logger log = getLogger(CommandSendPhoneVerificationPin.class);
    private final String personRef;
    private final String userTokenId;
    private URI crmServiceUri;
    private String appTokenXml;
    private String phoneNo;

    public CommandSendPhoneVerificationPin(URI crmServiceUri, String appTokenXml, String userTokenId, String personRef, String phoneNo) {
        super(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup"));
        this.crmServiceUri = crmServiceUri;
        this.appTokenXml = appTokenXml;
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.phoneNo = phoneNo;
        if (this.crmServiceUri == null || this.appTokenXml == null || this.phoneNo == null) {
            log.error("{} initialized with null-values - will fail", CommandSendPhoneVerificationPin.class.getSimpleName());
        }
    }

    @Override
    protected Boolean run() {
        log.trace("{} - appTokenXml={}, ", CommandSendPhoneVerificationPin.class.getSimpleName(), appTokenXml);

        String myAppTokenId = UserTokenXpathHelper.getAppTokenIdFromAppToken(appTokenXml);

        Client crmClient = ClientBuilder.newClient();
        WebTarget sts = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef);

        WebTarget webResource = sts.path(myAppTokenId).path("verify").path("phone").queryParam("phoneNo", phoneNo);

        Response response = webResource.request().get(Response.class);
        if (response.getStatus() == 200) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    protected Boolean getFallback() {
        log.warn("{} - fallback - crmUri={}", CommandSendPhoneVerificationPin.class.getSimpleName(), crmServiceUri);
        return Boolean.FALSE;
    }
}
