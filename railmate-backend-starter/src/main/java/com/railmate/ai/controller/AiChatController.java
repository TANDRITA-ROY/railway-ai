
package com.railmate.ai.controller;

import com.railmate.ai.service.AiChatService;
import org.springframework.web.bind.annotation.*;

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

        return new ChatResponse(answer);
    }

    // New tool-calling endpoint
    @PostMapping("/api/assistant/chat")
    public ChatResponse chatWithTools(@RequestBody ChatRequest request) {

        String answer =
                aiChatService.chatWithTools(request.message());

        return new ChatResponse(answer);
    }

    public record ChatRequest(String message) {}

    public record ChatResponse(String response) {}
}
