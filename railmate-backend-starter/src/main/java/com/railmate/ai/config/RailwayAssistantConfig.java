package com.railmate.ai.config;

import com.railmate.ai.service.RailwayAssistant;
import com.railmate.tool.TrainSearchTool;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RailwayAssistantConfig {

    @Bean
    public RailwayAssistant railwayAssistant(
            OllamaChatModel ollamaChatModel,
            TrainSearchTool trainSearchTool
    ) {

        return AiServices.builder(RailwayAssistant.class)
                .chatModel(ollamaChatModel)
                .tools(trainSearchTool)
                .build();
    }
}