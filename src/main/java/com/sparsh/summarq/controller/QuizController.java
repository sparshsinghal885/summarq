package com.sparsh.summarq.controller;

import com.sparsh.summarq.model.Document;
import com.sparsh.summarq.model.Quiz;
import com.sparsh.summarq.model.User;
import com.sparsh.summarq.service.QuizService;
import com.sparsh.summarq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
public class QuizController {
    @Autowired
    private QuizService quizService;

    @Autowired
    private UserService userService;

    @GetMapping("/{documentId}")
    public ResponseEntity<?> fetchQuiz(@PathVariable Long documentId,
                                       @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        if(user.getDocumentIds().contains(documentId)){
            Quiz quiz = quizService.getQuiz(documentId);
            return new ResponseEntity<>(quiz, HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body("User does not have any document uploaded with this id " + documentId);
    }
}
