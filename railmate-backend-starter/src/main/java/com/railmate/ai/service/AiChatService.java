
package com.railmate.ai.service;

import com.railmate.dto.BookingCancellationPreparationResponse;
import com.railmate.dto.BookingPreparationResponse;
import com.railmate.dto.TrainSearchResponse;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.Result;
import dev.langchain4j.service.tool.ToolExecution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

@Service
public class AiChatService {

    private static final Logger log =
            LoggerFactory.getLogger(AiChatService.class);

    private final OllamaChatModel chatModel;
    private final RailwayAssistant railwayAssistant;
    private final TrainSearchResultContext trainResultContext;
    private final BookingPreparationResultContext bookingResultContext;
    private final CancellationPreparationResultContext cancellationPreparationResultContext;
    private final ObjectMapper objectMapper;

    public AiChatService(
            OllamaChatModel chatModel,
            RailwayAssistant railwayAssistant,
            TrainSearchResultContext trainResultContext,
            BookingPreparationResultContext bookingResultContext,
            CancellationPreparationResultContext cancellationPreparationResultContext,
            ObjectMapper objectMapper
    ) {
        this.chatModel = chatModel;
        this.railwayAssistant = railwayAssistant;
        this.trainResultContext = trainResultContext;
        this.bookingResultContext = bookingResultContext;
        this.cancellationPreparationResultContext =
                cancellationPreparationResultContext;
        this.objectMapper = objectMapper;
    }

    // Existing direct Qwen chat
    public String chat(String message) {
        return chatModel.chat(message);
    }

    // Chat with LangChain4j tool calling
    public AssistantChatResult chatWithTools(
            String message,
            String conversationId
    ) {
        try {
            // Get the assistant response and executed tool details
            Result<String> result =
                    railwayAssistant.chat(conversationId, message);

            String answer = result.content();

            // Extract structured train data from the MCP tool execution
            List<TrainSearchResponse> trains =
                    extractTrainSearchResults(result);

            // Existing booking preparation handling
            // Extract structured booking preparation data from MCP
            BookingPreparationResponse pendingBooking =
                    extractBookingPreparationResult(result);

           // Fallback to the old context while the direct tool is still available
            if (pendingBooking == null) {
                pendingBooking = bookingResultContext.getResult();
            }

            // Existing cancellation preparation handling
            BookingCancellationPreparationResponse pendingCancellation =
                    extractCancellationPreparationResult(result);

            if (pendingCancellation == null) {
                pendingCancellation = cancellationPreparationResultContext.getResult();
            }

            return new AssistantChatResult(
                    answer,
                    trains,
                    pendingBooking,
                    pendingCancellation
            );

        } finally {
            // Clear contexts after every request
            trainResultContext.clear();
            bookingResultContext.clear();
            cancellationPreparationResultContext.clear();
        }
    }

    private List<TrainSearchResponse> extractTrainSearchResults(
            Result<String> result
    ) {
        for (ToolExecution toolExecution : result.toolExecutions()) {

            // We only need the searchTrains MCP tool result here
            if ("searchTrains".equals(toolExecution.request().name())) {

                String toolResult = toolExecution.result();

                if (toolResult == null || !toolResult.trim().startsWith("[")) {
                    return List.of();
                }

                try {
                    TrainSearchResponse[] trainArray =
                            objectMapper.readValue(
                                    toolResult,
                                    TrainSearchResponse[].class
                            );

                    return Arrays.asList(trainArray);

                } catch (JacksonException exception) {
                    log.error(
                            "Unable to parse train search result from MCP",
                            exception
                    );
                    return List.of();
                }
            }
        }

        // No searchTrains tool was executed
        return List.of();
    }

    private BookingPreparationResponse extractBookingPreparationResult(
            Result<String> result
    ) {
        for (ToolExecution toolExecution : result.toolExecutions()) {

            // We only need the prepareBooking MCP tool result
            if ("prepareBooking".equals(toolExecution.request().name())) {

                String toolResult = toolExecution.result();

                if (toolResult == null || toolResult.isBlank()) {
                    return null;
                }

                try {
                    return objectMapper.readValue(
                            toolResult,
                            BookingPreparationResponse.class
                    );

                } catch (JacksonException exception) {
                    log.error(
                            "Unable to parse booking preparation result from MCP",
                            exception
                    );
                    return null;
                }
            }
        }

        // No prepareBooking tool was executed
        return null;
    }


    private BookingCancellationPreparationResponse extractCancellationPreparationResult(
            Result<String> result
    ) {
        for (ToolExecution toolExecution : result.toolExecutions()) {

            // We only need the requestCancellation MCP tool result
            if ("requestCancellation".equals(toolExecution.request().name())) {

                String toolResult = toolExecution.result();

                if (toolResult == null || toolResult.isBlank()) {
                    return null;
                }

                try {
                    return objectMapper.readValue(
                            toolResult,
                            BookingCancellationPreparationResponse.class
                    );

                } catch (JacksonException exception) {
                    log.error(
                            "Unable to parse cancellation preparation result from MCP",
                            exception
                    );
                    return null;
                }
            }
        }

        // No requestCancellation tool was executed
        return null;
    }


    public record AssistantChatResult(
            String response,
            List<TrainSearchResponse> trains,
            BookingPreparationResponse pendingBooking,
            BookingCancellationPreparationResponse pendingCancellation
    ) {}
}
