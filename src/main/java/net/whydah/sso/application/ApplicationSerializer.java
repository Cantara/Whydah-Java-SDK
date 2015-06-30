package net.whydah.sso.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Responsible for serializing Application to/from json and xml.
 *
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-06-30
 */
public class ApplicationSerializer {
    private static final Logger log = LoggerFactory.getLogger(ApplicationSerializer.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Application application) {
        String applicationCreatedJson = null;
        try {
            applicationCreatedJson = mapper.writeValueAsString(application);
        } catch (IOException e) {
            log.warn("Could not convert application to Json {}", application.toString());
        }
        return applicationCreatedJson;
    }

    //list of application data, no wrapping element "applications". Need to decide.
    public static String toJson(List<Application> applications) {
        String applicationCreatedJson = null;
        try {
            applicationCreatedJson = mapper.writeValueAsString(applications);
        } catch (IOException e) {
            log.warn("Could not convert applications to Json.");
        }
        return applicationCreatedJson;
    }


    //Should probably use JsonPath
    public static Application fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Application application = mapper.readValue(json, Application.class);
            return application;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error mapping json for " + json, e);
        }
    }
}
