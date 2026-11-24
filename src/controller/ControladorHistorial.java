package controller;

import model.Estadisticas;
import model.RegistroPartida;
import model.Usuario;
import persistence.HistorialRepositorio;

import java.util.ArrayList;

// Entrega a la ventana de Historial los datos que necesita.
public class ControladorHistorial {

    private HistorialRepositorio historial;

    public ControladorHistorial(HistorialRepositorio historial) {
        this.historial = historial;
    }

    // Filas de la tabla, de la ronda más reciente a la más antigua.
    public ArrayList<RegistroPartida> getRegistros(Usuario usuario) {
        return historial.getDeUsuario(usuario.getNombre());
    }

    // Datos de las tarjetas: % de victorias y racha actual.
    // El saldo actual se lee directamente del usuario.
    public Estadisticas getEstadisticas(Usuario usuario) {
        return usuario.getEstadisticas();
    }
}