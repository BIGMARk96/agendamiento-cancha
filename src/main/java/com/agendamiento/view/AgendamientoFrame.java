package com.agendamiento.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Calendar;
import java.util.Vector;
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

    // division canchas y tablas
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

        // inicializar imagenes
        cargarImagenes();

        // panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // panel seleccion de cancha
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(51, 153, 255), 2), "Selección de Cancha"));
        leftPanel.setPreferredSize(new Dimension(250, 0));
        
        // panel canchas
        canchasPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        canchasPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        cargarBotonesCanchas();
        
        JScrollPane scrollCanchas = new JScrollPane(canchasPanel);
        leftPanel.add(scrollCanchas, BorderLayout.CENTER);

        // panel central datos del que solicita agendamiento
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(51, 153, 255), 2), "Detalles de Agendamiento"));
        centerPanel.setPreferredSize(new Dimension(300, 0));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // datos del solicitante
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        // nombre
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(lblNombre, gbc);
        
        txtNombre = new JTextField(20);
        txtNombre.setFont(fieldFont);
        txtNombre.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        centerPanel.add(txtNombre, gbc);

        // rut
        JLabel lblRut = new JLabel("RUT:");
        lblRut.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(lblRut, gbc);
        
        txtRut = new JTextField(15);
        txtRut.setFont(fieldFont);
        txtRut.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        centerPanel.add(txtRut, gbc);

        // celular
        JLabel lblTelefono = new JLabel("Teléfono:");
        lblTelefono.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(lblTelefono, gbc);
        
        txtTelefono = new JTextField(15);
        txtTelefono.setFont(fieldFont);
        txtTelefono.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        centerPanel.add(txtTelefono, gbc);

        // fecha
        JLabel lblFecha = new JLabel("Fecha:");
        lblFecha.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(lblFecha, gbc);

        // calendario
        datePicker = new JXDatePicker();
        datePicker.setDate(Calendar.getInstance().getTime());
        datePicker.setFormats(new java.text.SimpleDateFormat("dd/MM/yyyy"));
        
        // estilo del calendario
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
        datePicker.setPreferredSize(new Dimension(200, 35));
        
        // fecha de hoy disponible y borra dias anteriores
        datePicker.getMonthView().setLowerBound(Calendar.getInstance().getTime());
        
        // estilo de fecha
        datePicker.getEditor().setFont(new Font("Segoe UI", Font.PLAIN, 14));
        datePicker.getEditor().setBackground(Color.WHITE);
        datePicker.getEditor().setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        gbc.gridx = 1;
        centerPanel.add(datePicker, gbc);

        // hora inicio
        JLabel lblHoraInicio = new JLabel("Hora Inicio:");
        lblHoraInicio.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(lblHoraInicio, gbc);

        cmbHoraInicio = new JComboBox<>();
        cmbHoraInicio.setFont(fieldFont);
        cmbHoraInicio.setPreferredSize(new Dimension(200, 35));
        llenarHorarios(cmbHoraInicio);
        gbc.gridx = 1;
        centerPanel.add(cmbHoraInicio, gbc);

        // hora fin
        JLabel lblHoraFin = new JLabel("Hora Fin:");
        lblHoraFin.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(lblHoraFin, gbc);

        cmbHoraFin = new JComboBox<>();
        cmbHoraFin.setFont(fieldFont);
        cmbHoraFin.setPreferredSize(new Dimension(200, 35));
        llenarHorarios(cmbHoraFin);
        gbc.gridx = 1;
        centerPanel.add(cmbHoraFin, gbc);

        // boton agendar
        JButton btnAgendar = new JButton("Agendar Cancha");
        btnAgendar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgendar.setPreferredSize(new Dimension(250, 40));
        btnAgendar.setBackground(new Color(255, 165, 0));
        btnAgendar.setForeground(Color.BLACK);
        btnAgendar.setFocusPainted(false);
        btnAgendar.setBorderPainted(false);
        btnAgendar.setOpaque(true);
        btnAgendar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 8, 0, 8);
        centerPanel.add(btnAgendar, gbc);

        // panel derecho para lista de agendamientos
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(51, 153, 255), 2), "Mis Agendamientos"));
        rightPanel.setPreferredSize(new Dimension(900, 0));

        // panel con pestañas para diferentes canchas
        JTabbedPane tabbedPaneCanchas = new JTabbedPane(JTabbedPane.TOP);
        tabbedPaneCanchas.setFont(new Font("Arial", Font.BOLD, 14));
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // llamar canchas
            String sqlCanchas = "SELECT DISTINCT c.nombre, c.tipo FROM canchas c ORDER BY c.nombre";
            Statement stmtCanchas = conn.createStatement();
            ResultSet rsCanchas = stmtCanchas.executeQuery(sqlCanchas);

            while (rsCanchas.next()) {
                String nombreCancha = rsCanchas.getString("nombre");
                String tipoCancha = rsCanchas.getString("tipo");
                
                // crear panel para esta cancha
                JPanel canchaPanel = new JPanel(new BorderLayout());
                
                // crear pestañas de tiempo para esta cancha
                JTabbedPane tabbedPaneTiempo = new JTabbedPane(JTabbedPane.TOP);
                tabbedPaneTiempo.setFont(new Font("Arial", Font.BOLD, 12));
                
                // tablas con agendamiento cancha
                String[] columnas = {"Día", "Mes", "Año", "Hora", "Nombre", "RUT", "Teléfono", "Acciones"};
                
                // crear y almacenar modelos para esta cancha
                DefaultTableModel modelHoy = new DefaultTableModel(columnas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                    
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return String.class;
                    }
                };
                
                DefaultTableModel modelSemana = new DefaultTableModel(columnas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                    
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return String.class;
                    }
                };
                
                DefaultTableModel modelPosteriores = new DefaultTableModel(columnas, 0) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                    
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return String.class;
                    }
                };

                // almacenar los modelos en los mapas
                modelosHoy.put(nombreCancha, modelHoy);
                modelosSemana.put(nombreCancha, modelSemana);
                modelosPosteriores.put(nombreCancha, modelPosteriores);

                // crear y almacenar tablas
                JTable tblHoy = crearTablaPersonalizada(modelHoy);
                JTable tblSemana = crearTablaPersonalizada(modelSemana);
                JTable tblPosteriores = crearTablaPersonalizada(modelPosteriores);

                tablasHoy.put(nombreCancha, tblHoy);
                tablasSemana.put(nombreCancha, tblSemana);
                tablasPosteriores.put(nombreCancha, tblPosteriores);

                // agregar las tablas a scrollpanes
                JScrollPane scrollHoy = new JScrollPane(tblHoy);
                JScrollPane scrollSemana = new JScrollPane(tblSemana);
                JScrollPane scrollPosteriores = new JScrollPane(tblPosteriores);

                // agregar las pestañas de tiempo
                tabbedPaneTiempo.addTab("Hoy", scrollHoy);
                tabbedPaneTiempo.addTab("Esta Semana", scrollSemana);
                tabbedPaneTiempo.addTab("Posteriores", scrollPosteriores);

                // cargar los agendamientos para esta cancha
                cargarAgendamientosPorCancha(conn, nombreCancha, modelHoy, modelSemana, modelPosteriores);

                // ajustar anchos de columna
                ajustarAnchoColumnas(tblHoy);
                ajustarAnchoColumnas(tblSemana);
                ajustarAnchoColumnas(tblPosteriores);

                canchaPanel.add(tabbedPaneTiempo, BorderLayout.CENTER);
                
                // agregar la pestaña de la cancha
                tabbedPaneCanchas.addTab(nombreCancha, canchaPanel);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar canchas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        rightPanel.add(tabbedPaneCanchas, BorderLayout.CENTER);

        // agregar 3 paneles al panel principal
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        // eventos
        btnAgendar.addActionListener(e -> realizarAgendamiento());
        cmbHoraInicio.addActionListener(e -> actualizarHoraFin());

        // agendamientos existentes
        cargarAgendamientos();

        add(mainPanel);
    }

    // cargar imagenes desde recursos
    private void cargarImagenes() {
        imagenesCanchas = new HashMap<>();
        try {
            
            ImageIcon imgFutbol = new ImageIcon(getClass().getResource("/images/futbol.jpg"));
            ImageIcon imgFutsal = new ImageIcon(getClass().getResource("/images/futsal.jpg"));
            ImageIcon imgBabyFutbol = new ImageIcon(getClass().getResource("/images/baby_futbol.jpg"));

            // tamaño imagen
            int width = 200;  
            int height = 150;
            
            imagenesCanchas.put("futbol", new ImageIcon(imgFutbol.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            imagenesCanchas.put("futsal", new ImageIcon(imgFutsal.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
            imagenesCanchas.put("baby futbol", new ImageIcon(imgBabyFutbol.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH)));
        } catch (Exception e) {
            System.err.println("Error al cargar imagenes: " + e.getMessage());
        }
    }

    private void cargarBotonesCanchas() {
        canchasPanel.removeAll();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // modificar la consulta sql para evitar duplicados
            String sql = "SELECT DISTINCT nombre, tipo FROM canchas ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // crear un set para rastrear las canchas ya procesadas
            java.util.Set<String> canchasProcesadas = new java.util.HashSet<>();

            while (rs.next()) {
                String nombre = rs.getString("nombre");
                String tipo = rs.getString("tipo");
                String claveCancha = nombre + "|" + tipo;
                
                // solo procesar si no hemos visto esta combinacion antes
                if (!canchasProcesadas.contains(claveCancha)) {
                    canchasProcesadas.add(claveCancha);
                    
                    JPanel canchaPanel = new JPanel(new BorderLayout(5, 5));
                    canchaPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                    canchaPanel.setBackground(Color.WHITE);
                    canchaPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    
                    // etiqueta con imagen
                    JLabel lblImagen = new JLabel(imagenesCanchas.get(tipo.toLowerCase()));
                    lblImagen.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                    
                    // panel de informacion con gridlayout para mejor espaciado
                    JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
                    infoPanel.setBackground(Color.WHITE);
                    
                    // nombre de la cancha con fuente mas grande
                    JLabel lblNombre = new JLabel(nombre);
                    lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 18));
                    lblNombre.setHorizontalAlignment(JLabel.CENTER);
                    
                    // tipo de cancha tambien con fuente mas grande
                    JLabel lblTipo = new JLabel(tipo);
                    lblTipo.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    lblTipo.setHorizontalAlignment(JLabel.CENTER);
                    
                    infoPanel.add(lblNombre);
                    infoPanel.add(lblTipo);
                    infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
                    
                    canchaPanel.add(lblImagen, BorderLayout.CENTER);
                    canchaPanel.add(infoPanel, BorderLayout.SOUTH);
                    
                    // hacer el panel seleccionable con efectos visuales mejorados
                    canchaPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            seleccionarCancha(nombre + " - " + tipo);
                            // actualizar bordes de todos los paneles
                            for (Component c : canchasPanel.getComponents()) {
                                if (c instanceof JPanel) {
                                    JPanel panel = (JPanel) c;
                                    panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                                    panel.setBackground(Color.WHITE);
                                    // resetear el tamaño de fuente de las etiquetas
                                    Component[] components = ((JPanel)panel.getComponent(1)).getComponents();
                                    ((JLabel)components[0]).setFont(new Font("Segoe UI", Font.BOLD, 18));
                                    ((JLabel)components[1]).setFont(new Font("Segoe UI", Font.PLAIN, 16));
                                }
                            }
                            // resaltar el panel seleccionado
                            canchaPanel.setBorder(BorderFactory.createLineBorder(new Color(51, 153, 255), 3));
                            canchaPanel.setBackground(new Color(242, 247, 255));
                            // aumentar el tamaño de la fuente cuando esta seleccionado
                            Component[] components = infoPanel.getComponents();
                            ((JLabel)components[0]).setFont(new Font("Segoe UI", Font.BOLD, 20));
                            ((JLabel)components[1]).setFont(new Font("Segoe UI", Font.PLAIN, 18));
                        }
                        
                        public void mouseEntered(java.awt.event.MouseEvent evt) {
                            if (!canchaPanel.getBackground().equals(new Color(242, 247, 255))) {
                                canchaPanel.setBackground(new Color(250, 250, 250));
                            }
                        }
                        
                        public void mouseExited(java.awt.event.MouseEvent evt) {
                            if (!canchaPanel.getBorder().equals(BorderFactory.createLineBorder(new Color(51, 153, 255), 3))) {
                                canchaPanel.setBackground(Color.WHITE);
                            }
                        }
                    });
                    
                    canchasPanel.add(canchaPanel);
                }
            }
            
            canchasPanel.revalidate();
            canchasPanel.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar canchas: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void llenarHorarios(JComboBox<LocalTime> comboBox) {
        LocalTime inicio = LocalTime.of(8, 0); // 8:00 am
        LocalTime fin = LocalTime.of(22, 0);   // 10:00 pm
        
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
            LocalTime fin = LocalTime.of(22, 0); // cambiado a 22:00 para evitar problemas
            
            while (!hora.isAfter(fin)) {
                cmbHoraFin.addItem(hora);
                hora = hora.plusHours(1);
            }
            
            // si no hay horas disponibles, mostrar mensaje
            if (cmbHoraFin.getItemCount() == 0) {
                JOptionPane.showMessageDialog(this, 
                    "No hay horarios disponibles despues de la hora seleccionada",
                    "Aviso", 
                    JOptionPane.INFORMATION_MESSAGE);
                cmbHoraInicio.setSelectedIndex(0); // resetear a la primera hora
                actualizarHoraFin(); // actualizar nuevamente
            }
        }
    }

    private void seleccionarCancha(String cancha) {
        this.canchaSeleccionada = cancha;
    }

    private void cargarAgendamientos() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("Iniciando carga de agendamientos...");
            
            // limpiar todas las tablas
            for (DefaultTableModel model : modelosHoy.values()) {
                model.setRowCount(0);
            }
            for (DefaultTableModel model : modelosSemana.values()) {
                model.setRowCount(0);
            }
            for (DefaultTableModel model : modelosPosteriores.values()) {
                model.setRowCount(0);
            }

            String sql = "SELECT a.*, c.nombre as cancha_nombre " +
                        "FROM agendamientos a " +
                        "JOIN canchas c ON a.cancha_id = c.id " +
                        "WHERE a.usuario_id = ? " +
                        "ORDER BY c.nombre, a.fecha, a.hora_inicio";
            
            System.out.println("SQL Query: " + sql);
            System.out.println("Usuario ID: " + usuarioId);
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, usuarioId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    // obtener fecha actual
                    java.util.Date hoy = new java.util.Date();
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(hoy);
                    cal.add(java.util.Calendar.DAY_OF_YEAR, 7);
                    java.util.Date finSemana = cal.getTime();

                    int contadorAgendamientos = 0;
                    while (rs.next()) {
                        contadorAgendamientos++;
                        String nombreCancha = rs.getString("cancha_nombre");
                        java.util.Date fechaAgendamiento = rs.getDate("fecha");
                        
                        System.out.println("Procesando agendamiento: Cancha=" + nombreCancha + 
                                         ", Fecha=" + fechaAgendamiento);
                        
                        Object[] row = {
                            new java.text.SimpleDateFormat("dd").format(fechaAgendamiento),
                            new java.text.SimpleDateFormat("MMMM", new java.util.Locale("es", "ES")).format(fechaAgendamiento),
                            new java.text.SimpleDateFormat("yyyy").format(fechaAgendamiento),
                            rs.getTime("hora_inicio") + " - " + rs.getTime("hora_fin"),
                            rs.getString("nombre_cliente"),
                            rs.getString("rut_cliente"),
                            rs.getString("telefono_cliente"),
                            "Eliminar"
                        };

                        // obtener los modelos correspondientes a la cancha
                        DefaultTableModel modelHoy = modelosHoy.get(nombreCancha);
                        DefaultTableModel modelSemana = modelosSemana.get(nombreCancha);
                        DefaultTableModel modelPosteriores = modelosPosteriores.get(nombreCancha);

                        if (modelHoy == null || modelSemana == null || modelPosteriores == null) {
                            System.out.println("Error: Modelos no encontrados para la cancha " + nombreCancha);
                            continue;
                        }

                        // clasificar el agendamiento segun la fecha
                        if (fechaEsHoy(fechaAgendamiento)) {
                            modelHoy.addRow(row);
                            System.out.println("Agregado a Hoy: " + nombreCancha);
                        } else if (fechaAgendamiento.after(hoy) && fechaAgendamiento.before(finSemana)) {
                            modelSemana.addRow(row);
                            System.out.println("Agregado a Esta Semana: " + nombreCancha);
                        } else if (fechaAgendamiento.after(hoy)) {
                            modelPosteriores.addRow(row);
                            System.out.println("Agregado a Posteriores: " + nombreCancha);
                        }
                    }
                    System.out.println("Total de agendamientos procesados: " + contadorAgendamientos);
                }
            }

            // ajustar anchos de columna para todas las tablas
            for (JTable tabla : tablasHoy.values()) {
                ajustarAnchoColumnas(tabla);
            }
            for (JTable tabla : tablasSemana.values()) {
                ajustarAnchoColumnas(tabla);
            }
            for (JTable tabla : tablasPosteriores.values()) {
                ajustarAnchoColumnas(tabla);
            }
        } catch (SQLException ex) {
            System.out.println("Error al cargar agendamientos: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar agendamientos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean fechaEsHoy(Date fecha) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(fecha);
        cal2.setTime(new Date());
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private void cargarAgendamientosPorCancha(Connection conn, String nombreCancha, 
            DefaultTableModel modelHoy, DefaultTableModel modelSemana, DefaultTableModel modelPosteriores) 
            throws SQLException {
        
        String sql = "SELECT a.*, c.nombre as cancha_nombre " +
                    "FROM agendamientos a " +
                    "JOIN canchas c ON a.cancha_id = c.id " +
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
                rs.getString("nombre_cliente"),
                rs.getString("rut_cliente"),
                rs.getString("telefono_cliente"),
                "Eliminar"
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
        if (tabla == null || tabla.getColumnCount() == 0) {
            System.out.println("Error: Tabla nula o sin columnas");
            return;
        }

        // ajustar el ancho de las columnas segun el contenido
        int[] anchos = {60, 100, 70, 150, 150, 100, 100, 80};
        
        for (int i = 0; i < tabla.getColumnCount() && i < anchos.length; i++) {
            TableColumn columna = tabla.getColumnModel().getColumn(i);
            columna.setPreferredWidth(anchos[i]);
            columna.setMinWidth(anchos[i]);
            
            // la ultima columna (acciones) tiene un ancho fijo
            if (i == tabla.getColumnCount() - 1) {
                columna.setMaxWidth(anchos[i]);
            }
        }
        
        // configurar la altura de las filas
        tabla.setRowHeight(35);
    }

    private JTable crearTablaPersonalizada(DefaultTableModel modelo) {
        // crear la tabla con el modelo proporcionado
        JTable tabla = new JTable(modelo);
        
        // configurar la apariencia de la tabla
        tabla.setRowHeight(30);
        tabla.setIntercellSpacing(new Dimension(10, 5));
        tabla.setGridColor(Color.LIGHT_GRAY);
        tabla.setShowVerticalLines(true);
        tabla.setShowHorizontalLines(true);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoCreateRowSorter(true);

        // configurar el renderizador para centrar el contenido
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // aplicar el renderizador centrado a todas las columnas excepto la ultima
        for (int i = 0; i < tabla.getColumnCount() - 1; i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // configurar el renderizador para el boton eliminar
        tabla.getColumnModel().getColumn(tabla.getColumnCount() - 1).setCellRenderer(new DefaultTableCellRenderer() {
            private final JButton button = new JButton("Eliminar");
            {
                button.setBackground(new Color(220, 53, 69));
                button.setForeground(Color.WHITE);
                button.setOpaque(true);
                button.setBorderPainted(false);
                button.setFocusPainted(false);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                return button;
            }
        });

        // configurar el manejador de clics para el boton eliminar
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tabla.columnAtPoint(e.getPoint());
                int row = tabla.rowAtPoint(e.getPoint());

                if (row >= 0 && column == tabla.getColumnCount() - 1) {
                    int response = JOptionPane.showConfirmDialog(
                        SwingUtilities.getWindowAncestor(tabla),
                        "¿Está seguro que desea eliminar este agendamiento?",
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (response == JOptionPane.YES_OPTION) {
                        try {
                            eliminarAgendamiento(tabla, row);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null,
                                "Error al eliminar el agendamiento: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                int column = tabla.columnAtPoint(e.getPoint());
                if (column == tabla.getColumnCount() - 1) {
                    tabla.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tabla.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return tabla;
    }

    private void eliminarAgendamiento(JTable tabla, int row) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            DefaultTableModel model = (DefaultTableModel) tabla.getModel();
            
            // obtener los datos de la fila
            String dia = model.getValueAt(row, 0).toString();
            String mes = model.getValueAt(row, 1).toString();
            String año = model.getValueAt(row, 2).toString();
            String hora = model.getValueAt(row, 3).toString();
            String rut = model.getValueAt(row, 5).toString();
            
            // construir la fecha en formato correcto para mysql
            String fecha = String.format("%s-%s-%s", año, obtenerNumeroMes(mes), dia);
            String[] horas = hora.split(" - ");
            
            // primero obtener el id del agendamiento
            String sqlSelect = "SELECT id FROM agendamientos " +
                             "WHERE usuario_id = ? " +
                             "AND DATE(fecha) = ? " +
                             "AND TIME(hora_inicio) = ? " +
                             "AND TIME(hora_fin) = ? " +
                             "AND rut_cliente = ? " +
                             "LIMIT 1";
            
            try (PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect)) {
                pstmtSelect.setInt(1, usuarioId);
                pstmtSelect.setString(2, fecha);
                pstmtSelect.setString(3, horas[0]);
                pstmtSelect.setString(4, horas[1]);
                pstmtSelect.setString(5, rut);
                
                ResultSet rs = pstmtSelect.executeQuery();
                if (rs.next()) {
                    int agendamientoId = rs.getInt("id");
                    
                    // ahora eliminar usando el id
                    String sqlDelete = "DELETE FROM agendamientos WHERE id = ?";
                    try (PreparedStatement pstmtDelete = conn.prepareStatement(sqlDelete)) {
                        pstmtDelete.setInt(1, agendamientoId);
                        
                        int filasAfectadas = pstmtDelete.executeUpdate();
                        if (filasAfectadas > 0) {
                            JOptionPane.showMessageDialog(null, 
                                "Agendamiento eliminado exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                            
                            // recargar los agendamientos
                            SwingUtilities.invokeLater(() -> {
                                cargarAgendamientos();
                                tabla.repaint();
                            });
                        } else {
                            JOptionPane.showMessageDialog(null,
                                "No se pudo eliminar el agendamiento",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                        "No se encontró el agendamiento para eliminar",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                "Error al eliminar el agendamiento: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private String obtenerNumeroMes(String nombreMes) {
        Map<String, String> meses = new HashMap<>();
        meses.put("enero", "01");
        meses.put("febrero", "02");
        meses.put("marzo", "03");
        meses.put("abril", "04");
        meses.put("mayo", "05");
        meses.put("junio", "06");
        meses.put("julio", "07");
        meses.put("agosto", "08");
        meses.put("septiembre", "09");
        meses.put("octubre", "10");
        meses.put("noviembre", "11");
        meses.put("diciembre", "12");
        return meses.get(nombreMes.toLowerCase());
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

        // nueva validacion de fecha que solo compara el dia, mes y año
        Calendar calHoy = Calendar.getInstance();
        Calendar calSeleccionada = Calendar.getInstance();
        calSeleccionada.setTime(fechaSeleccionada);
        
        // resetear las horas, minutos, segundos y milisegundos para ambas fechas
        calHoy.set(Calendar.HOUR_OF_DAY, 0);
        calHoy.set(Calendar.MINUTE, 0);
        calHoy.set(Calendar.SECOND, 0);
        calHoy.set(Calendar.MILLISECOND, 0);
        
        calSeleccionada.set(Calendar.HOUR_OF_DAY, 0);
        calSeleccionada.set(Calendar.MINUTE, 0);
        calSeleccionada.set(Calendar.SECOND, 0);
        calSeleccionada.set(Calendar.MILLISECOND, 0);
        
        if (calSeleccionada.before(calHoy)) {
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
            conn.setAutoCommit(false); // iniciar transaccion

            try {
                // obtener id de la cancha
                String sqlCancha = "SELECT id FROM canchas WHERE nombre = ? AND tipo = ?";
                PreparedStatement pstmtCancha = conn.prepareStatement(sqlCancha);
                pstmtCancha.setString(1, nombreCancha);
                pstmtCancha.setString(2, tipoCancha);
                ResultSet rsCancha = pstmtCancha.executeQuery();
                
                if (!rsCancha.next()) {
                    throw new SQLException("Cancha no encontrada");
                }
                int canchaId = rsCancha.getInt("id");

                // verificar si ya existe un agendamiento para la misma cancha, fecha y hora
                String sqlCheckAgendamiento = "SELECT COUNT(*) FROM agendamientos " +
                    "WHERE cancha_id = ? AND fecha = ? AND " +
                    "((hora_inicio <= ? AND hora_fin > ?) OR " +
                    "(hora_inicio < ? AND hora_fin >= ?) OR " +
                    "(hora_inicio >= ? AND hora_fin <= ?))";
                
                PreparedStatement pstmtCheck = conn.prepareStatement(sqlCheckAgendamiento);
                pstmtCheck.setInt(1, canchaId);
                pstmtCheck.setDate(2, new java.sql.Date(fechaSeleccionada.getTime()));
                pstmtCheck.setTime(3, Time.valueOf(horaInicio));
                pstmtCheck.setTime(4, Time.valueOf(horaInicio));
                pstmtCheck.setTime(5, Time.valueOf(horaFin));
                pstmtCheck.setTime(6, Time.valueOf(horaFin));
                pstmtCheck.setTime(7, Time.valueOf(horaInicio));
                pstmtCheck.setTime(8, Time.valueOf(horaFin));
                
                ResultSet rsCheck = pstmtCheck.executeQuery();
                rsCheck.next();
                if (rsCheck.getInt(1) > 0) {
                    throw new SQLException("Ya existe un agendamiento para este horario");
                }

                // insertar agendamiento con los datos personales
                String sqlAgendamiento = "INSERT INTO agendamientos (usuario_id, cancha_id, fecha, hora_inicio, hora_fin, nombre_cliente, rut_cliente, telefono_cliente) " +
                                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmtAgendamiento = conn.prepareStatement(sqlAgendamiento);
                pstmtAgendamiento.setInt(1, usuarioId);
                pstmtAgendamiento.setInt(2, canchaId);
                pstmtAgendamiento.setDate(3, new java.sql.Date(fechaSeleccionada.getTime()));
                pstmtAgendamiento.setTime(4, Time.valueOf(horaInicio));
                pstmtAgendamiento.setTime(5, Time.valueOf(horaFin));
                pstmtAgendamiento.setString(6, nombre);
                pstmtAgendamiento.setString(7, rut);
                pstmtAgendamiento.setString(8, telefono);
                pstmtAgendamiento.executeUpdate();

                conn.commit(); // confirmar transaccion
                JOptionPane.showMessageDialog(this, "Agendamiento realizado exitosamente!");
                
                // limpiar los campos despues del agendamiento exitoso
                txtNombre.setText("");
                txtRut.setText("");
                txtTelefono.setText("");
                cmbHoraInicio.setSelectedIndex(0);
                cmbHoraFin.setSelectedIndex(0);
                datePicker.setDate(Calendar.getInstance().getTime());
                
                // recargar los agendamientos
                cargarAgendamientos();
            } catch (SQLException ex) {
                conn.rollback(); // revertir transaccion en caso de error
                throw ex;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al realizar agendamiento: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 
