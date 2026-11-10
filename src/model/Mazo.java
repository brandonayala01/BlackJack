package model;

import java.util.ArrayList;
import java.util.Collections;

public class Mazo {

    private static final String[] PALOS = {"Corazones", "Diamantes", "Treboles", "Picas"};
    private static final String[] VALORES = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    private ArrayList<Carta> cartas;
    private int cantidadBarajas;

    public Mazo(int cantidadBarajas) {
        this.cantidadBarajas = cantidadBarajas;
        this.cartas = new ArrayList<Carta>();
        reconstruir();
    }

    public Mazo() {
        this(6);
    }

    private void reconstruir() {
        cartas.clear();
        for (int b = 0; b < cantidadBarajas; b++) {
            for (int p = 0; p < PALOS.length; p++) {
                for (int v = 0; v < VALORES.length; v++) {
                    cartas.add(new Carta(VALORES[v], PALOS[p]));
                }
            }
        }
        Collections.shuffle(cartas);
    }

    public Carta repartir() {
        if (cartas.isEmpty()) {
            reconstruir();
        }
        return cartas.remove(cartas.size() - 1);
    }

    public int cartasRestantes() {
        return cartas.size();
    }

    public boolean necesitaBarajar() {
        return cartas.size() < 52;
    }

    public void barajar() {
        reconstruir();
    }
}