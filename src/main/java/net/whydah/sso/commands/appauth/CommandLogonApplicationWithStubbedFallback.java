package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationHelper;
import net.whydah.sso.application.types.ApplicationCredential;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

public class CommandLogonApplicationWithStubbedFallback extends CommandLogonApplication {
    private static final Logger log = LoggerFactory.getLogger(CommandLogonApplicationWithStubbedFallback.class);


    public CommandLogonApplicationWithStubbedFallback(URI tokenServiceUri, ApplicationCredential appCredential) {
        super(tokenServiceUri, appCredential);
    }

    @Override
        protected String getFallback() {

        log.warn("CommandLogonApplicationWithStubbedFallback - getFallback - override with fallback ");
        return ApplicationHelper.getDummyApplicationToken();
        }
    }