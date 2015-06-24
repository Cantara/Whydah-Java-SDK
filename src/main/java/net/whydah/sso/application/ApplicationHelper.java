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
                return "{\n" +
                        "  \"applications\": [\n" +
                        "    {\n" +
                        "      \"id\": \"11\",\n" +
                        "      \"name\": \"SecurityTokenService\",\n" +
                        "      \"applicationUrl\": \"https://webtest.example.com\",\n" +
                        "      \"applicationLogo\": \"https://webtest.exapmle.com/test.png\",\n" +
                        "      \"applicationDescription\": \"Web enabled proscess admin tool.\",\n" +
                        "      \"applicationAuditLevel\": 0,\n" +
                        "      \"userTokenFilter\": true,\n" +
                        "      \"defaults\": {\n" +
                        "        \"defaultRoleName\": \"SSOApplication\",\n" +
                        "        \"defaultOrgName\": \"Whydah\"\n" +
                        "      },\n" +
                        "      \"organisationNames\": [\n" +
                        "        \"whydah.org\",\n" +
                        "        \"getwhydah.com\",\n" +
                        "        \"cantara.no\"\n" +
                        "      ],\n" +
                        "      \"roleNames\": [\n" +
                        "        \"WhydahAdmin\",\n" +
                        "        \"WhydahUser\"\n" +
                        "      ],\n" +
                        "      \"security\": {\n" +
                        "        \"minimumSecurityLevel\": 0,\n" +
                        "        \"minimumDEFCON\": 3,\n" +
                        "        \"minimumUpdateFrequency\": \"60d\",\n" +
                        "        \"crypto\": [\n" +
                        "          {\n" +
                        "            \"cryptoId\": \"asdfaddsgasd1234\",\n" +
                        "            \"algorithm\": \"forwardHash128\",\n" +
                        "            \"seed\": \"adsfsadf\",\n" +
                        "            \"secret\": \"asdfdlkflsljløfdsplk\",\n" +
                        "            \"applicationSecret\": \"6r46g3q986Ep6By7B9J46m96D\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"12\",\n" +
                        "      \"name\": \"UserAdminSerevice\",\n" +
                        "      \"applicationUrl\": \"https://processService.example.com\",\n" +
                        "      \"applicationLogo\": \"https://processService.exapmle.com/process.png\",\n" +
                        "      \"applicationDescription\": \"Process engine.\",\n" +
                        "      \"applicationAuditLevel\": 0,\n" +
                        "      \"userTokenFilter\": true,\n" +
                        "      \"defaults\": {\n" +
                        "        \"defaultRoleName\": \"SSOApplication\",\n" +
                        "        \"defaultOrgName\": \"Whydah\"\n" +
                        "      },\n" +
                        "      \"organisationNames\": [\n" +
                        "        \"whydah.org\",\n" +
                        "        \"getwhydah.com\",\n" +
                        "        \"cantara.no\"\n" +
                        "      ],\n" +
                        "      \"roleNames\": [\n" +
                        "        \"WhydahAdmin\",\n" +
                        "        \"WhydahUser\"\n" +
                        "      ],\n" +
                        "      \"security\": {\n" +
                        "        \"minimumSecurityLevel\": 0,\n" +
                        "        \"minimumDEFCON\": 3,\n" +
                        "        \"minimumUpdateFrequency\": \"60d\",\n" +
                        "        \"crypto\": [\n" +
                        "          {\n" +
                        "            \"cryptoId\": \"321asdfaddsgasd\",\n" +
                        "            \"algorithm\": \"forwardHash128\",\n" +
                        "            \"seed\": \"321adsfsadf\",\n" +
                        "            \"secret\": \"321asdfdlkflsljløfdsplk\",\n" +
                        "            \"applicationSecret\": \"9ju592A4t8dzz8mz7a5QQJ7Px\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"15\",\n" +
                        "      \"name\": \"SSOLoginWebApplication\",\n" +
                        "      \"applicationUrl\": \"https://processService.example.com\",\n" +
                        "      \"applicationLogo\": \"https://processService.exapmle.com/process.png\",\n" +
                        "      \"applicationDescription\": \"Process engine.\",\n" +
                        "      \"applicationAuditLevel\": 0,\n" +
                        "      \"userTokenFilter\": true,\n" +
                        "      \"defaults\": {\n" +
                        "        \"defaultRoleName\": \"WhydahUserAdmin\",\n" +
                        "        \"defaultOrgName\": \"Whydah\"\n" +
                        "      },\n" +
                        "      \"organisationNames\": [\n" +
                        "        \"whydah.org\",\n" +
                        "        \"getwhydah.com\",\n" +
                        "        \"cantara.no\"\n" +
                        "      ],\n" +
                        "      \"roleNames\": [\n" +
                        "        \"WhydahAdmin\",\n" +
                        "        \"WhydahUser\"\n" +
                        "      ],\n" +
                        "      \"security\": {\n" +
                        "        \"minimumSecurityLevel\": 0,\n" +
                        "        \"minimumDEFCON\": 3,\n" +
                        "        \"minimumUpdateFrequency\": \"60d\",\n" +
                        "        \"crypto\": [\n" +
                        "          {\n" +
                        "            \"cryptoId\": \"321asdfaddsgasd\",\n" +
                        "            \"algorithm\": \"forwardHash128\",\n" +
                        "            \"seed\": \"321adsfsadf\",\n" +
                        "            \"secret\": \"321asdfdlkflsljløfdsplk\",\n" +
                        "            \"applicationSecret\": \"9EH5u5wJFKsUvJFmhypwK7j6D\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"19\",\n" +
                        "      \"name\": \"UserAdminWebApp\",\n" +
                        "      \"applicationUrl\": \"https://processService.example.com\",\n" +
                        "      \"applicationLogo\": \"https://processService.exapmle.com/process.png\",\n" +
                        "      \"applicationDescription\": \"Process engine.\",\n" +
                        "      \"applicationAuditLevel\": 0,\n" +
                        "      \"userTokenFilter\": true,\n" +
                        "      \"defaults\": {\n" +
                        "        \"defaultRoleName\": \"WhydahUserAdmin\",\n" +
                        "        \"defaultOrgName\": \"Whydah\"\n" +
                        "      },\n" +
                        "      \"organisationNames\": [\n" +
                        "        \"whydah.org\",\n" +
                        "        \"getwhydah.com\",\n" +
                        "        \"cantara.no\"\n" +
                        "      ],\n" +
                        "      \"roleNames\": [\n" +
                        "        \"WhydahAdmin\",\n" +
                        "        \"WhydahUser\"\n" +
                        "      ],\n" +
                        "      \"security\": {\n" +
                        "        \"minimumSecurityLevel\": 0,\n" +
                        "        \"minimumDEFCON\": 3,\n" +
                        "        \"minimumUpdateFrequency\": \"60d\",\n" +
                        "        \"crypto\": [\n" +
                        "          {\n" +
                        "            \"cryptoId\": \"321asdfaddsgasd\",\n" +
                        "            \"algorithm\": \"forwardHash128\",\n" +
                        "            \"seed\": \"321adsfsadf\",\n" +
                        "            \"secret\": \"321asdfdlkflsljløfdsplk\",\n" +
                        "            \"applicationSecret\": \"9ju592A4t8dzz8mz7a5QQJ7Px\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"99\",\n" +
                        "      \"name\": \"WhydahTestWebApplication\",\n" +
                        "      \"applicationUrl\": \"https://processService.example.com\",\n" +
                        "      \"applicationLogo\": \"https://processService.exapmle.com/process.png\",\n" +
                        "      \"applicationDescription\": \"Process engine.\",\n" +
                        "      \"applicationAuditLevel\": 0,\n" +
                        "      \"userTokenFilter\": true,\n" +
                        "      \"defaults\": {\n" +
                        "        \"defaultRoleName\": \"WhydahUserAdmin\",\n" +
                        "        \"defaultOrgName\": \"Whydah\"\n" +
                        "      },\n" +
                        "      \"organisationNames\": [\n" +
                        "        \"whydah.org\",\n" +
                        "        \"getwhydah.com\",\n" +
                        "        \"cantara.no\"\n" +
                        "      ],\n" +
                        "      \"roleNames\": [\n" +
                        "        \"WhydahAdmin\",\n" +
                        "        \"WhydahUser\"\n" +
                        "      ],\n" +
                        "      \"security\": {\n" +
                        "        \"minimumSecurityLevel\": 0,\n" +
                        "        \"minimumDEFCON\": 3,\n" +
                        "        \"minimumUpdateFrequency\": \"60d\",\n" +
                        "        \"crypto\": [\n" +
                        "          {\n" +
                        "            \"cryptoId\": \"321asdfaddsgasd\",\n" +
                        "            \"algorithm\": \"forwardHash128\",\n" +
                        "            \"seed\": \"321adsfsadf\",\n" +
                        "            \"secret\": \"321asdfdlkflsljløfdsplk\",\n" +
                        "            \"applicationSecret\": \"33879936R6Jr47D4Hj5R6p9qT\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"100\",\n" +
                        "      \"name\": \"ACS\",\n" +
                        "      \"applicationUrl\": \"https://processService.example.com\",\n" +
                        "      \"applicationLogo\": \"https://processService.exapmle.com/process.png\",\n" +
                        "      \"applicationDescription\": \"Process engine.\",\n" +
                        "      \"applicationAuditLevel\": 0,\n" +
                        "      \"userTokenFilter\": true,\n" +
                        "      \"defaults\": {\n" +
                        "        \"defaultRoleName\": \"Employee\",\n" +
                        "        \"defaultOrgName\": \"ACSOrganization\"\n" +
                        "      },\n" +
                        "      \"organisationNames\": [\n" +
                        "        \"whydah.org\",\n" +
                        "        \"getwhydah.com\",\n" +
                        "        \"cantara.no\"\n" +
                        "      ],\n" +
                        "      \"roleNames\": [\n" +
                        "        \"WhydahAdmin\",\n" +
                        "        \"WhydahUser\"\n" +
                        "      ],\n" +
                        "      \"security\": {\n" +
                        "        \"minimumSecurityLevel\": 0,\n" +
                        "        \"minimumDEFCON\": 3,\n" +
                        "        \"minimumUpdateFrequency\": \"60d\",\n" +
                        "        \"crypto\": [\n" +
                        "          {\n" +
                        "            \"cryptoId\": \"321asdfaddsgasd\",\n" +
                        "            \"algorithm\": \"forwardHash128\",\n" +
                        "            \"seed\": \"321adsfsadf\",\n" +
                        "            \"secret\": \"321asdfdlkflsljløfdsplk\",\n" +
                        "            \"applicationSecret\": \"45fhRM6nbKZ2wfC6RMmMuzXpk\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"id\": \"2001\",\n" +
                        "      \"name\": \"m2Circle\",\n" +
                        "      \"applicationUrl\": \"https://processService.example.com\",\n" +
                        "      \"applicationLogo\": \"https://processService.exapmle.com/process.png\",\n" +
                        "      \"applicationDescription\": \"Process engine.\",\n" +
                        "      \"applicationAuditLevel\": 0,\n" +
                        "      \"userTokenFilter\": true,\n" +
                        "      \"defaults\": {\n" +
                        "        \"defaultRoleName\": \"member\",\n" +
                        "        \"defaultOrgName\": \"Whydah\"\n" +
                        "      },\n" +
                        "      \"organisationNames\": [\n" +
                        "        \"whydah.org\",\n" +
                        "        \"getwhydah.com\",\n" +
                        "        \"cantara.no\"\n" +
                        "      ],\n" +
                        "      \"roleNames\": [\n" +
                        "        \"WhydahAdmin\",\n" +
                        "        \"WhydahUser\"\n" +
                        "      ],\n" +
                        "      \"security\": {\n" +
                        "        \"minimumSecurityLevel\": 0,\n" +
                        "        \"minimumDEFCON\": 3,\n" +
                        "        \"minimumUpdateFrequency\": \"60d\",\n" +
                        "        \"crypto\": [\n" +
                        "          {\n" +
                        "            \"cryptoId\": \"321asdfaddsgasd\",\n" +
                        "            \"algorithm\": \"forwardHash128\",\n" +
                        "            \"seed\": \"321adsfsadf\",\n" +
                        "            \"secret\": \"321asdfdlkflsljløfdsplk\",\n" +
                        "            \"applicationSecret\": \"YKHH54bNpnvQEF2vCJSWtctB\"\n" +
                        "          }\n" +
                        "        ]\n" +
                        "      }\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";
        }
}
