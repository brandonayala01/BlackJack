package model;

public class Estadisticas {

    private int rondasJugadas;
    private int victorias;
    private int derrotas;
    private int empates;
    private int rachaActual;  
    private int mejorRacha;
    private int gananciaTotal;

    public void registrarRonda(int gananciaNeta) {
        rondasJugadas++;
        gananciaTotal += gananciaNeta;

        if (gananciaNeta > 0) {
            victorias++;
            rachaActual++;
            if (rachaActual > mejorRacha) {
                mejorRacha = rachaActual;
            }
        } else if (gananciaNeta < 0) {
            derrotas++;
            rachaActual = 0;
        } else {
            empates++; 
        }
    }

    public int getPorcentajeVictorias() {
        if (rondasJugadas == 0) {
            return 0;
        }
        return victorias * 100 / rondasJugadas;
    }

    public int getRondasJugadas() {
        return rondasJugadas;
    }

    public int getVictorias() {
        return victorias;
    }

    public int getDerrotas() {
        return derrotas;
    }

    public int getEmpates() {
        return empates;
    }

    public int getRachaActual() {
        return rachaActual;
    }

    public int getMejorRacha() {
        return mejorRacha;
    }

    public int getGananciaTotal() {
        return gananciaTotal;
    }
}