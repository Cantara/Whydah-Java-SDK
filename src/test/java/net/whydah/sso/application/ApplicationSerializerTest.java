package net.whydah.sso.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationRole;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:erik-dev@fjas.no">Erik Drolshammer</a> 2015-06-30
 */
public class ApplicationSerializerTest {
    private static final Logger log = LoggerFactory.getLogger(ApplicationSerializerTest.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private static Application app1;

    @BeforeClass
    public static void initTestData() {
        app1 = new Application("appId1", "applicationName1");
        app1.setDescription("description of application");
        app1.setApplicationUrl("https://webtest.exapmle.com/test.png");
        app1.setLogoUrl("https://webtest.example.com");
        app1.addRole(new ApplicationRole("roleId1", "roleName1"));
        app1.addOrganizationName("organizationName1");
        app1.setDefaultRoleName("defaultRoleName");
        app1.setDefaultRoleName("roleName1");
        app1.setDefaultOrganizationName("defaultOrgName");
        app1.addOrganizationName(app1.getDefaultOrganizationName());
        app1.addAcl(new ApplicationACL("11","/user","READ"));

        app1.getSecurity().setSecret("veryVerySecret");
    }

    @Test
    public void testToFromJson() throws Exception {
        String json = ApplicationSerializer.toJson(app1);
        String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(json, Object.class));
        log.debug("\n" + indented);

        Application applicationFromJson = ApplicationSerializer.fromJson(json);
        assertEquals(app1, applicationFromJson);
    }

    @Test
    public void testToApplicationList() throws Exception {
        Application app2 = new Application("appId2", "applicationName2");
        String json = ApplicationSerializer.toJson(Arrays.asList(new Application[]{app1, app2}));
        String indented = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(json, Object.class));
        log.debug("\n" + indented);

        List<Application> applications = ApplicationSerializer.fromJsonList(json);
        assertEquals(applications.size(), 2);
    }


    @Test
    public void fromRealJson() throws Exception{
        Application applicationFromJson = ApplicationSerializer.fromJson(ApplicationHelper.getDummyAppllicationJson());
        log.debug(ApplicationSerializer.toJson(applicationFromJson));
    }

    @Test
    public void fromRealJsonList() throws Exception{
        List<Application> applications = ApplicationSerializer.fromJsonList(ApplicationHelper.getDummyAppllicationListJson());
        for (Application application : applications) {
            log.debug(ApplicationSerializer.toJson(application));
        }

    }
}
