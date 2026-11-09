package controller;

import model.Estadisticas;
import model.RegistroPartida;
import model.Usuario;
import persistence.HistorialRepositorio;

import java.util.ArrayList;

public class ControladorHistorial {

    private HistorialRepositorio historial;

    public ControladorHistorial(HistorialRepositorio historial) {
        this.historial = historial;
    }

    public ArrayList<RegistroPartida> getRegistros(Usuario usuario) {
        return historial.getDeUsuario(usuario.getNombre());
    }

    public Estadisticas getEstadisticas(Usuario usuario) {
        return usuario.getEstadisticas();
    }
}