package net.whydah.sso.application;

import java.util.Map;

/**
 * Created by totto on 24.06.15.
 */
public class ApplicationData {
    private Map defaults;
    private String applicationurl;
    private String usertokenfilter;
    private String applicationdescription;
    private String applicationname;
    private String applicationlogo;
    private Map organizations;
    private Map roles;
    private String applicationauditlevel;
    private Map security;
    private String applicationid;

    public Map getDefaults() {
        return defaults;
    }

    public void setDefaults(Map defaults) {
        this.defaults = defaults;
    }

    public String getApplicationurl() {
        return applicationurl;
    }

    public void setApplicationurl(String applicationurl) {
        this.applicationurl = applicationurl;
    }

    public String getUsertokenfilter() {
        return usertokenfilter;
    }

    public void setUsertokenfilter(String usertokenfilter) {
        this.usertokenfilter = usertokenfilter;
    }

    public String getApplicationdescription() {
        return applicationdescription;
    }

    public void setApplicationdescription(String applicationdescription) {
        this.applicationdescription = applicationdescription;
    }

    public String getApplicationname() {
        return applicationname;
    }

    public void setApplicationname(String applicationname) {
        this.applicationname = applicationname;
    }

    public String getApplicationlogo() {
        return applicationlogo;
    }

    public void setApplicationlogo(String applicationlogo) {
        this.applicationlogo = applicationlogo;
    }

    public Map getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Map organizations) {
        this.organizations = organizations;
    }

    public Map getRoles() {
        return roles;
    }

    public void setRoles(Map roles) {
        this.roles = roles;
    }

    public String getApplicationauditlevel() {
        return applicationauditlevel;
    }

    public void setApplicationauditlevel(String applicationauditlevel) {
        this.applicationauditlevel = applicationauditlevel;
    }

    public Map getSecurity() {
        return security;
    }

    public void setSecurity(Map security) {
        this.security = security;
    }

    public String getApplicationid() {
        return applicationid;
    }

    public void setApplicationid(String applicationid) {
        this.applicationid = applicationid;
    }

    @Override
    public String toString() {
        return "ApplicationData [defaults = " + defaults + ", applicationurl = " + applicationurl + ", usertokenfilter = " + usertokenfilter + ", applicationdescription = " + applicationdescription + ", applicationname = " + applicationname + ", applicationlogo = " + applicationlogo + ", organizations = " + organizations + ", roles = " + roles + ", applicationauditlevel = " + applicationauditlevel + ", security = " + security + ", applicationid = " + applicationid + "]";
    }
}