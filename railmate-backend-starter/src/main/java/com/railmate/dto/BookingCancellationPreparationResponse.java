
package com.railmate.dto;

public record BookingCancellationPreparationResponse(
        String confirmationToken,
        Long bookingId,
        String pnr,
        String message
) {}
