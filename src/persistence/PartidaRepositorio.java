package persistence;

import com.google.gson.JsonSyntaxException;
import model.Ronda;
import model.Usuario;

import java.util.ArrayList;

public class PartidaRepositorio {

    private ArchivoJson archivo;
    private ArrayList<PartidaGuardada> partidas;

    public PartidaRepositorio() {
        this.archivo = new ArchivoJson("partidas.json");
        this.partidas = new ArrayList<PartidaGuardada>();
        cargar();
    }

    private void cargar() {
        String texto = archivo.leer();
        if (texto.trim().isEmpty()) {
            return;
        }
        try {
            PartidaGuardada[] lista = archivo.getGson().fromJson(texto, PartidaGuardada[].class);
            if (lista != null) {
                for (PartidaGuardada partida : lista) {
                    partidas.add(partida);
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println("partidas.json está dañado, se empieza sin partidas guardadas.");
            archivo.respaldarCorrupto();
        }
    }

    private void escribirArchivo() {
        archivo.escribir(archivo.getGson().toJson(partidas));
    }

    private int buscarPosicion(String nombreUsuario) {
        for (int i = 0; i < partidas.size(); i++) {
            if (partidas.get(i).getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return i;
            }
        }
        return -1;
    }

    public boolean existe(String nombreUsuario) {
        return buscarPosicion(nombreUsuario) != -1;
    }

    public void guardar(Usuario usuario, Ronda ronda) {
        int posicion = buscarPosicion(usuario.getNombre());
        if (posicion != -1) {
            partidas.remove(posicion);
        }
        partidas.add(new PartidaGuardada(usuario.getNombre(), ronda));
        escribirArchivo();
    }

    public Ronda obtener(Usuario usuario) {
        int posicion = buscarPosicion(usuario.getNombre());
        if (posicion == -1) {
            return null;
        }
        Ronda ronda = partidas.get(posicion).getRonda();
        ronda.setUsuario(usuario);
        return ronda;
    }

    public void eliminar(String nombreUsuario) {
        int posicion = buscarPosicion(nombreUsuario);
        if (posicion != -1) {
            partidas.remove(posicion);
            escribirArchivo();
        }
    }

    public void cambiarNombreUsuario(String nombreViejo, String nombreNuevo) {
        int posicion = buscarPosicion(nombreViejo);
        if (posicion != -1) {
            partidas.get(posicion).setNombreUsuario(nombreNuevo);
            escribirArchivo();
        }
    }
}