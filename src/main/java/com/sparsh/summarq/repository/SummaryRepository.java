package com.sparsh.summarq.repository;

import com.sparsh.summarq.model.Quiz;
import com.sparsh.summarq.model.Summary;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryRepository extends MongoRepository<Summary, Long> {
    public Summary findByDocumentId(Long documentId);
}
