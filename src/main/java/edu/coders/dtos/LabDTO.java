package edu.coders.dtos;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabDTO {
    private Long id;
    private String title;
    private String description;
    private String starterCode;
    private List<TestCaseDTO> testCases;
}
