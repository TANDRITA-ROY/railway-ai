
package com.railmate.ai.controller;

import com.railmate.ai.service.AiChatService;
import com.railmate.ai.service.AiChatService.AssistantChatResult;
import com.railmate.dto.TrainSearchResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    // Existing direct AI endpoint
    @PostMapping("/api/ai/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String answer = aiChatService.chat(request.message());

        return new ChatResponse(answer, List.of());
    }

    // Tool-calling endpoint with structured train results
    @PostMapping("/api/assistant/chat")
    public ChatResponse chatWithTools(@RequestBody ChatRequest request) {

        AssistantChatResult result =
                aiChatService.chatWithTools(request.message());

        return new ChatResponse(
                result.response(),
                result.trains()
        );
    }

    public record ChatRequest(String message) {}

    public record ChatResponse(
            String response,
            List<TrainSearchResponse> trains
    ) {}
}
