package net.whydah.sso.session.baseclasses;

import net.whydah.sso.application.mappers.ApplicationMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.basehelpers.JsonPathHelper;
import net.whydah.sso.commands.application.CommandListApplications;
import net.whydah.sso.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;


public class ApplicationModelUtil {
    public static final String maxSessionTimeoutSeconds = "$.security.maxSessionTimeoutSeconds";
    public static final String minDEFCON = "$.security.minDEFCON";
    public static final String minSecurityLevel = "$.security.minSecurityLevel";
    private static final Logger log = LoggerFactory.getLogger(ApplicationModelUtil.class);

    private static final List<Application> applications = new ArrayList<>();

    public static List<Application> getApplicationList() {
        synchronized (applications) {
            return new ArrayList<>(applications);
        }
    }

    public static Application getApplication(String applicationID) {
        synchronized (applications) {
            for (Application application : applications) {
                if (application.getId().equalsIgnoreCase(applicationID)) {
                    return application;
                }
            }
        }
        return null;
    }


    // JsonPath query against Application.json to find value, empty string if not found
    public static String getParameterForApplication(String param, String applicationID) {
        try {
            synchronized (applications) {
                for (Application application : applications) {
                    if (applicationID.equalsIgnoreCase(application.getId())) {
                        log.debug("Found application, looking for ", param);
                        return JsonPathHelper.findJsonPathValue(ApplicationMapper.toJson(application), param);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Attempt to find {} from applicationID: {} failed. returning empty string.", param, applicationID);
        }
        return "";

    }


    public static void updateApplicationList(URI userAdminServiceUri, String myAppTokenId, String userTokenId) {
        if (userAdminServiceUri != null) {
            String applicationsJson = new net.whydah.sso.commands.application.CommandListApplications(userAdminServiceUri, myAppTokenId).execute();
            log.trace("AppLications returned:" + LoggerUtil.first50(applicationsJson));
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    try {
                        List<Application> newapplications = ApplicationMapper.fromJsonList(applicationsJson);
                        updateApplications(newapplications);
                    } catch (Exception e) {
                        log.warn("Unable to update applicationList");
                    }
                }
            }
        }
    }

    public static void updateApplicationList(URI userAdminServiceUri, String myAppTokenId) {
        if (userAdminServiceUri != null) {
            String applicationsJson = new CommandListApplications(userAdminServiceUri, myAppTokenId).execute();
            log.trace("AppLications returned:" + LoggerUtil.first50(applicationsJson));
            if (applicationsJson != null) {
                if (applicationsJson.length() > 20) {
                    try {
                        List<Application> newapplications = ApplicationMapper.fromJsonList(applicationsJson);
                        updateApplications(newapplications);
                    } catch (Exception e) {
                        log.warn("Unable to update applicationList");
                    }
                }
            }
        }
    }

    private static void updateApplications(List<Application> newapplications) {
        synchronized (applications) {
            applications.clear();
            applications.addAll(newapplications);
        }
    }

    public static boolean shouldUpdate() {
        int max = 100;
        return (5 >= ((int) (ThreadLocalRandom.current().nextDouble() * max)));  // update on 5 percent of requests
    }

    public static boolean shouldUpdate(int percentage) {
        int max = 100;
        return (percentage >= ((int) (ThreadLocalRandom.current().nextDouble() * max)));  // update on 5 percent of requests
    }

}
