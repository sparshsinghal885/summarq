package com.sparsh.summarq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Document(collection = "quizzes")
public class Quiz {
    @Id
    private Long id;
    private Long documentId;   // Reference to Document
    private Long userId;       // Reference to User
    private List<Question> questions;  // List of AI-generated questions
    private Date createdAt;
}