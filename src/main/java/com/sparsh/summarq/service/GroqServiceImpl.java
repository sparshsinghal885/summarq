package com.sparsh.summarq.service;

import com.sparsh.summarq.model.Document;
import com.sparsh.summarq.model.Question;
import com.sparsh.summarq.model.Quiz;
import com.sparsh.summarq.model.Summary;
import com.sparsh.summarq.repository.QuizRepository;
import com.sparsh.summarq.repository.SummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.*;

@Service
public class GroqServiceImpl implements GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private DocumentService documentService;

    @Autowired
    private SummaryRepository summaryRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Override
    public Summary generateSummary(Long documentId) throws IOException {
        Document document = documentService.findDocument(documentId);

        // Step 1: Check if summary already exists
        Optional<Summary> existingSummary = Optional.ofNullable(summaryRepository.findByDocumentId(documentId));

        // Step 2: Generate Summary Prompt
        String prompt = "Summarize the following text in headings, subheadings, and concise sentences:\n\n"
                + document.getExtractedText();

        // Step 3: Call Groq API
        String summaryText = sendRequestToGroq(prompt);

        Summary summary;
        if (existingSummary.isPresent()) {
            // Update existing summary
            summary = existingSummary.get();
            summary.setSummaryText(summaryText);
            summary.setGeneratedAt(new Date());
        } else {
            // Create new summary
            summary = new Summary();
            summary.setId(sequenceGeneratorService.generateSequence("summary_sequence"));
            summary.setDocumentId(document.getId());
            summary.setUserId(document.getUserId());
            summary.setSummaryText(summaryText);
            summary.setGeneratedAt(new Date());
        }

        return summaryRepository.save(summary);
    }

    @Override
    public Quiz generateQuiz(Long documentId) throws Exception {
        Document document = documentService.findDocument(documentId);

        // Step 1: Check if quiz already exists
        Optional<Quiz> existingQuiz = Optional.ofNullable(quizRepository.findByDocumentId(documentId));

        // Step 2: Generate Quiz Prompt
        String prompt = "Generate a 5-question quiz based on the following document in JSON format. "
                + "Do not include any other text, just the JSON format of questions. Each question should have 4 options, "
                + "with the correct answer indicated by an index (0-3). The response should be valid JSON with an array "
                + "of objects, where each object contains 'question', 'options' (array), and 'correct' (index of correct option):\n\n"
                + document.getExtractedText();

        // Step 3: Call Groq API
        String quizText = sendRequestToGroq(prompt);

        // Step 4: Parse AI Response (Assuming AI returns JSON-formatted questions)
        List<Question> questions = parseQuizQuestions(quizText);

        Quiz quiz;
        if (existingQuiz.isPresent()) {
            // Update existing quiz
            quiz = existingQuiz.get();
            quiz.setQuestions(questions);
            quiz.setCreatedAt(new Date());
        } else {
            // Create new quiz
            quiz = new Quiz();
            quiz.setId(sequenceGeneratorService.generateSequence("quiz_sequence"));
            quiz.setDocumentId(document.getId());
            quiz.setUserId(document.getUserId());
            quiz.setQuestions(questions);
            quiz.setCreatedAt(new Date());
        }

        return quizRepository.save(quiz);
    }

    private String sendRequestToGroq(String prompt) {
        // Prepare the request payload
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.3-70b-versatile");
        requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
        ));
        requestBody.put("temperature", 0.7);

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            // Extract the response body
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    if (firstChoice.containsKey("message")) {
                        Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                        return (String) message.get("content");  // Extract AI-generated text
                    }
                }
            }

            return "No valid response from Groq API.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating response from Groq API.";
        }
    }

    public List<Question> parseQuizQuestions(String quizJson) throws Exception {
        List<Question> questions = new ArrayList<>();

        // Create an ObjectMapper to parse the JSON string into a JsonNode
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode quizArray = objectMapper.readTree(quizJson);

        // Iterate over each quiz item in the JSON array
        for (JsonNode quizNode : quizArray) {
            Question question = new Question();

            // Extract the question text
            question.setQuestionText(quizNode.get("question").asText());

            // Extract options
            List<String> options = new ArrayList<>();
            JsonNode optionsNode = quizNode.get("options");
            for (JsonNode optionNode : optionsNode) {
                options.add(optionNode.asText());
            }
            question.setOptions(options);

            // Extract the correct answer (index is provided)
            int correctAnswerIndex = quizNode.get("correct").asInt();
            question.setCorrectAnswer(options.get(correctAnswerIndex));

            questions.add(question);
        }

        return questions;
    }
}
