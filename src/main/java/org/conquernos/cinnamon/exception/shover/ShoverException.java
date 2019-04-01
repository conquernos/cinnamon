package org.conquernos.cinnamon.exception.shover;


import org.conquernos.cinnamon.exception.CinnamonException;


public class ShoverException extends CinnamonException {

    public ShoverException() {
    }

    public ShoverException(String message) {
        super(message);
    }

    public ShoverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShoverException(Throwable cause) {
        super(cause);
    }

    public ShoverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
