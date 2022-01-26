package net.whydah.sso.commands.threat;

import net.whydah.sso.session.WhydahApplicationSession;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.slf4j.LoggerFactory.getLogger;

//observer for each application
public class ThreatObserver {

    private static final Logger logger = getLogger(ThreatObserver.class);

    public static int LOGS_CHECK_INTERVAL = 5;

    final List<IThreatDefinition> threatDefs = new CopyOnWriteArrayList<>();
    final ThreatActivityLogCollector collector;
    final WhydahApplicationSession was;
    final ThreatObserver me = this;
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    {
        scheduler.scheduleAtFixedRate(
                new Runnable() {
                    public void run() {
                        try {

                            detect();

                        } catch (Exception ex) {
                            logger.error("Detection process failed! - message: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                },
                1, LOGS_CHECK_INTERVAL, TimeUnit.SECONDS);
    }

    public ThreatObserver() {
        this.was = null;
        this.collector = new ThreatActivityLogCollector();
    }

    public ThreatObserver(WhydahApplicationSession was) {
        this.was = was;
        this.collector = new ThreatActivityLogCollector();
    }

    public ThreatObserver(WhydahApplicationSession was, Map<String, ThreatActivityLog> logsRepository, Map<String, Long> blackList) {
        this.was = was;
        this.collector = new ThreatActivityLogCollector(logsRepository, blackList);
    }

    public ThreatObserver(WhydahApplicationSession was, int time_to_remove_or_block_threats_in_milliseconds) {
        this.was = was;
        this.collector = new ThreatActivityLogCollector();
        this.collector.REMOVAL_TIME_FOR_OLD_LOGS = time_to_remove_or_block_threats_in_milliseconds;
    }

    public ThreatObserver(WhydahApplicationSession was, Map<String, ThreatActivityLog> logsRepository, Map<String, Long> blackList, int time_to_remove_or_block_threats_in_milliseconds) {
        this.was = was;
        this.collector = new ThreatActivityLogCollector(logsRepository, blackList);
        this.collector.REMOVAL_TIME_FOR_OLD_LOGS = time_to_remove_or_block_threats_in_milliseconds;
    }

    public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions) {
        this.was = was;
        this.collector = new ThreatActivityLogCollector();
        this.threatDefs.addAll(threatDefinitions);
    }

    public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions, Map<String, ThreatActivityLog> logsRepository, Map<String, Long> blackList) {
        this.was = was;
        this.threatDefs.addAll(threatDefinitions);
        this.collector = new ThreatActivityLogCollector(logsRepository, blackList);
    }

    public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions, int time_to_remove_or_block_threats_in_milliseconds) {
        this.was = was;
        this.threatDefs.addAll(threatDefinitions);
        this.collector = new ThreatActivityLogCollector();
        this.collector.REMOVAL_TIME_FOR_OLD_LOGS = time_to_remove_or_block_threats_in_milliseconds;
    }

    public ThreatObserver(WhydahApplicationSession was, List<IThreatDefinition> threatDefinitions, Map<String, ThreatActivityLog> logsRepository, Map<String, Long> blackList, int time_to_remove_or_block_threats_in_milliseconds) {
        this.was = was;
        this.threatDefs.addAll(threatDefinitions);
        this.collector = new ThreatActivityLogCollector(logsRepository, blackList);
        this.collector.REMOVAL_TIME_FOR_OLD_LOGS = time_to_remove_or_block_threats_in_milliseconds;
    }


    public void registerDefinition(IThreatDefinition definition) {
        threatDefs.add(definition);
    }


    final Lock lock = new ReentrantLock();

    public void addLogForDetection(ThreatActivityLog log) {
        collector.addLogForDetection(log);
    }


    private void detect() {
        if (!lock.tryLock()) {
            return;
        }
        try {
            long start = System.currentTimeMillis();
            logger.debug("detecting  " + collector.get_AllLogCollection().size() + " request records");
            //clean up first, remove logs having 1 hour old request time
            collector.cleanOldThreats();
            //trigger detection
            for (IThreatDefinition def : threatDefs) {
                def.triggerDetection(collector, me);
            }
            logger.debug("detection done in " + String.valueOf(((System.currentTimeMillis() - start) / 1000)) + " seconds");
        } finally {
            lock.unlock();
        }
    }


    public boolean isDetectionDone() {

        for (IThreatDefinition def : threatDefs) {
            if (def.isDetecting()) {//there is a lock inside to make sure the trigger is fired only one time
                return false;
            }
        }
        return true;
    }

    //call back from IThreatDefinition.detect(...)
    public void commitThreat(ThreatSignalInfo info) {

        //set appId if any. This should be assigned when running in real time
        if (this.was != null) {
            info.setAppId(this.was.getMyApplicationCredential().getApplicationID());
        }

        List<ThreatActivityLog> origin = info.getActivityLogList();
        int limit = Math.min(500, info.getActivityLogList().size());
        //copy maximum 500 records only, no need to commit everything
        int fromIndex = info.getActivityLogList().size() - limit;
        List<ThreatActivityLog> subList = origin.subList(fromIndex, info.getActivityLogList().size() - 1);
        info.setActivityLogList(subList);

        logger.info("THREAT FOUND - " + ThreatSignalInfo.toJson(info));

        //add the suspect to our blacklist
        this.addToBlackList(info);

        //commit to STS
        if (was != null) {
            //TODO: some more info for ThreatSignal possibly
            this.was.reportThreatSignal(ThreatSignalInfo.toJson(info));
        }

        //remove this log after reporting
        collector.removeLogs(origin);


    }

    public void addToBlackList(ThreatSignalInfo info) {

        if (was != null) {
            if (!this.was.isWhiteListed(info.getSuspect())) {
                this.collector.addToBlackList(info);
            }
        } else {
            this.collector.addToBlackList(info);
        }
    }

    public boolean isBlackListed(String identity) {
        return this.collector._blackList.containsKey(identity);
    }
}
