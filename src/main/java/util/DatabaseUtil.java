package util;

import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseUtil {
    public static Connection getConnection() throws SQLException {
        return DatabaseConfig.getConnection();
    }

    public static void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception e) {
                    // Игнорируем ошибки закрытия
                }
            }
        }
    }
}