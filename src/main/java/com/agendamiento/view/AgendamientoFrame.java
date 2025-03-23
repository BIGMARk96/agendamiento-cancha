package com.agendamiento.view;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import com.agendamiento.util.DatabaseConnection;

public class AgendamientoFrame extends JFrame {
    private int usuarioId;
    private JComboBox<String> cmbCancha;
    private JSpinner spnFecha;
    private JSpinner spnHoraInicio;
    private JSpinner spnHoraFin;
    private JTable tblAgendamientos;
    private DefaultListModel<String> listModel;
    private JList<String> lstAgendamientos;

    public AgendamientoFrame(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Agendamiento de Canchas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Panel de agendamiento
        JPanel panelAgendamiento = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Título
        JLabel lblTitulo = new JLabel("Nuevo Agendamiento");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 20, 5);
        panelAgendamiento.add(lblTitulo, gbc);

        // Campos de agendamiento
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Cancha
        gbc.gridy = 1;
        gbc.gridx = 0;
        panelAgendamiento.add(new JLabel("Cancha:"), gbc);
        cmbCancha = new JComboBox<>();
        cargarCanchas();
        gbc.gridx = 1;
        panelAgendamiento.add(cmbCancha, gbc);

        // Fecha
        gbc.gridy = 2;
        gbc.gridx = 0;
        panelAgendamiento.add(new JLabel("Fecha:"), gbc);
        spnFecha = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnFecha, "yyyy-MM-dd");
        spnFecha.setEditor(dateEditor);
        gbc.gridx = 1;
        panelAgendamiento.add(spnFecha, gbc);

        // Hora inicio
        gbc.gridy = 3;
        gbc.gridx = 0;
        panelAgendamiento.add(new JLabel("Hora Inicio:"), gbc);
        spnHoraInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditorInicio = new JSpinner.DateEditor(spnHoraInicio, "HH:mm");
        spnHoraInicio.setEditor(timeEditorInicio);
        gbc.gridx = 1;
        panelAgendamiento.add(spnHoraInicio, gbc);

        // Hora fin
        gbc.gridy = 4;
        gbc.gridx = 0;
        panelAgendamiento.add(new JLabel("Hora Fin:"), gbc);
        spnHoraFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeEditorFin = new JSpinner.DateEditor(spnHoraFin, "HH:mm");
        spnHoraFin.setEditor(timeEditorFin);
        gbc.gridx = 1;
        panelAgendamiento.add(spnHoraFin, gbc);

        // Botón de agendamiento
        JButton btnAgendar = new JButton("Agendar");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 10, 5);
        panelAgendamiento.add(btnAgendar, gbc);

        // Lista de agendamientos
        listModel = new DefaultListModel<>();
        lstAgendamientos = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(lstAgendamientos);

        // Agregar paneles al panel principal
        mainPanel.add(panelAgendamiento, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Evento de agendamiento
        btnAgendar.addActionListener(e -> realizarAgendamiento());

        // Cargar agendamientos existentes
        cargarAgendamientos();

        add(mainPanel);
    }

    private void cargarCanchas() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT nombre, tipo FROM canchas";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                cmbCancha.addItem(rs.getString("nombre") + " - " + rs.getString("tipo"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar canchas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarAgendamientos() {
        listModel.clear();
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT a.*, c.nombre as cancha_nombre, c.tipo as cancha_tipo " +
                        "FROM agendamientos a " +
                        "JOIN canchas c ON a.cancha_id = c.id " +
                        "WHERE a.usuario_id = ? " +
                        "ORDER BY a.fecha, a.hora_inicio";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String agendamiento = String.format("%s - %s - %s %s - %s",
                    rs.getString("cancha_nombre"),
                    rs.getString("cancha_tipo"),
                    rs.getDate("fecha"),
                    rs.getTime("hora_inicio"),
                    rs.getTime("hora_fin"));
                listModel.addElement(agendamiento);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar agendamientos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarAgendamiento() {
        String canchaSeleccionada = (String) cmbCancha.getSelectedItem();
        if (canchaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una cancha",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] partes = canchaSeleccionada.split(" - ");
        String nombreCancha = partes[0];
        String tipoCancha = partes[1];

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Obtener ID de la cancha
            String sqlCancha = "SELECT id FROM canchas WHERE nombre = ? AND tipo = ?";
            PreparedStatement pstmtCancha = conn.prepareStatement(sqlCancha);
            pstmtCancha.setString(1, nombreCancha);
            pstmtCancha.setString(2, tipoCancha);
            ResultSet rsCancha = pstmtCancha.executeQuery();
            
            if (!rsCancha.next()) {
                throw new SQLException("Cancha no encontrada");
            }
            int canchaId = rsCancha.getInt("id");

            // Insertar agendamiento
            String sql = "INSERT INTO agendamientos (usuario_id, cancha_id, fecha, hora_inicio, hora_fin) " +
                        "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, canchaId);
            pstmt.setDate(3, new java.sql.Date(((java.util.Date) spnFecha.getValue()).getTime()));
            pstmt.setTime(4, new java.sql.Time(((java.util.Date) spnHoraInicio.getValue()).getTime()));
            pstmt.setTime(5, new java.sql.Time(((java.util.Date) spnHoraFin.getValue()).getTime()));
            
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Agendamiento realizado exitosamente!");
            cargarAgendamientos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al realizar agendamiento: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 