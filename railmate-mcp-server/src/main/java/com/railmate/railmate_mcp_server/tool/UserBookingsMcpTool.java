
package com.railmate.railmate_mcp_server.tool;

import com.railmate.railmate_mcp_server.client.RailmateBackendClient;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

import static io.modelcontextprotocol.spec.McpSchema.*;

@Component
public class UserBookingsMcpTool {

    private final RailmateBackendClient backendClient;
    private final ObjectMapper objectMapper;

    public UserBookingsMcpTool(
            RailmateBackendClient backendClient,
            ObjectMapper objectMapper
    ) {
        this.backendClient = backendClient;
        this.objectMapper = objectMapper;
    }

    public SyncToolSpecification specification() {

        Map<String, Object> properties = Map.of(
                "userId", Map.of(
                        "type", "integer",
                        "description", "The ID of the user whose bookings should be retrieved"
                )
        );

        JsonSchema inputSchema = new JsonSchema(
                "object",
                properties,
                List.of("userId"),
                false,
                null,
                null
        );

        Tool tool = Tool.builder()
                .name("getUserBookings")
                .description("Get all railway bookings belonging to a user using their user ID, including train details, journey date, booking status and fare.")
                .inputSchema(inputSchema)
                .build();

        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {

                    Object userIdValue = request.arguments().get("userId");

                    if (userIdValue == null) {
                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent("User ID is required.")
                                ))
                                .isError(true)
                                .build();
                    }

                    try {
                        Long userId = Long.valueOf(userIdValue.toString());

                        if (userId <= 0) {
                            return CallToolResult.builder()
                                    .content(List.of(
                                            new TextContent("User ID must be a positive number.")
                                    ))
                                    .isError(true)
                                    .build();
                        }

                        List<Map<String, Object>> bookings =
                                backendClient.getUserBookings(userId);

                        String json = bookings.isEmpty()
                                ? "No bookings found for this user."
                                : objectMapper.writeValueAsString(bookings);

                        return CallToolResult.builder()
                                .content(List.of(new TextContent(json)))
                                .isError(false)
                                .build();

                    } catch (RestClientException exception) {
                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Unable to retrieve user bookings: "
                                                        + exception.getMessage()
                                        )
                                ))
                                .isError(true)
                                .build();

                    } catch (NumberFormatException exception) {
                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent("User ID must be a valid number.")
                                ))
                                .isError(true)
                                .build();

                    } catch (JacksonException exception) {
                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent("Unable to process user bookings.")
                                ))
                                .isError(true)
                                .build();
                    }
                })
                .build();
    }
}
