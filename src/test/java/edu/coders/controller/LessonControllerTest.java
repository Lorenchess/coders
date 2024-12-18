package edu.coders.controller;

import edu.coders.dtos.LessonDTO;
import edu.coders.dtos.QuizDTO;
import edu.coders.services.LessonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LessonController.class)
class LessonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LessonService lessonService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getLessonById() throws Exception {
        given(lessonService.getLessonById(anyLong())).willReturn(
          LessonDTO.builder()
                  .id(1L)
                  .title("Introduction to Java")
                  .filePath("classPath:lesson")
                  .content("This is a test content")
                  .quiz(QuizDTO.builder()
                          .id(1L)
                          .title("Introduction to Java")
                          .questions(List.of())
                          .build())
                  .build()
        );

        mockMvc.perform(get("/lessons/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Introduction to Java"));

    }

    @Test
    void searchLessons() {
    }

    @Test
    void getLessonByTitle() {
    }
}