package net.whydah.sso.session.baseclasses;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.basehelpers.JsonPathHelper;
import net.whydah.sso.commands.adminapi.application.CommandListApplications;
import net.whydah.sso.util.LoggerUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ApplicationModelUtil {
    public static final String maxSessionTimeoutSeconds = "$.security.maxSessionTimeoutSeconds";
    public static final String minDEFCON = "$.security.minDEFCON";
    public static final String minSecurityLevel = "$.security.minSecurityLevel";
    private static final Logger log = LoggerFactory.getLogger(ApplicationModelUtil.class);
    private static List<Application> applications = new ArrayList<Application>();
   
    public static List<Application> getApplicationList() {
        return applications;
    }

    public static Application getApplication(String applicationID) {
        for (Application application : applications) {
            if (application.getId().equalsIgnoreCase(applicationID)) {
                return application;
            }
        }
        return null;
    }


    // JsonPath query against Application.json to find value, empty string if not found
    public static String getParameterForApplication(String param, String applicationID) {
        if (applications == null) {
            return "";
        }
        try {
            for (Application application : applications) {
                if (applicationID.equalsIgnoreCase(application.getId())) {
                    log.debug("Found application, looking for ", param);
                    return JsonPathHelper.findJsonPathValue(ApplicationMapper.toJson(application), param);
                }
            }
        } catch (Exception e) {
            log.warn("Attempt to find {} from applicationID: {} failed. returning empty string.", param, applicationID);
        }
        return "";

    }


    public static void updateApplicationList(URI userAdminServiceUri, String myAppTokenId, String userTokenId) {
        if (userAdminServiceUri != null){
            String applicationsJson = new net.whydah.sso.commands.adminapi.application.CommandListApplications(userAdminServiceUri, myAppTokenId).execute();
            log.trace("AppLications returned:" + LoggerUtil.first50(applicationsJson));
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    applications = ApplicationMapper.fromJsonList(applicationsJson);
                }
            }
        }
    }
    
    public static void updateApplicationList(URI userAdminServiceUri, String myAppTokenId) {
        if (userAdminServiceUri != null){
            String applicationsJson = new CommandListApplications(userAdminServiceUri, myAppTokenId).execute();
            log.debug("AppLications returned:" + applicationsJson);
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    applications = ApplicationMapper.fromJsonList(applicationsJson);
                }
            }
        }
    }

    public static boolean shouldUpdate() {
        int max = 100;
        return (5 >= ((int) (Math.random() * max)));  // update on 5 percent of requests
    }

    public static boolean shouldUpdate(int percentage) {
        int max = 100;
        return (percentage >= ((int) (Math.random() * max)));  // update on 5 percent of requests
    }

}
