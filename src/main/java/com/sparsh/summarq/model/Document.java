package com.sparsh.summarq.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@org.springframework.data.mongodb.core.mapping.Document(collection = "documents")
public class Document {
    @Id
    private Long id;
    private Long userId;
    private String fileName;
    private String extractedText;
    private Date uploadDate;
    private boolean processed;

    @Transient
    private Summary summary;

    @Transient
    private Quiz quiz;
}