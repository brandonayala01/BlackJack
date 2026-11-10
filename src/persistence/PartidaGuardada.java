package persistence;

import model.Ronda;

// Une una ronda en curso con el nombre del usuario al que pertenece.
// Es lo que se guarda en partidas.json para poder reanudar la partida.
public class PartidaGuardada {

    private String nombreUsuario;
    private Ronda ronda;

    public PartidaGuardada(String nombreUsuario, Ronda ronda) {
        this.nombreUsuario = nombreUsuario;
        this.ronda = ronda;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Ronda getRonda() {
        return ronda;
    }
}