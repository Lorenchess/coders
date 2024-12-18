package edu.coders.utils;

import edu.coders.controller.LessonController;
import edu.coders.dtos.LessonDTO;
import edu.coders.exceptions.LessonFileNotFoundException;
import edu.coders.exceptions.LessonNotFoundException;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public class HATEOASUtils {

    public static Link createSelfLessonLink(Long lessonId) throws LessonFileNotFoundException, LessonNotFoundException {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LessonController.class).getLessonById(lessonId)).withSelfRel();
    }

    public static Link createSearchLessonLink(String keyword) {
        return WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LessonController.class).searchLessons(keyword)).withRel("search");
    }

    public static Link createSearchLessonByTitleLink(String title) throws LessonFileNotFoundException, LessonNotFoundException {
        return WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(LessonController.class).getLessonByTitle(title)
        ).withRel("searchByTitle");
    }

    public static void addLessonLinks(LessonDTO lessonDTO) throws LessonFileNotFoundException, LessonNotFoundException {
        lessonDTO.add(createSelfLessonLink(lessonDTO.getId()));
        lessonDTO.add(createSearchLessonLink("keyword"));
        lessonDTO.add(createSearchLessonByTitleLink(lessonDTO.getTitle()));
    }
}
