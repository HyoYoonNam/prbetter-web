package prbetter.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static String readJsonFile(String filePath) {
        try {
            return Files.readString(Path.of("src/test/resources/" + filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
