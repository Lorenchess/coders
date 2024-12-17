package edu.coders.dtos;

import edu.coders.entities.Quiz;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LessonDTO {
    private Long id;

    private String title;

    private String filePath;

    private String content;

    private Quiz quiz;

}
