package edu.coders.dtos;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
    private Long id;
    private String question;
    private List<String> options;
    private List<AnswerDTO> answerDTOS;
}
