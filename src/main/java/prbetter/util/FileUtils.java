package prbetter.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    private FileUtils() {
    }

    public static byte[] getBytes(String filePath) {
        try (InputStream inputStream = getInputStream(filePath)) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readString(String filePath) {
        return new String(getBytes(filePath), StandardCharsets.UTF_8);
    }

    public static String readLocalFileToString(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean exists(String filePath) {
        return Files.exists(Path.of(filePath));
    }

    private static InputStream getInputStream(String filePath) {
        InputStream resourceAsStream =
                ClassLoader.getSystemClassLoader().getResourceAsStream(filePath);
        if (resourceAsStream == null) {
            throw new IllegalArgumentException(filePath + " is not found");
        }
        return resourceAsStream;
    }
}
