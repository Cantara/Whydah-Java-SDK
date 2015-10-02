package net.whydah.sso.user;

import net.whydah.sso.user.types.UserRole;

public class UserRoleJsonHelper {


    public static UserRole fromXml(String roleXml) {

        String id = UserXpathHelper.findValue(roleXml, "/application/id");
        String userId = UserXpathHelper.findValue(roleXml, "/application/uid");
        String appId = UserXpathHelper.findValue(roleXml, "/application/appId");
        String appName = UserXpathHelper.findValue(roleXml, "/application/applicationName");
        String orgName = UserXpathHelper.findValue(roleXml, "/application/orgName");
        String roleName = UserXpathHelper.findValue(roleXml, "/application/roleName");
        String roleValue = UserXpathHelper.findValue(roleXml, "/application/roleValue");
        UserRole userRole = new UserRole(null, appId, orgName, roleName, roleValue);
        userRole.setId(id);
        userRole.setUserId(userId);
        return userRole;
    }


    public static String toJson(UserRole userrole) {
        String json = "{";
        if (isNotEmpty(userrole.getId())) {
            json = json + "\"roleId\":\"" + userrole.getId() + "\",";
        }
        if (isNotEmpty(userrole.getUserId())) {
            json = json + "\"uid\":\"" + userrole.getUserId() + "\",";
        }

        json = json + "\"applicationId\":\"" + userrole.getApplicationId() + "\"," +
                "\"applicationName\":\"" + null + "\"," +
                "\"applicationRoleName\":\"" + userrole.getRoleName() + "\"," +
                "\"applicationRoleValue\":\"" + userrole.getRoleValue() + "\"," +
                "\"organizationName\":\"" + userrole.getOrgName() + "\"}";

        return json;

    }


    private static boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }


}
