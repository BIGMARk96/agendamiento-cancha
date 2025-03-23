package com.agendamiento.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestConnection {
    public static void main(String[] args) {
        try {
            System.out.println("Intentando conectar a la base de datos...");
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("¡Conexión exitosa!");

            // Probar consulta simple
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM canchas");
            
            System.out.println("\nCanchas disponibles:");
            while (rs.next()) {
                System.out.printf("ID: %d, Nombre: %s, Tipo: %s, Precio: $%.2f%n",
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("tipo"),
                    rs.getDouble("precio_hora"));
            }

            // Cerrar conexiones
            rs.close();
            stmt.close();
            conn.close();
            System.out.println("\nConexión cerrada correctamente.");

        } catch (Exception e) {
            System.err.println("Error al conectar a la base de datos:");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }
} 