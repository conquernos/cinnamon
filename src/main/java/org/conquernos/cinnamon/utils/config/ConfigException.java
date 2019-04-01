package org.conquernos.cinnamon.utils.config;

public class ConfigException extends RuntimeException {

	public ConfigException() {
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}

	public static class WrongPathOrNullValue extends ConfigException {

		public WrongPathOrNullValue(String path) {
			super("ConfigException : must have the path '" + path + "' and value");
		}

	}

	public static class WrongTypeValue extends ConfigException {

		public WrongTypeValue(String path) {
			super("ConfigException : the value of the path '" + path + "' is wrong type");
		}

	}

}
