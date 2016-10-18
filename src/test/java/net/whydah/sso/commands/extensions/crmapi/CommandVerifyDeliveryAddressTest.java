package net.whydah.sso.commands.extensions.crmapi;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;

public class CommandVerifyDeliveryAddressTest {


    @Test
    @Ignore // Hardcoded Google code info has been revoked
    public void testCommandVerifyDeliveryAddress() throws Exception {

        String clientid = "";
        byte[] key = null;
//        String testAddress = "Møllefaret 30E, 0750 Oslo, Norway";
        String testAddress = "Frankfurstein+ring+105a,M%C3%BCnchen,de,80000";
        String customerJson = new CommandVerifyDeliveryAddress(testAddress, clientid, key).execute();
        System.out.println("Returned result: " + customerJson);
        assertTrue(customerJson.length() > 100);

    }
}
