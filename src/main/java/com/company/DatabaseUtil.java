package com.company;

import java.sql.*;

public class DatabaseUtil {
    Connection conn;
    String DATABASE_URL = "jdbc:sqlite:C:\\Users\\Admin\\SQLite databases\\ProperTestDB.db";

    public DatabaseUtil(String databaseUrl) {
        this.DATABASE_URL = databaseUrl;
    }
    public DatabaseUtil(String databaseUrl, Connection conn) {
        this.DATABASE_URL = databaseUrl;
        this.conn = conn;
    }
    public Connection getConnection() {
        {
            try {
                if (conn == null || conn.isClosed()) {
                    conn = DriverManager.getConnection(DATABASE_URL);
                }
            } catch (SQLException e) {
                System.out.println("Problem creating connection: " + e.getMessage());
            }
        }
        return conn;
    }
    public void closeConnection() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Problem closing connection: " + e.getMessage());
            } finally {
                conn = null;
            }
        }
    }

    public void closeResources(AutoCloseable... resources) {
        for (AutoCloseable resource : resources) {
            if (resource != null) {
                try {
                    resource.close();
                } catch (Exception e) {
                    System.out.println("Problem closing resource " + resource.toString() + " : " + e.getMessage());
                }
            }
        }
    }
}
