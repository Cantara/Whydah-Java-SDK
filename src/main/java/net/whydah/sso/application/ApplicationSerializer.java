package net.whydah.sso.application;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.application.types.Application;
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

    public static List<Application> fromJsonList(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Application> applications = mapper.readValue(json, new TypeReference<List<Application>>() { });
            return applications;
        } catch (IOException e) {
            throw new IllegalArgumentException("Error mapping json for " + json, e);
        }
    }

    /*
    //from UIB
    public String toXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +
                " <application>\n" +
                "   <applicationid>" + id + "</applicationid>\n" +
                "   <applicationname>" + name + "</applicationname>\n" +
                "   <defaultrolename>" + defaultRoleName + "</defaultrolename>\n" +
                "   <defaultorganizationname>" + defaultOrganizationName + "</defaultorganizationname>\n" +
                "  " + buildAvailableOrgAsXml() + "\n" +
                "  " + buildAvailableRoleAsXml() + "\n" +
                " </application>\n";
    }

    private String buildAvailableOrgAsXml() {
        if(organizationNames == null || organizationNames.size() == 0) {
            return "<organizationsnames/>";
        }else {
            StringBuilder availableXml = new StringBuilder("<organizationsnames>\n");
            for (String availableOrgName : organizationNames) {
                availableXml.append("<orgName>").append(availableOrgName).append("</orgName>").append("\n");
            }
            availableXml.append("</organizationsnames>");
            return availableXml.toString();
        }
    }

    private String buildAvailableRoleAsXml() {
        if (getAvailableRoleNames() == null || getAvailableRoleNames().size() == 0) {
            return "<rolenames/>";
        } else {
            StringBuilder availableXml = new StringBuilder("<rolenames>\n");
            for (String roleName : getAvailableRoleNames()) {
                availableXml.append("<roleName>").append(roleName).append("</roleName>").append("\n");
            }
            availableXml.append("</rolenames>");
            return availableXml.toString();
        }
    }

    //from UAS
    public static Application fromXml(String applicationXml) {
        log.debug("Build application from xml {}", applicationXml);
        Application application = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
            Document doc = documentBuilder.parse(new InputSource(new StringReader(applicationXml)));
            XPath xPath = XPathFactory.newInstance().newXPath();
            String id = (String) xPath.evaluate("/application/applicationid", doc, XPathConstants.STRING);
            String name = (String) xPath.evaluate("/application/applicationname", doc, XPathConstants.STRING);
            String defaultrole = (String) xPath.evaluate("/application/defaultrole", doc, XPathConstants.STRING);
            String defaultorgid = (String) xPath.evaluate("/application/defaultorgid", doc, XPathConstants.STRING);
            NodeList availableOrgIds = (NodeList) xPath.evaluate("/application/availableOrgIds/orgId", doc, XPathConstants.NODESET);

            application = new Application(id,name,defaultrole, defaultorgid);
            if (availableOrgIds != null && availableOrgIds.getLength() > 0) {
                for (int i = 0; i < availableOrgIds.getLength(); i++) {
                    Node node = availableOrgIds.item(i);
                    XPathExpression pathExpr = xPath.compile(".");
                    String orgId = (String) pathExpr.evaluate(node, XPathConstants.STRING);
                    log.debug("orgId {}", orgId);
                    application.addAvailableOrgId(orgId);
                }
            }
        } catch (Exception e) {
            log.warn("Could not create an Application from this xml {}", applicationXml, e);
        }
        return application;

    }
    */
}
