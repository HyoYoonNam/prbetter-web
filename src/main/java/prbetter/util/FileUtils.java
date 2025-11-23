package prbetter.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    private FileUtils() {
    }

    public static byte[] getBytes(String filePath) {
        try {
            return Files.readString(Path.of(filePath)).getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readString(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean exists(String filePath) {
        return Files.exists(Path.of(filePath));
    }
}
