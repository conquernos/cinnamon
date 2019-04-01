package org.conquernos.cinnamon.utils.config;

import com.typesafe.config.Config;
import jodd.util.ClassLoaderUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.Serializable;
import java.util.List;


public abstract class Configuration implements Serializable {

    private final Config config;

    protected Configuration(Config config) {
        this.config = config;
    }

    protected void loadClassPath(List<String> resourcePaths) {
        for (String path : resourcePaths) {
            loadClassPath(path);
        }
    }

    protected void loadClassPath(String resourcePath) {
        ClassLoaderUtil.addFileToClassPath(new File(resourcePath), Thread.currentThread().getContextClassLoader());
    }

    public Config getConfig() {
        return config;
    }

    public static String toListString(List<String> list) {
        StringBuilder listString = new StringBuilder();
        list.forEach(str -> {
            if (listString.length() > 0) listString.append(',');
            listString.append(str);
        });

        return listString.toString();
    }

    protected static Config getConfig(Config config, String path) {
        return config.getConfig(path);
    }

    protected static String getStringFromConfig(Config config, String path, String defaultValue) {
        String value = getStringFromConfig(config, path, false);
        return (value == null) ? defaultValue : value;
    }

    protected static String getStringFromConfig(Config config, String path, boolean notNull) {
        String value = null;
        try {
            if (config.hasPath(path)) {
                try {
                    value = config.getString(path).trim();
                } catch (com.typesafe.config.ConfigException.WrongType e) {
                    throw new ConfigException.WrongTypeValue(path);
                }
                if (value.length() == 0) value = null;
            }
        } catch (com.typesafe.config.ConfigException.BadPath e) {
            throw new ConfigException.WrongPathOrNullValue(path);
        }

        if (notNull && value == null) throw new ConfigException.WrongPathOrNullValue(path);

        return value;
    }

    protected static Integer getIntegerFromConfig(Config config, String path, Integer defaultValue) {
        Integer value = getIntegerFromConfig(config, path, false);
        return (value == null) ? defaultValue : value;
    }

    protected static Integer getIntegerFromConfig(Config config, String path, boolean notNull) {
        Integer value = null;
        try {
            if (config.hasPath(path)) {
                try {
                    value = config.getInt(path);
                } catch (com.typesafe.config.ConfigException.WrongType e) {
                    throw new ConfigException.WrongTypeValue(path);
                }
            }
        } catch (com.typesafe.config.ConfigException.BadPath e) {
            throw new ConfigException.WrongPathOrNullValue(path);
        }

        if (notNull && value == null) throw new ConfigException.WrongPathOrNullValue(path);

        return value;
    }

    protected static Long getLongFromConfig(Config config, String path, Long defaultValue) {
        Long value = getLongFromConfig(config, path, false);
        return (value == null) ? defaultValue : value;
    }

    protected static Long getLongFromConfig(Config config, String path, boolean notNull) {
        Long value = null;
        try {
            if (config.hasPath(path)) {
                try {
                    value = config.getLong(path);
                } catch (com.typesafe.config.ConfigException.WrongType e) {
                    throw new ConfigException.WrongTypeValue(path);
                }
            }
        } catch (com.typesafe.config.ConfigException.BadPath e) {
            throw new ConfigException.WrongPathOrNullValue(path);
        }

        if (notNull && value == null) throw new ConfigException.WrongPathOrNullValue(path);

        return value;
    }

    protected static Double getDoubleFromConfig(Config config, String path, Double defaultValue) {
        Double value = getDoubleFromConfig(config, path, false);
        return (value == null) ? defaultValue : value;
    }

    protected static Double getDoubleFromConfig(Config config, String path, boolean notNull) {
        Double value = null;
        try {
            if (config.hasPath(path)) {
                try {
                    value = config.getDouble(path);
                } catch (com.typesafe.config.ConfigException.WrongType e) {
                    throw new ConfigException.WrongTypeValue(path);
                }
            }
        } catch (com.typesafe.config.ConfigException.BadPath e) {
            throw new ConfigException.WrongPathOrNullValue(path);
        }

        if (notNull && value == null) throw new ConfigException.WrongPathOrNullValue(path);

        return value;
    }

    protected static List<String> getStringListFromConfig(Config config, String path, List<String> defaultValue) {
        List<String> value = getStringListFromConfig(config, path, false);
        return (value == null) ? defaultValue : value;
    }

    protected static List<String> getStringListFromConfig(Config config, String path, boolean notNull) {
        List<String> values = null;
        try {
            if (config.hasPath(path)) {
                try {
                    values = config.getStringList(path);
                } catch (com.typesafe.config.ConfigException.WrongType e) {
                    throw new ConfigException.WrongTypeValue(path);
                }

                if (values.size() == 0) {
                    values = null;
                } else {
                    for (int valueIdx = 0; valueIdx < values.size(); valueIdx++) {
                        values.set(valueIdx, values.get(valueIdx).trim());
                    }
                }
            }
        } catch (com.typesafe.config.ConfigException.BadPath e) {
            throw new ConfigException.WrongPathOrNullValue(path);
        }

        if (notNull && values == null) throw new ConfigException.WrongPathOrNullValue(path);

        return values;
    }

    protected static DateTime getDateTimeFromConfig(Config config, String path, DateTimeFormatter dateFormatter, DateTime defaultValue) {
        DateTime value = getDateTimeFromConfig(config, path, dateFormatter, false);
        return (value == null) ? defaultValue : value;
    }

    protected static DateTime getDateTimeFromConfig(Config config, String path, DateTimeFormatter dateFormatter, boolean notNull) {
        DateTime value = null;
        try {
            if (config.hasPath(path)) {
                String stringValue;
                try {
                    stringValue = config.getString(path).trim();
                } catch (com.typesafe.config.ConfigException.WrongType e) {
                    throw new ConfigException.WrongTypeValue(path);
                }
                if (stringValue.length() == 0) {
                    value = null;
                } else {
                    try {
                        value = dateFormatter.parseDateTime(stringValue);
                    } catch (IllegalArgumentException e) {
                        throw new ConfigException.WrongTypeValue(path);
                    }
                }
            }
        } catch (com.typesafe.config.ConfigException.BadPath e) {
            throw new ConfigException.WrongPathOrNullValue(path);
        }

        if (notNull && value == null) throw new ConfigException.WrongPathOrNullValue(path);

        return value;
    }

    protected static Boolean getBooleanFromConfig(Config config, String path, Boolean defaultValue) {
        Boolean value = getBooleanFromConfig(config, path, false);
        return (value == null) ? defaultValue : value;
    }

    protected static Boolean getBooleanFromConfig(Config config, String path, boolean notNull) {
        Boolean value = null;
        try {
            if (config.hasPath(path)) {
                try {
                    value = config.getBoolean(path);
                } catch (com.typesafe.config.ConfigException.WrongType e) {
                    throw new ConfigException.WrongTypeValue(path);
                }
            }
        } catch (com.typesafe.config.ConfigException.BadPath e) {
            throw new ConfigException.WrongPathOrNullValue(path);
        }

        if (notNull && value == null) throw new ConfigException.WrongPathOrNullValue(path);

        return value;
    }

    protected static boolean hasStringValue(Config config, String path) {
        return getStringFromConfig(config, path, false) != null;
    }

    protected static boolean hasIntegerValue(Config config, String path) {
        return getIntegerFromConfig(config, path, false) != null;
    }

    protected static boolean hasStringListValue(Config config, String path) {
        return getStringListFromConfig(config, path, false) != null;
    }

}
