package net.whydah.sso.session;

import net.whydah.sso.application.ApplicationXpathHelper;
import net.whydah.sso.util.WhydahUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger log = LoggerFactory.getLogger(WhydahApplicationSession.class);
    private String sts;
    private String appId;
    private String appSecret;
    private String applicationTokenId;
    private String applicationTokenXML;


    public WhydahApplicationSession() {
        this("https://whydahdev.altrancloud.com/tokenservice/", "99", "33879936R6Jr47D4Hj5R6p9qT");

    }


    public WhydahApplicationSession(String sts, String appId, String appSecret) {
        this.sts = sts;
        this.appId = appId;
        this.appSecret = appSecret;
        initializeWhydahApplicationSession();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        renewWhydahApplicationSession();
                    }
                },
                1, 50, TimeUnit.SECONDS);
    }

    public static boolean expiresBeforeNextSchedule(Long timestamp) {

        long i = System.currentTimeMillis();
        long j = (timestamp);
        long diffSeconds = j - i;
        if (diffSeconds < 60) {
            log.debug("re-new application session..");
            return true;
        }
        return false;
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

    /*
    * @return true is session is active and working
     */
    public boolean hasActiveSession(){
        if (applicationTokenXML==null || applicationTokenXML.length() < 4) {
            return false;
        }
        return true;
    }

    private void renewWhydahApplicationSession() {
        log.debug("Trying to renew applicationsession");
        applicationTokenXML = WhydahUtil.logOnApplication(sts, appId, appSecret);
        applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
        if (!hasActiveSession()) {
            log.info("Error, unable to renew application session, applicationTokenXml:"+applicationTokenXML);
            for (int n=0;n<7 || !hasActiveSession();n++){
                applicationTokenXML = WhydahUtil.logOnApplication(sts, appId, appSecret);
                applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
                log.debug("Retrying renewing application session");
                try {
                    Thread.sleep(1000 * n);
                } catch (InterruptedException ie){
                }
            }

        } else {
            log.info("Success in renew applicationsession, applicationTokenXml:" + applicationTokenXML);
            Long expires = ApplicationXpathHelper.getExpiresFromAppTokenXml(applicationTokenXML);
            if (expiresBeforeNextSchedule(expires)) {
                applicationTokenXML = WhydahUtil.extendApplicationSession(sts, appId, appSecret);
                applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
            }
        }


    }

    private void initializeWhydahApplicationSession() {
        log.info("Initializing new application session");
        applicationTokenXML = WhydahUtil.logOnApplication(sts, appId, appSecret);
        applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
        if (applicationTokenXML==null || applicationTokenXML.length() < 4) {
            log.info("Error, unable to initialize new application session, applicationTokenXml:"+applicationTokenXML);

        } else {
            log.debug("Initializing new application session, applicationTokenXml:" + applicationTokenXML);
//        applicationTokenXML = WhydahUtil.extendApplicationSession(sts, appId, appSecret);
            applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppTokenXml(applicationTokenXML);
        }
    }
}
