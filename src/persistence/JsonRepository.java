package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

public class JsonRepository<T> {

    private final Gson gson;
    private final String filePath;
    private final Type type;

    public JsonRepository(String filePath, Type type) {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.filePath = filePath;
        this.type = type;
    }

    public void writeFile(T data) {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }

    public T readFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        } catch (Exception e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
        return null;
    }
}
