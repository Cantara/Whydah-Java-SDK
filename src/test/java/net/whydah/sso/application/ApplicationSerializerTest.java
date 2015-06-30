package net.whydah.sso.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-06-30
 */
public class ApplicationSerializerTest {
    private static final Logger log = LoggerFactory.getLogger(ApplicationSerializerTest.class);
    private static final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testToFromJson() throws Exception {
        Application application = new Application("appId1", "applicationName1");
        application.setDescription("description of application");
        application.setSecret("veryVerySecret");
        application.addRole(new Role("roleId1", "roleName1"));
        application.addOrganizationName("organizationName1");
        application.setDefaultRoleName("defaultRoleName");
        application.setDefaultRoleName("roleName1");
        application.setDefaultOrganizationName("defaultOrgName");
        application.addOrganizationName(application.getDefaultOrganizationName());
        String json = ApplicationSerializer.toJson(application);
        String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(json, Object.class));
        log.debug(indented);

        Application applicationFromJson = ApplicationSerializer.fromJson(json);
        assertEquals(application, applicationFromJson);
    }
}
