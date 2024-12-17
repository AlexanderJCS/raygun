package raytracer.config;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public record RenderConfig(Savepoint[] savepoints, RenderQuality quality, Camera camera) {

    public static RenderConfig fromFile(String filename) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(filename)));
            Gson gson = new Gson();
            return gson.fromJson(jsonContent, RenderConfig.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read config file: " + filename, e);
        }
    }

}
