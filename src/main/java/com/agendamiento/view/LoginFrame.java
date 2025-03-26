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
    private JButton btnRestaurar;

    public LoginFrame() {
        setTitle("Login - Sistema de Agendamiento");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        JLabel lblTitulo = new JLabel("Bienvenido a las canchas de joselito");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(51, 51, 51));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 30, 10);
        mainPanel.add(lblTitulo, gbc);

        // Usuario
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mainPanel.add(lblUsuario, gbc);

        txtUsuario = new JTextField(20);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsuario.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        mainPanel.add(txtUsuario, gbc);

        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mainPanel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setPreferredSize(new Dimension(250, 40));
        gbc.gridx = 1;
        mainPanel.add(txtPassword, gbc);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        // Estilo común para botones
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(180, 40);
        
        // Botón Login
        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setFont(buttonFont);
        btnLogin.setPreferredSize(buttonSize);
        btnLogin.setBackground(new Color(51, 153, 255));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setOpaque(true);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Botón Registro
        btnRegistro = new JButton("Registrarse");
        btnRegistro.setFont(buttonFont);
        btnRegistro.setPreferredSize(buttonSize);
        btnRegistro.setBackground(new Color(46, 204, 113));
        btnRegistro.setForeground(Color.WHITE);
        btnRegistro.setFocusPainted(false);
        btnRegistro.setBorderPainted(false);
        btnRegistro.setOpaque(true);
        btnRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Botón Restaurar
        btnRestaurar = new JButton("Restaurar Contraseña");
        btnRestaurar.setFont(buttonFont);
        btnRestaurar.setPreferredSize(new Dimension(200, 35));
        btnRestaurar.setBackground(new Color(240, 240, 240));
        btnRestaurar.setForeground(new Color(51, 51, 51));
        btnRestaurar.setFocusPainted(false);
        btnRestaurar.setBorderPainted(true);
        btnRestaurar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnRegistro);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 15, 10);
        mainPanel.add(buttonPanel, gbc);

        // Añadir botón restaurar en una nueva fila
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 10, 20, 10);
        mainPanel.add(btnRestaurar, gbc);

        // Eventos
        btnLogin.addActionListener(e -> login());
        btnRegistro.addActionListener(e -> abrirRegistro());
        btnRestaurar.addActionListener(e -> abrirRestaurarPassword());

        // Añadir panel principal con borde
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        add(containerPanel);
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

    private void abrirRestaurarPassword() {
        new RestaurarPasswordFrame().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
} 