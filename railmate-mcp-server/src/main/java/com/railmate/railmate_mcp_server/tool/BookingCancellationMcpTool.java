
package com.railmate.railmate_mcp_server.tool;

import com.railmate.railmate_mcp_server.client.RailmateBackendClient;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import io.modelcontextprotocol.spec.McpSchema.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Component
public class BookingCancellationMcpTool {

    private final RailmateBackendClient backendClient;
    private final ObjectMapper objectMapper;

    public BookingCancellationMcpTool(
            RailmateBackendClient backendClient,
            ObjectMapper objectMapper
    ) {
        this.backendClient = backendClient;
        this.objectMapper = objectMapper;
    }

    public SyncToolSpecification specification() {

        Map<String, Object> properties = Map.of(
                "bookingId", Map.of(
                        "type", "integer",
                        "description", "The booking ID to cancel"
                ),
                "userId", Map.of(
                        "type", "integer",
                        "description", "The user ID who owns the booking"
                )
        );

        JsonSchema inputSchema = new JsonSchema(
                "object",
                properties,
                List.of("bookingId", "userId"),
                null,
                null,
                null
        );

        Tool tool = Tool.builder()
                .name("requestCancellation")
                .description(
                        "Prepare cancellation of a user's railway booking. "
                                + "This does not cancel the booking. "
                                + "It only generates a confirmation token "
                                + "and returns cancellation details "
                                + "for the user to review."
                )
                .inputSchema(inputSchema)
                .build();

        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {

                    try {
                        Map<String, Object> arguments = request.arguments();

                        Long bookingId = ((Number) arguments.get("bookingId"))
                                .longValue();

                        Long userId = ((Number) arguments.get("userId"))
                                .longValue();

                        if (bookingId <= 0 || userId <= 0) {
                            throw new IllegalArgumentException(
                                    "Booking ID and user ID must be positive numbers."
                            );
                        }

                        Map<String, Object> response =
                                backendClient.requestCancellation(
                                        bookingId,
                                        userId
                                );

                        String json = objectMapper.writeValueAsString(response);

                        return CallToolResult.builder()
                                .content(List.of(new TextContent(json)))
                                .isError(false)
                                .build();

                    } catch (IllegalArgumentException |
                             IllegalStateException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(exception.getMessage())
                                ))
                                .isError(true)
                                .build();

                    } catch (RestClientException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Backend cancellation preparation failed: "
                                                        + exception.getMessage()
                                        )
                                ))
                                .isError(true)
                                .build();

                    } catch (JacksonException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Failed to serialize cancellation response."
                                        )
                                ))
                                .isError(true)
                                .build();
                    }
                })
                .build();
    }
}
