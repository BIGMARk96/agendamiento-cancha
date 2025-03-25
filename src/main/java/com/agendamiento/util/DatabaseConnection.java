package com.agendamiento.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/agendamiento_canchas";
    private static final String USER = "root";
    private static final String PASSWORD = "peteto10";

    public static Connection getConnection() throws SQLException {
        try {
            // cargar el driver explícitamente
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Intentar la conexión con timeout
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/agendamiento_canchas?allowPublicKeyRetrieval=true&useSSL=false",
                "root",
                "peteto10"
            );
            
            // desactivar el modo seguro
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SET SQL_SAFE_UPDATES = 0");
            }
            
            // verificar la conexion
            if (conn == null || conn.isClosed()) {
                throw new SQLException("No se pudo establecer la conexión con la base de datos");
            }
            
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el driver de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            throw new SQLException("Error al conectar con la base de datos: " + e.getMessage() + 
                "\nURL: " + URL + 
                "\nUsuario: " + USER);
        } catch (Exception e) {
            throw new SQLException("Error inesperado al conectar: " + e.getMessage());
        }
    }
} 