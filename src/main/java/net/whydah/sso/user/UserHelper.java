package net.whydah.sso.user;

/**
 * Created by totto on 12/3/14.
 */
public class UserHelper {


        public static  String getDummyToken(){
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<usertoken xmlns:ns2=\"http://www.w3.org/1999/xhtml\" id=\"759799fe-2e2f-4c8e-b096-d5796733d4d2\">\n" +
                    "    <uid>7583278592730985723</uid>\n" +
                    "    <securitylevel>0</securitylevel>\n" +
                    "    <personRef></personRef>\n" +
                    "    <firstname>Olav</firstname>\n" +
                    "    <lastname>Nordmann</lastname>\n" +
                    "    <email></email>\n" +
                    "    <timestamp>7982374982374</timestamp>\n" +
                    "    <lifespan>3600000</lifespan>\n" +
                    "    <issuer>/iam/issuer/tokenverifier</issuer>\n" +
                    "    <application ID=\"2349785543\">\n" +
                    "        <applicationName>MyApp</applicationName>\n" +
                    "        <organization ID=\"2349785543\">\n" +
                    "            <organizationName>myCompany</organizationName>\n" +
                    "            <role name=\"janitor\" value=\"Employed\"/>\n" +
                    "            <role name=\"board\" value=\"President\"/>\n" +
                    "        </organization>\n" +
                    "        <organization ID=\"0078\">\n" +
                    "            <organizationName>myDayJobCompany</organizationName>\n" +
                    "            <role name=\"board\" value=\"\"/>\n" +
                    "        </organization>\n" +
                    "    </application>\n" +
                    "    <application ID=\"appa\">\n" +
                    "        <applicationName>App A</applicationName>\n" +
                    "        <organization ID=\"1078\">\n" +
                    "            <organizationName>myFotballClub</organizationName>\n" +
                    "            <role name=\"janitor\" value=\"Employed\"/>\n" +
                    "        </organization>\n" +
                    "    </application>\n" +
                    "\n" +
                    "    <ns2:link type=\"application/xml\" href=\"/\" rel=\"self\"/>\n" +
                    "    <hash type=\"MD5\">7671ec2d5bac82d1e70b33c59b5c96a3</hash>\n" +
                    "</usertoken>";

        }

        public static String getDummyUserListJson(){
                return "{\n" +
                        "   \"rows\": \"3\",\n" +
                        "   \"result\": [\n" +
                        "      {\n" +
                        "         \"personRef\": \"42\",\n" +
                        "         \"uid\": \"useradmin\",\n" +
                        "         \"username\": \"useradmin\",\n" +
                        "         \"firstName\": \"UserAdmin\",\n" +
                        "         \"lastName\": \"UserAdminWebApp\",\n" +
                        "         \"email\": \"whydahadmin@getwhydah.com\",\n" +
                        "         \"cellPhone\": \"87654321\",\n" +
                        "         \"uri\": \"http://ip-172-31-33-110.ec2.internal:9995/uib/useradmin/users/useradmin/\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"personRef\": \"001\",\n" +
                        "         \"uid\": \"acsemployee\",\n" +
                        "         \"username\": \"acsemployee\",\n" +
                        "         \"firstName\": \"Karl\",\n" +
                        "         \"lastName\": \"Employee\",\n" +
                        "         \"email\": \"leon.ho@altran.com\",\n" +
                        "         \"cellPhone\": \"0\",\n" +
                        "         \"uri\": \"http://ip-172-31-33-110.ec2.internal:9995/uib/useradmin/users/acsemployee/\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "         \"personRef\": \"002\",\n" +
                        "         \"uid\": \"acsmanager\",\n" +
                        "         \"username\": \"acsmanager\",\n" +
                        "         \"firstName\": \"Karl\",\n" +
                        "         \"lastName\": \"Manager\",\n" +
                        "         \"email\": \"leon.ho@altran.com\",\n" +
                        "         \"cellPhone\": \"98765432\",\n" +
                        "         \"uri\": \"http://ip-172-31-33-110.ec2.internal:9995/uib/useradmin/users/acsmanager/\"\n" +
                        "      }\n" +
                        "   ]\n" +
                        "}";
        };
}
