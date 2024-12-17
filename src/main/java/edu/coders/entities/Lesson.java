package edu.coders.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "lesson", indexes = @Index(name = "idx_lesson_title", columnList = "title"))
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    @NotNull
    private String filePath;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_id", referencedColumnName = "id")
    private Quiz quiz;

    @PrePersist
    @PreUpdate
    private void validateLesson() {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalStateException(String.format("Lesson must have a filePath. Filepath not found: %s", filePath));
        }
    }

}
