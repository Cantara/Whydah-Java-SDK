package net.whydah.sso.commands.extensions.crmapi;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import net.whydah.sso.util.SSLTool;
import org.slf4j.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.ACCEPTED;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandUpdateCRMCustomer extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandCreateCRMCustomer.class);
    private URI crmServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String personRef;
    private String customerJson;


    public CommandUpdateCRMCustomer(URI crmServiceUri, String myAppTokenId, String adminUserTokenId, String personRef, String customerJson) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.crmServiceUri = crmServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.personRef = personRef;
        this.customerJson = customerJson;

        if (crmServiceUri == null || personRef == null || customerJson == null) {
            log.error("CommandUpdateCRMCustomer initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandUpdateCRMCustomer - myAppTokenId={}", myAppTokenId);

        Client crmClient;
        if (!SSLTool.isCertificateCheckDisabled()) {
            crmClient = ClientBuilder.newClient();
        } else {
            crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
        }

        WebTarget createCustomer = crmClient.target(crmServiceUri).path("customer").path(personRef);

        Response response = createCustomer.request().put(Entity.entity(customerJson, MediaType.APPLICATION_JSON_TYPE));

        log.debug("CommandUpdateCRMCustomer - Returning CRM location {}", response.getStatus());
        if (response.getStatus() == ACCEPTED.getStatusCode()) {
            String locationHeader = response.getHeaderString("location");
            log.debug("CommandUpdateCRMCustomer - Returning CRM location {}", locationHeader);
            return locationHeader;
        }
        String responseJson = response.readEntity(String.class);
        log.debug("CommandUpdateCRMCustomer - Returning CRM location '{}', status {}", responseJson, response.getStatus());
        return null;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandUpdateCRMCustomer - fallback - uri={}", crmServiceUri.toString());
        return null;
    }


}
