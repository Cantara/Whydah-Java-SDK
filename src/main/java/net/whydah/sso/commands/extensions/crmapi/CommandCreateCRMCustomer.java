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

import static javax.ws.rs.core.Response.Status.CREATED;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandCreateCRMCustomer extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandCreateCRMCustomer.class);
    private URI crmServiceUri;
    private String myAppTokenId;
    private String adminUserTokenId;
    private String personRef;
    private String customerJson;


    public CommandCreateCRMCustomer(URI crmServiceUri, String myAppTokenId, String adminUserTokenId, String personRef, String customerJson) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.crmServiceUri = crmServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.adminUserTokenId = adminUserTokenId;
        this.personRef = personRef;
        this.customerJson = customerJson;

        if (crmServiceUri == null || customerJson == null) {
            log.error("CommandCreateCRMCustomer initialized with null-values - will fail");
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

        WebTarget createCustomer = crmClient.target(crmServiceUri).path("customer");

        if (personRef != null) {
            createCustomer = createCustomer.path(personRef);
        }

        Response response = createCustomer.request().post(Entity.entity(customerJson, MediaType.APPLICATION_JSON_TYPE));

        log.debug("CommandCreateCRMCustomer - Returning status {}", response.getStatus());
        if (response.getStatus() == CREATED.getStatusCode()) {
            String locationHeader = response.getHeaderString("location");
            String crmCustomerId = locationHeader.substring(locationHeader.lastIndexOf("/")+1);
            log.debug("CommandCreateCRMCustomer - Returning CRM Id {}", crmCustomerId);
            return crmCustomerId;
        }
        String responseJson = response.readEntity(String.class);
        log.debug("CommandCreateCRMCustomer - Returning response '{}', status {}", responseJson, response.getStatus());
        return null;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandCreateCRMCustomer - fallback - uri={}", crmServiceUri.toString());
        return null;
    }


}
