
package com.railmate.ai.config;

import dev.langchain4j.store.embedding.chroma.ChromaApiVersion;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChromaConfig {

    @Bean
    public ChromaEmbeddingStore chromaEmbeddingStore(
            @Value("${railmate.ai.chroma.base-url}") String baseUrl,
            @Value("${railmate.ai.chroma.tenant}") String tenant,
            @Value("${railmate.ai.chroma.database}") String database,
            @Value("${railmate.ai.chroma.collection}") String collection) {

        return ChromaEmbeddingStore.builder()
                .apiVersion(ChromaApiVersion.V2)
                .baseUrl(baseUrl)
                .tenantName(tenant)
                .databaseName(database)
                .collectionName(collection)
                .build();
    }
}
