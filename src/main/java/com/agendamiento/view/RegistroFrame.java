package com.agendamiento.view;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.agendamiento.util.DatabaseConnection;

public class RegistroFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtTelefono;
    private JButton btnRegistrar;
    private JButton btnCancelar;

    public RegistroFrame() {
        setTitle("Registro de Usuario");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campos
        int row = 0;
        
        // Usuario
        addField(mainPanel, gbc, row++, "Usuario:", txtUsuario = new JTextField(20));
        
        // Contraseña
        addField(mainPanel, gbc, row++, "Contraseña:", txtPassword = new JPasswordField(20));
        
        // Nombre
        addField(mainPanel, gbc, row++, "Nombre:", txtNombre = new JTextField(20));
        
        // RUT
        addField(mainPanel, gbc, row++, "RUT:", txtRut = new JTextField(20));
        
        // Teléfono
        addField(mainPanel, gbc, row++, "Teléfono:", txtTelefono = new JTextField(20));

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        btnRegistrar = new JButton("Registrar");
        btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);

        // Eventos
        btnRegistrar.addActionListener(e -> registrar());
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

    private void registrar() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        String nombre = txtNombre.getText().trim();
        String rut = txtRut.getText().trim();
        String telefono = txtTelefono.getText().trim();

        // Validaciones
        if (usuario.isEmpty() || password.isEmpty() || nombre.isEmpty() || rut.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO usuarios (usuario, password, nombre, rut, telefono) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, usuario);
                pstmt.setString(2, password);
                pstmt.setString(3, nombre);
                pstmt.setString(4, rut);
                pstmt.setString(5, telefono);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente");
                dispose();
            }
        } catch (SQLException ex) {
            if (ex.getMessage().contains("Duplicate entry")) {
                if (ex.getMessage().contains("usuario")) {
                    JOptionPane.showMessageDialog(this, "El usuario ya existe",
                            "Error", JOptionPane.ERROR_MESSAGE);
                } else if (ex.getMessage().contains("rut")) {
                    JOptionPane.showMessageDialog(this, "El RUT ya está registrado",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar usuario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 