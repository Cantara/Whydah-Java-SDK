package net.whydah.sso.application;

/**
 * Created by totto on 12/3/14.
 */
public class ApplicationHelper {
        public static String getDummyApplicationToken() {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> \n " +

                    "    <applicationtoken>\n" +
                    "        <params>\n" +
                    "            <applicationtokenID>47289347982137421</applicationtokenID>\n" +
                    "            <applicationid>45</applicationid>\n" +
                    "            <applicationname>dummyapp</applicationname>\n" +
                    "            <expires>3453453</expires>\n" +
                    "        </params> \n" +
                    "           <Url type=\"application/xml\"" +
                    "                template=\"http://example.com/user/47289347982137421/get_usertoken_by_usertokenid\"/>" +
                    "    </applicationtoken>\n";
        }


        public static String getDummyAppllicationListJson(){
                return
                        "\n" +
                                "[\n" +
                                "   {\n" +
                                "      \"id\": \"100\",\n" +
                                "      \"name\": \"ACS\",\n" +
                                "      \"description\": null,\n" +
                                "      \"applicationUrl\": null,\n" +
                                "      \"logoUrl\": null,\n" +
                                "      \"roles\": [],\n" +
                                "      \"organizationNames\": [],\n" +
                                "      \"defaultRoleName\": \"Employee\",\n" +
                                "      \"defaultOrganizationName\": \"ACSOrganization\",\n" +
                                "      \"security\": {\n" +
                                "         \"minSecurityLevel\": \"0\",\n" +
                                "         \"minDEFCON\": \"DEFCON5\",\n" +
                                "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                                "         \"allowedIpAddresses\": [\n" +
                                "            \"0.0.0.0/0\"\n" +
                                "         ],\n" +
                                "         \"userTokenFilter\": \"true\",\n" +
                                "         \"secret\": \"45fhRM6nbKZ2wfC6RMmMuzXpk\"\n" +
                                "      },\n" +
                                "      \"acl\": []\n" +
                                "   },\n" +
                                "   {\n" +
                                "      \"id\": \"101\",\n" +
                                "      \"name\": \"ACSResource\",\n" +
                                "      \"description\": null,\n" +
                                "      \"applicationUrl\": null,\n" +
                                "      \"logoUrl\": null,\n" +
                                "      \"roles\": [],\n" +
                                "      \"organizationNames\": [],\n" +
                                "      \"defaultRoleName\": \"Employee\",\n" +
                                "      \"defaultOrganizationName\": \"ACSOrganization\",\n" +
                                "      \"security\": {\n" +
                                "         \"minSecurityLevel\": \"0\",\n" +
                                "         \"minDEFCON\": \"DEFCON5\",\n" +
                                "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                                "         \"allowedIpAddresses\": [\n" +
                                "            \"0.0.0.0/0\"\n" +
                                "         ],\n" +
                                "         \"userTokenFilter\": \"true\",\n" +
                                "         \"secret\": \"55fhRM6nbKZ2wfC6RMmMuzXpk\"\n" +
                                "      },\n" +
                                "      \"acl\": []\n" +
                                "   },\n" +
                                "   {\n" +
                                "      \"id\": \"11\",\n" +
                                "      \"name\": \"SecurityTokenService\",\n" +
                                "      \"description\": null,\n" +
                                "      \"applicationUrl\": null,\n" +
                                "      \"logoUrl\": null,\n" +
                                "      \"roles\": [],\n" +
                                "      \"organizationNames\": [],\n" +
                                "      \"defaultRoleName\": \"SSOApplication\",\n" +
                                "      \"defaultOrganizationName\": \"Whydah\",\n" +
                                "      \"security\": {\n" +
                                "         \"minSecurityLevel\": \"0\",\n" +
                                "         \"minDEFCON\": \"DEFCON5\",\n" +
                                "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                                "         \"allowedIpAddresses\": [\n" +
                                "            \"0.0.0.0/0\"\n" +
                                "         ],\n" +
                                "         \"userTokenFilter\": \"true\",\n" +
                                "         \"secret\": \"6r46g3q986Ep6By7B9J46m96D\"\n" +
                                "      },\n" +
                                "      \"acl\": []\n" +
                                "   },\n" +
                                "   {\n" +
                                "      \"id\": \"12\",\n" +
                                "      \"name\": \"UserAdminService\",\n" +
                                "      \"description\": null,\n" +
                                "      \"applicationUrl\": null,\n" +
                                "      \"logoUrl\": null,\n" +
                                "      \"roles\": [],\n" +
                                "      \"organizationNames\": [],\n" +
                                "      \"defaultRoleName\": \"WhydahUserAdmin\",\n" +
                                "      \"defaultOrganizationName\": \"Whydah\",\n" +
                                "      \"security\": {\n" +
                                "         \"minSecurityLevel\": \"0\",\n" +
                                "         \"minDEFCON\": \"DEFCON5\",\n" +
                                "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                                "         \"allowedIpAddresses\": [\n" +
                                "            \"0.0.0.0/0\"\n" +
                                "         ],\n" +
                                "         \"userTokenFilter\": \"true\",\n" +
                                "         \"secret\": \"9ju592A4t8dzz8mz7a5QQJ7Px\"\n" +
                                "      },\n" +
                                "      \"acl\": []\n" +
                                "   },\n" +
                                "   {\n" +
                                "      \"id\": \"15\",\n" +
                                "      \"name\": \"SSOLoginWebApplication\",\n" +
                                "      \"description\": null,\n" +
                                "      \"applicationUrl\": null,\n" +
                                "      \"logoUrl\": null,\n" +
                                "      \"roles\": [],\n" +
                                "      \"organizationNames\": [],\n" +
                                "      \"defaultRoleName\": \"SSOApplication\",\n" +
                                "      \"defaultOrganizationName\": \"Whydah\",\n" +
                                "      \"security\": {\n" +
                                "         \"minSecurityLevel\": \"0\",\n" +
                                "         \"minDEFCON\": \"DEFCON5\",\n" +
                                "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                                "         \"allowedIpAddresses\": [\n" +
                                "            \"0.0.0.0/0\"\n" +
                                "         ],\n" +
                                "         \"userTokenFilter\": \"true\",\n" +
                                "         \"secret\": \"33779936R6Jr47D4Hj5R6p9qT\"\n" +
                                "      },\n" +
                                "      \"acl\": []\n" +
                                "   },\n" +
                                "   {\n" +
                                "      \"id\": \"19\",\n" +
                                "      \"name\": \"UserAdminWebApp\",\n" +
                                "      \"description\": null,\n" +
                                "      \"applicationUrl\": null,\n" +
                                "      \"logoUrl\": null,\n" +
                                "      \"roles\": [],\n" +
                                "      \"organizationNames\": [],\n" +
                                "      \"defaultRoleName\": \"WhydahUserAdmin\",\n" +
                                "      \"defaultOrganizationName\": \"Whydah\",\n" +
                                "      \"security\": {\n" +
                                "         \"minSecurityLevel\": \"0\",\n" +
                                "         \"minDEFCON\": \"DEFCON5\",\n" +
                                "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                                "         \"allowedIpAddresses\": [\n" +
                                "            \"0.0.0.0/0\"\n" +
                                "         ],\n" +
                                "         \"userTokenFilter\": \"true\",\n" +
                                "         \"secret\": \"9EH5u5wJFKsUvJFmhypwK7j6D\"\n" +
                                "      },\n" +
                                "      \"acl\": []\n" +
                                "   },\n" +
                                "   {\n" +
                                "      \"id\": \"2001\",\n" +
                                "      \"name\": \"m2Circle\",\n" +
                                "      \"description\": null,\n" +
                                "      \"applicationUrl\": null,\n" +
                                "      \"logoUrl\": null,\n" +
                                "      \"roles\": [],\n" +
                                "      \"organizationNames\": [],\n" +
                                "      \"defaultRoleName\": \"member\",\n" +
                                "      \"defaultOrganizationName\": \"Whydah\",\n" +
                                "      \"security\": {\n" +
                                "         \"minSecurityLevel\": \"0\",\n" +
                                "         \"minDEFCON\": \"DEFCON5\",\n" +
                                "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                                "         \"allowedIpAddresses\": [\n" +
                                "            \"0.0.0.0/0\"\n" +
                                "         ],\n" +
                                "         \"userTokenFilter\": \"true\",\n" +
                                "         \"secret\": \"YKHH54bNpnvQEF2vCJSWtctB\"\n" +
                                "      },\n" +
                                "      \"acl\": []\n" +
                                "   },\n" +
                                "   {\n" +
                                "      \"id\": \"99\",\n" +
                                "      \"name\": \"WhydahTestWebApplication\",\n" +
                                "      \"description\": null,\n" +
                                "      \"applicationUrl\": null,\n" +
                                "      \"logoUrl\": null,\n" +
                                "      \"roles\": [],\n" +
                                "      \"organizationNames\": [],\n" +
                                "      \"defaultRoleName\": \"SSOApplication\",\n" +
                                "      \"defaultOrganizationName\": \"Whydah\",\n" +
                                "      \"security\": {\n" +
                                "         \"minSecurityLevel\": \"0\",\n" +
                                "         \"minDEFCON\": \"DEFCON5\",\n" +
                                "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                                "         \"allowedIpAddresses\": [\n" +
                                "            \"0.0.0.0/0\"\n" +
                                "         ],\n" +
                                "         \"userTokenFilter\": \"true\",\n" +
                                "         \"secret\": \"33879936R6Jr47D4Hj5R6p9qT\"\n" +
                                "      },\n" +
                                "      \"acl\": []\n" +
                                "   }\n" +
                                "]";
        }



        public static String getDummyAppllicationJson(){
                return "{\n" +
                        "      \"id\": \"100\",\n" +
                        "      \"name\": \"ACS\",\n" +
                        "      \"description\": null,\n" +
                        "      \"applicationUrl\": null,\n" +
                        "      \"logoUrl\": null,\n" +
                        "      \"roles\": [],\n" +
                        "      \"organizationNames\": [],\n" +
                        "      \"defaultRoleName\": \"Employee\",\n" +
                        "      \"defaultOrganizationName\": \"ACSOrganization\",\n" +
                        "      \"security\": {\n" +
                        "         \"minSecurityLevel\": \"0\",\n" +
                        "         \"minDEFCON\": \"DEFCON5\",\n" +
                        "         \"maxSessionTimoutSeconds\": \"86400\",\n" +
                        "         \"allowedIpAddresses\": [\n" +
                        "            \"0.0.0.0/0\"\n" +
                        "         ],\n" +
                        "         \"userTokenFilter\": \"true\",\n" +
                        "         \"secret\": \"45fhRM6nbKZ2wfC6RMmMuzXpk\"\n" +
                        "      },\n" +
                        "      \"acl\": []\n" +
                        "   }";
        }
}
