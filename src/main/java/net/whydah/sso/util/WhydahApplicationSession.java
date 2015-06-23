package net.whydah.sso.util;

import net.whydah.sso.application.ApplicationXpathHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by totto on 23.06.15.
 *
 * A thread which initiales and keep your application session running
 *
 *
 */
public class WhydahApplicationSession {

    private String sts;
    private String appId;
    private String appSecret;

    private String applicationTokenId;
    private String applicationToken;

    public WhydahApplicationSession(){
        this("https://whydahdev.altrancloud.com/tokenservice/","15","33779936R6Jr47D4Hj5R6p9qT");

    }


    public WhydahApplicationSession(String sts, String appId, String appSecret){
        this.sts=sts;
        this.appId=appId;
        this.appSecret=appSecret;
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        releaseWhydahConnection();
                    }
                },
                1,  60, TimeUnit.SECONDS );
    }


    public String getActiveApplicationTokenId(){
        return applicationTokenId;
    }

    public String getActiveApplicationToken(){
        return applicationToken;
    }

    private void releaseWhydahConnection(){
        String appTokenXML = WhydahUtil.logOnApplication(sts, appId, appSecret);
        String expires = ApplicationXpathHelper.getExpiresFromAppToken(appTokenXML);
        if (expiresBeforeNextSchedule(expires)){
            applicationToken = WhydahUtil.extendApplicationSession(sts, appId, appSecret);
            applicationTokenId = ApplicationXpathHelper.getAppTokenIdFromAppToken(applicationToken);
        }

    }

    public  static boolean expiresBeforeNextSchedule(String timestamp){

        long i = System.currentTimeMillis();
        long j = Long.parseLong(timestamp);
        long diffSeconds  = j-i;
        if (diffSeconds<60){
            return true;
        }
        return false;
    }
}
