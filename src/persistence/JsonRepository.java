package persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JsonRepository<T> {

    private String filePath;
    private Gson gson;

    public JsonRepository(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public void save(T object) {

        try (FileWriter writer = new FileWriter(filePath)) {

            gson.toJson(object, writer);

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    public T read(Class<T> type) {

        try (FileReader reader = new FileReader(filePath)) {

            return gson.fromJson(reader, type);

        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }
    }
}