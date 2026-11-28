
package com.railmate.railmate_mcp_server.tool;

import com.railmate.railmate_mcp_server.client.RailmateBackendClient;

import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static io.modelcontextprotocol.spec.McpSchema.*;

@Component
public class SeatAvailabilityMcpTool {

    private final RailmateBackendClient backendClient;
    private final ObjectMapper objectMapper;

    public SeatAvailabilityMcpTool(
            RailmateBackendClient backendClient,
            ObjectMapper objectMapper
    ) {
        this.backendClient = backendClient;
        this.objectMapper = objectMapper;
    }

    public SyncToolSpecification specification() {

        Map<String, Object> properties = Map.of(
                "trainNumber", Map.of(
                        "type", "string",
                        "description", "Train number, for example 12301"
                ),
                "source", Map.of(
                        "type", "string",
                        "description", "Source station code or name"
                ),
                "destination", Map.of(
                        "type", "string",
                        "description", "Destination station code or name"
                ),
                "journeyDate", Map.of(
                        "type", "string",
                        "description", "Journey date in yyyy-MM-dd format"
                ),
                "coachType", Map.of(
                        "type", "string",
                        "description", "Coach class, for example 3A, SL, or 2A"
                )
        );

        JsonSchema inputSchema = new JsonSchema(
                "object",
                properties,
                List.of(
                        "trainNumber",
                        "source",
                        "destination",
                        "journeyDate",
                        "coachType"
                ),
                false,
                null,
                null
        );

        Tool tool = Tool.builder()
                .name("checkSeatAvailability")
                .description(
                        "Check available seats and fare for a train journey. "
                                + "Requires train number, source, destination, "
                                + "journey date, and coach class."
                )
                .inputSchema(inputSchema)
                .build();

        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {

                    String trainNumber =
                            (String) request.arguments().get("trainNumber");

                    String source =
                            (String) request.arguments().get("source");

                    String destination =
                            (String) request.arguments().get("destination");

                    String journeyDate =
                            (String) request.arguments().get("journeyDate");

                    String coachType =
                            (String) request.arguments().get("coachType");

                    if (trainNumber == null || trainNumber.isBlank()
                            || source == null || source.isBlank()
                            || destination == null || destination.isBlank()
                            || journeyDate == null || journeyDate.isBlank()
                            || coachType == null || coachType.isBlank()) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Train number, source, destination, "
                                                        + "journey date, and coach type "
                                                        + "are required."
                                        )
                                ))
                                .isError(true)
                                .build();
                    }

                    try {

                        List<Map<String, Object>> availability =
                                backendClient.checkSeatAvailability(
                                        trainNumber,
                                        source,
                                        destination,
                                        journeyDate,
                                        coachType
                                );

                        String json = availability.isEmpty()
                                ? "No seat availability found for the requested journey."
                                : objectMapper.writeValueAsString(availability);

                        return CallToolResult.builder()
                                .content(List.of(new TextContent(json)))
                                .isError(false)
                                .build();

                    } catch (RestClientException | JacksonException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Seat availability check failed: "
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
