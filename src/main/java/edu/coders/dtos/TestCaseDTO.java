package edu.coders.dtos;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseDTO {
    private Long id;

    private String input;

    private String expectedOutput;

    private String explanation;
}
