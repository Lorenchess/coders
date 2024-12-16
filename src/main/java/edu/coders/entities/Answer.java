package edu.coders.entities;

import jakarta.persistence.Embeddable;
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
   String text;

   boolean isCorrect;

   String explanation;
}
