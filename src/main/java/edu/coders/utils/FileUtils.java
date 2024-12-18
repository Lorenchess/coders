package edu.coders.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import edu.coders.dtos.QuizDTO;
import edu.coders.exceptions.FileProcessingException;
import edu.coders.exceptions.TitleExtractionException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class FileUtils {

    private FileUtils() {
    }


    public static <E extends Exception>String loadContentFromFile(String filePath, ExceptionSupplier<E> exceptionSupplier) throws E {
        try {
           if (filePath.startsWith("classpath:")) {

               Resource resource = new ClassPathResource(filePath.replace("classpath:", ""));
               if (!resource.exists()) {
                   throw exceptionSupplier.get(String.format("File not found in classpath: %s", filePath));
               }
               try (InputStream inputStream = resource.getInputStream()) {
                   return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
               }
           } else {
               Path path = Paths.get(filePath);
               if (!Files.exists(path)) {
                   throw exceptionSupplier.get(String.format("File not found with path %s", filePath));
               }
               return Files.readString(path);
           }
        } catch(IOException e) {
            throw exceptionSupplier.get(String.format("Error reading file from path: %s", filePath));
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
            System.out.println("File name: " + fileName);
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
        System.out.println("Passing for extractTitleFromMarkdown with filePath: " + filePath);
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines.filter(line -> line.startsWith("#"))
                    .findFirst()
                    .map(line -> splitTitleAfterColon(line.replace("#", "").trim()))
                    .orElseThrow(() -> new TitleExtractionException("No title found in Markdown file: " + filePath));
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            throw new FileProcessingException("Error reading file: " + filePath, e);
        }
    }

    /**
     * Extracts title from a JSON file.
     *
     * @param filePath Path to the JSON file.
     * @return Title extracted from the JSON file.
     */
    private static String extractTitleFromJson(Path filePath) throws IOException {
        System.out.println("Passing for extractTitleFromJson with filePath: " + filePath);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(Files.newInputStream(filePath));
        String title = rootNode.get("title").asText();
        System.out.println("Title: " + title);
        return splitTitleAfterColon(title);
    }

    
    /**
     * Parses a quiz file and converts its content into an instance of {@link QuizDTO}.
     * 
     * This method handles both absolute file paths and classpath resources. If the file exists
     * in the classpath, it loads the content and processes it into a {@link QuizDTO} object.
     *
     * @param filePath The {@link Path} object pointing to the quiz file.
     * @return An instance of {@link QuizDTO} representing the parsed content from the file.
     * @throws FileProcessingException If an I/O error occurs or the file cannot be parsed.
     */
    public static QuizDTO parseQuizFromFile(Path filePath) {
    
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (filePath.toString().contains("target")) {
                return objectMapper.readValue(Files.newInputStream(filePath), QuizDTO.class);
            } else {
                // Handle classpath resource
                String resourcePath = filePath.toString().replace("\\", "/");
                System.out.println(resourcePath);
                Resource resource = new ClassPathResource(resourcePath);
                try (InputStream inputStream = resource.getInputStream()) {
                    return objectMapper.readValue(inputStream, QuizDTO.class);
                }
            }

        } catch (IOException e) {
            throw new FileProcessingException("Error reading file: " + filePath, e);
        }
    }


    /**
     * Splits a title and returns text after ':' if present.
     *
     * @param title The raw title.
     * @return Cleaned title after ':' or the original title.
     */
    private static String splitTitleAfterColon(String title) {
        if (title.contains(":")) {
            System.out.println("Title after split: " + title);
            return title.split(":", 2)[1].trim();
        }
        System.out.println("Title without split: " + title );
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
            return content.split("\n")[0].replace("#", "").trim().split(":", 2)[1].trim();
        } else { // JSON file
            String title = JsonPath.read(content, "$.title");
            return title.split(":", 2)[1].trim();
        }
    }

    
    /**
     * Builds the file path for a given input {@link Resource} using the provided base directory.
     *
     * This method resolves the file name from the given resource, either by extracting 
     * the path for a {@link ClassPathResource} or simply using the resource's filename,
     * and then constructs a full path by appending it to the base directory.
     *
     * @param resource     The resource from which the file path is resolved.
     * @param baseDirectory The base directory to prepend to the resolved file name.
     * @return Constructed file path as a String.
     * @throws FileProcessingException If the resource path cannot be resolved or an error occurs.
     */
    public static String buildFilePathForInputStream(Resource resource, String baseDirectory) {
        try {
    
            String fileName;
    
            if (resource instanceof ClassPathResource) {
                String path = ((ClassPathResource) resource).getPath();
                fileName = Paths.get(path).getFileName().toString();
            } else {
                fileName = resource.getFilename();
            }
            System.out.println("Filename: " + fileName);
    
            return baseDirectory + "/" + fileName;
        } catch (Exception e) {
            throw new FileProcessingException("Failed to resolve resource path for: " + resource.getDescription(), e);
        }
    }
}
