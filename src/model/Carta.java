package model;

public class Carta {

    private String valor;
    private String palo;

    public Carta(String valor, String palo) {
        this.valor = valor;
        this.palo = palo;
    }

    public String getValor() {
        return valor;
    }

    public String getPalo() {
        return palo;
    }

    public int getPuntos() {
        if (valor.equals("A")) {
            return 11;
        }
        if (valor.equals("J") || valor.equals("Q") || valor.equals("K")) {
            return 10;
        }
        return Integer.parseInt(valor);
    }

    public boolean esAs() {
        return valor.equals("A");
    }
    public boolean esRoja() {
        return palo.equals("Corazones") || palo.equals("Diamantes");
    }

    public String getSimboloPalo() {
        if (palo.equals("Corazones")) {
            return "\u2665";
        }
        if (palo.equals("Diamantes")) {
            return "\u2666";
        }
        if (palo.equals("Treboles")) {
            return "\u2663";
        }
        return "\u2660";
    }

    @Override
    public String toString() {
        return valor + " de " + palo;
    }
}