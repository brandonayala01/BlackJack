package controller;

import model.Usuario;
import persistence.HistorialRepositorio;
import persistence.PartidaRepositorio;
import persistence.UsuarioRepositorio;

// Maneja la ventana "Perfil y ajustes de guardado": avatar y nombre.
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

    // Las flechas del avatar dan la vuelta: después del último viene el primero.
    public int siguienteAvatar(int actual) {
        return (actual + 1) % TOTAL_AVATARES;
    }

    public int anteriorAvatar(int actual) {
        return (actual - 1 + TOTAL_AVATARES) % TOTAL_AVATARES;
    }

    // Botón "Guardar cambios". Devuelve el error o null si todo salió bien.
    public String guardarCambios(Usuario usuario, String nuevoNombre, int nuevoAvatar) {
        nuevoNombre = nuevoNombre.trim();

        String error = ControladorAutenticacion.validarNombre(nuevoNombre);
        if (error != null) {
            return error;
        }

        String nombreViejo = usuario.getNombre();
        if (!nuevoNombre.equals(nombreViejo)) {
            Usuario otro = usuarios.buscarPorNombre(nuevoNombre);
            if (otro != null && otro != usuario) {
                return "Ese nombre de usuario ya está en uso.";
            }
            // El historial y la partida guardada pasan a llevar el nombre nuevo.
            historial.cambiarNombreUsuario(nombreViejo, nuevoNombre);
            partidas.cambiarNombreUsuario(nombreViejo, nuevoNombre);
            usuario.setNombre(nuevoNombre);
        }

        usuario.setAvatar(nuevoAvatar);
        usuarios.guardar();
        return null;
    }
}