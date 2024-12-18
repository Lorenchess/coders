package edu.coders.dtos;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO extends RepresentationModel<LessonDTO> {
    private Long id;

    private String title;

    private String filePath;

    private String content;

    private QuizDTO quiz;

}
