package com.agendamiento.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/agendamiento_canchas";
    private static final String USER = "root";
    private static final String PASSWORD = "peteto10";

    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver explícitamente
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Configurar propiedades adicionales
            Properties props = new Properties();
            props.setProperty("user", USER);
            props.setProperty("password", PASSWORD);
            props.setProperty("useSSL", "false");
            props.setProperty("allowPublicKeyRetrieval", "true");
            props.setProperty("serverTimezone", "America/Santiago");
            props.setProperty("autoReconnect", "true");

            // Intentar la conexión con timeout
            DriverManager.setLoginTimeout(5);
            Connection conn = DriverManager.getConnection(URL, props);
            
            // Verificar la conexión
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