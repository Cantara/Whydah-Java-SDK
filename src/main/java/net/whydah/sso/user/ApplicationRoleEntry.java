package net.whydah.sso.user;

import java.io.Serializable;

public class ApplicationRoleEntry implements Serializable {
    private static final long serialVersionUID = -1557134588400171584L;
    private String applicationId;
    private String applicationName;
    private String organizationName;
    private String roleName;
    private String roleValue;

    public ApplicationRoleEntry() {
    }

    public ApplicationRoleEntry(String applicationId, String applicationName, String organizationName, String roleName, String roleValue) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.organizationName = organizationName;
        this.roleName = roleName;
        this.roleValue = roleValue;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleValue() {
        return roleValue;
    }

    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue;
    }

    @Override
    public String toString() {
        return "ApplicationRoleEntry{" +
                "applicationId='" + applicationId + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", roleName='" + roleName + '\'' +
                ", roleValue='" + roleValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ApplicationRoleEntry roleEntry = (ApplicationRoleEntry) o;

        if (!applicationId.equals(roleEntry.applicationId)) return false;
        if (applicationName != null ? !applicationName.equals(roleEntry.applicationName) : roleEntry.applicationName != null)
            return false;
        if (!organizationName.equals(roleEntry.organizationName)) return false;
        if (!roleName.equals(roleEntry.roleName)) return false;
        if (!roleValue.equals(roleEntry.roleValue)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = applicationId.hashCode();
        result = 31 * result + (applicationName != null ? applicationName.hashCode() : 0);
        result = 31 * result + organizationName.hashCode();
        result = 31 * result + roleName.hashCode();
        result = 31 * result + roleValue.hashCode();
        return result;
    }
}
