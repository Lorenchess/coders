package edu.coders.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "lesson", indexes = @Index(name = "idx_lesson_title", columnList = "title"))
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String title;

    private String filePath;

    @OneToOne(cascade = CascadeType.ALL)
    private Quiz quiz;

}
