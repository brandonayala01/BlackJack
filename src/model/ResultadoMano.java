package model;

public class ResultadoMano {

    public static final String GANA = "GANA";
    public static final String PIERDE = "PIERDE";
    public static final String EMPATE = "EMPATE";
    public static final String BLACKJACK = "BLACKJACK";

    private String tipo;
    private int apuesta;
    private int ganancia;  
    private int pago;        
    private int puntajeJugador;
    private int puntajeCrupier;

    public ResultadoMano(Mano manoJugador, Mano manoCrupier) {
        this.apuesta = manoJugador.getApuesta();
        this.puntajeJugador = manoJugador.getPuntaje();
        this.puntajeCrupier = manoCrupier.getPuntaje();

        boolean jugadorBlackjack = manoJugador.esBlackjack();
        boolean crupierBlackjack = manoCrupier.esBlackjack();

        if (manoJugador.sePaso()) {
            tipo = PIERDE;
        } else if (jugadorBlackjack && crupierBlackjack) {
            tipo = EMPATE;
        } else if (crupierBlackjack) {
            tipo = PIERDE;
        } else if (jugadorBlackjack) {
            tipo = BLACKJACK;
        } else if (manoCrupier.sePaso()) {
            tipo = GANA;
        } else if (puntajeJugador > puntajeCrupier) {
            tipo = GANA;
        } else if (puntajeJugador < puntajeCrupier) {
            tipo = PIERDE;
        } else {
            tipo = EMPATE;
        }

        calcularPago();
    }


    private void calcularPago() {
        if (tipo.equals(GANA)) {
            ganancia = apuesta;
            pago = apuesta * 2;
        } else if (tipo.equals(BLACKJACK)) {
            ganancia = apuesta * 3 / 2;
            pago = apuesta + ganancia;
        } else if (tipo.equals(EMPATE)) {
            ganancia = 0;
            pago = apuesta;
        } else {
            ganancia = -apuesta;
            pago = 0;
        }
    }

    public String getTipo() {
        return tipo;
    }

    public int getApuesta() {
        return apuesta;
    }

    public int getGanancia() {
        return ganancia;
    }

    public int getPago() {
        return pago;
    }

    public int getPuntajeJugador() {
        return puntajeJugador;
    }

    public int getPuntajeCrupier() {
        return puntajeCrupier;
    }
}