package com.abdelrahman.elemary.httpserver.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostgresJDBCConnector {
    private static PostgresJDBCConnector instance;
    private final String url = "jdbc:postgresql://localhost:5432/Todo";
    private final String user = "postgres";
    private final String password = "password";

    private PostgresJDBCConnector() {
        // No need to initialize url, user, password here as they are already set
    }

    public static PostgresJDBCConnector getInstance() {
        if (instance == null) {
            synchronized (PostgresJDBCConnector.class) {
                if (instance == null) {
                    instance = new PostgresJDBCConnector();
                }
            }
        }
        return instance;
    }

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    public void executeQuery(String query) {
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getString(2)); // Adjust based on your query
            }

        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }
}
