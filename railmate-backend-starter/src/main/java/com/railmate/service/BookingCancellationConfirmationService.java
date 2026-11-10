package com.railmate.service;

import com.railmate.dto.BookingCancellationResponse;
import org.springframework.stereotype.Service;

@Service
public class BookingCancellationConfirmationService {

    private final CancellationConfirmationStore confirmationStore;
    private final BookingCancellationService cancellationService;

    public BookingCancellationConfirmationService(
            CancellationConfirmationStore confirmationStore,
            BookingCancellationService cancellationService
    ) {
        this.confirmationStore = confirmationStore;
        this.cancellationService = cancellationService;
    }

    public BookingCancellationResponse confirmCancellation(
            String confirmationToken
    ) {

        CancellationConfirmationStore.PendingCancellation pending =
                confirmationStore.consume(confirmationToken)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Invalid, expired, or already-used cancellation token."
                                )
                        );

        return cancellationService.executeCancellation(
                pending.bookingId(),
                pending.userId()
        );
    }
}