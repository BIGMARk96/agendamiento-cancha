package com.agendamiento.view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.agendamiento.util.DatabaseConnection;

public class LoginFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegistro;

    public LoginFrame() {
        setTitle("Login - Sistema de Agendamiento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        // `anel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // titulo
        JLabel lblTitulo = new JLabel("Bienvenido a las canchas de joselito");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 20, 5);
        mainPanel.add(lblTitulo, gbc);

        // usuario
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new JLabel("Usuario:"), gbc);

        txtUsuario = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(txtUsuario, gbc);

        // contraseña
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Contraseña:"), gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // botones
        JPanel buttonPanel = new JPanel();
        btnLogin = new JButton("Iniciar sesion");
        btnRegistro = new JButton("registrarse");
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegistro);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 10, 5);
        mainPanel.add(buttonPanel, gbc);

        // eventos
        btnLogin.addActionListener(e -> login());
        btnRegistro.addActionListener(e -> abrirRegistro());

        add(mainPanel);
    }

    private void login() {
        String usuario = txtUsuario.getText();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese usuario y contraseña",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            //error por si la conexion a la base no agarra
            if (conn == null || conn.isClosed()) {
                throw new SQLException("No se pudo establecer la conexión con la base de datos");
            }

            String sql = "SELECT id, usuario, nombre, rut, telefono FROM usuarios WHERE usuario = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, usuario);
                pstmt.setString(2, password);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "¡Bienvenido " + rs.getString("nombre") + "!");
                        this.dispose();
                        new AgendamientoFrame(rs.getInt("id")).setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace(); // consloa avisa error
            String errorMessage = "Error al conectar con la base de datos\n" +
                                "Detalles: " + ex.getMessage();
            if (ex.getCause() != null) {
                errorMessage += "\nCausa: " + ex.getCause().getMessage();
            }
            JOptionPane.showMessageDialog(this, errorMessage,
                    "Error de Base de Datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirRegistro() {
        new RegistroFrame().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
} 