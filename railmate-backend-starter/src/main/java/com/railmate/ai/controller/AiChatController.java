package com.railmate.ai.controller;

import com.railmate.ai.service.AiChatService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String answer = aiChatService.chat(request.message());

        return new ChatResponse(answer);
    }

    public record ChatRequest(String message) {}

    public record ChatResponse(String response) {}
}