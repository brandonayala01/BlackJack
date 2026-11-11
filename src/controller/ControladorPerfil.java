package controller;

import model.Usuario;
import persistence.HistorialRepositorio;
import persistence.PartidaRepositorio;
import persistence.UsuarioRepositorio;

public class ControladorPerfil {

    public static final int TOTAL_AVATARES = 6;

    private UsuarioRepositorio usuarios;
    private HistorialRepositorio historial;
    private PartidaRepositorio partidas;

    public ControladorPerfil(UsuarioRepositorio usuarios, HistorialRepositorio historial,
                            PartidaRepositorio partidas) {
        this.usuarios = usuarios;
        this.historial = historial;
        this.partidas = partidas;
    }

    public int siguienteAvatar(int actual) {
        return (actual + 1) % TOTAL_AVATARES;
    }

    public int anteriorAvatar(int actual) {
        return (actual - 1 + TOTAL_AVATARES) % TOTAL_AVATARES;
    }

    public String guardarCambios(Usuario usuario, String nuevoNombre, int nuevoAvatar) {
        nuevoNombre = nuevoNombre.trim();

        String error = ControladorAutenticacion.validarNombre(nuevoNombre);
        if (error != null) {
            return error;
        }

        String nombreViejo = usuario.getNombre();
        if (!nuevoNombre.equals(nombreViejo)) {
            // --- BYPASS PERSISTENCIA PARA EXPOSICIÓN ---
            /*
            Usuario otro = usuarios.buscarPorNombre(nuevoNombre);
            if (otro != null && otro != usuario) { return "Ese nombre de usuario ya está en uso."; }
            historial.cambiarNombreUsuario(nombreViejo, nuevoNombre);
            partidas.cambiarNombreUsuario(nombreViejo, nuevoNombre);
            */
            usuario.setNombre(nuevoNombre);
        }

        usuario.setAvatar(nuevoAvatar);
        // usuarios.guardar(); // BYPASS PERSISTENCIA
        return null;
    }
}