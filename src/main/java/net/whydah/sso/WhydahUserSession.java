package net.whydah.sso;

import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserXpathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by totto on 23.06.15.
 */
public class WhydahUserSession {


        private WhydahApplicationSession was;
        private UserCredential userCredential;
        private static final Logger log = LoggerFactory.getLogger(WhydahUserSession.class);

        private String userTokenId;
        private String userTokenXML;

        private WhydahUserSession(){

        }


        public WhydahUserSession(WhydahApplicationSession was,UserCredential userCredential){
            if (was==null || was.getActiveApplicationTokenId()==null || was.getActiveApplicationTokenId().length() < 4) {
                log.error("Error, unable to initialize new user session, application session invalid:"+was.getActiveApplicationTokenId());

            }

            this.was=was;
            this.userCredential=userCredential;
            initializeUserSession();
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
                    new Runnable() {
                        public void run() {
                            renewWhydahUserSession();
                        }
                    },
                    1,  60, TimeUnit.SECONDS );
        }


        public String getActiveUserTokenId(){
            return userTokenId;
        }

        public String getActiveUserToken(){
            return userTokenXML;
        }

    /*
    * @return true is session is active and working
     */
    public boolean hasActiveSession(){
        if (userTokenXML==null || userTokenXML.length() < 4) {
            return false;
        }
        return true;
    }


    private void renewWhydahUserSession(){
            log.info("Renew user session");
            userTokenXML = WhydahUtil.logOnUser(was,userCredential) ;
            if (userTokenXML==null || userTokenXML.length() < 4) {
                log.error("Error, unable to initialize new user session, userTokenXML:"+userTokenXML);
            } else {
                log.info("Renew user session successfull.  userTokenXml:"+userTokenXML);
                Long expires = UserXpathHelper.getTimestampFromUserTokenXml(userTokenXML) + UserXpathHelper.getLifespanFromUserTokenXml(userTokenXML);
                if (expiresBeforeNextSchedule(expires)) {
                    this.userTokenXML = WhydahUtil.extendUserSession(was, userCredential);
                    userTokenId = UserXpathHelper.getUserTokenId(this.userTokenXML);
                }
            }

        }

    private void initializeUserSession() {
        log.info("Initializing new user session");
        userTokenXML = WhydahUtil.logOnUser(was, userCredential) ;
        if (userTokenXML==null || userTokenXML.length() < 4) {
            log.error("Error, unable to initialize new user session, userTokenXML:"+userTokenXML);
        } else {
            log.info("Initializing user session successfull.  userTokenXml:"+userTokenXML);
            userTokenId = UserXpathHelper.getUserTokenId(this.userTokenXML);
        }
    }

    public  static boolean expiresBeforeNextSchedule(Long timestamp){

            long i = System.currentTimeMillis();
            long j = timestamp;
            long diffSeconds  = j-i;
            if (diffSeconds<60){
                return true;
            }
            return false;
        }
}


