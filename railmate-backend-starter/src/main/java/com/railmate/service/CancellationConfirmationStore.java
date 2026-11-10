
package com.railmate.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class CancellationConfirmationStore {

    private final ConcurrentMap<String, PendingCancellation> pending =
            new ConcurrentHashMap<>();

    public String create(Long bookingId, Long userId) {

        String token = UUID.randomUUID().toString();

        PendingCancellation request = new PendingCancellation(
                bookingId,
                userId,
                Instant.now().plusSeconds(300)
        );

        pending.put(token, request);

        return token;
    }

    public Optional<PendingCancellation> consume(String token) {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        PendingCancellation request = pending.remove(token);

        if (request == null) {
            return Optional.empty();
        }

        if (Instant.now().isAfter(request.expiresAt())) {
            return Optional.empty();
        }

        return Optional.of(request);
    }

    public record PendingCancellation(
            Long bookingId,
            Long userId,
            Instant expiresAt
    ) {}
}
