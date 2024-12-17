package edu.coders.dtos;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {

   private String text;

   private boolean isCorrect;

   private String explanation;
}
