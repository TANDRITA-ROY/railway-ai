
package com.railmate.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record BookingPreparationResponse(
        String confirmationToken,
        Long userId,
        String trainNumber,
        String trainName,
        String source,
        String destination,
        LocalDate journeyDate,
        String coachType,
        int passengerCount,
        List<PassengerRequest> passengers,
        BigDecimal estimatedTotalFare,
        String message
) {}
