package com.sparsh.summarq.repository;

import com.sparsh.summarq.model.Quiz;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, Long> {
    public Quiz findByDocumentId(Long documentId);
}
