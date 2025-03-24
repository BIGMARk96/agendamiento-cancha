package com.agendamiento.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        try {
            System.out.println("Intentando conectar a la base de datos...");
            System.out.println("Driver de MySQL: " + com.mysql.cj.jdbc.Driver.class.getName());
            
            Connection conn = DatabaseConnection.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            
            System.out.println("¡Conexión exitosa!");
            System.out.println("URL de la base de datos: " + metaData.getURL());
            System.out.println("Usuario: " + metaData.getUserName());
            System.out.println("Versión del servidor: " + metaData.getDatabaseProductVersion());
            
            conn.close();
        } catch (Exception e) {
            System.out.println("Error detallado:");
            e.printStackTrace();
            
            // Imprimir la cadena de excepciones
            Throwable cause = e.getCause();
            while (cause != null) {
                System.out.println("Causado por:");
                cause.printStackTrace();
                cause = cause.getCause();
            }
        }
    }
} 