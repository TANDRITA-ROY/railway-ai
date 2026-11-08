package com.railmate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookingCreationResponse(
        Long bookingId,
        String pnr,
        String bookingStatus,
        String ticketStatus,
        String trainNumber,
        String trainName,
        String source,
        String destination,
        LocalDate journeyDate,
        String coachNumber,
        BigDecimal totalFare,
        List<PassengerDetailsResponse> passengers,
        String message
) {}