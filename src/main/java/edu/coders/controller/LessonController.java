package edu.coders.controller;

import edu.coders.dtos.LessonDTO;
import edu.coders.exceptions.LessonFileNotFoundException;
import edu.coders.exceptions.LessonNotFoundException;
import edu.coders.services.LessonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lessons")

public class LessonController {

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    private final LessonService lessonService;

    @GetMapping("/{id}")
    public ResponseEntity<LessonDTO> getLessonById(@PathVariable Long id) throws LessonNotFoundException, LessonFileNotFoundException {

        LessonDTO lesson = lessonService.getLessonById(id);

        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchLessons(@RequestParam String keyword){
        List<String> lessons = lessonService.searchLessonsByTitle(keyword);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/search/{title}")
    public ResponseEntity<LessonDTO> getLessonByTitle(@PathVariable String title) throws LessonFileNotFoundException, LessonNotFoundException {
        LessonDTO lesson = lessonService.getLessonByTitle(title);
        return ResponseEntity.ok(lesson);
    }

}
