package net.whydah.sso.commands.extensions.crmapi;

import org.junit.Ignore;
import org.junit.Test;

public class CommandVerifyDeliveryAddressTest {


    @Test
    @Ignore
    public void testCommandVerifyDeliveryAddress() throws Exception {

//        String testAddress = "Møllefaret 30E, 0750 Oslo, Norway";
        String testAddress = "Frankfurstein+ring+105a,M%C3%BCnchen,de,80000";
        String customerJson = new CommandVerifyDeliveryAddress(testAddress).execute();
        System.out.println("Returned result: " + customerJson);

    }
}
