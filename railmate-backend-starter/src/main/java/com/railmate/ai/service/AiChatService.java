
package com.railmate.ai.service;

import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final OllamaChatModel chatModel;
    private final RailwayAssistant railwayAssistant;

    public AiChatService(
            OllamaChatModel chatModel,
            RailwayAssistant railwayAssistant
    ) {
        this.chatModel = chatModel;
        this.railwayAssistant = railwayAssistant;
    }

    // Existing direct Qwen chat
    public String chat(String message) {
        return chatModel.chat(message);
    }

    // New chat with LangChain4j tool calling
    public String chatWithTools(String message) {
        return railwayAssistant.chat(message);
    }
}
