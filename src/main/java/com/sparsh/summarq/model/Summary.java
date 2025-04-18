package com.sparsh.summarq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Document(collection = "summaries")
public class Summary {
    @Id
    private Long id;
    private Long documentId;
    private Long userId;
    private String summaryText;
    private Date generatedAt;
}