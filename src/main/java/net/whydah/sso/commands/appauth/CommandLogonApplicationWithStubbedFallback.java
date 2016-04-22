package net.whydah.sso.commands.appauth;

import java.net.URI;

import net.whydah.sso.application.helpers.ApplicationHelper;
import net.whydah.sso.application.types.ApplicationCredential;

public class CommandLogonApplicationWithStubbedFallback extends CommandLogonApplication {
    
    public CommandLogonApplicationWithStubbedFallback(URI tokenServiceUri, ApplicationCredential appCredential) {
        super(tokenServiceUri, appCredential);
    }

    @Override
        protected String getFallback() {

        log.warn("CommandLogonApplicationWithStubbedFallback - getFallback - override with fallback ");
        return ApplicationHelper.getDummyApplicationToken();
        }
    }