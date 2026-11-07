package com.railmate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record BookingDetailsResponse(
        Long bookingId,
        String pnr,
        String ticketStatus,
        LocalDateTime issuedAt,

        String trainNumber,
        String trainName,

        String sourceStationCode,
        String sourceStationName,

        String destinationStationCode,
        String destinationStationName,

        LocalDate journeyDate,
        String bookingStatus,
        BigDecimal totalFare,

        List<PassengerDetailsResponse> passengers
) {}