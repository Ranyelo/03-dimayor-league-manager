package controlador;

import modelo.ModeloLiga;
import vista.VistaLiga;

import javax.swing.*;
import java.awt.Desktop;
import java.io.File;
import java.io.PrintWriter;

public class ControladorLiga {

    private ModeloLiga modelo;
    private VistaLiga vista;

    public ControladorLiga(ModeloLiga modelo, VistaLiga vista) {
        this.modelo = modelo;
        this.vista = vista;

        inicializarDatos();

        // Asignación de todos los eventos
        this.vista.btnRegistrar.addActionListener(e -> procesarPartido());
        this.vista.btnAleatorio.addActionListener(e -> generarJornadaAleatoria());
        this.vista.btnReporte.addActionListener(e -> generarReportePDF());
        this.vista.btnExportar.addActionListener(e -> exportarExcelCSV()); // NUEVO
        this.vista.btnLimpiar.addActionListener(e -> limpiarTabla());      // NUEVO

        this.vista.cbOrden.addActionListener(e -> cambiarOrdenamiento());
    }

    private void inicializarDatos() {
        actualizarCombos();
        actualizarTablas();
    }

    private void procesarPartido() {
        int idxLocal = vista.cbLocal.getSelectedIndex();
        int idxVisitante = vista.cbVisitante.getSelectedIndex();

        if (idxLocal == idxVisitante) {
            JOptionPane.showMessageDialog(vista, "Seleccione equipos diferentes.");
            return;
        }

        int gL = (int) vista.spGolesLocal.getValue(); int gV = (int) vista.spGolesVisitante.getValue();
        int taL = (int) vista.spAmarillasL.getValue(); int trL = (int) vista.spRojasL.getValue();
        int taV = (int) vista.spAmarillasV.getValue(); int trV = (int) vista.spRojasV.getValue();

        modelo.registrarPartido(idxLocal, idxVisitante, gL, gV, taL, trL, taV, trV);

        limpiarInputs();
        actualizarTablas();
    }

    private void generarJornadaAleatoria() {
        modelo.simularJornadaCompleta();
        actualizarTablas();
        JOptionPane.showMessageDialog(vista, "¡Jornada " + modelo.getJornadaActual() + " simulada con éxito!");
    }

    private void cambiarOrdenamiento() {
        int criterio = vista.cbOrden.getSelectedIndex();
        modelo.setCriterioOrden(criterio);
        actualizarTablas();
    }

    // NUEVO: Método para borrar todos los datos
    private void limpiarTabla() {
        int respuesta = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de que deseas limpiar toda la tabla? Se perderán todas las jornadas jugadas.",
                "Confirmar limpieza", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (respuesta == JOptionPane.YES_OPTION) {
            modelo.limpiarDatos();
            vista.cbOrden.setSelectedIndex(0); // Vuelve al filtro de Puntos
            limpiarInputs();
            actualizarCombos();
            actualizarTablas();
            JOptionPane.showMessageDialog(vista, "Tabla reiniciada correctamente.");
        }
    }

    private void actualizarCombos() {
        String[] equipos = modelo.getVectorEquipos();
        vista.cbLocal.setModel(new DefaultComboBoxModel<>(equipos));
        vista.cbVisitante.setModel(new DefaultComboBoxModel<>(equipos));
        vista.cbVisitante.setSelectedIndex(1);
    }

    private void actualizarTablas() {
        vista.modeloTabla.setRowCount(0);
        String[] eq = modelo.getVectorEquipos();
        int[][] st = modelo.getMatrizStats();

        for (int i = 0; i < 20; i++) {
            Object[] fila = { (i + 1), eq[i], st[i][5], st[i][0], st[i][1], st[i][2], st[i][3], st[i][4], st[i][6], st[i][7], st[i][8], st[i][9] };
            vista.modeloTabla.addRow(fila);
        }

        vista.modeloInsights.setRowCount(0);
        calcularLlenarInsights(eq, st);
    }

    private void calcularLlenarInsights(String[] equipos, int[][] stats) {
        int maxGoles = -1, minGC = 999, maxTA = -1, maxTR = -1;
        String eqGol = "", eqDef = "", eqTA = "", eqTR = "";

        for (int i = 0; i < 20; i++) {
            if(stats[i][5] == 0) continue;
            if (stats[i][3] > maxGoles) { maxGoles = stats[i][3]; eqGol = equipos[i]; }
            if (stats[i][4] < minGC) { minGC = stats[i][4]; eqDef = equipos[i]; }
            if (stats[i][8] > maxTA) { maxTA = stats[i][8]; eqTA = equipos[i]; }
            if (stats[i][9] > maxTR) { maxTR = stats[i][9]; eqTR = equipos[i]; }
        }

        if (stats[0][5] > 0 && vista.cbOrden.getSelectedIndex() == 0) {
            vista.modeloInsights.addRow(new Object[]{"👑 Líder / Campeón", equipos[0] + " (" + stats[0][7] + " PTS)"});
            vista.modeloInsights.addRow(new Object[]{"🔥 Más Goleador", eqGol + " (" + maxGoles + " GF)"});
            vista.modeloInsights.addRow(new Object[]{"🛡️ Mejor Defensa", eqDef + " (" + minGC + " GC)"});
            vista.modeloInsights.addRow(new Object[]{"🟨 Juego Fuerte (TA)", eqTA + " (" + maxTA + " TA)"});
            vista.modeloInsights.addRow(new Object[]{"🟥 Más Expulsiones", eqTR + " (" + maxTR + " TR)"});
            vista.modeloInsights.addRow(new Object[]{"⬇️ Zona Descenso", equipos[18] + " y " + equipos[19]});
        } else if (stats[0][5] > 0) {
            vista.modeloInsights.addRow(new Object[]{"ℹ️ Filtro Activo", "Vuelve a ordenar por Puntos"});
            vista.modeloInsights.addRow(new Object[]{"", "para ver las distinciones."});
        }
    }

    private void limpiarInputs() {
        vista.spGolesLocal.setValue(0); vista.spGolesVisitante.setValue(0);
        vista.spAmarillasL.setValue(0); vista.spAmarillasV.setValue(0);
        vista.spRojasL.setValue(0); vista.spRojasV.setValue(0);
        vista.spTiempo.setValue(90); vista.spAdicion.setValue(0);
    }

    // NUEVO: Método recuperado para exportar a Excel (CSV)
    private void exportarExcelCSV() {
        try {
            File f = new File("Tabla_Liga_BetPlay.csv");
            PrintWriter pw = new PrintWriter(f);
            pw.println("POS,EQUIPO,PJ,PG,PE,PP,GF,GC,DG,PTS,TA,TR");
            String[] eq = modelo.getVectorEquipos();
            int[][] st = modelo.getMatrizStats();
            for (int i = 0; i < 20; i++) {
                pw.printf("%d,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d\n",
                        (i+1), eq[i], st[i][5], st[i][0], st[i][1], st[i][2], st[i][3], st[i][4], st[i][6], st[i][7], st[i][8], st[i][9]);
            }
            pw.close();
            JOptionPane.showMessageDialog(vista, "Datos exportados correctamente a Excel (Archivo .csv)");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al exportar a Excel.");
        }
    }

    private void generarReportePDF() {
        try {
            File file = new File("Reporte_Dimayor.html");
            PrintWriter pw = new PrintWriter(file);
            String[] eq = modelo.getVectorEquipos();
            int[][] st = modelo.getMatrizStats();

            pw.println("<html><head><meta charset='UTF-8'><title>Reporte Dimayor</title>");
            pw.println("<style>body{font-family: Arial, sans-serif; padding: 20px;} table{width: 100%; border-collapse: collapse; margin-top: 20px;} th, td{border: 1px solid #ddd; padding: 8px; text-align: center;} th{background-color: #0f172a; color: white;} .campeon{background-color: #fef08a;} .clasificado{background-color: #bbf7d0;} .descenso{background-color: #fecaca;}</style></head>");
            pw.println("<body onload='window.print()'>");
            pw.println("<h2>⚽ Reporte Oficial Dimayor - Jornada " + modelo.getJornadaActual() + "</h2>");
            pw.println("<table><tr><th>POS</th><th>EQUIPO</th><th>PJ</th><th>PG</th><th>PE</th><th>PP</th><th>GF</th><th>GC</th><th>DG</th><th>PTS</th><th>TA</th><th>TR</th></tr>");

            for (int i = 0; i < 20; i++) {
                String clase = (i == 0) ? "campeon" : (i < 8) ? "clasificado" : (i >= 18) ? "descenso" : "";
                pw.printf("<tr class='%s'><td>%d</td><td>%s</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td><b>%d</b></td><td>%d</td><td>%d</td></tr>",
                        clase, (i+1), eq[i], st[i][5], st[i][0], st[i][1], st[i][2], st[i][3], st[i][4], st[i][6], st[i][7], st[i][8], st[i][9]);
            }
            pw.println("</table><p><i>Reporte generado automáticamente por Dimayor Pro.</i></p>");
            pw.println("</body></html>");
            pw.close();

            Desktop.getDesktop().browse(file.toURI());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al generar el reporte.");
        }
    }
}