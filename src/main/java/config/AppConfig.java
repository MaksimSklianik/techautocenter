package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConfig {
    private static final Properties properties = new Properties();


    static {
        try (InputStream input = AppConfig.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                throw new RuntimeException("Не найден файл конфигурации config.properties");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки конфигурации", e);
        }
    }

    public static String getDatabaseUrl() {
        return properties.getProperty("database.url", "jdbc:sqlite:autoservice.db");
    }

    public static String getTwilioAccountSid() {
        return properties.getProperty("twilio.account_sid");
    }

    public static String getTwilioAuthToken() {
        return properties.getProperty("twilio.auth_token");
    }

    public static String getTwilioFromNumber() {
        return properties.getProperty("twilio.from_number");
    }

    public static String getYooKassaShopId() {
        return properties.getProperty("yookassa.shop_id");
    }

    public static String getYooKassaSecretKey() {
        return properties.getProperty("yookassa.secret_key");
    }

    public static String getReturnUrl() {
        return properties.getProperty("");
    }
}