package edu.coders.services;

import edu.coders.dtos.QuizDTO;
import edu.coders.exceptions.ParseJsonContentInvalidException;
import edu.coders.exceptions.QuizNotFoundException;

public interface QuizService {
    QuizDTO getQuizById(Long id) throws QuizNotFoundException, ParseJsonContentInvalidException;
}
