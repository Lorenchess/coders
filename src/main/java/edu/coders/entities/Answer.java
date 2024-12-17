package edu.coders.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Answer {
   @NotNull
   String text;

   boolean isCorrect;

   @NotNull
   String explanation;
}
