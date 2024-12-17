package edu.coders.config;

import edu.coders.entities.Lesson;
import edu.coders.entities.Quiz;
import edu.coders.exceptions.FileProcessingException;
import edu.coders.repositories.LessonRepository;
import edu.coders.repositories.QuizRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static edu.coders.utils.FileUtils.*;


@Configuration
@RequiredArgsConstructor
public class MetadataInitializer implements CommandLineRunner {

    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;
    private final ResourcePatternResolver resourceResolver;
    private final AppPathsConfig appPathsConfig;

    
    
    /**
     * Initializes metadata by scanning quiz and lesson files from specified resource directories,
     * and saving the extracted data into the database repositories. Quiz files are scanned,
     * and data is generated into `Quiz` entities, while lesson files are associated with their
     * corresponding quizzes and stored as `Lesson` entities.
     *
     * @param args Command-line arguments (currently unused).
     */
    @Override
    @Transactional
    public void run(String... args)  {

        List<Quiz> quizzes = scanFiles(appPathsConfig.getQuizzesDir(),
                path -> Quiz.builder()
                        .title(extractTitleFromFile(path))
                        .filePath(path.toString())
                        .build(),
                inputStream -> {
                    String content = readContentFromInputStream(inputStream);
                    String title = extractTitleFromContent(content);
                    return Quiz.builder()
                            .title(title)
                            .filePath("classpath:quizzes")
                            .build();
                });

        quizRepository.saveAll(quizzes);

        List<Lesson> lessons = scanFiles(appPathsConfig.getLessonsDir(),
                path -> {
                    String title = extractTitleFromFile(path);
                    Quiz currentQuiz = quizRepository.findByTitle(title).orElse(null);
                    return Lesson.builder()
                            .title(title)
                            .filePath(path.toString())
                            .quiz(currentQuiz)
                            .build();
                },
                inputStream -> {
                    String content = readContentFromInputStream(inputStream);
                    String title = extractTitleFromContent(content);
                    Quiz currentQuiz = quizRepository.findByTitle(title).orElse(null);
                    return Lesson.builder()
                            .title(title)
                            .filePath("classpath:lessons")
                            .quiz(currentQuiz)
                            .build();
                });

        lessonRepository.saveAll(lessons);
        
        System.out.println("Metadata initialization completed successfully!");
    
    }

    
    /**
     * Scans a given directory for resources, processes each resource, and converts it into a list of objects of type {@code T}.
     * <p>
     * The method supports two ways to map resources: 
     * 1. Using a {@link java.nio.file.Path} if the resource can be resolved as a file. 
     * 2. Using an {@link java.io.InputStream} if the resource cannot be resolved as a file.
     * </p>
     *
     * @param <T>         The type of the objects to be created from the resources.
     * @param directoryPath The path to the directory containing the resources to scan. It must be a valid pattern understood by
     *                      {@link ResourcePatternResolver#getResources(String)}.
     * @param pathMapper    A function that maps a {@link Path} to an instance of {@code T}. This is used when the resource 
     *                      supports a file representation.
     * @param streamMapper  A function that maps an {@link InputStream} to an instance of {@code T}. This is used when no file 
     *                      representation can be resolved.
     * @return A list of objects of type {@code T} that were created by processing the resources in the specified directory.
     * @throws FileProcessingException if an {@link IOException} occurs while processing the directory or resources.
     */
     <T>List<T> scanFiles(String directoryPath, Function<Path, T> pathMapper, Function<InputStream, T> streamMapper) {
        List<T> results = new ArrayList<>();
        try {
            Resource[] resources = resourceResolver.getResources(directoryPath + "/*");
            System.out.println("Quantity of resources found: " + resources.length);
    
            for (Resource resource : resources) {
                System.out.println("Processing resource: " + resource.getFilename());
                if (resource.isReadable()) {
                    try {
                        // Use Path if resource can be resolved as a File
                        if (resource.getFile().exists()) {
                            results.add(pathMapper.apply(resource.getFile().toPath()));
                        }
                    } catch (IOException e) {
                        // Fallback to InputStream if the file cannot be resolved
                        try (InputStream inputStream = resource.getInputStream()) {
                            results.add(streamMapper.apply(inputStream));
                        }
                    }
                } else {
                    System.out.println("Resource is not readable: " + resource.getFilename());
                }
            }
        } catch (IOException e) {
            throw new FileProcessingException("Failed to process files in directory: " + directoryPath, e);
        }
        return results;
    }
}
