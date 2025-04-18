package com.sparsh.summarq.service;

import com.sparsh.summarq.model.Document;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    public Document saveDocument(String fileName, String extractedText, Long userId);
    public void deleteDocument(Long documentId) throws IOException;
    public Document findDocument(Long documentId) throws IOException;
    List<Document> findDocumentsByUserId(Long userId);
}
