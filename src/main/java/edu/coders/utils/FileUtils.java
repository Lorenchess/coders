package edu.coders.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    private FileUtils() {
    }

    public static <E extends Exception>String loadContentFromFile(String filePath, ExceptionSupplier<E> exceptionSupplier) throws E {
      try {
         return Files.readString(Paths.get(filePath));
      } catch (IOException e) {
          throw exceptionSupplier.get(String.format("File not found with path %s", filePath));
      }
    }

    @FunctionalInterface
    public interface ExceptionSupplier<E extends Exception> {
        E get(String message) throws E;
    }
}
