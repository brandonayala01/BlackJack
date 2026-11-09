package persistence;

import com.google.gson.JsonSyntaxException;
import model.RegistroPartida;

import java.util.ArrayList;

public class HistorialRepositorio {

    private ArchivoJson archivo;
    private ArrayList<RegistroPartida> registros;

    public HistorialRepositorio() {
        this.archivo = new ArchivoJson("historial.json");
        this.registros = new ArrayList<RegistroPartida>();
        cargar();
    }

    private void cargar() {
        String texto = archivo.leer();
        if (texto.trim().isEmpty()) {
            return;
        }
        try {
            RegistroPartida[] lista = archivo.getGson().fromJson(texto, RegistroPartida[].class);
            if (lista != null) {
                for (RegistroPartida registro : lista) {
                    registros.add(registro);
                }
            }
        } catch (JsonSyntaxException e) {
            System.out.println("historial.json está dañado, se empieza con un historial vacío.");
            archivo.respaldarCorrupto();
        }
    }

    private void guardar() {
        archivo.escribir(archivo.getGson().toJson(registros));
    }

    public int siguienteId() {
        int mayor = 0;
        for (RegistroPartida registro : registros) {
            if (registro.getId() > mayor) {
                mayor = registro.getId();
            }
        }
        return mayor + 1;
    }

    public void agregar(RegistroPartida registro) {
        registros.add(registro);
        guardar();
    }

    public ArrayList<RegistroPartida> getDeUsuario(String nombreUsuario) {
        ArrayList<RegistroPartida> lista = new ArrayList<RegistroPartida>();
        for (int i = registros.size() - 1; i >= 0; i--) {
            RegistroPartida registro = registros.get(i);
            if (registro.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                lista.add(registro);
            }
        }
        return lista;
    }

    public void cambiarNombreUsuario(String nombreViejo, String nombreNuevo) {
        for (RegistroPartida registro : registros) {
            if (registro.getNombreUsuario().equalsIgnoreCase(nombreViejo)) {
                registro.setNombreUsuario(nombreNuevo);
            }
        }
        guardar();
    }
}