package com.railmate.dto;

public record BookingCancellationResponse(
        Long bookingId,
        String pnr,
        String bookingStatus,
        String ticketStatus,
        String message
) {}