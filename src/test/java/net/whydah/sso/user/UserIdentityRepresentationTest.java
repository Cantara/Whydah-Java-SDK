package net.whydah.sso.user;

import javax.xml.parsers.DocumentBuilderFactory;

import net.whydah.sso.user.types.UserIdentity;

import org.junit.Before;

/**
 * Created by baardl on 18.06.15.
 */
public class UserIdentityRepresentationTest {

    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    private String username = null;
    private UserIdentity userIdentity = null;

    @Before
    public void setUp() throws Exception {
        username = "_temp_username_" + System.currentTimeMillis();
        userIdentity = new UserIdentity(username, "first", "last", "ref", username + "@example.com", "+4712345678");
    }

}