package org.conquernos.cinnamon.exception;


public class CinnamonException extends Exception {

	public CinnamonException() {
	}

	public CinnamonException(String message) {
		super(message);
	}

	public CinnamonException(String message, Throwable cause) {
		super(message, cause);
	}

	public CinnamonException(Throwable cause) {
		super(cause);
	}

	public CinnamonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
