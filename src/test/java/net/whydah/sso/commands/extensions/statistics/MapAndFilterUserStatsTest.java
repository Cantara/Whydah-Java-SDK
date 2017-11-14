package net.whydah.sso.commands.extensions.statistics;

import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.whydah.sso.basehelpers.JsonPathHelper;
import net.whydah.sso.ddd.model.UserTokenId;
import net.whydah.sso.session.WhydahApplicationSession;
import net.whydah.sso.session.WhydahUserSession;
import net.whydah.sso.user.types.UserCredential;
import net.whydah.sso.util.SystemTestBaseConfig;
import net.whydah.sso.whydah.TimeLimitedCodeBlock;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MapAndFilterUserStatsTest {

    private static final ObjectMapper mapper;
    private final static Logger log = LoggerFactory.getLogger(MapAndFilterUserStatsTest.class);
    public static String userName = "admin";
    public static String password = "whydahadmin";
    static SystemTestBaseConfig config;
    private static WhydahApplicationSession applicationSession;

    static {
        mapper = (new ObjectMapper()).configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        userName = config.userName;
        password = config.password;
        if (config.isSystemTestEnabled()) {
            applicationSession = WhydahApplicationSession.getInstance(config.tokenServiceUri.toString(), config.appCredential);
        }

    }


    @Test
    public void testUserLoginsCustomerCommand() throws Exception {
        if (config.isStatisticsExtensionSystemtestEnabled()) {

            UserCredential userCredential = new UserCredential(userName, password);
            WhydahUserSession userSession2 = new WhydahUserSession(applicationSession, userCredential);
            String userTokenId = userSession2.getActiveUserTokenId();
            assertTrue(UserTokenId.isValid(userTokenId));

            String userLogins = new CommandListUserActivities(config.statisticsServiceUri, applicationSession.getActiveApplicationTokenId(), userTokenId, config.userName).execute();
            assertTrue(userLogins != null);
            log.debug("Returned list {} of userlogins: {}", userLogins.length(), userLogins);
            assertTrue(userLogins.length() > 10);
            String mappedUL = getTimedFilteredUserSessionsJsonFromUserActivityJson(userLogins, config.userName);
            log.debug("Mapped:  {} getFilteredUserSessionsJsonFromUserActivityJson: {}", mappedUL.length(), mappedUL);

        }
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
