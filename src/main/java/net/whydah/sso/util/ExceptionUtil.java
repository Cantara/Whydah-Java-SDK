package net.whydah.sso.util;

import com.github.kevinsawicki.http.HttpRequest;

/**
 * Created by totto on 12/2/14.
 */
public class ExceptionUtil {


//    public static  String printableUrlErrorMessage(String errorMessage, WebTarget request, Response response) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(errorMessage);
//        sb.append(" Code: ");
//        if(response != null) {
//            sb.append(response.getStatus());
//            sb.append(" URL: ");
//        }
//        if(request != null) {
//            sb.append(request.toString());
//        }
//        return sb.toString();
//    }
    
    public static  String printableUrlErrorMessage(String errorMessage, HttpRequest request, int statusCode) {
        StringBuilder sb = new StringBuilder();
        sb.append(errorMessage);
        sb.append(" Code: ");
        sb.append(statusCode);
        sb.append(" URL: ");
        if(request != null) {
            sb.append(request.toString());
        }
        return sb.toString();
    }

}
