
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
public class BookingDetailsMcpTool {

    private final RailmateBackendClient backendClient;
    private final ObjectMapper objectMapper;

    public BookingDetailsMcpTool(
            RailmateBackendClient backendClient,
            ObjectMapper objectMapper
    ) {
        this.backendClient = backendClient;
        this.objectMapper = objectMapper;
    }

    public SyncToolSpecification specification() {

        // Define the input parameter
        Map<String, Object> properties = Map.of(
                "bookingId",
                Map.of(
                        "type", "integer",
                        "description", "The ID of the railway booking"
                )
        );

        JsonSchema inputSchema = new JsonSchema(
                "object",
                properties,
                List.of("bookingId"),
                false,
                null,
                null
        );

        // Define the MCP tool
        Tool tool = Tool.builder()
                .name("getBookingDetails")
                .description(
                        "Get complete details of a railway booking using its booking ID, " +
                                "including PNR, ticket status, train, journey, fare and passenger details."
                )
                .inputSchema(inputSchema)
                .build();

        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {

                    Object bookingIdValue =
                            request.arguments().get("bookingId");

                    if (bookingIdValue == null) {
                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent("Booking ID is required.")
                                ))
                                .isError(true)
                                .build();
                    }

                    try {
                        Long bookingId = Long.valueOf(
                                bookingIdValue.toString()
                        );

                        if (bookingId <= 0) {
                            return CallToolResult.builder()
                                    .content(List.of(
                                            new TextContent(
                                                    "Booking ID must be a positive number."
                                            )
                                    ))
                                    .isError(true)
                                    .build();
                        }

                        Map<String, Object> bookingDetails =
                                backendClient.getBookingDetails(bookingId);

                        String json = objectMapper.writeValueAsString(
                                bookingDetails
                        );

                        return CallToolResult.builder()
                                .content(List.of(new TextContent(json)))
                                .isError(false)
                                .build();

                    } catch (RestClientException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Unable to retrieve booking details: "
                                                        + exception.getMessage()
                                        )
                                ))
                                .isError(true)
                                .build();

                    } catch (NumberFormatException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Booking ID must be a valid number."
                                        )
                                ))
                                .isError(true)
                                .build();

                    } catch (JacksonException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Unable to process booking details."
                                        )
                                ))
                                .isError(true)
                                .build();
                    }
                })
                .build();
    }
}
