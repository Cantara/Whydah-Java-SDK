package net.whydah.sso.user;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by baardl on 22.06.15.
 */
public class UserRoleMapper {
    private static final Logger log = getLogger(UserRoleMapper.class);


    public static List<UserRole> rolesFromXml(String rolesXml){

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new InputSource(new StringReader(rolesXml)));
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("/application");
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
//            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList products = (NodeList) result;
            for (int i = 0; i < products.getLength(); i++) {
                Node n = products.item(i);
                if (n != null && n.getNodeType() == Node.ELEMENT_NODE) {
                    Element product = (Element) n;
                    String id = findValue(n,"id");
                    NodeList nodes = (NodeList)  expr.evaluate(product,XPathConstants.NODESET); //Find the 'title' in the 'product'
                    System.out.println("TITLE: " + nodes.item(0).getTextContent()); // And here is the title
                }
            }
        } catch (Exception e) {
            log.error("getRealName - userTokenXml - getTimestamp parsing error", e);
        }
        return null;
    }

    public static String findValue(Node nodeList,  String expression) {
        String value = "";
        try {
            XPath xPath = XPathFactory.newInstance().newXPath();

            XPathExpression xPathExpression = xPath.compile(expression);
            value = xPathExpression.evaluate(nodeList);
        } catch (Exception e) {
            log.warn("Failed to parse xml. Expression {}, xml {}, ", expression, nodeList, e);
        }
        return value;
    }
}
