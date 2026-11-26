
package com.railmate.railmate_mcp_server.tool;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.railmate.railmate_mcp_server.client.RailmateBackendClient;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;

import static io.modelcontextprotocol.spec.McpSchema.*;

@Component
public class TrainSearchMcpTool {

    private final RailmateBackendClient backendClient;
    private final ObjectMapper objectMapper;

    public TrainSearchMcpTool(
            RailmateBackendClient backendClient,
            ObjectMapper objectMapper
    ) {
        this.backendClient = backendClient;
        this.objectMapper = objectMapper;
    }

    public SyncToolSpecification specification() {

        Map<String, Object> properties = Map.of(
                "source", Map.of(
                        "type", "string",
                        "description", "Source railway station code or name"
                ),
                "destination", Map.of(
                        "type", "string",
                        "description", "Destination railway station code or name"
                )
        );

        JsonSchema inputSchema = new JsonSchema(
                "object",
                properties,
                List.of("source", "destination"),
                false,
                null,
                null
        );

        Tool tool = Tool.builder()
                .name("searchTrains")
                .description("Search for trains running between two railway stations")
                .inputSchema(inputSchema)
                .build();

        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {

                    String source = (String) request.arguments().get("source");
                    String destination =
                            (String) request.arguments().get("destination");

                    if (source == null || source.isBlank()
                            || destination == null || destination.isBlank()) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Source and destination are required."
                                        )
                                ))
                                .isError(true)
                                .build();
                    }

                    try {

                        List<Map<String, Object>> trains =
                                backendClient.searchTrains(source, destination);

                        String json = trains.isEmpty()
                                ? "No trains found for the requested stations."
                                : objectMapper.writeValueAsString(trains);

                        return CallToolResult.builder()
                                .content(List.of(new TextContent(json)))
                                .isError(false)
                                .build();

                    } catch (RestClientException
                             | JacksonException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Train search failed: "
                                                        + exception.getMessage()
                                        )
                                ))
                                .isError(true)
                                .build();
                    }
                })
                .build();
    }
}
