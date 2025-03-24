package com.agendamiento.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import org.jdesktop.swingx.JXDatePicker;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import com.agendamiento.util.DatabaseConnection;

public class AgendamientoFrame extends JFrame {
    private int usuarioId;
    private JPanel canchasPanel;
    private JComboBox<LocalTime> cmbHoraInicio;
    private JComboBox<LocalTime> cmbHoraFin;
    private JXDatePicker datePicker;
    private JTextField txtNombre;
    private JTextField txtRut;
    private JTextField txtTelefono;
    private Map<String, ImageIcon> imagenesCanchas;
    private String canchaSeleccionada = null;

    // Modelos y tablas para cada cancha
    private Map<String, DefaultTableModel> modelosHoy = new HashMap<>();
    private Map<String, DefaultTableModel> modelosSemana = new HashMap<>();
    private Map<String, DefaultTableModel> modelosPosteriores = new HashMap<>();
    private Map<String, JTable> tablasHoy = new HashMap<>();
    private Map<String, JTable> tablasSemana = new HashMap<>();
    private Map<String, JTable> tablasPosteriores = new HashMap<>();

    public AgendamientoFrame(int usuarioId) {
        this.usuarioId = usuarioId;
        setTitle("Agendamiento de Canchas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);

        // Inicializar imágenes
        cargarImagenes();

        // Panel principal con BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel izquierdo para selección de cancha
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(51, 153, 255), 2), "Selección de Cancha"));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        
        // Panel para las canchas
        canchasPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        canchasPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cargarBotonesCanchas();
        
        JScrollPane scrollCanchas = new JScrollPane(canchasPanel);
        leftPanel.add(scrollCanchas, BorderLayout.CENTER);

        // Panel central para fecha y hora
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(51, 153, 255), 2), "Detalles de Agendamiento"));
        centerPanel.setPreferredSize(new Dimension(250, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Datos personales con fuente más pequeña
        Font labelFont = new Font("Arial", Font.PLAIN, 12);
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);

        // Nombre
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(lblNombre, gbc);
        
        txtNombre = new JTextField(15);
        txtNombre.setFont(fieldFont);
        gbc.gridx = 1;
        centerPanel.add(txtNombre, gbc);

        // RUT
        JLabel lblRut = new JLabel("RUT:");
        lblRut.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(lblRut, gbc);
        
        txtRut = new JTextField(10);
        txtRut.setFont(fieldFont);
        gbc.gridx = 1;
        centerPanel.add(txtRut, gbc);

        // Teléfono
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(lblTelefono, gbc);
        
        txtTelefono = new JTextField(12);
        txtTelefono.setFont(fieldFont);
        gbc.gridx = 1;
        centerPanel.add(txtTelefono, gbc);

        // Fecha
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(lblFecha, gbc);

        // Crear y configurar el JXDatePicker
        datePicker = new JXDatePicker();
        datePicker.setDate(Calendar.getInstance().getTime());
        datePicker.setFormats(new java.text.SimpleDateFormat("dd/MM/yyyy"));
        
        // Configurar el selector de fechas
        datePicker.getMonthView().setSelectionModel(new SingleDaySelectionModel());
        datePicker.getMonthView().setTodayBackground(new Color(255, 220, 220));
        datePicker.getMonthView().setMonthStringBackground(new Color(200, 200, 255));
        datePicker.getMonthView().setDayForeground(Calendar.SUNDAY, Color.RED);
        datePicker.getMonthView().setDayForeground(Calendar.SATURDAY, Color.RED);
        datePicker.getMonthView().setSelectionBackground(new Color(220, 0, 0));
        datePicker.getMonthView().setSelectionForeground(Color.WHITE);
        datePicker.getMonthView().setTodayBackground(new Color(255, 220, 220));
        datePicker.getMonthView().setPreferredColumnCount(2);
        datePicker.getMonthView().setTraversable(true);
        datePicker.getMonthView().setShowingLeadingDays(true);
        datePicker.getMonthView().setShowingTrailingDays(true);
        datePicker.setPreferredSize(new Dimension(150, 30));
        
        // Establecer fecha mínima como hoy
        datePicker.getMonthView().setLowerBound(Calendar.getInstance().getTime());
        
        // Establecer el estilo del editor de fecha
        datePicker.getEditor().setFont(new Font("Arial", Font.PLAIN, 14));
        datePicker.getEditor().setBackground(Color.WHITE);
        datePicker.getEditor().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        gbc.gridx = 1;
        centerPanel.add(datePicker, gbc);

        // Hora inicio
        JLabel lblHoraInicio = new JLabel("Hora Inicio:");
        lblHoraInicio.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(lblHoraInicio, gbc);

        cmbHoraInicio = new JComboBox<>();
        cmbHoraInicio.setFont(fieldFont);
        llenarHorarios(cmbHoraInicio);
        gbc.gridx = 1;
        centerPanel.add(cmbHoraInicio, gbc);

        // Hora fin
        JLabel lblHoraFin = new JLabel("Hora Fin:");
        lblHoraFin.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(lblHoraFin, gbc);

        cmbHoraFin = new JComboBox<>();
        cmbHoraFin.setFont(fieldFont);
        llenarHorarios(cmbHoraFin);
        gbc.gridx = 1;
        centerPanel.add(cmbHoraFin, gbc);

        // Botón de agendamiento
        JButton btnAgendar = new JButton("Agendar Cancha");
        btnAgendar.setFont(new Font("Arial", Font.BOLD, 12));
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(btnAgendar, gbc);

        // Panel derecho para lista de agendamientos
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(51, 153, 255), 2), "Mis Agendamientos"));
        rightPanel.setPreferredSize(new Dimension(900, 0));

        // Panel con pestañas para diferentes canchas
        JTabbedPane tabbedPaneCanchas = new JTabbedPane(JTabbedPane.TOP);
        tabbedPaneCanchas.setFont(new Font("Arial", Font.BOLD, 14));
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Obtener todas las canchas
            String sqlCanchas = "SELECT DISTINCT c.nombre, c.tipo FROM canchas c ORDER BY c.nombre";
            Statement stmtCanchas = conn.createStatement();
            ResultSet rsCanchas = stmtCanchas.executeQuery(sqlCanchas);

            while (rsCanchas.next()) {
                String nombreCancha = rsCanchas.getString("nombre");
                String tipoCancha = rsCanchas.getString("tipo");
                
                // Crear panel para esta cancha
                JPanel canchaPanel = new JPanel(new BorderLayout());
                
                // Crear pestañas de tiempo para esta cancha
                JTabbedPane tabbedPaneTiempo = new JTabbedPane(JTabbedPane.TOP);
                tabbedPaneTiempo.setFont(new Font("Arial", Font.BOLD, 12));
                
                // Crear modelos y tablas para cada período de tiempo
                String[] columnas = {"Día", "Mes", "Año", "Hora", "Nombre", "RUT", "Teléfono"};
                
                // Crear y almacenar modelos para esta cancha
                DefaultTableModel modelHoy = new DefaultTableModel(columnas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                DefaultTableModel modelSemana = new DefaultTableModel(columnas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                DefaultTableModel modelPosteriores = new DefaultTableModel(columnas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };

                // Almacenar los modelos en los mapas
                modelosHoy.put(nombreCancha, modelHoy);
                modelosSemana.put(nombreCancha, modelSemana);
                modelosPosteriores.put(nombreCancha, modelPosteriores);

                // Crear y almacenar tablas
                JTable tblHoy = crearTablaPersonalizada(modelHoy);
                JTable tblSemana = crearTablaPersonalizada(modelSemana);
                JTable tblPosteriores = crearTablaPersonalizada(modelPosteriores);

                tablasHoy.put(nombreCancha, tblHoy);
                tablasSemana.put(nombreCancha, tblSemana);
                tablasPosteriores.put(nombreCancha, tblPosteriores);

                // Agregar las tablas a scrollpanes
                JScrollPane scrollHoy = new JScrollPane(tblHoy);
                JScrollPane scrollSemana = new JScrollPane(tblSemana);
                JScrollPane scrollPosteriores = new JScrollPane(tblPosteriores);

                // Agregar las pestañas de tiempo
                tabbedPaneTiempo.addTab("Hoy", scrollHoy);
                tabbedPaneTiempo.addTab("Esta Semana", scrollSemana);
                tabbedPaneTiempo.addTab("Posteriores", scrollPosteriores);

                // Cargar los agendamientos para esta cancha
                cargarAgendamientosPorCancha(conn, nombreCancha, modelHoy, modelSemana, modelPosteriores);

                // Ajustar anchos de columna
                ajustarAnchoColumnas(tblHoy);
                ajustarAnchoColumnas(tblSemana);
                ajustarAnchoColumnas(tblPosteriores);

                canchaPanel.add(tabbedPaneTiempo, BorderLayout.CENTER);
                
                // Agregar la pestaña de la cancha
                tabbedPaneCanchas.addTab(nombreCancha, canchaPanel);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar canchas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        rightPanel.add(tabbedPaneCanchas, BorderLayout.CENTER);

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
            int width = 200;  // Reducir el tamaño de las imágenes
            int height = 150;
            
            imagenesCanchas.put("futbol", new ImageIcon(imgFutbol.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            imagenesCanchas.put("futsal", new ImageIcon(imgFutsal.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            imagenesCanchas.put("baby futbol", new ImageIcon(imgBabyFutbol.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            System.err.println("Error al cargar imágenes: " + e.getMessage());
        }
    }

    private void cargarBotonesCanchas() {
        canchasPanel.removeAll();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT nombre, tipo FROM canchas";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                
                JPanel canchaPanel = new JPanel(new BorderLayout(5, 5));
                canchaPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                canchaPanel.setBackground(Color.WHITE);
                canchaPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                // Etiqueta con imagen
                JLabel lblImagen = new JLabel(imagenesCanchas.get(tipo.toLowerCase()));
                lblImagen.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                
                // Panel de información
                JPanel infoPanel = new JPanel(new GridLayout(2, 1));
                infoPanel.setBackground(Color.WHITE);
                JLabel lblNombre = new JLabel(nombre);
                JLabel lblTipo = new JLabel(tipo);
                lblNombre.setFont(new Font("Arial", Font.BOLD, 14));
                lblTipo.setFont(new Font("Arial", Font.PLAIN, 12));
                lblNombre.setHorizontalAlignment(JLabel.CENTER);
                lblTipo.setHorizontalAlignment(JLabel.CENTER);
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
                        // Resaltar el panel seleccionado con borde rojo
                        canchaPanel.setBorder(BorderFactory.createLineBorder(new Color(255, 0, 0), 2));
                    }
                });
                
                canchasPanel.add(canchaPanel);
            }
            
            canchasPanel.revalidate();
            canchasPanel.repaint();
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
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Limpiar todas las tablas
            for (DefaultTableModel model : modelosHoy.values()) {
                model.setRowCount(0);
            }
            for (DefaultTableModel model : modelosSemana.values()) {
                model.setRowCount(0);
            }
            for (DefaultTableModel model : modelosPosteriores.values()) {
                model.setRowCount(0);
            }

            String sql = "SELECT a.*, c.nombre as cancha_nombre, " +
                        "u.nombre as nombre_usuario, u.rut, u.telefono " +
                        "FROM agendamientos a " +
                        "JOIN canchas c ON a.cancha_id = c.id " +
                        "JOIN usuarios u ON a.usuario_id = u.id " +
                        "WHERE a.usuario_id = ? " +
                        "ORDER BY c.nombre, a.fecha, a.hora_inicio";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuarioId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    // Obtener fecha actual
                    java.util.Date hoy = new java.util.Date();
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(hoy);
                    cal.add(java.util.Calendar.DAY_OF_YEAR, 7);
                    java.util.Date finSemana = cal.getTime();

                    while (rs.next()) {
                        String nombreCancha = rs.getString("cancha_nombre");
                        java.util.Date fechaAgendamiento = rs.getDate("fecha");
                        
                        Object[] row = {
                            new java.text.SimpleDateFormat("dd").format(fechaAgendamiento),
                            new java.text.SimpleDateFormat("MMMM", new java.util.Locale("es", "ES")).format(fechaAgendamiento),
                            new java.text.SimpleDateFormat("yyyy").format(fechaAgendamiento),
                            rs.getTime("hora_inicio") + " - " + rs.getTime("hora_fin"),
                            rs.getString("nombre_usuario"),
                            rs.getString("rut"),
                            rs.getString("telefono")
                        };

                        // Obtener los modelos correspondientes a la cancha
                        DefaultTableModel modelHoy = modelosHoy.get(nombreCancha);
                        DefaultTableModel modelSemana = modelosSemana.get(nombreCancha);
                        DefaultTableModel modelPosteriores = modelosPosteriores.get(nombreCancha);

                        // Clasificar el agendamiento según la fecha
                        if (fechaAgendamiento.equals(hoy)) {
                            modelHoy.addRow(row);
                        } else if (fechaAgendamiento.after(hoy) && fechaAgendamiento.before(finSemana)) {
                            modelSemana.addRow(row);
                        } else if (fechaAgendamiento.after(hoy)) {
                            modelPosteriores.addRow(row);
                        }
                    }
                }
            }

            // Ajustar anchos de columna para todas las tablas
            for (JTable tabla : tablasHoy.values()) {
                ajustarAnchoColumnas(tabla);
            }
            for (JTable tabla : tablasSemana.values()) {
                ajustarAnchoColumnas(tabla);
            }
            for (JTable tabla : tablasPosteriores.values()) {
                ajustarAnchoColumnas(tabla);
            }

            // Inicializar campos vacíos
            txtNombre.setText("");
            txtRut.setText("");
            txtTelefono.setText("");
            cmbHoraInicio.setSelectedIndex(0);
            cmbHoraFin.setSelectedIndex(0);
            datePicker.setDate(Calendar.getInstance().getTime());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar agendamientos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarAgendamientosPorCancha(Connection conn, String nombreCancha, 
            DefaultTableModel modelHoy, DefaultTableModel modelSemana, DefaultTableModel modelPosteriores) 
            throws SQLException {
        
        String sql = "SELECT a.*, u.nombre as nombre_usuario, u.rut, u.telefono " +
                    "FROM agendamientos a " +
                    "JOIN canchas c ON a.cancha_id = c.id " +
                    "JOIN usuarios u ON a.usuario_id = u.id " +
                    "WHERE a.usuario_id = ? AND c.nombre = ? " +
                    "ORDER BY a.fecha, a.hora_inicio";
                    
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, usuarioId);
        pstmt.setString(2, nombreCancha);
        ResultSet rs = pstmt.executeQuery();

        // Obtener fecha actual
        java.util.Date hoy = new java.util.Date();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(hoy);
        cal.add(java.util.Calendar.DAY_OF_YEAR, 7);
        java.util.Date finSemana = cal.getTime();

        while (rs.next()) {
            java.util.Date fechaAgendamiento = rs.getDate("fecha");
            Object[] row = {
                new java.text.SimpleDateFormat("dd").format(fechaAgendamiento),
                new java.text.SimpleDateFormat("MMMM", new java.util.Locale("es", "ES")).format(fechaAgendamiento),
                new java.text.SimpleDateFormat("yyyy").format(fechaAgendamiento),
                rs.getTime("hora_inicio") + " - " + rs.getTime("hora_fin"),
                rs.getString("nombre_usuario"),
                rs.getString("rut"),
                rs.getString("telefono")
            };

            // Clasificar el agendamiento según la fecha
            if (fechaAgendamiento.equals(hoy)) {
                modelHoy.addRow(row);
            } else if (fechaAgendamiento.after(hoy) && fechaAgendamiento.before(finSemana)) {
                modelSemana.addRow(row);
            } else if (fechaAgendamiento.after(hoy)) {
                modelPosteriores.addRow(row);
            }
        }
    }

    private void ajustarAnchoColumnas(JTable tabla) {
        // Ajustar el ancho de las columnas según el contenido
        int[] anchos = {60, 120, 80, 120, 150, 100, 100}; // Agregado ancho para la columna de teléfono
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);
        }
    }

    private JTable crearTablaPersonalizada(DefaultTableModel modelo) {
        // Actualizar las columnas para incluir teléfono
        String[] columnas = {"Día", "Mes", "Año", "Hora", "Nombre", "RUT", "Teléfono"};
        modelo.setColumnIdentifiers(columnas);

        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Arial", Font.PLAIN, 12));
        tabla.setRowHeight(30);
        tabla.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setBackground(Color.WHITE);
        tabla.getTableHeader().setBackground(new Color(240, 240, 240));
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.setSelectionBackground(new Color(51, 153, 255));
        tabla.setSelectionForeground(Color.WHITE);

        // Renderizador personalizado para las celdas
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return tabla;
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

        Date fechaSeleccionada = datePicker.getDate();
        if (fechaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una fecha",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validar que la fecha no sea anterior a hoy
        if (fechaSeleccionada.before(new Date())) {
            JOptionPane.showMessageDialog(this, "La fecha seleccionada no puede ser anterior a hoy",
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

            // Insertar agendamiento usando la fecha del datePicker
            String sql = "INSERT INTO agendamientos (usuario_id, cancha_id, fecha, hora_inicio, hora_fin) " +
                        "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, canchaId);
            pstmt.setDate(3, new java.sql.Date(datePicker.getDate().getTime()));
            pstmt.setTime(4, Time.valueOf(horaInicio));
            pstmt.setTime(5, Time.valueOf(horaFin));
            
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Agendamiento realizado exitosamente!");
            
            // Limpiar los campos después del agendamiento exitoso
            txtNombre.setText("");
            txtRut.setText("");
            txtTelefono.setText("");
            cmbHoraInicio.setSelectedIndex(0);
            cmbHoraFin.setSelectedIndex(0);
            datePicker.setDate(Calendar.getInstance().getTime()); // Resetear la fecha al día actual
            
            // Recargar los agendamientos
            cargarAgendamientos();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al realizar agendamiento: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 