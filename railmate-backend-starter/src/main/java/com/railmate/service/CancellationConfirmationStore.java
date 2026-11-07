package com.railmate.service;

import org.springframework.stereotype.Component;

import java.time.Instant;
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

    public boolean consume(String token, Long bookingId, Long userId) {

        PendingCancellation request = pending.get(token);

        if (request == null) {
            return false;
        }

        if (Instant.now().isAfter(request.expiresAt())) {
            pending.remove(token, request);
            return false;
        }

        if (!request.bookingId().equals(bookingId)
                || !request.userId().equals(userId)) {
            return false;
        }

        return pending.remove(token, request);
    }

    private record PendingCancellation(
            Long bookingId,
            Long userId,
            Instant expiresAt
    ) {}
}