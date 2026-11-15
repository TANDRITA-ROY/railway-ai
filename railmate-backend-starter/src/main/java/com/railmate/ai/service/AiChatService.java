
package com.railmate.ai.service;

import com.railmate.dto.TrainSearchResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiChatService {

    private final OllamaChatModel chatModel;
    private final RailwayAssistant railwayAssistant;
    private final TrainSearchResultContext resultContext;

    public AiChatService(
            OllamaChatModel chatModel,
            RailwayAssistant railwayAssistant,
            TrainSearchResultContext resultContext
    ) {
        this.chatModel = chatModel;
        this.railwayAssistant = railwayAssistant;
        this.resultContext = resultContext;
    }

    // Existing direct Qwen chat
    public String chat(String message) {
        return chatModel.chat(message);
    }

    // Chat with LangChain4j tool calling
    public AssistantChatResult chatWithTools(String message) {

        try {
            // Step 1: Get the AI-generated response
            String answer = railwayAssistant.chat(message);

            // Step 2: Retrieve captured train results
            List<TrainSearchResponse> trains =
                    resultContext.getResults();

            // Step 3: Return both AI response and train data
            return new AssistantChatResult(answer, trains);

        } finally {
            // Always clean up, even if an exception occurs
            resultContext.clear();
        }
    }

    public record AssistantChatResult(
            String response,
            List<TrainSearchResponse> trains
    ) {}
}
