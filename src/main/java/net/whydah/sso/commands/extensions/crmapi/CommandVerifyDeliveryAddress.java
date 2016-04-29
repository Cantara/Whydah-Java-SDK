package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;
import net.whydah.sso.commands.baseclasses.HttpSender;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class CommandVerifyDeliveryAddress extends BaseHttpGetHystrixCommand<String> {

    
    private static final String googleMapsUrl = "https://maps-api-ssl.google.com/maps/api/geocode/xml";
    private static final String googleMapsUrlParamer = "&sensor=false&client=";
    private static byte[] key;
    private static String googleMapsClientID;
    private String deliveryAddress;

    public CommandVerifyDeliveryAddress(String streetAddress, String googleMapsClientID, byte[] googlemapapikey) {
    	super(URI.create("https://maps-api-ssl.google.com/maps/api/geocode/xml?address=Frankfurstein+ring+105a,M%C3%BCnchen,de,80000,&sensor=false&client=gme-kickzag&signature=RD8P7J07rJbfmClUeMEY4adIoTs="), "","", "STSApplicationAdminGroup", 3000);
      
        this.deliveryAddress = streetAddress;
        this.key = googlemapapikey;
        this.googleMapsClientID = googleMapsClientID;


        if (streetAddress == null) {
            log.error(TAG + " initialized with null-values - will fail");
        }


    }

    //  https://maps-api-ssl.google.com/maps/api/geocode/xml?address=Frankfurstein+ring+105a,M%C3%BCnchen,de,80000,&sensor=false&client=gme-kickzag&signature=RD8P7J07rJbfmClUeMEY4adIoTs=

//    @Override
//    protected String run() {
//        log.trace("CommandVerifyDeliveryAddress - whydahServiceUri={}", googleMapsUrl);
//
//        //   HttpRequest request = HttpRequest.get(signRequest(googleMapsUrl + "?address=" + deliveryAddress+googleMapsUrlParamer+googleMapsClientID)).contentType(HttpSender.APPLICATION_FORM_URLENCODED);
//        HttpRequest request = HttpRequest.get("https://maps-api-ssl.google.com/maps/api/geocode/xml?address=Frankfurstein+ring+105a,M%C3%BCnchen,de,80000,&sensor=false&client=gme-kickzag&signature=RD8P7J07rJbfmClUeMEY4adIoTs=").contentType(HttpSender.APPLICATION_FORM_URLENCODED);
//        int statusCode = request.code();
//        String responseBody = request.body();
//        switch (statusCode) {
//            case HttpSender.STATUS_OK:
//                log.debug("CommandVerifyDeliveryAddress - Response: {}", responseBody);
//                return responseBody;
//            default:
//                log.warn("Unexpected response from STS. Response is {} ", responseBody);
//
//        }
//        throw new RuntimeException("CommandVerifyDeliveryAddress -  failed");
//
//    }

    @Override
    protected String dealWithFailedResponse(String responseBody, int statusCode) {
    
    	 throw new RuntimeException("CommandVerifyDeliveryAddress -  failed");
    	
    }
    
    @Override
    protected HttpRequest dealWithRequestBeforeSend(HttpRequest request) {
    	return request.contentType(HttpSender.APPLICATION_FORM_URLENCODED);
    }

//    @Override
//    protected String getFallback() {
//        log.warn("CommandVerifyDeliveryAddress - fallback - whydahServiceUri={}", googleMapsUrl.toString());
//        return null;
//    }


    public String signRequest(String inputUrl) throws NoSuchAlgorithmException,
            InvalidKeyException, UnsupportedEncodingException, URISyntaxException, MalformedURLException {

        // Convert the string to a URL so we can parse it
        URL url = new URL(inputUrl);


        // Get an HMAC-SHA1 signing key from the raw key bytes
        SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

        // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(sha1Key);

        // compute the binary signature for the request
        byte[] sigBytes = mac.doFinal(url.getPath().getBytes());

        // base 64 encode the binary signature
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        String signature = Base64.getEncoder().encodeToString(sigBytes);

        // convert the signature to 'web safe' base 64
        signature = signature.replace('+', '-');
        signature = signature.replace('/', '_');

        return url + "&signature=" + signature;
    }

	@Override
	protected String getTargetPath() {
		// TODO Auto-generated method stub
		return null;
	}
}








