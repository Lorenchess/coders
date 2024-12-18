package edu.coders.controller;

import edu.coders.dtos.LessonDTO;
import edu.coders.exceptions.LessonFileNotFoundException;
import edu.coders.exceptions.LessonNotFoundException;
import edu.coders.services.LessonService;
import edu.coders.utils.HATEOASUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/{id}")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable Long id) throws LessonNotFoundException, LessonFileNotFoundException {

        LessonDTO lesson = lessonService.getLessonById(id);
        HATEOASUtils.addLessonLinks(lesson);

        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/search")
    public ResponseEntity<CollectionModel<String>> searchLessons(@RequestParam String keyword){
        List<String> lessons = lessonService.searchLessonsByTitle(keyword);

        CollectionModel<String> collectionModel = CollectionModel.of(
                lessons,
                HATEOASUtils.createSearchLessonLink(keyword)
        );
        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<LessonDTO> getLessonByTitle(@PathVariable String title) throws LessonFileNotFoundException, LessonNotFoundException {
        LessonDTO lesson = lessonService.getLessonByTitle(title);
        HATEOASUtils.addLessonLinks(lesson);
        return ResponseEntity.ok(lesson);
    }


}
