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
        // --- BYPASS PERSISTENCIA PARA EXPOSICIÓN ---
        // return historial.getDeUsuario(usuario.getNombre());
        return new ArrayList<>(); // Retorna lista vacía para evitar errores
    }

    public Estadisticas getEstadisticas(Usuario usuario) {
        return usuario.getEstadisticas();
    }
}