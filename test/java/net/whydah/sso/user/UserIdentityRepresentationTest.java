package net.whydah.sso.user;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

import static org.junit.Assert.assertEquals;

/**
 * Created by baardl on 18.06.15.
 */
public class UserIdentityRepresentationTest {

    private String username = null;
    private UserIdentityRepresentation userIdentity = null;
    private static final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    @Before
    public void setUp() throws Exception {
        username = "_temp_username_" + System.currentTimeMillis();
         userIdentity = new UserIdentityRepresentation(username,"first","last","ref",username +"@example.com","+4712345678");
    }

    @Test
    public void testToXML() throws Exception {
        DocumentBuilder db = dbf.newDocumentBuilder();
        String userIdentityXml = userIdentity.toXML();
        Document doc = db.parse(new InputSource(new StringReader(userIdentityXml)));
        XPath xPath = XPathFactory.newInstance().newXPath();

        String usernameXml = (String) xPath.evaluate("/whydahuser/identity/username", doc, XPathConstants.STRING);
        assertEquals(username, usernameXml);
        String cellPhone = (String) xPath.evaluate("/whydahuser/identity/cellPhone", doc, XPathConstants.STRING);
        assertEquals("+4712345678", cellPhone);
        String personRef = (String) xPath.evaluate("/whydahuser/identity/personRef", doc, XPathConstants.STRING);
        assertEquals("ref", personRef);
        String firstName = (String) xPath.evaluate("/whydahuser/identity/firstname", doc, XPathConstants.STRING);
        assertEquals("first", firstName);
        String lastName = (String) xPath.evaluate("/whydahuser/identity/lastname", doc, XPathConstants.STRING);
        assertEquals("last", lastName);
        String email = (String) xPath.evaluate("/whydahuser/identity/email", doc, XPathConstants.STRING);
        assertEquals(username +"@example.com",email);
    }
}