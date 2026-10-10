package com.hotelbooking.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigReader {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input =
                ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Không tìm thấy file config.properties");
            }

            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Không thể đọc file config.properties", e);
        }
    }

    public static String get(String key) {
        String value = properties.getProperty(key);

        if (value == null) {
            throw new RuntimeException("Không tìm thấy config: " + key);
        }

        return value;
    }
}
