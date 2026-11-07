package com.railmate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserBookingResponse(
        Long bookingId,
        String trainNumber,
        String trainName,
        String sourceStation,
        String destinationStation,
        LocalDate journeyDate,
        String bookingStatus,
        BigDecimal totalFare
) {}