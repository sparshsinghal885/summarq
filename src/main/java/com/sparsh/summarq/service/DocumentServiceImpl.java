package com.sparsh.summarq.service;

import com.sparsh.summarq.model.Document;
import com.sparsh.summarq.model.Quiz;
import com.sparsh.summarq.model.Summary;
import com.sparsh.summarq.repository.DocumentRepository;
import com.sparsh.summarq.repository.QuizRepository;
import com.sparsh.summarq.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService{

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private SummaryRepository summaryRepository;

    @Autowired
    private QuizRepository quizRepository;


    @Override
    public Document saveDocument(String fileName, String extractedText, Long userId) {
        Document document = new Document();
        document.setFileName(fileName);
        document.setUploadDate(new Date());
        document.setExtractedText(extractedText);
        document.setUserId(userId);
        document.setProcessed(true);
        document.setId(sequenceGeneratorService.generateSequence("document_sequence"));

        Document savedDocument =  documentRepository.save(document);

        return savedDocument;
    }

    @Override
    public void deleteDocument(Long documentId) throws IOException {
        Document document = findDocument(documentId);
        documentRepository.delete(document);
    }

    @Override
    public Document findDocument(Long documentId) throws IOException {
        Optional<Document> optionalDocument = documentRepository.findById(documentId);

        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();

            Summary summary = summaryRepository.findByDocumentId(documentId);
            Quiz quiz = quizRepository.findByDocumentId(documentId);

            document.setSummary(summary);
            document.setQuiz(quiz);

            return document;
        } else {
            throw new IOException("Document with ID " + documentId + " not found.");
        }
    }

    @Override
    public List<Document> findDocumentsByUserId(Long userId) {
        return documentRepository.findByUserId(userId);
    }
}
