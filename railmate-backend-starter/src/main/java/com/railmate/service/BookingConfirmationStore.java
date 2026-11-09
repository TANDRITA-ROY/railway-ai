
package com.railmate.service;

import com.railmate.dto.BookingCreationRequest;
import com.railmate.dto.PassengerRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class BookingConfirmationStore {

    private final ConcurrentMap<String, PendingBooking> pendingBookings =
            new ConcurrentHashMap<>();

    public String create(BookingCreationRequest request) {

        String token = UUID.randomUUID().toString();

        // Create an immutable snapshot of the booking request.
        BookingCreationRequest requestSnapshot =
                new BookingCreationRequest(
                        request.userId(),
                        request.trainNumber(),
                        request.source(),
                        request.destination(),
                        request.journeyDate(),
                        request.coachType(),
                        List.copyOf(request.passengers())
                );

        PendingBooking pendingBooking = new PendingBooking(
                requestSnapshot,
                Instant.now().plusSeconds(300)
        );

        pendingBookings.put(token, pendingBooking);

        return token;
    }

    public Optional<BookingCreationRequest> consume(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        PendingBooking pendingBooking = pendingBookings.remove(token);

        if (pendingBooking == null) {
            return Optional.empty();
        }

        if (Instant.now().isAfter(pendingBooking.expiresAt())) {
            return Optional.empty();
        }

        return Optional.of(pendingBooking.request());
    }

    private record PendingBooking(
            BookingCreationRequest request,
            Instant expiresAt
    ) {}
}
