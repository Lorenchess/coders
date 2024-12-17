package edu.coders.controller;

import edu.coders.dtos.QuizDTO;
import edu.coders.exceptions.ParseJsonContentInvalidException;
import edu.coders.exceptions.QuizNotFoundException;
import edu.coders.services.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private QuizService quizService;

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(@PathVariable Long id) throws QuizNotFoundException, ParseJsonContentInvalidException {
        QuizDTO quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }
}
