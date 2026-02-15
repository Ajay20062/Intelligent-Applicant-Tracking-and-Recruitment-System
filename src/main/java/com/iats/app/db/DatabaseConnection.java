package com.iats.app.db;

import com.iats.app.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String URL = AppConfig.get("db.url");
    private static final String USERNAME = AppConfig.get("db.username");
    private static final String PASSWORD = AppConfig.get("db.password");

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
