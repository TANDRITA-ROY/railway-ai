
package com.railmate.railmate_mcp_server.tool;

import com.railmate.railmate_mcp_server.client.RailmateBackendClient;
import io.modelcontextprotocol.server.McpServerFeatures.SyncToolSpecification;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static io.modelcontextprotocol.spec.McpSchema.*;

@Component
public class BookingPreparationMcpTool {

    private final RailmateBackendClient backendClient;
    private final ObjectMapper objectMapper;

    public BookingPreparationMcpTool(
            RailmateBackendClient backendClient,
            ObjectMapper objectMapper
    ) {
        this.backendClient = backendClient;
        this.objectMapper = objectMapper;
    }

    public SyncToolSpecification specification() {

        Map<String, Object> passengerProperties = Map.of(
                "name", Map.of(
                        "type", "string",
                        "description", "Passenger full name"
                ),
                "age", Map.of(
                        "type", "integer",
                        "description", "Passenger age"
                ),
                "gender", Map.of(
                        "type", "string",
                        "description", "Passenger gender, for example MALE or FEMALE"
                )
        );

        Map<String, Object> passengerSchema = Map.of(
                "type", "object",
                "properties", passengerProperties,
                "required", List.of("name", "age", "gender"),
                "additionalProperties", false
        );

        Map<String, Object> properties = Map.of(
                "userId", Map.of(
                        "type", "integer",
                        "description", "User ID"
                ),
                "trainNumber", Map.of(
                        "type", "string",
                        "description", "Train number, for example 12301"
                ),
                "source", Map.of(
                        "type", "string",
                        "description", "Source station code, for example HWH"
                ),
                "destination", Map.of(
                        "type", "string",
                        "description", "Destination station code, for example PNBE"
                ),
                "journeyDate", Map.of(
                        "type", "string",
                        "description", "Journey date in yyyy-MM-dd format"
                ),
                "coachType", Map.of(
                        "type", "string",
                        "description", "Coach class, for example 3A"
                ),
                "passengers", Map.of(
                        "type", "array",
                        "description", "List of passengers travelling",
                        "items", passengerSchema,
                        "minItems", 1
                )
        );

        JsonSchema inputSchema = new JsonSchema(
                "object",
                properties,
                List.of(
                        "userId",
                        "trainNumber",
                        "source",
                        "destination",
                        "journeyDate",
                        "coachType",
                        "passengers"
                ),
                false,
                null,
                null
        );

        Tool tool = Tool.builder()
                .name("prepareBooking")
                .description(
                        "Prepare a railway booking and return its summary and confirmation token. " +
                                "This does NOT create or confirm a booking. " +
                                "The user must explicitly confirm through the frontend."
                )
                .inputSchema(inputSchema)
                .build();

        return SyncToolSpecification.builder()
                .tool(tool)
                .callHandler((exchange, request) -> {

                    try {
                        Map<String, Object> arguments = request.arguments();

                        Long userId = Long.valueOf(
                                arguments.get("userId").toString()
                        );

                        String trainNumber = getRequiredString(
                                arguments, "trainNumber"
                        );

                        String source = getRequiredString(
                                arguments, "source"
                        );

                        String destination = getRequiredString(
                                arguments, "destination"
                        );

                        String journeyDate = getRequiredString(
                                arguments, "journeyDate"
                        );

                        String coachType = getRequiredString(
                                arguments, "coachType"
                        );

                        if (userId <= 0) {
                            throw new IllegalArgumentException(
                                    "User ID must be a positive number."
                            );
                        }

                        List<Map<String, Object>> passengers =
                                readPassengers(arguments.get("passengers"));

                        if (passengers.isEmpty()) {
                            throw new IllegalArgumentException(
                                    "At least one passenger is required."
                            );
                        }

                        Map<String, Object> backendRequest = Map.of(
                                "userId", userId,
                                "trainNumber", trainNumber,
                                "source", source,
                                "destination", destination,
                                "journeyDate", journeyDate,
                                "coachType", coachType,
                                "passengers", passengers
                        );

                        Map<String, Object> response =
                                backendClient.prepareBooking(backendRequest);

                        if (response.isEmpty()) {
                            throw new IllegalStateException(
                                    "The backend returned an empty preparation response."
                            );
                        }

                        String json =
                                objectMapper.writeValueAsString(response);

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
                                                "Unable to prepare booking: "
                                                        + exception.getMessage()
                                        )
                                ))
                                .isError(true)
                                .build();

                    } catch (JacksonException exception) {

                        return CallToolResult.builder()
                                .content(List.of(
                                        new TextContent(
                                                "Unable to serialize booking preparation response."
                                        )
                                ))
                                .isError(true)
                                .build();
                    }
                })
                .build();
    }

    private String getRequiredString(
            Map<String, Object> arguments,
            String key
    ) {
        Object value = arguments.get(key);

        if (value == null || value.toString().isBlank()) {
            throw new IllegalArgumentException(
                    key + " is required."
            );
        }

        return value.toString().trim();
    }

    private List<Map<String, Object>> readPassengers(Object value) {

        if (!(value instanceof List<?> passengerList)) {
            throw new IllegalArgumentException(
                    "Passengers must be provided as a list."
            );
        }

        List<Map<String, Object>> passengers = new ArrayList<>();

        for (Object passenger : passengerList) {

            if (!(passenger instanceof Map<?, ?> passengerMap)) {
                throw new IllegalArgumentException(
                        "Each passenger must be an object."
                );
            }

            Object name = passengerMap.get("name");
            Object age = passengerMap.get("age");
            Object gender = passengerMap.get("gender");

            if (name == null || name.toString().isBlank()) {
                throw new IllegalArgumentException(
                        "Every passenger must have a name."
                );
            }

            if (age == null) {
                throw new IllegalArgumentException(
                        "Every passenger must have an age."
                );
            }

            if (gender == null || gender.toString().isBlank()) {
                throw new IllegalArgumentException(
                        "Every passenger must have a gender."
                );
            }

            int passengerAge;

            try {
                passengerAge = Integer.parseInt(age.toString());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                        "Passenger age must be a valid integer."
                );
            }

            if (passengerAge <= 0) {
                throw new IllegalArgumentException(
                        "Passenger age must be positive."
                );
            }

            passengers.add(Map.of(
                    "name", name.toString().trim(),
                    "age", passengerAge,
                    "gender", gender.toString().trim()
            ));
        }

        return passengers;
    }
}
