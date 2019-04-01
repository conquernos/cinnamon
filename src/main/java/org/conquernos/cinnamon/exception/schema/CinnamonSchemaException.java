package org.conquernos.cinnamon.exception.schema;


import org.conquernos.cinnamon.exception.CinnamonException;


public class CinnamonSchemaException extends CinnamonException {

	public CinnamonSchemaException() {
	}

	public CinnamonSchemaException(String message) {
		super(message);
	}

	public CinnamonSchemaException(String message, Throwable cause) {
		super(message, cause);
	}

	public CinnamonSchemaException(Throwable cause) {
		super(cause);
	}

	public CinnamonSchemaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
