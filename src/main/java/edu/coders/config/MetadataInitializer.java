package edu.coders.config;

import edu.coders.entities.Lesson;
import edu.coders.entities.Quiz;
import edu.coders.exceptions.FileProcessingException;
import edu.coders.repositories.LessonRepository;
import edu.coders.repositories.QuizRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

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
                (inputStream, resource) -> {
                    String content = readContentFromInputStream(inputStream);
                    String title = extractTitleFromContent(content);
                    return Quiz.builder()
                            .title(title)
                            .filePath(buildFilePathForInputStream(resource, appPathsConfig.getQuizzesDir()))
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
                (inputStream, resource) -> {
                    String content = readContentFromInputStream(inputStream);
                    String title = extractTitleFromContent(content);
                    Quiz currentQuiz = quizRepository.findByTitle(title).orElse(null);
                    return Lesson.builder()
                            .title(title)
                            .filePath(buildFilePathForInputStream(resource, appPathsConfig.getLessonsDir()))
                            .quiz(currentQuiz)
                            .build();
                });

        lessonRepository.saveAll(lessons);
        
        System.out.println("Metadata initialization completed successfully!");
    
    }

    
   
     /**
      * Scans files within a specified directory, processes them using provided mapping functions,
      * and returns a list of mapped results. Files are either processed as `Path` if accessible as a physical file
      * or as `InputStream` otherwise.
      *
      * @param directoryPath The path to the target directory containing files to scan.
      * @param pathMapper A function that maps a file represented as `Path` to the desired result object.
      * @param streamMapper A function that maps a file represented as `InputStream` and its associated `Resource`
      *                     to the desired result object.
      * @param <T> The type of objects being produced from the processed files.
      * @return A list of objects mapped from the processed files.
      * @throws FileProcessingException If there is an error accessing or processing the files within the directory.
      */
     <T>List<T> scanFiles(String directoryPath, Function<Path, T> pathMapper, BiFunction<InputStream, Resource, T> streamMapper) {
         List<T> results = new ArrayList<>();
         try {
             Resource[] resources = resourceResolver.getResources(directoryPath + "/*");
     
             for (Resource resource : resources) {
     
                 if (resource.isReadable()) {
                     try {
                         // Use Path if resource can be resolved as a File
                         if (resource.getFile().exists()) {
                             System.out.println("Path resource can be resolved as a File");
                             results.add(pathMapper.apply(resource.getFile().toPath()));
                         }
                     } catch (IOException e) {
                         // Fallback to InputStream if the file cannot be resolved
                         try (InputStream inputStream = resource.getInputStream()) {
                             System.out.println("Path resource can be resolved as a InputStream");
                             results.add(streamMapper.apply(inputStream, resource));
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
