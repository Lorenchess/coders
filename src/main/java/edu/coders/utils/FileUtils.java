package edu.coders.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import edu.coders.exceptions.FileProcessingException;
import edu.coders.exceptions.LessonFileNotFoundException;
import edu.coders.exceptions.TitleExtractionException;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

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

    /**
     * Extracts the title from a file (supports JSON and Markdown formats).
     * For JSON: Extracts "title" field.
     * For Markdown: Extracts the first line starting with "#".
     *
     * @param filePath The file Path object.
     * @return The extracted title (text after ':').
     */
    public static String extractTitleFromFile(Path filePath) {

        try {
            String fileName = filePath.getFileName().toString();

            if (fileName.endsWith(".json")) {
                return extractTitleFromJson(filePath);
            } else if (fileName.endsWith(".md")) {
                return extractTitleFromMarkdown(filePath);
            } else {
                throw new TitleExtractionException("File extension not supported " + fileName);
            }
        } catch (IOException e) {
            throw new FileProcessingException("Error reading file: " + filePath, e);
        }
    }

    /**
     * Extract tile from .md markdown file.
     *
     * @param filePath Path to the markdown file
     * @return Title extracted from markdown file
     * @throws IOException if the operation fails
     */
    private static String extractTitleFromMarkdown(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.filter(line -> line.startsWith("#"))
                    .findFirst()
                    .map(line -> splitTitleAfterColon(line.replace("#", "").trim()))
                    .orElseThrow(() -> new TitleExtractionException("No title found in Markdown file: " + filePath));
        }
    }

    /**
     * Extracts title from a JSON file.
     *
     * @param filePath Path to the JSON file.
     * @return Title extracted from the JSON file.
     */
    private static String extractTitleFromJson(Path filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(Files.newInputStream(filePath));
        String title = rootNode.get("title").asText();
        return splitTitleAfterColon(title);
    }

    /**
     * Splits a title and returns text after ':' if present.
     *
     * @param title The raw title.
     * @return Cleaned title after ':' or the original title.
     */
    private static String splitTitleAfterColon(String title) {
        if (title.contains(":")) {
            return title.split(":", 2)[1].trim();
        }
        return title;
    }

    
    /**
     * Reads content from the given InputStream and returns it as a String.
     * 
     * @param inputStream The InputStream to read data from.
     * @return A String containing the content of the InputStream.
     * @throws FileProcessingException If an I/O error occurs while reading from the InputStream.
     */
    public static String readContentFromInputStream(InputStream inputStream) {
        try {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileProcessingException("Error reading file from InputStream", e);
        }
    }

    
    /**
     * Extracts the title from the given content string.
     * 
     * This method supports two types of content formats:
     * 1. Markdown-like: If the content starts with "#", the first line will be 
     *    considered as the title, with the "#" character removed.
     * 2. JSON: If the content is assumed to be in JSON format, it retrieves the value 
     *    of the "title" field using a JSONPath query.
     *
     * @param content The input content as a string.
     * @return The extracted title based on the content format.
     * @throws com.jayway.jsonpath.PathNotFoundException If the "title" field is missing in JSON content.
     */
    public static String extractTitleFromContent(String content) {
        if (content.startsWith("#")) { 
            return content.split("\n")[0].replace("#", "").trim();
        } else { // JSON file
            return JsonPath.read(content, "$.title"); 
        }
    }
}
