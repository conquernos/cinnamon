package org.conquernos.cinnamon.config;


import akka.util.Timeout;
import org.conquernos.cinnamon.utils.config.Configuration;
import com.typesafe.config.Config;

import java.util.concurrent.TimeUnit;


public abstract class CinnamonConfig extends Configuration {

	private final Timeout askTimeout;
	private final String host;
	private final Integer port;


	protected CinnamonConfig(Config config) {
		super(config);

		host = getStringFromConfig(config, "akka.remote.netty.tcp.hostname", true);
		port = getIntegerFromConfig(config, "akka.remote.netty.tcp.port", true);
		askTimeout = new Timeout(getIntegerFromConfig(config, "akka.ask.timeout", 10), TimeUnit.SECONDS);
	}

}
