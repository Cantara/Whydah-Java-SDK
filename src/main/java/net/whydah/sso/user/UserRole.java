package net.whydah.sso.user;

/**
 * Created by baardl on 19.06.15.
 */
public class UserRole {

    private final String userName;
    private final String applicationId;
    private final String orgName;
    private final String roleName;
    private String roleValue;

    public UserRole(String userName, String applicationId, String orgName, String roleName) {
        this.userName = userName;
        this.applicationId = applicationId;
        this.orgName = orgName;
        this.roleName = roleName;
    }

    public UserRole(String userName, String applicationId, String orgName, String roleName, String roleValue) {
        this.userName = userName;
        this.applicationId = applicationId;
        this.orgName = orgName;
        this.roleName = roleName;
        this.roleValue = roleValue;
    }

    public String getUserName() {
        return userName;
    }

    public String getRoleValue() {
        return roleValue;
    }

    public void setRoleValue(String roleValue) {
        this.roleValue = roleValue;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String toXML() {
        return "<application>" +
                "            <uid>" + getUserName() + "</uid>\n" +
                "            <appId>" + getApplicationId() + "</appId>\n" +
//                "            <applicationName>" + getApplicationName() + "</applicationName>\n" +
                "            <orgName>" + getOrgName() + "</orgName>\n" +
                "            <roleName>" + getRoleName() + "</roleName>\n" +
                "            <roleValue>" + getRoleValue() + "</roleValue>\n" +
                "        </application>";
    }
}
