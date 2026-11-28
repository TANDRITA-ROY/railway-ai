package com.railmate.ai.config;

import com.railmate.ai.service.RailwayAssistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import dev.langchain4j.mcp.McpToolProvider;

@Configuration
public class RailwayAssistantConfig {

    @Bean
    public RailwayAssistant railwayAssistant(
            OllamaChatModel ollamaChatModel,
            McpToolProvider mcpToolProvider
    ) {

        return AiServices.builder(RailwayAssistant.class)
                .chatModel(ollamaChatModel)
                .chatMemoryProvider(memoryId ->
                        MessageWindowChatMemory.builder()
                                .id(memoryId)
                                .maxMessages(20)
                                .build()
                )
                // MCP tools: searchTrains and checkSeatAvailability
                .toolProvider(mcpToolProvider)
                .build();
    }
}