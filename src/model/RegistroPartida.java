package model;

import java.util.ArrayList;

public class RegistroPartida {

    private int id;
    private String nombreUsuario;
    private String fechaHora;      
    private int apuesta;
    private String manosJugador;    
    private int puntajeCrupier;
    private int ganancia;          
    private String resultado;     
    private ArrayList<ArrayList<Carta>> cartasJugador;
    private ArrayList<Carta> cartasCrupier;

    public RegistroPartida(int id, String nombreUsuario, String fechaHora, int apuesta,
                        String manosJugador, int puntajeCrupier, int ganancia, String resultado,
                        ArrayList<ArrayList<Carta>> cartasJugador, ArrayList<Carta> cartasCrupier) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.fechaHora = fechaHora;
        this.apuesta = apuesta;
        this.manosJugador = manosJugador;
        this.puntajeCrupier = puntajeCrupier;
        this.ganancia = ganancia;
        this.resultado = resultado;
        this.cartasJugador = cartasJugador;
        this.cartasCrupier = cartasCrupier;
    }

    public boolean tieneCartas() {
        return cartasJugador != null && cartasCrupier != null;
    }

    public ArrayList<ArrayList<Carta>> getCartasJugador() {
        return cartasJugador;
    }

    public ArrayList<Carta> getCartasCrupier() {
        return cartasCrupier;
    }

    public String getManosJugador() {
        return manosJugador;
    }

    public int getPuntajeCrupier() {
        return puntajeCrupier;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getIdTexto() {
        return String.format("%05d", id);
    }


    public String getTextoMano() {
        return "Hand " + manosJugador + "/Dealer " + puntajeCrupier;
    }

    public String getTextoResultado() {
        String signo = "";
        if (ganancia > 0) {
            signo = "+";
        }
        return signo + ganancia + " [" + resultado + "]";
    }

    public int getId() {
        return id;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public int getApuesta() {
        return apuesta;
    }

    public int getGanancia() {
        return ganancia;
    }
}