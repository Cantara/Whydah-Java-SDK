package net.whydah.sso.user;

/**
 * Created by totto on 12/3/14.
 */
public class UserHelper {


        public static  String getDummyUserToken(){
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                    "<usertoken xmlns:ns2=\"http://www.w3.org/1999/xhtml\" id=\"a96a517f-cef3-4be7-92f5-f059b65e4071\">\n" +
                    "    <uid></uid>\n" +
                    "    <timestamp></timestamp>\n" +
                    "    <lifespan>3600000</lifespan>\n" +
                    "    <issuer>/token/issuer/tokenverifier</issuer>\n" +
                    "    <securitylevel>0</securitylevel>\n" +
                    "    <username>test_name</username>\n" +
                    "    <firstname>Olav</firstname>\n" +
                    "    <lastname>Nordmann</lastname>\n" +
                    "    <email></email>\n" +
                    "    <personRef></personRef>\n" +
                    "    <lastSeen></lastSeen>  <!-- Whydah 2.1 date and time of last registered user session -->\n" +
                    "    <application ID=\"2349785543\">\n" +
                    "        <applicationName>Whydah.net</applicationName>\n" +
                    "           <organizationName>Kunde 3</organizationName>\n" +
                    "              <role name=\"styremedlem\" value=\"\"/>\n" +
                    "              <role name=\"president\" value=\"\"/>\n" +
                    "           <organizationName>Kunde 4</organizationName>\n" +
                    "              <role name=\"styremedlem\" value=\"\"/>\n" +
                    "    </application>\n" +
                    "    <application ID=\"appa\">\n" +
                    "        <applicationName>whydag.org</applicationName>\n" +
                    "        <organizationName>Kunde 1</organizationName>\n" +
                    "        <role name=\"styremedlem\" value=\"Valla\"/>\n" +
                    "    </application>\n" +
                    " \n" +
                    "    <ns2:link type=\"application/xml\" href=\"/\" rel=\"self\"/>\n" +
                    "    <hash type=\"MD5\">8a37ef9624ed93db4873035b0de3d1ca</hash>\n" +
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

        public static String userDummyIdentityXML(){
                return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<whydahuser>\n" +
                        "   <identity>\n" +
                        "      <username>test_name</username>\n" +
                        "      <cellPhone>87654321</cellPhone>\n" +
                        "      <email>whydahadmin@getwhydah.com</email>\n" +
                        "      <firstname>UserAdmin</firstname>\n" +
                        "      <lastname>UserAdminWebApp</lastname>\n" +
                        "      <personRef>42</personRef>\n" +
                        "      <UID>useradmin</UID>\n" +
                        "   </identity>\n" +
                        "</whydahuser>";
        }

        public static String userDummyIdentityJson(){
                return "{  \n" +
                        "   \"username\":\"test_name\",\n" +
                        "   \"firstName\":\"Karl\",\n" +
                        "   \"lastName\":\"Manager\",\n" +
                        "   \"personRef\":\"002\",\n" +
                        "   \"email\":\"leon.ho@altran.com\",\n" +
                        "   \"cellPhone\":\"98765432\",\n" +
                        "   \"uid\":\"acsmanager\",\n" +
                        "   \"password\":null,\n" +
                        "   \"personName\":\"Karl Manager\"\n" +
                        "}";
        }
}
