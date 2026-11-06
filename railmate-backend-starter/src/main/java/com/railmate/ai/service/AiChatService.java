package com.railmate.ai.service;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final OllamaChatModel chatModel;

    public AiChatService(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    public String chat(String message) {
        return chatModel.chat(message);
    }
}