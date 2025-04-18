package com.sparsh.summarq.service;

import com.sparsh.summarq.model.Quiz;
import com.sparsh.summarq.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService{

    @Autowired
    private QuizRepository quizRepository;

    @Override
    public Quiz getQuiz(Long documentId) {
        Quiz quiz = quizRepository.findByDocumentId(documentId);
        return quiz;
    }
}
