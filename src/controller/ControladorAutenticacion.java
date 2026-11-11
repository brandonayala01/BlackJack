package controller;

import model.Usuario;
import persistence.UsuarioRepositorio;

public class ControladorAutenticacion {

    private UsuarioRepositorio usuarios;
    private Usuario usuarioActual;

    public ControladorAutenticacion(UsuarioRepositorio usuarios) {
        this.usuarios = usuarios;
        this.usuarioActual = null;
    }

    public static String validarNombre(String nombre) {
        if (nombre.isEmpty()) {
            return "Escribe un nombre de usuario.";
        }
        if (nombre.length() < 3 || nombre.length() > 15) {
            return "El usuario debe tener entre 3 y 15 caracteres.";
        }
        if (!nombre.matches("[A-Za-z0-9_]+")) {
            return "El usuario solo puede tener letras, números y guion bajo.";
        }
        return null;
    }

    public String iniciarSesion(String nombre, String contrasena) {
        nombre = nombre.trim();
        if (nombre.isEmpty() || contrasena.isEmpty()) {
            return "Escribe tu usuario y tu contraseña.";
        }

        Usuario usuario = new Usuario(nombre, "demo@demo.com", contrasena);
        usuario.agregarSaldo(1000);
        
        usuarioActual = usuario;
        return null;
    }

    public String registrar(String nombre, String correo, String contrasena, String confirmacion) {
        nombre = nombre.trim();
        correo = correo.trim();

        String error = validarNombre(nombre);
        if (error != null) return error;

        Usuario nuevo = new Usuario(nombre, correo, contrasena);
        nuevo.agregarSaldo(1000); 
        
        usuarioActual = nuevo;
        return null;
    }

    public boolean haySesion() {
        return usuarioActual != null;
    }

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }
}