package com.sparsh.summarq.service;

import com.sparsh.summarq.model.Summary;
import com.sparsh.summarq.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SummaryServiceImpl implements  SummaryService{

    @Autowired
    private SummaryRepository summaryRepository;

    @Override
    public Summary getSummary(Long documentId) {
        return summaryRepository.findByDocumentId(documentId);
    }
}
