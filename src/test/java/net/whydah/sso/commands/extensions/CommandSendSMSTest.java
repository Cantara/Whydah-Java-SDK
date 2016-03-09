package net.whydah.sso.commands.extensions;

import net.whydah.sso.commands.adminapi.user.CommandSendSMSToUser;
import org.junit.Ignore;
import org.junit.Test;


public class CommandSendSMSTest {


    @Ignore
    @Test
    public void testSendSMS() {
        String serviceURL = "https://smsgw.somewhere/../sendMessages/";
        String serviceAccount = "serviceAccount";
        String username = "smsserviceusername";
        String password = "smsservicepassword";
        String cellNo = "smsrecepient";
        String smsMessage = "testmessage til Totto2";
        String queryParam = "serviceId=serviceAccount&message[0].recipient=smsrecepient&message[0].content=smscontent&username=smsserviceusername&password=smsservicepassword";
        new CommandSendSMSToUser(serviceURL, serviceAccount, username, password, queryParam, cellNo, smsMessage).execute();
    }


}
