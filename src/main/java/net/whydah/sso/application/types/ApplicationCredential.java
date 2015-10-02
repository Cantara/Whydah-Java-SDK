package net.whydah.sso.application.types;

/**
 * Created by totto on 12/2/14.
 */
public class ApplicationCredential {
    private final String applicationID;
    private final String applicationSecret;

    public ApplicationCredential(String applicationID, String applicationSecret) {
        this.applicationID = applicationID;
        this.applicationSecret = applicationSecret;
    }

    public String getApplicationID() {
        return applicationID;
    }
    public String getApplicationSecret() {
        return applicationSecret;
    }
}
