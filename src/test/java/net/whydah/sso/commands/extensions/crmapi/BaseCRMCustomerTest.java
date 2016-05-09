package net.whydah.sso.commands.extensions.crmapi;

import net.whydah.sso.util.SSLTool;
import net.whydah.sso.util.SystemTestBaseConfig;

import org.junit.BeforeClass;

import java.net.URI;
import java.util.Random;

public class BaseCRMCustomerTest {
	protected static SystemTestBaseConfig config;
	
	String contenttype = "image/jpeg";
	
	
	
	
	@BeforeClass
	public static void setup() throws Exception {
		config = new SystemTestBaseConfig();
		SSLTool.disableCertificateValidation();
		//config.setSystemTest(false);
	}
	
	protected byte[] generateDummyCustomerPhoto(){
		byte[] customerPhotoData = {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82,
	            0, 0, 0, 15, 0, 0, 0, 15, 8, 6, 0, 0, 0, 59, -42, -107,
	            74, 0, 0, 0, 64, 73, 68, 65, 84, 120, -38, 99, 96, -64, 14, -2,
	            99, -63, 68, 1, 100, -59, -1, -79, -120, 17, -44, -8, 31, -121, 28, 81,
	            26, -1, -29, 113, 13, 78, -51, 100, -125, -1, -108, 24, 64, 86, -24, -30,
	            11, 101, -6, -37, 76, -106, -97, 25, 104, 17, 96, -76, 77, 97, 20, -89,
	            109, -110, 114, 21, 0, -82, -127, 56, -56, 56, 76, -17, -42, 0, 0, 0,
	            0, 73, 69, 78, 68, -82, 66, 96, -126};
		byte[] random = new byte[99];
		new Random().nextBytes(random);
		return mergeBytes(customerPhotoData, random) ;
	}
	
	byte[] mergeBytes(byte[]... bytes) {
        
        int size = 0;
        for (byte[] array : bytes) {
        	if(array ==null) array = new byte[0];
            size += array.length;
        }
        byte[] result = new byte[size];
        int lastPos = 0;
        for (byte[] b : bytes) {
        	if(b ==null) b = new byte[0];
            System.arraycopy(b, 0, result, lastPos, b.length);
            lastPos += b.length;
        }
        return result;
    }

	protected String generateDummyCustomerData(String customerRefId){
		String personJsonData = "{\n" +
	            "  \"id\" : \"" + customerRefId + "\",\n" +
	            "  \"firstname\" : \"First\",\n" +
	            "  \"lastname\" : \"Lastname\",\n" +
	            "  \"emailaddresses\" : {\n" +
	            "    \"jobb\" : {\n" +
	            "      \"emailaddress\" : \"totto@capraconsulting.no\",\n" +
	            "      \"tags\" : \"jobb, Capra\"\n" +
	            "    },\n" +
	            "    \"kobb-kunde\" : {\n" +
	            "      \"emailaddress\" : \"thor.henning.hetland@nmd.no\",\n" +
	            "      \"tags\" : \"jobb, kunde\"\n" +
	            "    },\n" +
	            "    \"community\" : {\n" +
	            "      \"emailaddress\" : \"totto@cantara.no\",\n" +
	            "      \"tags\" : \"opensource, privat, Whydah\"\n" +
	            "    },\n" +
	            "    \"hjemme\" : {\n" +
	            "      \"emailaddress\" : \"totto@totto.org\",\n" +
	            "      \"tags\" : \"hjemme, privat, OID\"\n" +
	            "    }\n" +
	            "  },\n" +
	            "  \"phonenumbers\" : {\n" +
	            "    \"tja\" : {\n" +
	            "      \"phonenumber\" : \"privat\",\n" +
	            "      \"tags\" : \"96909999\"\n" +
	            "    }\n" +
	            "  },\n" +
	            "  \"defaultAddressLabel\" : null,\n" +
	            "  \"deliveryaddresses\" : {\n" +
	            "    \"work, override\" : {\n" +
	            "      \"addressLine1\" : \"Stenersgata 2\",\n" +
	            "      \"addressLine2\" : null,\n" +
	            "      \"postalcode\" : \"0184\",\n" +
	            "      \"postalcity\" : \"Oslo\"\n" +
	            "    },\n" +
	            "    \"home\" : {\n" +
	            "      \"addressLine1\" : \"Møllefaret 30E\",\n" +
	            "      \"addressLine2\" : null,\n" +
	            "      \"postalcode\" : \"0750\",\n" +
	            "      \"postalcity\" : \"Oslo\"\n" +
	            "    }\n" +
	            "  }\n" +
	            "}";
		
		return personJsonData;
	}

	protected String generateUniqueuePersonRef() {
		Random rand = new Random();
		rand.setSeed(new java.util.Date().getTime());
		String user = "123456" + Integer.toString(rand.nextInt(100000000));
		return user;

	}
}
