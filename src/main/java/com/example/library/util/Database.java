package com.example.library.util;

import java.sql.*;

public class Database {
    private static final String URL = "jdbc:postgresql://ep-jolly-dream-a8oteob7-pooler.eastus2.azure.neon.tech/biblioteca?user=neondb_owner&password=npg_C20xPwudQpHD&sslmode=require&channelBinding=require";
    private static final String USER = "biblioteca";
    private static final String PASSWORD = "npg_C20xPwudQpHD";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Falha ao carregar o driver JDBC do PostgreSQL", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
