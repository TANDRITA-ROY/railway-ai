
package com.railmate.ai.service;

import com.railmate.dto.BookingCancellationPreparationResponse;
import com.railmate.dto.BookingPreparationResponse;
import com.railmate.dto.TrainSearchResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AiChatService {

    private final OllamaChatModel chatModel;
    private final RailwayAssistant railwayAssistant;
    private final TrainSearchResultContext trainResultContext;
    private final BookingPreparationResultContext bookingResultContext;
    private final CancellationPreparationResultContext cancellationPreparationResultContext;

    public AiChatService(
            OllamaChatModel chatModel,
            RailwayAssistant railwayAssistant,
            TrainSearchResultContext trainResultContext,
            BookingPreparationResultContext bookingResultContext,
            CancellationPreparationResultContext cancellationPreparationResultContext
    ) {
        this.chatModel = chatModel;
        this.railwayAssistant = railwayAssistant;
        this.trainResultContext = trainResultContext;
        this.bookingResultContext = bookingResultContext;
        this.cancellationPreparationResultContext=cancellationPreparationResultContext;
    }

    // Existing direct Qwen chat
    public String chat(String message) {
        return chatModel.chat(message);
    }

    // Chat with LangChain4j tool calling
    public AssistantChatResult chatWithTools(String message, String conversationId) {

        try {
            String answer = railwayAssistant.chat(conversationId, message);

            List<TrainSearchResponse> trains =
                    trainResultContext.getResults();

            BookingPreparationResponse pendingBooking =
                    bookingResultContext.getResult();

            BookingCancellationPreparationResponse pendingCancellation =
                    cancellationPreparationResultContext.getResult();

            return new AssistantChatResult(
                    answer,
                    trains,
                    pendingBooking,
                    pendingCancellation
            );

        } finally {
            // Clear both contexts after every request.
            trainResultContext.clear();
            bookingResultContext.clear();
            cancellationPreparationResultContext.clear();
        }
    }

    public record AssistantChatResult(
            String response,
            List<TrainSearchResponse> trains,
            BookingPreparationResponse pendingBooking,
            BookingCancellationPreparationResponse pendingCancellation
    ) {}
}
