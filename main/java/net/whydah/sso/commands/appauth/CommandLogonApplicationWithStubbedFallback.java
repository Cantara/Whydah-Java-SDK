package net.whydah.sso.commands.appauth;

import net.whydah.sso.application.ApplicationCredential;
import net.whydah.sso.application.ApplicationHelper;

import java.net.URI;

/**
 * Created by totto on 24.06.15.
 */
public class CommandLogonApplicationWithStubbedFallback extends CommandLogonApplication {


    public CommandLogonApplicationWithStubbedFallback(URI tokenServiceUri, ApplicationCredential appCredential) {
        super(tokenServiceUri, appCredential);
    }

    @Override
        protected String getFallback() {
            return  ApplicationHelper.getDummyApplicationToken();
        }
    }