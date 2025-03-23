package com.agendamiento.view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import com.agendamiento.util.DatabaseConnection;

public class RegistroFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtTelefono;

    public RegistroFrame() {
        setTitle("Registro - Sistema de Agendamiento");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel lblTitulo = new JLabel("Registro de Usuario");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 20, 5);
        mainPanel.add(lblTitulo, gbc);

        // Campos de registro
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Usuario
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Usuario:"), gbc);
        txtUsuario = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtUsuario, gbc);

        // Contraseña
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Nombre
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Nombre:"), gbc);
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtNombre, gbc);

        // RUT
        gbc.gridy = 4;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("RUT:"), gbc);
        txtRut = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtRut, gbc);

        // Teléfono
        gbc.gridy = 5;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Teléfono:"), gbc);
        txtTelefono = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtTelefono, gbc);

        // Botón de registro
        JButton btnRegistrar = new JButton("Registrarse");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 10, 5);
        mainPanel.add(btnRegistrar, gbc);

        // Evento de registro
        btnRegistrar.addActionListener(e -> registrarUsuario());

        add(mainPanel);
    }

    private void registrarUsuario() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());
        String nombre = txtNombre.getText();
        String rut = txtRut.getText();
        String telefono = txtTelefono.getText();

        if (usuario.isEmpty() || password.isEmpty() || nombre.isEmpty() || 
            rut.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO usuarios (usuario, password, nombre, rut, telefono) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, usuario);
            pstmt.setString(2, password);
            pstmt.setString(3, nombre);
            pstmt.setString(4, rut);
            pstmt.setString(5, telefono);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente!");
            this.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 