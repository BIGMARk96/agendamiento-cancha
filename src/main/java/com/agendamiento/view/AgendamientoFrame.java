package com.agendamiento.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import com.agendamiento.util.DatabaseConnection;
import com.toedter.calendar.JCalendar;

public class AgendamientoFrame extends JFrame {
    private int usuarioId;
    private JPanel canchasPanel;
    private JComboBox<LocalTime> cmbHoraInicio;
    private JComboBox<LocalTime> cmbHoraFin;
    private JCalendar calendar;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtTelefono;
    private DefaultTableModel tableModel;
    private JTable tblAgendamientos;
    private Map<String, ImageIcon> imagenesCanchas;
    private String canchaSeleccionada = null;

    public AgendamientoFrame(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Agendamiento de Canchas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Inicializar imágenes
        cargarImagenes();

        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel izquierdo para selección de cancha
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Selección de Cancha"));
        
        // Panel para las canchas
        canchasPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        cargarBotonesCanchas();
        
        JScrollPane scrollCanchas = new JScrollPane(canchasPanel);
        leftPanel.add(scrollCanchas, BorderLayout.CENTER);

        // Panel central para fecha y hora
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Detalles de Agendamiento"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Datos personales
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel("Nombre:"), gbc);
        
        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        centerPanel.add(txtNombre, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(new JLabel("RUT:"), gbc);
        
        txtRut = new JTextField(12);
        gbc.gridx = 1;
        centerPanel.add(txtRut, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(new JLabel("Teléfono:"), gbc);
        
        txtTelefono = new JTextField(15);
        gbc.gridx = 1;
        centerPanel.add(txtTelefono, gbc);

        // Fecha
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(new JLabel("Fecha:"), gbc);

        calendar = new JCalendar();
        calendar.setPreferredSize(new Dimension(250, 200));
        calendar.setFont(new Font("Arial", Font.PLAIN, 12));
        calendar.setLocale(new java.util.Locale("es", "ES")); // Configurar en español
        gbc.gridx = 1;
        centerPanel.add(calendar, gbc);

        // Hora inicio
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(new JLabel("Hora Inicio:"), gbc);

        cmbHoraInicio = new JComboBox<>();
        llenarHorarios(cmbHoraInicio);
        gbc.gridx = 1;
        centerPanel.add(cmbHoraInicio, gbc);

        // Hora fin
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(new JLabel("Hora Fin:"), gbc);

        cmbHoraFin = new JComboBox<>();
        llenarHorarios(cmbHoraFin);
        gbc.gridx = 1;
        centerPanel.add(cmbHoraFin, gbc);

        // Botón de agendamiento
        JButton btnAgendar = new JButton("Agendar Cancha");
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(btnAgendar, gbc);

        // Panel derecho para lista de agendamientos
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Mis Agendamientos"));
        
        // Crear modelo de tabla
        String[] columnas = {"Fecha", "Hora", "Cancha", "Tipo", "Nombre", "RUT", "Teléfono"};
        tableModel = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblAgendamientos = new JTable(tableModel);
        tblAgendamientos.setFont(new Font("Arial", Font.PLAIN, 12));
        tblAgendamientos.setRowHeight(25);
        tblAgendamientos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tblAgendamientos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblAgendamientos.setGridColor(new Color(230, 230, 230));
        tblAgendamientos.setShowGrid(true);
        tblAgendamientos.setShowVerticalLines(true);
        tblAgendamientos.setShowHorizontalLines(true);
        
        JScrollPane scrollAgendamientos = new JScrollPane(tblAgendamientos);
        rightPanel.add(scrollAgendamientos, BorderLayout.CENTER);

        // Agregar paneles al panel principal
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // Eventos
        btnAgendar.addActionListener(e -> realizarAgendamiento());
        cmbHoraInicio.addActionListener(e -> actualizarHoraFin());

        // Cargar agendamientos existentes
        cargarAgendamientos();

        add(mainPanel);
    }

    private void cargarImagenes() {
        imagenesCanchas = new HashMap<>();
        try {
            // Cargar imágenes desde recursos
            ImageIcon imgFutbol = new ImageIcon(getClass().getResource("/images/futbol.jpg"));
            ImageIcon imgFutsal = new ImageIcon(getClass().getResource("/images/futsal.jpg"));
            ImageIcon imgBabyFutbol = new ImageIcon(getClass().getResource("/images/baby_futbol.jpg"));

            // Redimensionar imágenes
            int width = 300;
            int height = 200;
            
            imagenesCanchas.put("futbol", new ImageIcon(imgFutbol.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            imagenesCanchas.put("futsal", new ImageIcon(imgFutsal.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            imagenesCanchas.put("baby futbol", new ImageIcon(imgBabyFutbol.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            System.err.println("Error al cargar imágenes: " + e.getMessage());
        }
    }

    private void cargarBotonesCanchas() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT nombre, tipo FROM canchas";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                
                JPanel canchaPanel = new JPanel(new BorderLayout(5, 5));
                canchaPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                
                // Etiqueta con imagen
                JLabel lblImagen = new JLabel(imagenesCanchas.get(tipo.toLowerCase()));
                lblImagen.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                // Panel de información
                JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                JLabel lblNombre = new JLabel(nombre);
                JLabel lblTipo = new JLabel(tipo);
                lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
                lblTipo.setFont(new Font("Arial", Font.PLAIN, 12));
                infoPanel.add(lblNombre);
                infoPanel.add(lblTipo);
                
                canchaPanel.add(lblImagen, BorderLayout.CENTER);
                canchaPanel.add(infoPanel, BorderLayout.SOUTH);
                
                // Hacer el panel seleccionable
                canchaPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        seleccionarCancha(nombre + " - " + tipo);
                        // Actualizar bordes de todos los paneles
                        for (Component c : canchasPanel.getComponents()) {
                            if (c instanceof JPanel) {
                                ((JPanel) c).setBorder(BorderFactory.createLineBorder(Color.GRAY));
                            }
                        }
                        // Resaltar el panel seleccionado
                        canchaPanel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
                    }
                });
                
                canchasPanel.add(canchaPanel);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar canchas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void llenarHorarios(JComboBox<LocalTime> comboBox) {
        LocalTime inicio = LocalTime.of(8, 0); // 8:00 AM
        LocalTime fin = LocalTime.of(22, 0);   // 10:00 PM
        
        while (!inicio.isAfter(fin)) {
            comboBox.addItem(inicio);
            inicio = inicio.plusHours(1);
        }
    }

    private void actualizarHoraFin() {
        LocalTime horaInicio = (LocalTime) cmbHoraInicio.getSelectedItem();
        if (horaInicio != null) {
            cmbHoraFin.removeAllItems();
            LocalTime hora = horaInicio.plusHours(1);
            LocalTime fin = LocalTime.of(22, 0); // Cambiado a 22:00 para evitar problemas
            
            while (!hora.isAfter(fin)) {
                cmbHoraFin.addItem(hora);
                hora = hora.plusHours(1);
            }
            
            // Si no hay horas disponibles, mostrar mensaje
            if (cmbHoraFin.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No hay horarios disponibles después de la hora seleccionada",
                    "Aviso", 
                    JOptionPane.INFORMATION_MESSAGE);
                cmbHoraInicio.setSelectedIndex(0); // Resetear a la primera hora
                actualizarHoraFin(); // Actualizar nuevamente
            }
        }
    }

    private void seleccionarCancha(String cancha) {
        this.canchaSeleccionada = cancha;
    }

    private void cargarAgendamientos() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT a.*, c.nombre as cancha_nombre, c.tipo as cancha_tipo, " +
                        "u.nombre as nombre_usuario, u.rut, u.telefono " +
                        "FROM agendamientos a " +
                        "JOIN canchas c ON a.cancha_id = c.id " +
                        "JOIN usuarios u ON a.usuario_id = u.id " +
                        "WHERE a.usuario_id = ? " +
                        "ORDER BY a.fecha, a.hora_inicio";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, usuarioId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Object[] row = {
                    rs.getDate("fecha"),
                    rs.getTime("hora_inicio") + " - " + rs.getTime("hora_fin"),
                    rs.getString("cancha_nombre"),
                    rs.getString("cancha_tipo"),
                    rs.getString("nombre_usuario"),
                    rs.getString("rut"),
                    rs.getString("telefono")
                };
                tableModel.addRow(row);
            }

            // Ajustar ancho de columnas
            for (int i = 0; i < tblAgendamientos.getColumnCount(); i++) {
                tblAgendamientos.getColumnModel().getColumn(i).setPreferredWidth(100);
            }

            // Cargar datos del usuario actual
            String sqlUser = "SELECT nombre, rut, telefono FROM usuarios WHERE id = ?";
            PreparedStatement pstmtUser = conn.prepareStatement(sqlUser);
            pstmtUser.setInt(1, usuarioId);
            ResultSet rsUser = pstmtUser.executeQuery();

            if (rsUser.next()) {
                txtNombre.setText(rsUser.getString("nombre"));
                txtRut.setText(rsUser.getString("rut"));
                txtTelefono.setText(rsUser.getString("telefono"));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar agendamientos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarAgendamiento() {
        if (canchaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una cancha",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String nombre = txtNombre.getText().trim();
        String rut = txtRut.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty() || rut.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe completar todos los campos personales",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] partes = canchaSeleccionada.split(" - ");
        String nombreCancha = partes[0];
        String tipoCancha = partes[1];

        LocalTime horaInicio = (LocalTime) cmbHoraInicio.getSelectedItem();
        LocalTime horaFin = (LocalTime) cmbHoraFin.getSelectedItem();

        if (horaInicio == null || horaFin == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar hora de inicio y fin",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que la hora fin sea posterior a la hora inicio
        if (horaFin.isBefore(horaInicio) || horaFin.equals(horaInicio)) {
            JOptionPane.showMessageDialog(this, "La hora de fin debe ser posterior a la hora de inicio",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

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

            // Actualizar datos del usuario
            String sqlUpdateUser = "UPDATE usuarios SET nombre = ?, rut = ?, telefono = ? WHERE id = ?";
            PreparedStatement pstmtUser = conn.prepareStatement(sqlUpdateUser);
            pstmtUser.setString(1, nombre);
            pstmtUser.setString(2, rut);
            pstmtUser.setString(3, telefono);
            pstmtUser.setInt(4, usuarioId);
            pstmtUser.executeUpdate();

            // Insertar agendamiento
            String sql = "INSERT INTO agendamientos (usuario_id, cancha_id, fecha, hora_inicio, hora_fin) " +
                        "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, canchaId);
            pstmt.setDate(3, new java.sql.Date(calendar.getDate().getTime()));
            pstmt.setTime(4, Time.valueOf(horaInicio));
            pstmt.setTime(5, Time.valueOf(horaFin));
            
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Agendamiento realizado exitosamente!");
            cargarAgendamientos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al realizar agendamiento: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 