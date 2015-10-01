package net.whydah.sso.user;


import org.junit.Test;

public class UserJsonHelperTest {


    @Test
    public void testParseUserAggregateJson() throws Exception {
        String userAggregateJson = "{\"identity\":{\"username\":\"91905054\",\"firstName\":\"Thor Henning\",\"lastName\":\"Hetland\",\"personRef\":\"\",\"email\":\"totto@cantara.no\",\"cellPhone\":\"91905054\",\"uid\":\"00d07a25-efbe-484a-b00e-67859a106dd4\"},\"roles\":[],\"personName\":\"Thor Henning Hetland\",\"personRef\":\"\",\"uid\":\"00d07a25-efbe-484a-b00e-67859a106dd4\",\"lastName\":\"Hetland\",\"email\":\"totto@cantara.no\",\"firstName\":\"Thor Henning\",\"username\":\"91905054\",\"cellPhone\":\"91905054\"}";
        UserToken myToken = UserJsonHelper.fromUserAggregateJson(userAggregateJson);


    }

}
