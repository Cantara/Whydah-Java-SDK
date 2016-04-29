package net.whydah.sso.util;

import com.github.kevinsawicki.http.HttpRequest;

public class ExceptionUtil {


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
