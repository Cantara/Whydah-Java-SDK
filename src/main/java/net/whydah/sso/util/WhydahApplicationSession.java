package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationXpathHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by totto on 23.06.15.
 * <p>
 * A thread which initiales and keep your application session running
 */
public class WhydahApplicationSession {

    private String sts;
    private String appId;
    private String appSecret;

    private String applicationTokenId;
    private String applicationTokenXML;
    private static boolean integrationMode = false;


    public WhydahApplicationSession() {
        this("https://whydahdev.altrancloud.com/tokenservice/", "15", "33779936R6Jr47D4Hj5R6p9qT");

    }


    public WhydahApplicationSession(String sts, String appId, String appSecret) {
        this.sts = sts;
        this.appId = appId;
        this.appSecret = appSecret;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        releaseWhydahConnection();
                    }
                },
                1, 60, TimeUnit.SECONDS);
    }


    public String getActiveApplicationTokenId() {
        return applicationTokenId;
    }

    public String getActiveApplicationToken() {
        return applicationTokenXML;
    }

    public String getSTS() {
        return sts;
    }


    private void releaseWhydahConnection() {
        if (integrationMode) {
            applicationTokenXML = WhydahUtil.logOnApplication(sts, appId, appSecret);
            Long expires = Long.parseLong(ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationTokenXML));
            if (expiresBeforeNextSchedule(expires)) {
                applicationTokenXML = WhydahUtil.extendApplicationSession(sts, appId, appSecret);
                applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
            }
        }


    }

    public static boolean expiresBeforeNextSchedule(Long timestamp) {

        long i = System.currentTimeMillis();
        long j = (timestamp);
        long diffSeconds = j - i;
        if (diffSeconds < 60) {
            return true;
        }
        return false;
    }
}
