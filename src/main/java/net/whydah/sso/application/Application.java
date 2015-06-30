package net.whydah.sso.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * DTO for Application.
 *
 * Erik's notes:
 *
 * applications
 *      application, applicationID, name
 *          relations   (organisation)
 *              relation1, id, name
 *                  properties / hashmap (mappet tidligere til roller)
 *              relation2, id, name
 *                  properties / hashmap
 *
* @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-06-30
*/
public class Application implements Serializable {
    private static final long serialVersionUID = -3784954322543961744L;
    private String id;
    private String name;
    private String description;
    private String applicationUrl;  // /sso/welcome
    private String logoUrl;         // /sso/welcome

    //private Map security;

    private String userTokenFilter; // default true, false will send userTokens with roles for all applications
    private String securityLevel;    //Minimum UserToken security level allowed to use application

    private String secret;

    //list roleNames
    private List<Role> roles;   //availableRoleNames
    private List<String> organizationNames;  //availableOrganizationNames

    //defaults Map defaults
    private String defaultRoleName;     //roleName or roleId here?
    private String defaultOrganizationName;


    private Application() {
    }

    public Application(String id, String name) {
        this.id = id;
        this.name = name;
        this.roles = new ArrayList<>();
        this.organizationNames = new ArrayList<>();
        this.userTokenFilter = "true";
    }

    public void addRole(Role role) {
        roles.add(role);
    }
    public void addOrganizationName(String organizationName) {
        organizationNames.add(organizationName);
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setApplicationUrl(String applicationUrl) {
        this.applicationUrl = applicationUrl;
    }
    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }
    public void setUserTokenFilter(String userTokenFilter) {
        this.userTokenFilter = userTokenFilter;
    }
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }
    public void setSecret(String secret) {
        this.secret = secret;
    }
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    public void setOrganizationNames(List<String> organizationNames) {
        this.organizationNames = organizationNames;
    }
    public void setDefaultRoleName(String defaultRoleName) {
        this.defaultRoleName = defaultRoleName;
    }
    public void setDefaultOrganizationName(String defaultOrganizationName) {
        this.defaultOrganizationName = defaultOrganizationName;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getApplicationUrl() {
        return applicationUrl;
    }
    public String getLogoUrl() {
        return logoUrl;
    }
    public String getUserTokenFilter() {
        return userTokenFilter;
    }
    public String getSecurityLevel() {
        return securityLevel;
    }
    public String getSecret() {
        return secret;
    }
    public List<Role> getRoles() {
        return roles;
    }
    public List<String> getOrganizationNames() {
        return organizationNames;
    }
    public String getDefaultRoleName() {
        return defaultRoleName;
    }
    public String getDefaultOrganizationName() {
        return defaultOrganizationName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Application that = (Application) o;

        if (defaultOrganizationName != null ? !defaultOrganizationName.equals(that.defaultOrganizationName) : that.defaultOrganizationName != null)
            return false;
        if (defaultRoleName != null ? !defaultRoleName.equals(that.defaultRoleName) : that.defaultRoleName != null)
            return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (!id.equals(that.id)) return false;
        if (!name.equals(that.name)) return false;
        if (organizationNames != null ? !organizationNames.equals(that.organizationNames) : that.organizationNames != null)
            return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;
        if (secret != null ? !secret.equals(that.secret) : that.secret != null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (secret != null ? secret.hashCode() : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (organizationNames != null ? organizationNames.hashCode() : 0);
        result = 31 * result + (defaultRoleName != null ? defaultRoleName.hashCode() : 0);
        result = 31 * result + (defaultOrganizationName != null ? defaultOrganizationName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        String availableOrgNamesString = "";
        if (this.organizationNames != null) {
            availableOrgNamesString = String.join(",", this.organizationNames);
        }

        String roleNamesString = null;
        if (roles != null) {
            StringBuilder strb = new StringBuilder();
            for (Role role : roles) {
                strb.append(role.getName()).append(",");
            }
            roleNamesString = strb.toString();
        }


        return "Application{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", secret='" + secret + '\'' +
                ", description='" + description + '\'' +
                ", roles=" + roleNamesString +
                ", defaultRoleName='" + defaultRoleName + '\'' +
                ", organizationNames=" + availableOrgNamesString +
                ", defaultOrganizationName='" + defaultOrganizationName + '\'' +
                '}';
    }

     /*
    public List<String> getAvailableRoleNames() {
        List<String> names = new ArrayList<>(roles.size());
        for (Role role : roles) {
            names.add(role.getName());
        }
        if (names.isEmpty()) {
            return null;
        }
        return names;
    }
    */


    /*
    public String toXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
                " <application>\n" +
                "   <applicationid>" + id + "</applicationid>\n" +
                "   <applicationname>" + name + "</applicationname>\n" +
                "   <defaultrolename>" + defaultRoleName + "</defaultrolename>\n" +
                "   <defaultorganizationname>" + defaultOrganizationName + "</defaultorganizationname>\n" +
                "  " + buildAvailableOrgAsXml() + "\n" +
                "  " + buildAvailableRoleAsXml() + "\n" +
                " </application>\n";
    }

    private String buildAvailableOrgAsXml() {
        if(organizationNames == null || organizationNames.size() == 0) {
            return "<organizationsnames/>";
        }else {
            StringBuilder availableXml = new StringBuilder("<organizationsnames>\n");
            for (String availableOrgName : organizationNames) {
                availableXml.append("<orgName>").append(availableOrgName).append("</orgName>").append("\n");
            }
            availableXml.append("</organizationsnames>");
            return availableXml.toString();
        }
    }

    private String buildAvailableRoleAsXml() {
        if (getAvailableRoleNames() == null || getAvailableRoleNames().size() == 0) {
            return "<rolenames/>";
        } else {
            StringBuilder availableXml = new StringBuilder("<rolenames>\n");
            for (String roleName : getAvailableRoleNames()) {
                availableXml.append("<roleName>").append(roleName).append("</roleName>").append("\n");
            }
            availableXml.append("</rolenames>");
            return availableXml.toString();
        }
    }
    */


    /*
    public void addAvailableOrgName(String availableOrgName) {
        if (organizationNames == null) {
            organizationNames = new ArrayList();
        }
        if (availableOrgName != null) {
            this.organizationNames.add(availableOrgName);
        }
    }
    public void removeAvailableOrgName(String availableOrgName) {
        if (organizationNames != null && availableOrgName != null) {
            organizationNames.remove(availableOrgName);
        }
    }
    public void addAvailableRoleName(String availableRoleName) {
        if (availableRoleNames == null) {
            availableRoleNames = new ArrayList();
        }
        if (availableRoleName != null) {
            this.availableRoleNames.add(availableRoleName);
        }
    }

    public void removeAvailableRoleName(String availableRoleName) {
        if (availableRoleNames != null && availableRoleName != null) {
            availableRoleNames.remove(availableRoleName);
        }
    }
    */
}
