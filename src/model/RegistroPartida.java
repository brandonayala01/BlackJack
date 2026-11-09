package model;

public class RegistroPartida {

    private int id;
    private String nombreUsuario;
    private String fechaHora;      
    private int apuesta;
    private String manosJugador;    
    private int puntajeCrupier;
    private int ganancia;          
    private String resultado;     

    public RegistroPartida(int id, String nombreUsuario, String fechaHora, int apuesta,
                        String manosJugador, int puntajeCrupier, int ganancia, String resultado) {
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.fechaHora = fechaHora;
        this.apuesta = apuesta;
        this.manosJugador = manosJugador;
        this.puntajeCrupier = puntajeCrupier;
        this.ganancia = ganancia;
        this.resultado = resultado;
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

    public String getResultado() {
        return resultado;
    }
}