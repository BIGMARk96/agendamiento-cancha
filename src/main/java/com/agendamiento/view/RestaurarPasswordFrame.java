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
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos
        int row = 0;
        
        // Usuario
        addField(mainPanel, gbc, row++, "Usuario:", txtUsuario = new JTextField(20));
        
        // RUT (para verificación)
        addField(mainPanel, gbc, row++, "RUT:", txtRut = new JTextField(20));
        
        // Nueva contraseña
        addField(mainPanel, gbc, row++, "Nueva Contraseña:", txtNuevaPassword = new JPasswordField(20));
        
        // Confirmar contraseña
        addField(mainPanel, gbc, row++, "Confirmar Contraseña:", txtConfirmarPassword = new JPasswordField(20));

        // Botones
        JPanel buttonPanel = new JPanel();
        btnRestaurar = new JButton("Restaurar");
        btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnRestaurar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        // Eventos
        btnRestaurar.addActionListener(e -> restaurarPassword());
        btnCancelar.addActionListener(e -> dispose());

        add(mainPanel);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
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