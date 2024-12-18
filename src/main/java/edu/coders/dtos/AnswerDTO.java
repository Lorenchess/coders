package edu.coders.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {

   private String text;

   @JsonProperty("isCorrect")
   private boolean isCorrect;

   private String explanation;
}
