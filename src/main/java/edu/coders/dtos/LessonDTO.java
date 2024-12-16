package edu.coders.dtos;

import edu.coders.entities.Quiz;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class LessonDTO {
    private Long id;

    private String title;

    private String filePath;

    private String content;

    private Quiz quiz;
}
