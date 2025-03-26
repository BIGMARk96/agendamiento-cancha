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
        setSize(650, 500); // Aumentado el ancho para acomodar los iconos de ayuda
        setLocationRelativeTo(null);

        // Configurar ToolTipManager para mejor visualización
        ToolTipManager.sharedInstance().setInitialDelay(100); // Mostrar más rápido
        ToolTipManager.sharedInstance().setDismissDelay(10000); // Mostrar por 10 segundos
        ToolTipManager.sharedInstance().setReshowDelay(100); // Mostrar rápido al cambiar entre tooltips
        
        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Registro de Usuario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(51, 51, 51));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3; // Aumentado para incluir columna de ayuda
        gbc.insets = new Insets(30, 10, 30, 10);
        mainPanel.add(lblTitulo, gbc);

        // Estilo común para campos
        Font labelFont = new Font("Segoe UI", Font.BOLD, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 16);
        Dimension fieldSize = new Dimension(250, 40);

        // Definir los mensajes de ayuda
        String helpUsuario = "Mínimo 6 caracteres";
        String helpPassword = "Mínimo 6 caracteres y debe incluir al menos un número";
        String helpNombre = "Ingrese su nombre completo";
        String helpRut = "Formato: 12345678-9 (sin puntos y con guión)";
        String helpTelefono = "9 dígitos numéricos";
        
        // Usuario
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        addStyledField(mainPanel, gbc, 1, "Usuario:", txtUsuario = new JTextField(20), 
                      labelFont, fieldFont, fieldSize, helpUsuario);
        
        // Contraseña
        addStyledField(mainPanel, gbc, 2, "Contraseña:", txtPassword = new JPasswordField(20),
                      labelFont, fieldFont, fieldSize, helpPassword);
        
        // Nombre
        addStyledField(mainPanel, gbc, 3, "Nombre:", txtNombre = new JTextField(20),
                      labelFont, fieldFont, fieldSize, helpNombre);
        
        // RUT
        addStyledField(mainPanel, gbc, 4, "RUT:", txtRut = new JTextField(20),
                      labelFont, fieldFont, fieldSize, helpRut);
        
        // Teléfono
        addStyledField(mainPanel, gbc, 5, "Teléfono:", txtTelefono = new JTextField(20),
                      labelFont, fieldFont, fieldSize, helpTelefono);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(240, 240, 240));
        
        // Estilo común para botones
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(180, 40);

        // Botón Registrar
        btnRegistrar = new JButton("Registrar");
        btnRegistrar.setFont(buttonFont);
        btnRegistrar.setPreferredSize(buttonSize);
        btnRegistrar.setBackground(new Color(46, 204, 113));
        btnRegistrar.setForeground(Color.WHITE);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorderPainted(false);
        btnRegistrar.setOpaque(true);
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        buttonPanel.add(btnRegistrar);
        buttonPanel.add(btnCancelar);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(30, 10, 20, 10);
        mainPanel.add(buttonPanel, gbc);

        // Eventos
        btnRegistrar.addActionListener(e -> registrar());
        btnCancelar.addActionListener(e -> dispose());

        // Añadir panel principal con borde
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        containerPanel.add(mainPanel, BorderLayout.CENTER);
        add(containerPanel);
    }

    private void addStyledField(JPanel panel, GridBagConstraints gbc, int row, String labelText, 
                              JComponent field, Font labelFont, Font fieldFont, Dimension fieldSize,
                              String helpText) {
        // Etiqueta
        JLabel label = new JLabel(labelText);
        label.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(label, gbc);

        // Campo de texto
        gbc.gridx = 1;
        field.setFont(fieldFont);
        field.setPreferredSize(fieldSize);
        panel.add(field, gbc);

        // Icono de ayuda
        JLabel helpIcon = new JLabel("?") {
            @Override
            public JToolTip createToolTip() {
                JToolTip tip = super.createToolTip();
                tip.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                tip.setBackground(new Color(255, 255, 225)); // Color amarillo claro
                tip.setForeground(Color.BLACK);
                tip.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(51, 153, 255)),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)
                ));
                return tip;
            }
        };
        
        helpIcon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        helpIcon.setForeground(new Color(51, 153, 255));
        helpIcon.setPreferredSize(new Dimension(25, 25));
        helpIcon.setHorizontalAlignment(SwingConstants.CENTER);
        helpIcon.setToolTipText("<html><p style='width: 250px'>" + helpText + "</p></html>");
        helpIcon.setBorder(BorderFactory.createLineBorder(new Color(51, 153, 255), 1, true));
        helpIcon.setOpaque(true);
        helpIcon.setBackground(Color.WHITE);
        helpIcon.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Panel para el icono de ayuda con margen izquierdo
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        helpPanel.setBackground(new Color(240, 240, 240));
        helpPanel.add(helpIcon);

        gbc.gridx = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(helpPanel, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Restaurar el fill original
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

        // Validar usuario
        if (usuario.length() < 6) {
            JOptionPane.showMessageDialog(this, "El usuario debe tener al menos 6 caracteres",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar contraseña
        if (password.length() < 6 || !password.matches(".*\\d.*")) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres y contener al menos un número",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar RUT
        if (!rut.matches("\\d{7,8}-[\\dkK]")) {
            JOptionPane.showMessageDialog(this, "El RUT debe tener el formato correcto (sin puntos y con guión)",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validar teléfono
        if (!telefono.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(this, "El teléfono debe tener 9 dígitos",
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
        }
    }
} 