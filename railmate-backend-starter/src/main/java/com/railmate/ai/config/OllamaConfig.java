package com.railmate.ai.config;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OllamaConfig {

    @Bean
    public OllamaChatModel ollamaChatModel(
            @Value("${railmate.ai.ollama.base-url}") String baseUrl,
            @Value("${railmate.ai.ollama.model-name}") String modelName) {

        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.2)
                .timeout(Duration.ofSeconds(90))
                .build();
    }

    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel(
            @Value("${railmate.ai.ollama.base-url}") String baseUrl,
            @Value("${railmate.ai.ollama.embedding-model-name}") String modelName) {

        return OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofSeconds(90))
                .build();
    }
}