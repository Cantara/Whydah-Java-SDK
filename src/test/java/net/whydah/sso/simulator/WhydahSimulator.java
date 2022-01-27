package net.whydah.sso.simulator;

import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.session.DefaultWhydahApplicationSession;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class WhydahSimulator implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(WhydahSimulator.class);

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int maxNumberOfAllowedLogons = Integer.MAX_VALUE;
        private boolean applicationLogonAlwaysFailing = false;

        private Builder() {
        }

        public Builder withMaxNumberOfAllowedLogons(int maxNumberOfAllowedLogons) {
            this.maxNumberOfAllowedLogons = maxNumberOfAllowedLogons;
            return this;
        }

        public Builder withApplicationLogonAlwaysFailing(boolean applicationLogonAlwaysFailing) {
            this.applicationLogonAlwaysFailing = applicationLogonAlwaysFailing;
            return this;
        }

        public WhydahSimulator build() {
            return new WhydahSimulator(maxNumberOfAllowedLogons, applicationLogonAlwaysFailing);
        }
    }

    private final int maxNumberOfAllowedLogons;
    private final boolean applicationLogonAlwaysFailing;
    private final AtomicInteger logonsAttempted = new AtomicInteger(0);
    private final List<DefaultWhydahApplicationSession> sessions = new CopyOnWriteArrayList<>();
    private final List<Throwable> errors = new CopyOnWriteArrayList<>();
    private final CountDownLatch firstErrorCountDownLatch = new CountDownLatch(1);

    private WhydahSimulator(int maxNumberOfAllowedLogons, boolean applicationLogonAlwaysFailing) {
        this.maxNumberOfAllowedLogons = maxNumberOfAllowedLogons;
        this.applicationLogonAlwaysFailing = applicationLogonAlwaysFailing;
        Spark.port(0);
        initRoutes();
        Spark.init();
        Spark.awaitInitialization();
    }

    public void expectPeriodWithoutAnyErrors(long timeout, TimeUnit unit) throws InterruptedException {
        long start = System.nanoTime();
        if (firstErrorCountDownLatch.await(timeout, unit)) {
            long duration = (System.nanoTime() - start) / 1_000_000;
            errors.get(0).printStackTrace();
            Assert.fail(String.format("Error after %d ms: %s", duration, errors.get(0).getMessage()));
        }
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public void initRoutes() {
        Spark.post("/sts/logon", (req, res) -> {
            log.info("WHYDAH-SIMULATOR: logon");
            if (logonsAttempted.incrementAndGet() > maxNumberOfAllowedLogons) {
                errors.add(new RuntimeException(String.format("More than %d number of logons attempted", maxNumberOfAllowedLogons)));
                firstErrorCountDownLatch.countDown();
                return "";
            }
            if (applicationLogonAlwaysFailing) {
                return "";
            }
            String applicationcredentialXml = req.raw().getParameter("applicationcredential");
            ApplicationCredential applicationCredential = ApplicationCredentialMapper.fromXml(applicationcredentialXml);
            ApplicationToken token = new ApplicationToken();
            token.setApplicationName(applicationCredential.getApplicationName());
            token.setApplicationID(applicationCredential.getApplicationID());
            token.setApplicationSecret(applicationCredential.getApplicationSecret());
            token.setApplicationTokenId(UUID.randomUUID().toString());
            token.setExpires(String.valueOf((System.currentTimeMillis() + (24 * 60 * 60 * 1000)))); // 24 HOURS
            String xml = ApplicationTokenMapper.toXML(token);
            return xml;
        });
        Spark.post("/sts/:applicationTokenId/renew_applicationtoken", (req, res) -> {
            log.info("WHYDAH-SIMULATOR: renew_applicationtoken");
            return "";
        });
        Spark.get("/sts/:applicationTokenId/get_application_key", (req, res) -> {
            log.info("WHYDAH-SIMULATOR: get_application_key");
            return "";
        });
        Spark.get("/sts/:applicationTokenId/validate", (req, res) -> {
            String applicationTokenId = req.params(":applicationTokenId");
            log.info("WHYDAH-SIMULATOR: {}/validate", applicationTokenId);
            return "";
        });
        Spark.get("/sts/threat/:applicationTokenId/signal", (req, res) -> {
            log.info("WHYDAH-SIMULATOR: threat/.../signal");
            return "";
        });
        Spark.get("/uas/:applicationTokenId/applications", (req, res) -> {
            log.info("WHYDAH-SIMULATOR: applications");
            return "";
        });
    }

    public String sts() {
        return String.format("http://localhost:%d/sts/", Spark.port());
    }

    public String uas() {
        return String.format("http://localhost:%d/uas/", Spark.port());
    }

    public DefaultWhydahApplicationSession createNewSession(Consumer<DefaultWhydahApplicationSession.Builder> sessionBuilderConsumer) {
        final ApplicationCredential defaultCredential = new ApplicationCredential("myappid", "MyApplication", "my-app-s3cr3t");
        DefaultWhydahApplicationSession.Builder builder = DefaultWhydahApplicationSession.builder()
                .withSts(sts())
                .withUas(uas())
                .withAppCred(defaultCredential);
        sessionBuilderConsumer.accept(builder);
        DefaultWhydahApplicationSession was = builder.build();
        sessions.add(was);
        return was;
    }

    @Override
    public void close() {
        try {
            for (DefaultWhydahApplicationSession was : sessions) {
                try {
                    was.close();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            Spark.stop();
        }
    }
}
