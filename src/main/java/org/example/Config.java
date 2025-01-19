package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IOException("Config file not found: config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config properties", e);
        }
    }

    /**
     * Retrieves a property value by key.
     *
     * @param key the property key
     * @return the property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}