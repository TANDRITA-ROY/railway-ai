
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


    public List<Map<String, Object>> checkSeatAvailability(
            String trainNumber,
            String source,
            String destination,
            String journeyDate,
            String coachType
    ) {

        List<Map<String, Object>> result = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/seats/availability")
                        .queryParam("trainNumber", trainNumber)
                        .queryParam("source", source)
                        .queryParam("destination", destination)
                        .queryParam("journeyDate", journeyDate)
                        .queryParam("coachType", coachType)
                        .build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        return result != null ? result : List.of();
    }


    public Map<String, Object> getBookingDetails(Long bookingId) {

        Map<String, Object> result = restClient.get()
                .uri("/api/bookings/{id}", bookingId)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        return result;
    }


    public List<Map<String, Object>> getUserBookings(Long userId) {

        List<Map<String, Object>> result = restClient.get()
                .uri("/api/users/{userId}/bookings", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        return result != null ? result : List.of();
    }

    public Map<String, Object> prepareBooking(Map<String, Object> request) {

        Map<String, Object> result = restClient.post()
                .uri("/api/bookings/prepare")
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        return result != null ? result : Map.of();
    }

    public Map<String, Object> requestCancellation(
            Long bookingId,
            Long userId
    ) {
        Map<String, Object> result = restClient.post()
                .uri(
                        "/api/bookings/{bookingId}/cancellation-request?userId={userId}",
                        bookingId,
                        userId
                )
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        return result != null ? result : Map.of();
    }
}
