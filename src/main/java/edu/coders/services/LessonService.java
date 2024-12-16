package edu.coders.services;

import edu.coders.dtos.LessonDTO;
import edu.coders.exceptions.LessonFileNotFoundException;
import edu.coders.exceptions.LessonNotFoundException;

import java.util.List;

public interface LessonService {
    LessonDTO getLessonById(Long id) throws LessonNotFoundException, LessonFileNotFoundException;

    List<String> searchLessonsByTitle(String keyword);

    LessonDTO getLessonByTitle(String title) throws LessonFileNotFoundException, LessonNotFoundException;
}
