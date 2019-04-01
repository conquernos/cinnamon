package org.conquernos.cinnamon.exception.kafka;


import org.conquernos.cinnamon.exception.CinnamonException;


public class CinnamonKafkaException extends CinnamonException {

	public CinnamonKafkaException() {
	}

	public CinnamonKafkaException(String message) {
		super(message);
	}

	public CinnamonKafkaException(String message, Throwable cause) {
		super(message, cause);
	}

	public CinnamonKafkaException(Throwable cause) {
		super(cause);
	}

	public CinnamonKafkaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
