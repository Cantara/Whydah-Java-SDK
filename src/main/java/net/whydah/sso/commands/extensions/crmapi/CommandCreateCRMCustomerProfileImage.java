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
import javax.ws.rs.core.Response;
import java.net.URI;

import static javax.ws.rs.core.Response.Status.CREATED;
import static org.slf4j.LoggerFactory.getLogger;

public class CommandCreateCRMCustomerProfileImage extends HystrixCommand<String> {
    private static final Logger log = getLogger(CommandCreateCRMCustomerProfileImage.class);
    private URI crmServiceUri;
    private String myAppTokenId;
    private String userTokenId;
    private String personRef;
    private String contentType;
    private byte[] imageData;


    public CommandCreateCRMCustomerProfileImage(URI crmServiceUri, String myAppTokenId, String userTokenId, String personRef, String contentType, byte[] imageData) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.crmServiceUri = crmServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.userTokenId = userTokenId;
        this.personRef = personRef;
        this.imageData = imageData;
        this.contentType = contentType;

        if (crmServiceUri == null || personRef == null || imageData == null || contentType == null) {
            log.error("CommandCreateCRMCustomerProfileImage initialized with null-values - will fail");
        }

    }

    @Override
    protected String run() {
        log.trace("CommandCreateCRMCustomerProfileImage - myAppTokenId={}", myAppTokenId);

        Client crmClient;
        if (!SSLTool.isCertificateCheckDisabled()) {
            crmClient = ClientBuilder.newClient();
        } else {
            crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
        }

        WebTarget createProfileImage = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef).path("image");

        Response response = createProfileImage.request().post(Entity.entity(imageData, contentType));

        log.debug("CommandCreateCRMCustomerProfileImage - Returning status {}", response.getStatus());
        if (response.getStatus() == CREATED.getStatusCode()) {
            String locationHeader = response.getHeaderString("location");
            log.debug("CommandCreateCRMCustomerProfileImage - Returning ProfileImage url {}", locationHeader);
            return locationHeader;
        }
        String responseJson = response.readEntity(String.class);
        log.debug("CommandCreateCRMCustomerProfileImage - Returning response '{}', status {}", responseJson, response.getStatus());
        return null;


    }

    @Override
    protected String getFallback() {
        log.warn("CommandCreateCRMCustomerProfileImage - fallback - uri={}", crmServiceUri.toString());
        return null;
    }


}
