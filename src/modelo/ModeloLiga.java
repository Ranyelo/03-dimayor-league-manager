package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ModeloLiga {
    // Array original constante para poder restaurarlo al limpiar
    private final String[] EQUIPOS_BASE = {
            "Millonarios", "Santa Fe", "Atl. Nacional", "America de Cali", "Deportivo Cali",
            "Junior FC", "Ind. Medellin", "Tolima", "Bucaramanga", "Once Caldas",
            "Pereira", "Pasto", "Equidad", "Aguilas Doradas", "Jaguares",
            "Boyaca Chico", "Envigado", "Alianza FC", "Fortaleza", "Patriotas"
    };

    private String[] vectorEquipos = new String[20];

    // Matriz 20x10: [0]PG, [1]PE, [2]PP, [3]GF, [4]GC, [5]PJ, [6]DG, [7]PTS, [8]TA, [9]TR
    private int[][] matrizStats = new int[20][10];
    private int jornadaActual = 0;
    private int criterioOrden = 0; // 0: PTS, 1: GF, 2: GC, 3: PG

    public ModeloLiga() {
        limpiarDatos(); // Inicializa los datos al arrancar
    }

    public String[] getVectorEquipos() { return vectorEquipos; }
    public int[][] getMatrizStats() { return matrizStats; }
    public int getJornadaActual() { return jornadaActual; }

    // NUEVO: Método para borrar todo y reiniciar el simulador
    public void limpiarDatos() {
        matrizStats = new int[20][10]; // Reinicia toda la matriz a ceros
        jornadaActual = 0;
        criterioOrden = 0;
        System.arraycopy(EQUIPOS_BASE, 0, vectorEquipos, 0, 20); // Restaura los nombres originales
    }

    public void setCriterioOrden(int criterio) {
        this.criterioOrden = criterio;
        ordenarMatrices();
    }

    public void registrarPartido(int idxLocal, int idxVisitante, int golesLocal, int golesVisitante, int taL, int trL, int taV, int trV) {
        matrizStats[idxLocal][5]++;
        matrizStats[idxVisitante][5]++;
        matrizStats[idxLocal][3] += golesLocal;
        matrizStats[idxLocal][4] += golesVisitante;
        matrizStats[idxVisitante][3] += golesVisitante;
        matrizStats[idxVisitante][4] += golesLocal;
        matrizStats[idxLocal][8] += taL;
        matrizStats[idxLocal][9] += trL;
        matrizStats[idxVisitante][8] += taV;
        matrizStats[idxVisitante][9] += trV;

        if (golesLocal > golesVisitante) {
            matrizStats[idxLocal][0]++;
            matrizStats[idxVisitante][2]++;
        } else if (golesLocal < golesVisitante) {
            matrizStats[idxLocal][2]++;
            matrizStats[idxVisitante][0]++;
        } else {
            matrizStats[idxLocal][1]++;
            matrizStats[idxVisitante][1]++;
        }

        matrizStats[idxLocal][6] = matrizStats[idxLocal][3] - matrizStats[idxLocal][4];
        matrizStats[idxLocal][7] = (matrizStats[idxLocal][0] * 3) + (matrizStats[idxLocal][1] * 1);
        matrizStats[idxVisitante][6] = matrizStats[idxVisitante][3] - matrizStats[idxVisitante][4];
        matrizStats[idxVisitante][7] = (matrizStats[idxVisitante][0] * 3) + (matrizStats[idxVisitante][1] * 1);

        ordenarMatrices();
    }

    public void simularJornadaCompleta() {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < 20; i++) indices.add(i);
        Collections.shuffle(indices);

        Random rnd = new Random();
        for (int i = 0; i < 20; i += 2) {
            int local = indices.get(i);
            int visita = indices.get(i + 1);

            int gl = rnd.nextInt(4); int gv = rnd.nextInt(4);
            int taL = rnd.nextInt(4); int trL = rnd.nextInt(10) == 0 ? 1 : 0;
            int taV = rnd.nextInt(4); int trV = rnd.nextInt(10) == 0 ? 1 : 0;

            registrarPartido(local, visita, gl, gv, taL, trL, taV, trV);
        }
        jornadaActual++;
    }

    private void ordenarMatrices() {
        for (int i = 0; i < 20 - 1; i++) {
            for (int j = 0; j < 20 - 1 - i; j++) {
                boolean intercambiar = false;

                switch (criterioOrden) {
                    case 0: // PUNTOS (Mayor a Menor) y desempate por DG
                        if (matrizStats[j][7] < matrizStats[j + 1][7]) intercambiar = true;
                        else if (matrizStats[j][7] == matrizStats[j + 1][7] && matrizStats[j][6] < matrizStats[j + 1][6]) intercambiar = true;
                        break;
                    case 1: // GOLES A FAVOR
                        if (matrizStats[j][3] < matrizStats[j + 1][3]) intercambiar = true;
                        break;
                    case 2: // GOLES EN CONTRA
                        if (matrizStats[j][4] > matrizStats[j + 1][4]) intercambiar = true;
                        break;
                    case 3: // VICTORIAS
                        if (matrizStats[j][0] < matrizStats[j + 1][0]) intercambiar = true;
                        break;
                }

                if (intercambiar) {
                    for (int k = 0; k < 10; k++) {
                        int temp = matrizStats[j][k];
                        matrizStats[j][k] = matrizStats[j + 1][k];
                        matrizStats[j + 1][k] = temp;
                    }
                    String tempNom = vectorEquipos[j];
                    vectorEquipos[j] = vectorEquipos[j + 1];
                    vectorEquipos[j + 1] = tempNom;
                }
            }
        }
    }
}