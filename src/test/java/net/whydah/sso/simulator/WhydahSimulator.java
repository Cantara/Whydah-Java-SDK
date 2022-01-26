package net.whydah.sso.simulator;

import net.whydah.sso.application.mappers.ApplicationCredentialMapper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.session.DefaultWhydahApplicationSession;
import spark.Spark;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class WhydahSimulator implements AutoCloseable {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Builder() {
        }

        public WhydahSimulator build() {
            return new WhydahSimulator();
        }
    }

    private final List<DefaultWhydahApplicationSession> sessions = new CopyOnWriteArrayList<>();

    private WhydahSimulator() {
        Spark.port(0);
        initRoutes();
        Spark.init();
        Spark.awaitInitialization();
    }

    public void initRoutes() {
        Spark.post("/sts/logon", (req, res) -> {
            System.out.printf("logon%n");
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
            System.out.printf("renew_applicationtoken%n");
            return "";
        });
        Spark.get("/sts/:applicationTokenId/get_application_key", (req, res) -> {
            System.out.printf("get_application_key%n");
            return "";
        });
        Spark.get("/sts/:applicationTokenId/validate", (req, res) -> {
            String applicationTokenId = req.params(":applicationTokenId");
            System.out.printf("%s/validate%n", applicationTokenId);
            return "";
        });
        Spark.get("/sts/threat/:applicationTokenId/signal", (req, res) -> {
            System.out.printf("threat/.../signal%n");
            return "";
        });
        Spark.get("/uas/:applicationTokenId/applications", (req, res) -> {
            System.out.printf("applications%n");
            return "";
        });
    }

    public String sts() {
        return String.format("http://localhost:%d/sts/", Spark.port());
    }

    public String uas() {
        return String.format("http://localhost:%d/uas/", Spark.port());
    }

    public DefaultWhydahApplicationSession createNewSession(String applicationId, String applicationName, String applicationSecret) {
        final ApplicationCredential credential = new ApplicationCredential(applicationId, applicationName, applicationSecret);
        DefaultWhydahApplicationSession was = DefaultWhydahApplicationSession.builder()
                .withSts(sts())
                .withUas(uas())
                .withAppCred(credential)
                .build();
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
