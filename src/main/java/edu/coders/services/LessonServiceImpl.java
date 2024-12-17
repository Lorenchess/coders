package edu.coders.services;

import edu.coders.dtos.LessonDTO;
import edu.coders.entities.Lesson;
import edu.coders.exceptions.LessonFileNotFoundException;
import edu.coders.exceptions.LessonNotFoundException;
import edu.coders.repositories.LessonRepository;
import edu.coders.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Override
    public LessonDTO getLessonById(Long id) throws LessonNotFoundException, LessonFileNotFoundException {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new LessonNotFoundException(String.format("Lesson %s not found", id)));

        String lessonContentFromFile = FileUtils.loadContentFromFile(lesson.getFilePath(), LessonFileNotFoundException::new);

        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lessonContentFromFile)
                .quiz(lesson.getQuiz())
                .build();
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
        String lessonContentFromFile = FileUtils.loadContentFromFile(lesson.getFilePath(), LessonFileNotFoundException::new);

        return LessonDTO.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lessonContentFromFile)
                .quiz(lesson.getQuiz())
                .build();
    }

}
