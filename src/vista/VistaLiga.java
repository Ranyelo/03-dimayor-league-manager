package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VistaLiga extends JFrame {

    // Paleta de colores Dark Blue estricta
    private Color bgDark = new Color(15, 23, 42);
    private Color bgPanel = new Color(30, 41, 59);
    private Color bgInput = new Color(51, 65, 85);
    private Color fgText = new Color(226, 232, 240);
    private Color accentBlue = new Color(56, 189, 248);

    private Color colorCampeon = new Color(234, 179, 8, 50);
    private Color colorTop8 = new Color(16, 185, 129, 30);
    private Color colorDescenso = new Color(239, 68, 68, 30);

    public JComboBox<String> cbLocal, cbVisitante, cbOrden;
    public JSpinner spGolesLocal, spGolesVisitante, spAmarillasL, spRojasL, spAmarillasV, spRojasV, spTiempo, spAdicion;
    public JButton btnRegistrar, btnReporte, btnAleatorio, btnExportar, btnLimpiar;
    public DefaultTableModel modeloTabla, modeloInsights;
    public JTable tabla, tablaInsights;

    public VistaLiga() {
        aplicarTemaOscuroGlobal();

        setTitle("Dimayor Pro - Dashboard de Torneo");
        setSize(1350, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(bgDark);

        inicializarComponentes();
    }

    private void aplicarTemaOscuroGlobal() {
        UIManager.put("Panel.background", bgDark);
        UIManager.put("OptionPane.background", bgPanel);
        UIManager.put("OptionPane.messageForeground", fgText);
        UIManager.put("ComboBox.background", bgInput);
        UIManager.put("ComboBox.foreground", fgText);
        UIManager.put("ComboBox.selectionBackground", accentBlue);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("Viewport.background", bgDark);
    }

    private void inicializarComponentes() {
        // --- PANEL SUPERIOR ---
        JPanel panelTop = new JPanel(new GridLayout(3, 8, 10, 10));
        panelTop.setBackground(bgPanel);
        panelTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(accentBlue), "Registro Detallado del Partido"));
        ((javax.swing.border.TitledBorder)panelTop.getBorder()).setTitleColor(accentBlue);

        cbLocal = crearCombo(); cbVisitante = crearCombo();
        spGolesLocal = crearSpinner(); spGolesVisitante = crearSpinner();
        spAmarillasL = crearSpinner(); spAmarillasV = crearSpinner();
        spRojasL = crearSpinner(); spRojasV = crearSpinner();
        spTiempo = crearSpinnerLimitado(90, 0, 120);
        spAdicion = crearSpinnerLimitado(0, 0, 15);

        panelTop.add(crearLabel("Equipo")); panelTop.add(crearLabel("Goles")); panelTop.add(crearLabel("Amarillas")); panelTop.add(crearLabel("Rojas"));
        panelTop.add(new JLabel("")); panelTop.add(crearLabel("Min. Jugados")); panelTop.add(crearLabel("Min. Adición")); panelTop.add(new JLabel(""));

        panelTop.add(cbLocal); panelTop.add(spGolesLocal); panelTop.add(spAmarillasL); panelTop.add(spRojasL);
        panelTop.add(crearLabel("  LOCAL")); panelTop.add(spTiempo); panelTop.add(spAdicion); panelTop.add(new JLabel(""));

        panelTop.add(cbVisitante); panelTop.add(spGolesVisitante); panelTop.add(spAmarillasV); panelTop.add(spRojasV);
        panelTop.add(crearLabel("  VISITA"));

        btnRegistrar = crearBoton("Registrar", new Color(37, 99, 235));
        btnAleatorio = crearBoton("Generar Jornada", new Color(139, 92, 246));
        panelTop.add(btnRegistrar); panelTop.add(btnAleatorio); panelTop.add(new JLabel(""));

        add(panelTop, BorderLayout.NORTH);

        // --- TABLA CENTRAL ---
        String[] colPosiciones = {"POS", "EQUIPO", "PJ", "PG", "PE", "PP", "GF", "GC", "DG", "PTS", "TA", "TR"};
        modeloTabla = new DefaultTableModel(colPosiciones, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tabla = estilizarTablaPrincipal(new JTable(modeloTabla));

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.getViewport().setBackground(bgDark);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 5));
        add(scrollTabla, BorderLayout.CENTER);

        // --- PANEL DERECHO (INSIGHTS) ---
        String[] colInsights = {"Distinción", "Equipo (Dato)"};
        modeloInsights = new DefaultTableModel(colInsights, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tablaInsights = estilizarTablaInsights(new JTable(modeloInsights));

        JScrollPane scrollInsights = new JScrollPane(tablaInsights);
        scrollInsights.setPreferredSize(new Dimension(380, 0));
        scrollInsights.getViewport().setBackground(bgDark);
        scrollInsights.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 10));
        add(scrollInsights, BorderLayout.EAST);

        // --- PANEL INFERIOR ---
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setBackground(bgDark);
        panelBottom.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel panelOrden = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelOrden.setBackground(bgDark);
        panelOrden.add(crearLabel("Filtrar Tabla: "));
        String[] opcionesOrden = {
                "🏆 Ordenar por Puntos (Oficial)",
                "🔥 Ordenar por Goles a Favor",
                "🛡️ Ordenar por Goles en Contra",
                "🏅 Ordenar por Victorias"
        };
        cbOrden = new JComboBox<>(opcionesOrden);
        cbOrden.setBackground(bgInput);
        cbOrden.setForeground(Color.WHITE);
        cbOrden.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panelOrden.add(cbOrden);

        // NUEVO: Agrupamos los 3 botones a la derecha
        JPanel panelBotonesExt = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotonesExt.setBackground(bgDark);

        btnLimpiar = crearBoton("Limpiar Tabla", new Color(245, 158, 11)); // Naranja
        btnExportar = crearBoton("Exportar a Excel", new Color(16, 185, 129)); // Verde
        btnReporte = crearBoton("Generar Reporte PDF", new Color(220, 38, 38)); // Rojo

        panelBotonesExt.add(btnLimpiar);
        panelBotonesExt.add(btnExportar);
        panelBotonesExt.add(btnReporte);

        panelBottom.add(panelOrden, BorderLayout.WEST);
        panelBottom.add(panelBotonesExt, BorderLayout.EAST);
        add(panelBottom, BorderLayout.SOUTH);
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setForeground(fgText); lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return lbl;
    }

    private JComboBox<String> crearCombo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setBackground(bgInput); cb.setForeground(Color.WHITE);
        return cb;
    }

    private JSpinner crearSpinner() { return crearSpinnerLimitado(0, 0, 20); }

    private JSpinner crearSpinnerLimitado(int val, int min, int max) {
        JSpinner sp = new JSpinner(new SpinnerNumberModel(val, min, max, 1));
        JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) sp.getEditor();
        editor.getTextField().setBackground(bgInput);
        editor.getTextField().setForeground(fgText);
        sp.setBackground(bgInput);
        return sp;
    }

    private JButton crearBoton(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JTable estilizarTablaInsights(JTable t) {
        t.setRowHeight(35); t.setBackground(bgPanel); t.setForeground(fgText);
        t.getTableHeader().setBackground(bgDark); t.getTableHeader().setForeground(accentBlue);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13)); t.setGridColor(bgDark);
        t.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setBackground(bgPanel); render.setForeground(fgText); render.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < t.getColumnCount(); i++) t.getColumnModel().getColumn(i).setCellRenderer(render);
        return t;
    }

    private JTable estilizarTablaPrincipal(JTable t) {
        t.setRowHeight(30); t.setBackground(bgPanel); t.setForeground(fgText);
        t.getTableHeader().setBackground(bgDark); t.getTableHeader().setForeground(accentBlue);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13)); t.setGridColor(bgDark);
        t.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer render = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, value, isSel, hasFocus, r, c);
                setHorizontalAlignment(JLabel.CENTER);
                if (!isSel) {
                    if (r == 0) comp.setBackground(colorCampeon);
                    else if (r < 8) comp.setBackground(colorTop8);
                    else if (r >= 18) comp.setBackground(colorDescenso);
                    else comp.setBackground(bgPanel);
                }
                return comp;
            }
        };
        for (int i = 0; i < t.getColumnCount(); i++) t.getColumnModel().getColumn(i).setCellRenderer(render);
        return t;
    }
}