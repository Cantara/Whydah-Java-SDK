package net.whydah.sso.user;

/**
 * Created by baardl on 23.06.15.
 */
public class UserRoleDTO {

        private String id;
        private String uid;
        private String appId;
    private String applicationName;
        private String orgName;
        private String roleName;
        private String roleValue;

        //getters, setters, toString
        public UserRole asUserRole(){
            UserRole userRole = new UserRole(uid, appId, orgName, roleName, roleValue);
            userRole.setId(id);
            return userRole;
        }

        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
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

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
}
