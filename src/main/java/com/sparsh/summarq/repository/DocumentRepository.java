package com.sparsh.summarq.repository;

import com.sparsh.summarq.model.Document;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
}
