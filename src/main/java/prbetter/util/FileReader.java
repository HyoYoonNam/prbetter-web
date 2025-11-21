package prbetter.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReader {
    private FileReader() {
    }

    public static byte[] getBytes(String filePath) {
        try {
            return Files.readString(Path.of("src/main/resources/" + filePath)).getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
