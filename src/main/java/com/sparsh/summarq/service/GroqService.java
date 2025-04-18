package com.sparsh.summarq.service;

import com.sparsh.summarq.model.Quiz;
import com.sparsh.summarq.model.Summary;

import java.io.IOException;

public interface GroqService {
    public Summary generateSummary(Long documentId) throws IOException;
    public Quiz generateQuiz(Long documentId) throws Exception;
}
