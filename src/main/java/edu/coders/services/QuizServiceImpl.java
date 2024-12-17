package edu.coders.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.coders.dtos.QuizDTO;
import edu.coders.entities.Quiz;
import edu.coders.exceptions.ParseJsonContentInvalidException;
import edu.coders.exceptions.QuizNotFoundException;
import edu.coders.repositories.QuizRepository;
import edu.coders.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService{

    private final QuizRepository quizRepository;

    @Override
    public QuizDTO getQuizById(Long id) throws QuizNotFoundException, ParseJsonContentInvalidException {
        Quiz quiz = quizRepository.findById(id).orElseThrow(() -> new QuizNotFoundException(String.format("Quiz with id: %s was not found", id)));

        String quizContent = FileUtils.loadContentFromFile(quiz.getFilePath(), QuizNotFoundException::new);

        return parseQuizFromJson(quizContent);
    }

    private QuizDTO parseQuizFromJson(String quizContent) throws ParseJsonContentInvalidException {
        ObjectMapper mapper = new ObjectMapper();
        try{
            return mapper.readValue(quizContent, QuizDTO.class);
        } catch (Exception e) {
            throw new ParseJsonContentInvalidException("Failed to parse quiz JSON: " + e.getMessage());
        }
    }
}
