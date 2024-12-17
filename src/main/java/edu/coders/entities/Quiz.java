package edu.coders.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


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

    @Column(unique = true, nullable = false)
    private String title;

    @NotNull
    private String filePath;

    @PrePersist
    @PreUpdate
    private void validateQuiz() {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalStateException(String.format("Quiz must have a file path. File path not found: %s", filePath));
        }
    }
}
