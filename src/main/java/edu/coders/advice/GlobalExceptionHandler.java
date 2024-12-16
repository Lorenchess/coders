package edu.coders.advice;

import edu.coders.exceptions.LessonFileNotFoundException;
import edu.coders.exceptions.LessonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final String KEY = "Error message: ";

    private <T extends Exception>Map<String, String> errorMapHandler(T exception){
        return Map.of(KEY, exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LessonNotFoundException.class)
    public Map<String,String> handleLessonNotFoundException(LessonNotFoundException exception){
        return errorMapHandler(exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(LessonFileNotFoundException.class)
    public Map<String,String> handleLessonFileNotFoundException(LessonFileNotFoundException exception){
        return errorMapHandler(exception);
    }
}
