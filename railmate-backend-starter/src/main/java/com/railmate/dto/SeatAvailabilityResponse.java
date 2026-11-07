package com.railmate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SeatAvailabilityResponse(
        String trainNumber,
        String trainName,
        String sourceStationCode,
        String sourceStationName,
        String destinationStationCode,
        String destinationStationName,
        LocalDate journeyDate,
        String coachNumber,
        String coachType,
        Integer availableSeats,
        BigDecimal fare,
        String availabilityStatus
) {}