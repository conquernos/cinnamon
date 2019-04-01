package org.conquernos.cinnamon.utils.config;

import org.conquernos.cinnamon.utils.file.FileUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import jodd.util.ClassLoaderUtil;

import java.io.File;


public class ConfigLoader {

	/**
	 * conf, properties, json
	 */
	public static Config load(String confFilePath) {
		String confDir = FileUtils.getDirectoryPath(confFilePath);
		String baseNameOfConfigFile;
		if (confDir != null) {
			baseNameOfConfigFile = FileUtils.getFileName(confFilePath);
			ClassLoaderUtil.addFileToClassPath(new File(confDir), Thread.currentThread().getContextClassLoader());
		} else {
			baseNameOfConfigFile = confFilePath;
		}

		int idx = baseNameOfConfigFile.lastIndexOf('.');
		if (idx != -1) {
			baseNameOfConfigFile = baseNameOfConfigFile.substring(0, idx);
		}

		return ConfigFactory.load(baseNameOfConfigFile);
	}

	public static Configuration build(Class<? extends Configuration> configurationClass, String configFilePath) {
		try {
			Config config = load(configFilePath);
			return configurationClass.getConstructor(Config.class).newInstance(config);
		} catch (Exception e) {
			throw new ConfigException(e);
		}
	}

	public static Configuration build(Class<? extends Configuration> configurationClass, Config config) {
		try {
			return configurationClass.getConstructor(Config.class).newInstance(config);
		} catch (Exception e) {
			throw new ConfigException(e);
		}
	}

	public static Configuration build(Class<? extends Configuration> configurationClass, Object... args) {
		try {
			return configurationClass.getConstructor(args.getClass()).newInstance(args);
		} catch (Exception e) {
			throw new ConfigException(e);
		}
	}

}
