package net.whydah.sso.session;

public class WhydahApplicationSessionClosedException extends RuntimeException {

    public WhydahApplicationSessionClosedException() {
    }

    public WhydahApplicationSessionClosedException(String message) {
        super(message);
    }

    public WhydahApplicationSessionClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public WhydahApplicationSessionClosedException(Throwable cause) {
        super(cause);
    }

    public WhydahApplicationSessionClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
