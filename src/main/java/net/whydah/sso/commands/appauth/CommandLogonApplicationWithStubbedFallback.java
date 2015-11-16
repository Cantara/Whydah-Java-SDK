package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.helpers.ApplicationHelper;
import net.whydah.sso.application.types.ApplicationCredential;

import java.net.URI;

public class CommandLogonApplicationWithStubbedFallback extends CommandLogonApplication {


    public CommandLogonApplicationWithStubbedFallback(URI tokenServiceUri, ApplicationCredential appCredential) {
        super(tokenServiceUri, appCredential);
    }

    @Override
        protected String getFallback() {
            return  ApplicationHelper.getDummyApplicationToken();
        }
    }