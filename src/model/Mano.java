package model;

import java.util.ArrayList;

public class Mano {

    private ArrayList<Carta> cartas;
    private int apuesta;
    private boolean doblada;
    private boolean vieneDeDivision;

    public Mano(int apuesta) {
        this.cartas = new ArrayList<Carta>();
        this.apuesta = apuesta;
        this.doblada = false;
        this.vieneDeDivision = false;
    }

    public Mano() {
        this(0);
    }

    public void agregarCarta(Carta carta) {
        cartas.add(carta);
    }

    public int getPuntaje() {
        int total = 0;
        int ases = 0;
        for (Carta carta : cartas) {
            total += carta.getPuntos();
            if (carta.esAs()) {
                ases++;
            }
        }
        while (total > 21 && ases > 0) {
            total -= 10;
            ases--;
        }
        return total;
    }

    public boolean esBlackjack() {
        return cartas.size() == 2 && getPuntaje() == 21 && !vieneDeDivision;
    }

    public boolean sePaso() {
        return getPuntaje() > 21;
    }

    public boolean puedeDividir() {
        return cartas.size() == 2
                && cartas.get(0).getPuntos() == cartas.get(1).getPuntos();
    }

    public boolean puedeDoblar() {
        return cartas.size() == 2 && !doblada;
    }

    public void doblar() {
        apuesta = apuesta * 2;
        doblada = true;
    }
    
    public Carta quitarSegundaCarta() {
        return cartas.remove(1);
    }

    public ArrayList<Carta> getCartas() {
        return cartas;
    }

    public int getCantidadCartas() {
        return cartas.size();
    }

    public int getApuesta() {
        return apuesta;
    }

    public boolean isDoblada() {
        return doblada;
    }

    public boolean isVieneDeDivision() {
        return vieneDeDivision;
    }

    public void setVieneDeDivision(boolean vieneDeDivision) {
        this.vieneDeDivision = vieneDeDivision;
    }
}