package net.whydah.sso.util;

import net.whydah.sso.user.UserCredential;
import net.whydah.sso.user.UserXpathHelper;

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

        private String userTokenId;
        private String userTokenXML;

        private WhydahUserSession(){

        }


        public WhydahUserSession(WhydahApplicationSession was,UserCredential userCredential){
            this.was=was;
            this.userCredential=userCredential;
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            ScheduledFuture<?> sf = scheduler.scheduleAtFixedRate(
                    new Runnable() {
                        public void run() {
                            renewWhydahUserConnection();
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

        private void renewWhydahUserConnection(){
            userTokenXML = WhydahUtil.logOnUser(was,userCredential) ;
            Long expires = UserXpathHelper.getTimestampFromUserTokenXml(userTokenXML)+UserXpathHelper.getLifespanFromUserTokenXml(userTokenXML);
            if (expiresBeforeNextSchedule(expires)){
                this.userTokenXML = WhydahUtil.extendUserSession(was,userCredential);
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


