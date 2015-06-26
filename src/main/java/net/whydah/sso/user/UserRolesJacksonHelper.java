package net.whydah.sso.user;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baardl on 22.06.15.
 */
@JacksonXmlRootElement(localName = "applications")
public class UserRolesJacksonHelper {

//
@JacksonXmlProperty(localName = "application")
@JacksonXmlElementWrapper(useWrapping = false)
    private UserRoleDTO[] applications;

    public List<UserRole> getUserRoles() {
        List<UserRole> userRoles = new ArrayList<>(applications.length);
        for (UserRoleDTO userRolesDTO : applications) {
            userRoles.add(userRolesDTO.asUserRole());
        }
        return userRoles;
    }
}
