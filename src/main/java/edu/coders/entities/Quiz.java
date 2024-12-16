package edu.coders.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;

    @PrePersist
    @PreUpdate
    private void validateQuiz() {
        if (questions == null || questions.isEmpty()) {
            throw new IllegalStateException("Quiz must have at least one question.");
        }
    }
}
