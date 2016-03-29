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
import java.io.IOException;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

public class CommandGetCRMCustomerProfileImage extends HystrixCommand<byte[]> {
    private static final Logger log = getLogger(CommandGetCRMCustomerProfileImage.class);
    private URI crmServiceUri;
    private String myAppTokenId;
    private String userTokenId;
    private String personRef;


    public CommandGetCRMCustomerProfileImage(URI crmServiceUri, String myAppTokenId, String userTokenId, String personRef) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CrmExtensionGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.crmServiceUri = crmServiceUri;
        this.myAppTokenId = myAppTokenId;
        this.userTokenId = userTokenId;
        this.personRef = personRef;

        if (crmServiceUri == null || personRef == null) {
            log.error("CommandGetCRMCustomerProfileImage initialized with null-values - will fail");
        }

    }

    @Override
    protected byte[] run() {
        log.trace("CommandGetCRMCustomerProfileImage - myAppTokenId={}", myAppTokenId);

        Client crmClient;
        if (!SSLTool.isCertificateCheckDisabled()) {
            crmClient = ClientBuilder.newClient();
        } else {
            crmClient = ClientBuilder.newBuilder().sslContext(SSLTool.sc).hostnameVerifier((s1, s2) -> true).build();
        }

        WebTarget getProfileImage = crmClient.target(crmServiceUri).path(myAppTokenId).path(userTokenId).path("customer").path(personRef).path("image");


        try {
            log.trace("Fetching image from path {}", getProfileImage.getUri().toURL());
            Response response = getProfileImage.request().get();

            //String contentType = response.getHeaderString("Content-Type");
            byte[] imagedata = response.readEntity(byte[].class);

            log.trace("CommandGetCRMCustomerProfileImage - returned image with length {}", imagedata == null ? 0:imagedata.length);
            return imagedata;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected byte[] getFallback() {
        log.warn("CommandGetCRMCustomerProfileImage - fallback - uri={}", crmServiceUri.toString());
        return null;
    }


}
