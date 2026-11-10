package persistence;

import com.google.gson.JsonSyntaxException;
import model.Usuario;

import java.util.ArrayList;

public class UsuarioRepositorio {

    private ArchivoJson archivo;
    private ArrayList<Usuario> usuarios;

    public UsuarioRepositorio() {
        this.archivo = new ArchivoJson("usuarios.json");
        this.usuarios = new ArrayList<Usuario>();
        cargar();
    }


    private void cargar() {
        String texto = archivo.leer();
        if (texto.trim().isEmpty()) {
            return;
        }
        try {
            Usuario[] lista = archivo.getGson().fromJson(texto, Usuario[].class);
            if (lista != null) {
                for (Usuario usuario : lista) {
                    usuarios.add(usuario);
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println("usuarios.json está dañado, se empieza con una lista vacía.");
            archivo.respaldarCorrupto();
        }
    }


    public void guardar() {
        archivo.escribir(archivo.getGson().toJson(usuarios));
    }

    public Usuario buscarPorNombre(String nombre) {
        for (Usuario usuario : usuarios) {
            if (usuario.getNombre().equalsIgnoreCase(nombre)) {
                return usuario;
            }
        }
        return null;
    }

    public boolean existeNombre(String nombre) {
        return buscarPorNombre(nombre) != null;
    }

    public boolean existeCorreo(String correo) {
        for (Usuario usuario : usuarios) {
            if (usuario.getCorreo().equalsIgnoreCase(correo)) {
                return true;
            }
        }
        return false;
    }

    public void agregar(Usuario usuario) {
        usuarios.add(usuario);
        guardar();
    }

    public Usuario iniciarSesion(String nombre, String contrasena) {
        Usuario usuario = buscarPorNombre(nombre);
        if (usuario != null && usuario.verificarContrasena(contrasena)) {
            return usuario;
        }
        return null;
    }
}