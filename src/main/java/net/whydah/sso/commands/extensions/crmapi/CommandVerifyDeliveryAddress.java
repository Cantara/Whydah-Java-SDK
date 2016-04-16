package net.whydah.sso.commands.extensions.crmapi;

import com.github.kevinsawicki.http.HttpRequest;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import net.whydah.sso.commands.appauth.CommandLogonApplication;
import net.whydah.sso.util.HttpSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandVerifyDeliveryAddress extends HystrixCommand<String> {

    private static final Logger log = LoggerFactory.getLogger(CommandLogonApplication.class);
    private static final String googleMapsUrl = "https://maps-api-ssl.google.com/maps/api/geocode/xml";
    private String deliveryAddress;

    public CommandVerifyDeliveryAddress(String streetAddress) {
        super(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("STSApplicationAdminGroup")).andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                .withExecutionTimeoutInMilliseconds(3000)));

        this.deliveryAddress = streetAddress;

        if (streetAddress == null) {
            log.error("CommandVerifyDeliveryAddress initialized with null-values - will fail");
        }
        HystrixRequestContext.initializeContext();

    }

    //  https://maps-api-ssl.google.com/maps/api/geocode/xml?address=Frankfurstein+ring+105a,M%C3%BCnchen,de,80000,&sensor=false&client=gme-kickzag&signature=RD8P7J07rJbfmClUeMEY4adIoTs=


    @Override
    protected String run() {
        log.trace("CommandVerifyDeliveryAddress - uri={}", googleMapsUrl);

        HttpRequest request = HttpRequest.get(googleMapsUrl + "?address=" + deliveryAddress).contentType(HttpSender.APPLICATION_FORM_URLENCODED);
        int statusCode = request.code();
        String responseBody = request.body();
        switch (statusCode) {
            case HttpSender.STATUS_OK:
                log.debug("CommandVerifyDeliveryAddress - Response: {}", responseBody);
                return responseBody;
            default:
                log.warn("Unexpected response from STS. Response is {} ", responseBody);

        }
        throw new RuntimeException("CommandVerifyDeliveryAddress -  failed");

    }


    @Override
    protected String getFallback() {
        log.warn("CommandVerifyDeliveryAddress - fallback - uri={}", googleMapsUrl.toString());
        return null;
    }

}








