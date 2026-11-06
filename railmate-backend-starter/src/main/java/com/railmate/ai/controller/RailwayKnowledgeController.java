
package com.railmate.ai.controller;

import com.railmate.ai.service.RailwayKnowledgeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class RailwayKnowledgeController {

    private final RailwayKnowledgeService railwayKnowledgeService;

    public RailwayKnowledgeController(
            RailwayKnowledgeService railwayKnowledgeService) {
        this.railwayKnowledgeService = railwayKnowledgeService;
    }

    @PostMapping("/policies")
    public ResponseEntity<StorePolicyResponse> storePolicy(
            @RequestBody StorePolicyRequest request) {

        String id = railwayKnowledgeService.storePolicy(
                request.title(),
                request.content()
        );

        return ResponseEntity.ok(
                new StorePolicyResponse(
                        "Railway policy stored successfully",
                        id
                )
        );
    }


    @PostMapping("/search")
    public ResponseEntity<List<RailwayKnowledgeService.PolicyMatch>> searchPolicy(
            @RequestBody SearchRequest request) {

        List<RailwayKnowledgeService.PolicyMatch> results =
                railwayKnowledgeService.retrievePolicy(request.question());

        return ResponseEntity.ok(results);
    }


    @PostMapping("/ask")
    public ResponseEntity<AnswerResponse> askQuestion(
            @RequestBody AskQuestionRequest request) {

        String answer = railwayKnowledgeService.answerQuestion(
                request.question()
        );

        return ResponseEntity.ok(
                new AnswerResponse(request.question(), answer)
        );
    }

    public record AskQuestionRequest(String question) {}

    public record AnswerResponse(String question, String answer) {}


    public record SearchRequest(String question) {}


    public record StorePolicyRequest(
            String title,
            String content
    ) {}

    public record StorePolicyResponse(
            String message,
            String id
    ) {}
}
