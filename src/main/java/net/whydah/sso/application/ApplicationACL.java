package net.whydah.sso.application;

import java.io.Serializable;
import java.util.List;

public class ApplicationACL implements Serializable {
    private static final long serialVersionUID = -8050935915438584578L;

    /**
     * The applicationId we grant access to
     *
     */
    private String applicationId;
    /**
     * The URI path we grant access to
     */
    private String applicationACLPath;

    /**
     * A list of granted access rights
     *
     * Valid rights:  "READ", "WRITE", "CREATE", "DELETE"
     */
    private List<String> accessRights;


    private ApplicationACL() {
    }

    public ApplicationACL(String applicationId, String applicationACLPath) {
        this.applicationId = applicationId;
        this.applicationACLPath = applicationACLPath;
    }

    public void setId(String applicationId) {
        this.applicationId = applicationId;
    }
    public void setName(String name) {
        this.applicationACLPath = name;
    }

    public String getApplicationId() {
        return applicationId;
    }
    public String getApplicationACLPath() {
        return applicationACLPath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationACL role = (ApplicationACL) o;

        if (!applicationId.equals(role.applicationId)) return false;
        if (!applicationACLPath.equals(role.applicationACLPath)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationId.hashCode();
        result = 31 * result + applicationACLPath.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ApplicationSecurity{" +
                "applicationId='" + applicationId + '\'' +
                ", applicationACLPath='" + applicationACLPath + '\'' +
                ", accessRights='" + accessRights + '\'' +
                '}';
    }

}
