package edu.coders.utils;

import edu.coders.exceptions.FileProcessingException;
import edu.coders.exceptions.TitleExtractionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;


class FileUtilsTest {

    @BeforeEach
    void setUp() {
    }

    @Test
    void loadContentFromFile() throws IOException {
        // Arrange
        String content = "This is a test file content";
        Path tempFile = Files.createTempFile("test_load_file", ".txt");
        Files.write(tempFile, content.getBytes());

        // Act
        String result = FileUtils.loadContentFromFile(tempFile.toString(), RuntimeException::new);

        // Assert
        assertThat(result).isEqualTo(content);

        // Clean up
        Files.deleteIfExists(tempFile);
    }

    @Test
    void testLoadContentFromFileThrowsException() {
        // Arrange
        String filePath = "non_existent_file.txt";

        // Act & Assert
        assertThatThrownBy(() -> FileUtils.loadContentFromFile(filePath, RuntimeException::new))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("File not found with path");
    }

    @Test
    void extractTitleFromFile_testing_Json_File() throws IOException {
        String jsonContent = """
                {"title": "Quiz 1: Introduction to Java"}
                """;
        Path tempFile = Files.createTempFile("quiz", ".json");
        Files.write(tempFile, jsonContent.getBytes());
        String title = FileUtils.extractTitleFromFile(tempFile);
        assertEquals("Introduction to Java", title);

        Files.deleteIfExists(tempFile);
    }

    @Test
    void extractTitleFromFile_testing_Markdown_File() throws IOException {
        String mdContent = "# Lesson 1: Introduction to Java\nThis is a test file.";
        Path tempFile = Files.createTempFile("lesson", ".md");
        Files.write(tempFile, mdContent.getBytes());
        String title = FileUtils.extractTitleFromFile(tempFile);
        assertEquals("Introduction to Java", title);

        Files.deleteIfExists(tempFile);
    }

    @Test
    void faildToExtractTitle_From_UnsuportedExtension () throws IOException {
        Path tempFile = Files.createTempFile("invalid", ".txt");
        assertThatThrownBy(() -> FileUtils.extractTitleFromFile(tempFile))
                .isInstanceOf(TitleExtractionException.class)
                .hasMessageContaining("File extension not supported");
        Files.deleteIfExists(tempFile);
    }

    @Test
    void readContentFromInputStream() {
        String content = "This is a test content";
        InputStream inputStream = new ByteArrayInputStream(content.getBytes());
        String result = FileUtils.readContentFromInputStream(inputStream);
        assertEquals(content, result);
    }

    @Test
    void test_throws_Exception_When_readContentFromInputStream() throws IOException {
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated exception");
            }
        };

        assertThatThrownBy(() -> FileUtils.readContentFromInputStream(inputStream))
                .isInstanceOf(FileProcessingException.class)
                .hasMessageContaining("Error reading file from InputStream");
        inputStream.close();
    }

    @Test
    void extract_complete_TitleFromContent_JSON() {
        String jsonContent = "{ \"title\": \"Lesson 1: Introduction to Java\" }";

        // Act
        String title = FileUtils.extractTitleFromContent(jsonContent);

        // Assert
        assertThat(title).isEqualTo("Introduction to Java");
    }
}