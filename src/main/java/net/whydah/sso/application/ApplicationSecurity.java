package net.whydah.sso.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-07-01
 */
public class ApplicationSecurity implements Serializable {
    private static final long serialVersionUID = -1064310396610968939L;

    /**
     * The minimum security level for user and application tokens allowed to use application.
     * Default 0 - no minimum security level.
     */
    private String minSecurityLevel;
    /**
     * The minimum DEFCON level an application accepts to have session under.
     */
    private String minDEFCON;
    /**
     * max length of a application and user session for a given application
     */
    private String maxSessionTimout;
    /**
     * The ip addresses/ip ranges we accept an application to send requests from to this application.
     */
    private List<String> allowedIpAddresses;

    //authentication info
    private String secret;    // TODO extend


    public ApplicationSecurity() {
        this.minSecurityLevel = "0";
        this.allowedIpAddresses = new ArrayList<>();
    }

    public void setMinSecurityLevel(String minSecurityLevel) {
        this.minSecurityLevel = minSecurityLevel;
    }
    public void setMinDEFCON(String minDEFCON) {
        this.minDEFCON = minDEFCON;
    }
    public void setMaxSessionTimout(String maxSessionTimout) {
        this.maxSessionTimout = maxSessionTimout;
    }
    public void setAllowedIpAddresses(List<String> allowedIpAddresses) {
        this.allowedIpAddresses = allowedIpAddresses;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getMinSecurityLevel() {
        return minSecurityLevel;
    }
    public String getMinDEFCON() {
        return minDEFCON;
    }
    public String getMaxSessionTimout() {
        return maxSessionTimout;
    }
    public List<String> getAllowedIpAddresses() {
        return allowedIpAddresses;
    }
    public String getSecret() {
        return secret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationSecurity that = (ApplicationSecurity) o;

        if (allowedIpAddresses != null ? !allowedIpAddresses.equals(that.allowedIpAddresses) : that.allowedIpAddresses != null)
            return false;
        if (minDEFCON != null ? !minDEFCON.equals(that.minDEFCON) : that.minDEFCON != null)
            return false;
        if (secret != null ? !secret.equals(that.secret) : that.secret != null) return false;
        if (minSecurityLevel != null ? !minSecurityLevel.equals(that.minSecurityLevel) : that.minSecurityLevel != null)
            return false;
        if (maxSessionTimout != null ? !maxSessionTimout.equals(that.maxSessionTimout) : that.maxSessionTimout != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = minSecurityLevel != null ? minSecurityLevel.hashCode() : 0;
        result = 31 * result + (minDEFCON != null ? minDEFCON.hashCode() : 0);
        result = 31 * result + (maxSessionTimout != null ? maxSessionTimout.hashCode() : 0);
        result = 31 * result + (allowedIpAddresses != null ? allowedIpAddresses.hashCode() : 0);
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        return result;
    }
}
