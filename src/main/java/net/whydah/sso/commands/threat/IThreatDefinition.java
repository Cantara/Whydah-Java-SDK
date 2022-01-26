package net.whydah.sso.commands.threat;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class IThreatDefinition {

    public final static int DEF_CODE_MANY_LOGIN_ATTEMPTS = 1111;
    public final static int DEF_CODE_MANY_REQUESTS_IN_A_SHORT_PERIOD = 2222;
    public final static int DEF_CODE_MANY_GET_PIN_ATTEMPTS = 3333;
    public final static int DEF_CODE_MANY_geT_PIN_ATTEMPTS_PER_PHONENUMBER = 4444;

    final Lock lock = new ReentrantLock();

    public abstract int getCode();

    public abstract String getDesc();

    public void triggerDetection(ThreatActivityLogCollector collector, ThreatObserver observer) //will be triggered when one activity log is added
    {
        if (!lock.tryLock()) {
            return;
        }
        try {
            detect(collector, observer);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    abstract void detect(ThreatActivityLogCollector collector, ThreatObserver observer);

    public boolean isDetecting() {
        if (lock.tryLock()) {
            lock.unlock();
            return false;
        } else {
            return true;
        }
    }

}
