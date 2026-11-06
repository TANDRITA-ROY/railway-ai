package com.railmate.dto;

import java.time.LocalTime;

public record TrainSearchResponse(
        Long trainId,
        String trainNumber,
        String trainName,

        String sourceStationCode,
        String sourceStationName,
        LocalTime sourceArrivalTime,
        LocalTime sourceDepartureTime,
        Integer sourceDayNumber,

        String destinationStationCode,
        String destinationStationName,
        LocalTime destinationArrivalTime,
        LocalTime destinationDepartureTime,
        Integer destinationDayNumber
) {
}