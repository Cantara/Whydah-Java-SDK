package net.whydah.sso.commands.extensions.statistics;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.basehelpers.JsonPathHelper;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.user.helpers.UserXpathHelper;
import net.whydah.sso.util.SystemTestBaseConfig;
import net.whydah.sso.whydah.TimeLimitedCodeBlock;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

    public static String getFilteredUserSessionsJsonFromUserActivityJson(String userActivityJson, String filterusername) {
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
            String myAppTokenXml = new CommandLogonApplication(config.tokenServiceUri, config.appCredential).execute();
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
            String mappedUL = getTimedFilteredUserSessionsJsonFromUserActivityJson(userLogins, config.userName);
            log.debug("Mapped:  {} getFilteredUserSessionsJsonFromUserActivityJson: {}", mappedUL.length(), mappedUL);

        }
    }

    public static String getTimedFilteredUserSessionsJsonFromUserActivityJson(String userActivityJson, String filterusername) {
            final long startTime = System.currentTimeMillis();
        log(startTime, "calling runWithTimeout!");
        String result = "";
        try {
            result = TimeLimitedCodeBlock.runWithTimeout(new Callable<String>() {
                @Override
                public String call() {
//                    try {
                    log(startTime, "starting sleep!");
                    String r = getFilteredUserSessionsJsonFromUserActivityJson(userActivityJson, filterusername);
                    log(startTime, "woke up!");
                    return r;

                    //throw new InterruptedException("");
                    //                  }
                    //                  catch (InterruptedException e) {
                    //                      log(startTime, "was interrupted!");
                    //                  }
                }
            }, 2, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            log(startTime, "got timeout!");
        } catch (Exception e) {
            log(startTime, "got exception!");
        }
        log(startTime, "end of main method!");
        return result;
    }

    private static void log(long startTime, String msg) {
        long elapsedSeconds = (System.currentTimeMillis() - startTime);
        log.info("{}ms [{}] {}\n", elapsedSeconds, Thread.currentThread().getName(), msg);
    }

}
