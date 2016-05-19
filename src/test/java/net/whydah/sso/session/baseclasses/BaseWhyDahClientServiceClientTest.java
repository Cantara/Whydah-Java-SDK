package net.whydah.sso.session.baseclasses;

import net.whydah.sso.application.helpers.ApplicationXpathHelper;
import net.whydah.sso.application.mappers.ApplicationTokenMapper;
import net.whydah.sso.application.types.Application;
import net.whydah.sso.application.types.ApplicationCredential;
import net.whydah.sso.application.types.ApplicationToken;
import net.whydah.sso.commands.adminapi.user.role.CommandAddUserRole;
import net.whydah.sso.commands.adminapi.user.role.CommandGetUserRoles;
import net.whydah.sso.commands.adminapi.user.role.CommandUpdateUserRole;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.commands.systemtestbase.SystemTestBaseConfig;
import net.whydah.sso.commands.userauth.CommandLogonUserByUserCredential;
import net.whydah.sso.session.baseclasses.BaseWhydahServiceClient;
import net.whydah.sso.user.mappers.UserRoleMapper;
import net.whydah.sso.user.mappers.UserTokenMapper;
import net.whydah.sso.user.types.UserApplicationRoleEntry;
import net.whydah.sso.user.types.UserIdentity;
import net.whydah.sso.user.types.UserToken;
import net.whydah.sso.util.SSLTool;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BaseWhyDahClientServiceClientTest {

    static SystemTestBaseConfig config;
    static BaseWhydahServiceClient client;

    @BeforeClass
    public static void setup() throws Exception {
        config = new SystemTestBaseConfig();
        client = new BaseWhydahServiceClient(config.tokenServiceUri.toString(), config.userAdminServiceUri.toString(), config.TEMPORARY_APPLICATION_ID, config.TEMPORARY_APPLICATION_NAME, config.TEMPORARY_APPLICATION_SECRET);
        
    }

    @Test
    public void testUpdateRoleAndRefreshUserToken(){
    	
    	assertTrue(client.updateOrCreateUserApplicationRoleEntry("", "ACSResource", "WhyDah", "INNData", "welcome", config.logOnSystemTestApplicationAndSystemTestUser_getTokenXML()));
    	
    }
}
