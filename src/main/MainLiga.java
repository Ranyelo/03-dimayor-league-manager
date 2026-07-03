package main;


import modelo.ModeloLiga;
import vista.VistaLiga;
import controlador.ControladorLiga;

import javax.swing.SwingUtilities;

/* ============================================================================
   SECCIÓN: EJECUCIÓN DEL PROGRAMA
   ============================================================================ */
public class MainLiga {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            // 1. Instanciar el Modelo (Los Datos)
            ModeloLiga modelo = new ModeloLiga();

            // 2. Instanciar la Vista (La Interfaz Gráfica)
            VistaLiga vista = new VistaLiga();

            // 3. Instanciar el Controlador pasándole el Modelo y la Vista
            ControladorLiga controlador = new ControladorLiga(modelo, vista);

            // 4. Mostrar la aplicación en pantalla
            vista.setVisible(true);
        });
    }
}