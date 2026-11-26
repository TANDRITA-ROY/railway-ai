
package com.railmate.railmate_mcp_server.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Component
public class RailmateBackendClient {

    private final RestClient restClient;

    public RailmateBackendClient(
            RestClient.Builder builder,
            @Value("${railmate.backend.base-url}") String backendUrl
    ) {
        this.restClient = builder
                .baseUrl(backendUrl)
                .build();
    }

    public List<Map<String, Object>> searchTrains(
            String source,
            String destination
    ) {

        List<Map<String, Object>> result = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/trains/search")
                        .queryParam("source", source)
                        .queryParam("destination", destination)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        return result != null ? result : List.of();
    }
}
