package com.sparsh.summarq.controller;

import com.sparsh.summarq.model.Quiz;
import com.sparsh.summarq.model.Summary;
import com.sparsh.summarq.model.User;
import com.sparsh.summarq.service.GroqService;
import com.sparsh.summarq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groq")
public class GroqController {

    @Autowired
    private GroqService groqService;

    @Autowired
    private UserService userService;

    @GetMapping("/generate-summary/{documentId}")
    public ResponseEntity<?> summarize(@PathVariable Long documentId, @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        if(!user.getDocumentIds().contains(documentId)){
            return ResponseEntity.badRequest().body("User does not have any document uploaded with this id " + documentId);
        }
        try {
            Summary summary = groqService.generateSummary(documentId);

            return new ResponseEntity<>(summary, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/generate-quiz/{documentId}")
    public ResponseEntity<?> generateQuiz(@PathVariable Long documentId,
                                             @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        if(!user.getDocumentIds().contains(documentId)){
            return ResponseEntity.badRequest().body("User does not have any document uploaded with this id " + documentId);
        }

        try {
            Quiz quiz = groqService.generateQuiz(documentId);

            return new ResponseEntity<>(quiz, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
