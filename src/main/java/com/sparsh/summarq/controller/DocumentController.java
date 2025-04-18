package com.sparsh.summarq.controller;

import com.sparsh.summarq.model.Document;
import com.sparsh.summarq.model.User;
import com.sparsh.summarq.repository.UserRepository;
import com.sparsh.summarq.response.FileUploadResponse;
import com.sparsh.summarq.service.DocumentService;
import com.sparsh.summarq.service.PdfService;
import com.sparsh.summarq.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.InputStream;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/document")
public class DocumentController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private PdfService pdfService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);

        String extractedText = extractTextFromPdf(file);

        Document document = documentService.saveDocument( file.getOriginalFilename(), extractedText, user.getId());

        if(user.getDocumentIds() == null){
            user.setDocumentIds(Arrays.asList(document.getId()));
        }else{
            user.getDocumentIds().add(document.getId());
        }
        userRepository.save(user);
        FileUploadResponse response = new FileUploadResponse();

        response.setMessage("File uploaded successfully!");
        response.setFileName(file.getOriginalFilename());
        response.setStatus(true);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }

    private String extractTextFromPdf(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream(); PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to extract text.";
        }
    }

    @DeleteMapping("/delete/{documentId}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long documentId, @RequestHeader("Authorization") String jwt) {
        try {
            User user = userService.findUserByJwtToken(jwt);

            if(user.getDocumentIds() == null){
                return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
            }
            List<Long> documentIds = user.getDocumentIds();
            if(documentIds.contains(documentId)){
                documentIds.removeIf(id -> id.equals(documentId));
                documentService.deleteDocument(documentId);
                user.setDocumentIds(documentIds);
                userRepository.save(user);
                return new ResponseEntity<>(true, HttpStatus.OK);
            }
            else{
                return ResponseEntity.badRequest().body("User does not have any document uploaded with this id " + documentId);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("{documentId}")
    public ResponseEntity<?> getDocument(@PathVariable Long documentId,
                                         @RequestHeader("Authorization") String jwt
                                         ) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        if(user.getDocumentIds().contains(documentId)){
            Document document = documentService.findDocument(documentId);
            return new ResponseEntity<>(document, HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body("User does not have any document uploaded with this id " + documentId);
    }

    @GetMapping("/user")
    public ResponseEntity<List<Document>> getDocumentsByUser(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserByJwtToken(jwt);
        List<Document>documents = documentService.findDocumentsByUserId(user.getId());

        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @GetMapping("/{documentId}/download-summary")
    public ResponseEntity<?> downloadSummary(@PathVariable Long documentId, @RequestHeader("Authorization") String jwt) throws Exception {

        User user = userService.findUserByJwtToken(jwt);
        if(user.getDocumentIds().contains(documentId)){
            Document document = documentService.findDocument(documentId);
            String summaryText = document.getSummary().getSummaryText();
            if (summaryText == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            byte[] pdfContent = pdfService.generateSummaryPdf(summaryText);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=summary.pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfContent);
        }

        return ResponseEntity.badRequest().body("User does not have any document uploaded with this id " + documentId);

    }
}
