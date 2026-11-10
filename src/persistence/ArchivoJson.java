package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class ArchivoJson {

    public static final String CARPETA = "datos";

    private Gson gson;
    private Path ruta;

    public ArchivoJson(String nombreArchivo) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.ruta = Paths.get(CARPETA, nombreArchivo);
    }

    public Gson getGson() {
        return gson;
    }

    public boolean existe() {
        return Files.exists(ruta);
    }

    public String leer() {
        if (!existe()) {
            return "";
        }
        try {
            byte[] bytes = Files.readAllBytes(ruta);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Error al leer " + ruta + ": " + e.getMessage());
            return "";
        }
    }

    public void escribir(String texto) {
        try {
            Files.createDirectories(ruta.getParent());
            Path temporal = Paths.get(ruta.toString() + ".tmp");
            Files.write(temporal, texto.getBytes(StandardCharsets.UTF_8));
            Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error al escribir " + ruta + ": " + e.getMessage());
        }
    }

    public void respaldarCorrupto() {
        try {
            Path respaldo = Paths.get(ruta.toString() + ".corrupto");
            Files.move(ruta, respaldo, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("No se pudo respaldar " + ruta + ": " + e.getMessage());
        }
    }
}