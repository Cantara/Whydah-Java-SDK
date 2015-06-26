package net.whydah.sso.user;

/**
 * Created by baardl on 19.06.15.
 */
public class UserRole {

    private String id = null;
    private String userId = null;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String toXML() {
        return "<application>" +
                "            <uid>" + getUserName() + "</uid>\n" +
                "            <appId>" + getApplicationId() + "</appId>\n" +
//                "            <applicationName>" + getApplicationName() + "</applicationName>\n" +
                "            <orgName>" + getOrgName() + "</orgName>\n" +
                "            <roleName>" + getRoleName() + "</roleName>\n" +
                "            <roleValue>" + getRoleValue() + "</roleValue>\n" +
                "</application>";
    }

    public static UserRole fromXml(String roleXml) {

        String id = UserXpathHelper.findValue(roleXml,"/application/id");
        String userId = UserXpathHelper.findValue(roleXml,"/application/uid");
        String appId = UserXpathHelper.findValue(roleXml,"/application/appId");
        String appName = UserXpathHelper.findValue(roleXml,"/application/applicationName");
        String orgName = UserXpathHelper.findValue(roleXml,"/application/orgName");
        String roleName = UserXpathHelper.findValue(roleXml,"/application/roleName");
        String roleValue = UserXpathHelper.findValue(roleXml,"/application/roleValue");
        UserRole userRole = new UserRole(null, appId,orgName,roleName,roleValue);
        userRole.setId(id);
        userRole.setUserId(userId);
        return userRole;
    }

}
