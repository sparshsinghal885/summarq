package com.sparsh.summarq.controller;

import com.sparsh.summarq.model.Summary;
import com.sparsh.summarq.model.User;
import com.sparsh.summarq.service.SummaryService;
import com.sparsh.summarq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/summary")
public class SummaryController {
    @Autowired
    private SummaryService summaryService;

    @Autowired
    private UserService userService;

    @GetMapping("/{documentId}")
    public ResponseEntity<?> fetchSummary(@PathVariable Long documentId,
                                       @RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        if(user.getDocumentIds().contains(documentId)){
            Summary summary = summaryService.getSummary(documentId);
            return new ResponseEntity<>(summary, HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body("User does not have any document uploaded with this id " + documentId);
    }
}
