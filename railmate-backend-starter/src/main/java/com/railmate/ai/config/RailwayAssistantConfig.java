package com.railmate.ai.config;

import com.railmate.ai.service.RailwayAssistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import com.railmate.tool.*;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RailwayAssistantConfig {

    @Bean
    public RailwayAssistant railwayAssistant(
            OllamaChatModel ollamaChatModel,
            TrainSearchTool trainSearchTool,
            SeatAvailabilityTool seatAvailabilityTool,
            BookingDetailsTool bookingDetailsTool,
            UserBookingsTool userBookingsTool,
            BookingCancellationTool bookingCancellationTool,
            BookingPreparationTool bookingPreparationTool
    ) {

        return AiServices.builder(RailwayAssistant.class)
                .chatModel(ollamaChatModel)
                .chatMemoryProvider(memoryId ->
                        MessageWindowChatMemory.builder()
                                .id(memoryId)
                                .maxMessages(20)
                                .build()
                )
                .tools(trainSearchTool,
                        seatAvailabilityTool,
                        bookingDetailsTool,
                        userBookingsTool,
                        bookingCancellationTool,
                        bookingPreparationTool)
                .build();
    }
}