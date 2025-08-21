package app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    // Delay in milliseconds
    public static long getDelay() {
        String value = props.getProperty("delay");
        if (value == null) throw new RuntimeException("Missing 'delay' in config.properties");
        return Long.parseLong(value);
    }

    // Period in milliseconds
    public static long getPeriod() {
        String value = props.getProperty("period");
        if (value == null) throw new RuntimeException("Missing 'period' in config.properties");
        return Long.parseLong(value);
    }

    // Generic method to get comma-separated lists
    private static List<String> getList(String key) {
        String value = props.getProperty(key);
        if (value == null) throw new RuntimeException("Missing '" + key + "' in config.properties");
        return Arrays.asList(value.split("\\s*,\\s*")); // split by comma, trim spaces
    }

    // Cookie selectors
    public static List<String> getCookieIds() {
        return getList("cookie.ids");
    }

    public static List<String> getCookieClasses() {
        return getList("cookie.classes");
    }

    public static List<String> getCookieTexts() {
        return getList("cookie.texts");
    }

    public static String[] getPriceSelectors() {
        return new String[]{
                "//span[contains(text(),'TL')]",
                "//div[contains(@class,'price')]",
                "//span[contains(@class,'amount')]"
        };
    }
}