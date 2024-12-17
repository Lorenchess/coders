package edu.coders.config;

import edu.coders.entities.Lesson;
import edu.coders.entities.Quiz;
import edu.coders.repositories.LessonRepository;
import edu.coders.repositories.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class MetadataInitializer implements CommandLineRunner {

    private final LessonRepository lessonRepository;
    private final QuizRepository quizRepository;


    @Override
    public void run(String... args) throws Exception {

        List<Lesson> lessons = scanFiles("lessons", path -> Lesson.builder()
                .title(extractTitle(path, ".md"))
                .filePath(path.toString())
                .build());
        lessonRepository.saveAll(lessons);

        List<Quiz> quizzes = scanFiles("quizzes", path -> Quiz.builder()
                .title(extractTitle(path, ".json"))
                .filePath(path.toString())
                .build());
        quizRepository.saveAll(quizzes);
    }

    /**
     * Scans a directory in the classpath and maps the files to objects of type T.
     *
     * @param directoryPath The directory in the classpath to scan.
     * @param mapper    A function to map a file path to an object of type T.
     * @param <T>       The type of object to create (e.g., Lesson, Quiz).
     * @return A list of objects of type T.
     */
    private <T>List<T> scanFiles(String directoryPath, Function<Path, T> mapper) throws FileNotFoundException {
        List<T> results = new ArrayList<>();
        try(Stream<Path> pathStream = Files.walk(new ClassPathResource(directoryPath).getFile().toPath())){
            pathStream
                    .filter(Files::isRegularFile)
                    .forEach(path -> results.add(mapper.apply(path)));
        } catch (IOException e) {
            throw new FileNotFoundException("Failded to scan files in directory: " + directoryPath);
        }

        return results;
    }

    private String extractTitle(Path path, String extension) {
        String fileName = path.getFileName().toString();
        return fileName.replace(extension, "").replace("_", " ");
    }
}
