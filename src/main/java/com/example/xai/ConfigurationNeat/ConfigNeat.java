package com.example.xai.ConfigurationNeat;

import java.io.InputStream;
import java.util.Properties;

/**
 * Um alle nötigen Parameter für NEAT zu speichern, wird diese Klasse verwendet.
 * Sie liest die Parameter aus einer Konfigurationsdatei und speichert sie in einer Properties-Instanz.
 * 
 * @version 1.0
 * @since 1.0
 * @autor Majid Moussa Adoyi
 * @date 10.07.2024
 */

public class ConfigNeat {
    private Properties properties = new Properties();

    public ConfigNeat(String filePath) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(filePath)) {
            if (input == null) {
                System.out.println("Sorry, unable to find " + filePath);
                return;
            }
            properties.load(input);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
