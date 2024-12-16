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
public class Lab {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @Lob
    private String starterCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestCase> testCases;
}
