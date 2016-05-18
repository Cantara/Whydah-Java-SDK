package net.whydah.sso.commands.extensions.statistics;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.basehelpers.JsonPathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.SSLTool;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class MapAndFilterUserStatsTest {

    private static final ObjectMapper mapper;
    private final static Logger log = LoggerFactory.getLogger(MapAndFilterUserStatsTest.class);
    static SystemTestBaseConfig config;

    static {
        mapper = (new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
    }

    public static String getUserSessionsJsonFromUserActivityJson(String userActivityJson, String filterusername) {
        try {
            if (userActivityJson != null) {
                List e = JsonPathHelper.findJsonpathList(userActivityJson, "$..userSessions.*");
                if (e == null) {
                    log.debug("jsonpath returned zero hits");
                    return null;
                }

                LinkedList userSessions = new LinkedList();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                GregorianCalendar c = new GregorianCalendar();
                int i = 0;

                for (LinkedList registeredApplication = new LinkedList(); i < e.size(); ++i) {
                    HashMap userSession = new HashMap();
                    String activityJson = mapper.writeValueAsString(e.get(e.size() - i - 1));
                    String timestamp = JsonPathHelper.findJsonpathList(userActivityJson, "$..userSessions[" + i + "].startTime").toString();
                    List data = JsonPathHelper.findJsonpathList(activityJson, "$..data.*");
                    String activityType = (String) data.get(0);
                    String applicationid = (String) data.get(1);
                    String username = (String) data.get(2);
                    String applicationtokenid = (String) data.get(3);
                    timestamp = timestamp.substring(1, timestamp.length() - 1);
                    c.setTimeInMillis(Long.parseLong(timestamp));
                    if ((filterusername == null || filterusername.length() < 1 || filterusername.equalsIgnoreCase(username)) && !registeredApplication.contains(applicationid + activityType)) {
                        userSession.put("applicationid", applicationid);
                        userSession.put("activityType", activityType);
                        userSession.put("timestamp", dateFormat.format(c.getTime()));
                        registeredApplication.add(applicationid + activityType);
                        userSessions.add(userSession);
                    }
                }

                return mapper.writeValueAsString(userSessions);
            }

            log.trace("getDataElementsFromUserActivityJson was empty, so returning null.");
        } catch (Exception var16) {
            log.warn("Could not convert getDataElementsFromUserActivityJson Json}");
        }

        return null;
    }

    @Test
    public void testUserLoginsCustomerCommand() throws Exception {
        if (config.isStatisticsExtensionSystemtestEnabled()) {
            SSLTool.disableCertificateValidation();
            ApplicationCredential appCredential = new ApplicationCredential(config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, appCredential).execute();
            String myApplicationTokenID = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(myAppTokenXml);
            assertTrue(myApplicationTokenID.length() > 10);

            String userticket = UUID.randomUUID().toString();
            String userToken = new CommandLogonUserByUserCredential(config.tokenServiceUri, myApplicationTokenID, myAppTokenXml, config.userCredential, userticket).execute();
            String userTokenId = UserXpathHelper.getUserTokenId(userToken);
            assertTrue(userTokenId.length() > 10);

            String userLogins = new CommandListUserActivities(config.statisticsServiceUri, myApplicationTokenID, userTokenId, config.userName).execute();
            log.debug("Returned list {} of userlogins: {}", userLogins.length(), userLogins);
            assertTrue(userLogins != null);
            assertTrue(userLogins.length() > 10);
            String mappedUL = getUserSessionsJsonFromUserActivityJson(userLogins, config.userName);
            log.debug("Mapped:  {} getUserSessionsJsonFromUserActivityJson: {}", mappedUL.length(), mappedUL);

        }
    }

}
