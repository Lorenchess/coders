package edu.coders.services;

import edu.coders.dtos.LessonDTO;
import edu.coders.dtos.QuizDTO;
import edu.coders.entities.Lesson;
import edu.coders.entities.Quiz;
import edu.coders.exceptions.LessonFileNotFoundException;
import edu.coders.exceptions.LessonNotFoundException;
import edu.coders.repositories.LessonRepository;
import edu.coders.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Override
    public LessonDTO getLessonById(Long id) throws LessonNotFoundException, LessonFileNotFoundException {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new LessonNotFoundException(String.format("Lesson %s not found", id)));

        return buildLessonDTOFromLesson(lesson);
    }

    @Override
    public List<String> searchLessonsByTitle(String keyword) {
        PageRequest pageRequest = PageRequest.of(0, 10);
        return lessonRepository.findByTitleContainingIgnoreCase(keyword, pageRequest);
    }

    @Override
    public LessonDTO getLessonByTitle(String title) throws LessonFileNotFoundException, LessonNotFoundException {
        Lesson lesson = lessonRepository.findByTitle(title);
        if (lesson == null) {
            throw new LessonNotFoundException(String.format("Lesson with title '%s' not found", title));
        }
        return buildLessonDTOFromLesson(lesson);

    }

    private LessonDTO buildLessonDTOFromLesson(Lesson lesson) throws LessonFileNotFoundException {
        String lessonContentFromFile = FileUtils.loadContentFromFile(lesson.getFilePath(), LessonFileNotFoundException::new);

        Quiz quiz = lesson.getQuiz();

        QuizDTO quizDTO = null;

        if (quiz != null) {
            String normalizedPath = quiz.getFilePath().replace("\\", "/").split(":", 2)[1].trim();
            Path quizFilePath = Path.of(normalizedPath);
            quizDTO = FileUtils.parseQuizFromFile(quizFilePath);
            quizDTO.setId(quiz.getId());
        }

        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lessonContentFromFile)
                .quiz(quizDTO)
                .build();
    }

}
