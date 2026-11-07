package com.railmate.service;

import com.railmate.dto.SeatAvailabilityResponse;
import com.railmate.entity.SeatInventory;
import com.railmate.entity.Station;
import com.railmate.repository.SeatInventoryRepository;
import com.railmate.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class SeatAvailabilityService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final StationRepository stationRepository;

    public SeatAvailabilityService(
            SeatInventoryRepository seatInventoryRepository,
            StationRepository stationRepository
    ) {
        this.seatInventoryRepository = seatInventoryRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional(readOnly = true)
    public List<SeatAvailabilityResponse> checkSeatAvailability(
            String trainNumber,
            String source,
            String destination,
            LocalDate journeyDate,
            String coachType
    ) {

        // Validate inputs
        if (trainNumber == null || trainNumber.isBlank()) {
            throw new IllegalArgumentException("Train number is required.");
        }

        if (source == null || source.isBlank()
                || destination == null || destination.isBlank()) {
            throw new IllegalArgumentException(
                    "Source and destination stations are required."
            );
        }

        if (journeyDate == null) {
            throw new IllegalArgumentException("Journey date is required.");
        }

        if (coachType == null || coachType.isBlank()) {
            throw new IllegalArgumentException("Coach type is required.");
        }

        // Resolve station names or codes
        Station sourceStation = resolveStation(source);
        Station destinationStation = resolveStation(destination);

        if (sourceStation.getId().equals(destinationStation.getId())) {
            throw new IllegalArgumentException(
                    "Source and destination cannot be the same."
            );
        }

        // Fetch matching inventory records
        List<SeatInventory> inventoryList =
                seatInventoryRepository.findAvailability(
                        trainNumber.trim(),
                        sourceStation.getId(),
                        destinationStation.getId(),
                        journeyDate,
                        coachType.trim()
                );

        // Convert entities into response DTOs
        return inventoryList.stream()
                .map(inventory -> {

                    Integer availableSeats = inventory.getAvailableSeats();

                    String status = availableSeats > 0
                            ? "AVAILABLE"
                            : "NOT_AVAILABLE";

                    return new SeatAvailabilityResponse(
                            inventory.getTrain().getTrainNumber(),
                            inventory.getTrain().getName(),
                            inventory.getFromStation().getCode(),
                            inventory.getFromStation().getName(),
                            inventory.getToStation().getCode(),
                            inventory.getToStation().getName(),
                            inventory.getJourneyDate(),
                            inventory.getCoach().getCoachNumber(),
                            inventory.getCoach().getCoachType(),
                            availableSeats,
                            inventory.getFare(),
                            status
                    );
                })
                .toList();
    }

    private Station resolveStation(String input) {

        String value = input.trim();

        return stationRepository.findByCodeIgnoreCase(value)
                .orElseGet(() -> {

                    List<Station> stations =
                            stationRepository.findByNameContainingIgnoreCase(value);

                    if (stations.isEmpty()) {
                        throw new IllegalArgumentException(
                                "No station found for: " + value
                        );
                    }

                    if (stations.size() > 1) {
                        throw new IllegalArgumentException(
                                "Multiple stations found for: " + value
                                        + ". Please provide a station code."
                        );
                    }

                    return stations.get(0);
                });
    }
}