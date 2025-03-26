package com.agendamiento.view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.agendamiento.util.DatabaseConnection;

public class RestaurarPasswordFrame extends JFrame {
    private JTextField txtUsuario;
    private JTextField txtRut;
    private JPasswordField txtNuevaPassword;
    private JPasswordField txtConfirmarPassword;
    private JButton btnRestaurar;
    private JButton btnCancelar;

    public RestaurarPasswordFrame() {
        setTitle("Restaurar Contraseña");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 450);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Restaurar Contraseña");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(51, 51, 51));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 30, 10);
        mainPanel.add(lblTitulo, gbc);

        // Estilo común para campos
        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        Dimension fieldSize = new Dimension(250, 40);
        
        // Usuario
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        addStyledField(mainPanel, gbc, 1, "Usuario:", txtUsuario = new JTextField(20),
                      labelFont, fieldFont, fieldSize);
        
        // RUT
        addStyledField(mainPanel, gbc, 2, "RUT:", txtRut = new JTextField(20),
                      labelFont, fieldFont, fieldSize);
        
        // Nueva contraseña
        addStyledField(mainPanel, gbc, 3, "Nueva Contraseña:", txtNuevaPassword = new JPasswordField(20),
                      labelFont, fieldFont, fieldSize);
        
        // Confirmar contraseña
        addStyledField(mainPanel, gbc, 4, "Confirmar Contraseña:", txtConfirmarPassword = new JPasswordField(20),
                      labelFont, fieldFont, fieldSize);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        // Estilo común para botones
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(180, 40);

        // Botón Restaurar
        btnRestaurar = new JButton("Restaurar");
        btnRestaurar.setFont(buttonFont);
        btnRestaurar.setPreferredSize(buttonSize);
        btnRestaurar.setBackground(new Color(51, 153, 255));
        btnRestaurar.setForeground(Color.WHITE);
        btnRestaurar.setFocusPainted(false);
        btnRestaurar.setBorderPainted(false);
        btnRestaurar.setOpaque(true);
        btnRestaurar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Botón Cancelar
        btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(buttonFont);
        btnCancelar.setPreferredSize(buttonSize);
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setOpaque(true);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        buttonPanel.add(btnRestaurar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 10, 20, 10);
        mainPanel.add(buttonPanel, gbc);

        // Eventos
        btnRestaurar.addActionListener(e -> restaurarPassword());
        btnCancelar.addActionListener(e -> dispose());

        // Añadir panel principal con borde
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        add(containerPanel);
    }

    private void addStyledField(JPanel panel, GridBagConstraints gbc, int row, String labelText, 
                              JComponent field, Font labelFont, Font fieldFont, Dimension fieldSize) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        panel.add(label, gbc);

        gbc.gridx = 1;
        field.setFont(fieldFont);
        field.setPreferredSize(fieldSize);
        panel.add(field, gbc);
    }

    private void restaurarPassword() {
        String usuario = txtUsuario.getText().trim();
        String rut = txtRut.getText().trim();
        String nuevaPassword = new String(txtNuevaPassword.getPassword());
        String confirmarPassword = new String(txtConfirmarPassword.getPassword());

        // Validaciones
        if (usuario.isEmpty() || rut.isEmpty() || nuevaPassword.isEmpty() || confirmarPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!nuevaPassword.equals(confirmarPassword)) {
            JOptionPane.showMessageDialog(this, "Las contraseñas no coinciden",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar si existe el usuario con el RUT proporcionado
            String sqlCheck = "SELECT id FROM usuarios WHERE usuario = ? AND rut = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlCheck)) {
                pstmt.setString(1, usuario);
                pstmt.setString(2, rut);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Actualizar la contraseña
                        String sqlUpdate = "UPDATE usuarios SET password = ? WHERE usuario = ? AND rut = ?";
                        try (PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate)) {
                            pstmtUpdate.setString(1, nuevaPassword);
                            pstmtUpdate.setString(2, usuario);
                            pstmtUpdate.setString(3, rut);
                            pstmtUpdate.executeUpdate();
                            
                            JOptionPane.showMessageDialog(this, "Contraseña actualizada exitosamente");
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Usuario o RUT incorrectos",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al restaurar contraseña: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 