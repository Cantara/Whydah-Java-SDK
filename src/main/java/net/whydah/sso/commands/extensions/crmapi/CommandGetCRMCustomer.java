package net.whydah.sso.commands.extensions.crmapi;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.OK;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandGetCRMCustomer extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandGetCRMCustomer.class);
    private URI crmServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String personRef;


    public CommandGetCRMCustomer(URI crmServiceUri, String myAppTokenId, String adminUserTokenId, String personRef) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.crmServiceUri = crmServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.personRef = personRef;
//        if (crmServiceUri == null || myAppTokenId == null || adminUserTokenId == null || personRef == null) {
        if (crmServiceUri == null || personRef == null) {
            log.error("CommandGetCRMCustomer initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandGetCRMCustomer - myAppTokenId={}", myAppTokenId);

//        Client crmClient = ClientBuilder.newClient();
        Client crmClient;
        if (!SSLTool.isCertificateCheckDisabled()) {
            crmClient = ClientBuilder.newClient();
        } else {
            crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
        }

//        WebTarget updateUser = crmClient.target(crmServiceUri).path(myAppTokenId).path(adminUserTokenId).path("customer").path(personRef);
        WebTarget getCustomer = crmClient.target(crmServiceUri).path("customer").path(personRef);
        Response response = getCustomer.request().get();
        log.debug("CommandGetCRMCustomer - Returning CRM customer {}", response.getStatus());
        if (response.getStatus() == OK.getStatusCode()) {
            String responseJson = response.readEntity(String.class);
            log.debug("CommandGetCRMCustomer - Returning CRM customer {}", responseJson);
            return responseJson;
        }
        String responseJson = response.readEntity(String.class);
        log.debug("CommandGetCRMCustomer - Returning CRM customer {}", responseJson);
        return null;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandGetCRMCustomer - fallback - uri={}", crmServiceUri.toString());
        return null;
    }


}
