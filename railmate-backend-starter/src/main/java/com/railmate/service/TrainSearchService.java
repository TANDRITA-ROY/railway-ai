package com.railmate.service;

import com.railmate.dto.TrainSearchResponse;
import com.railmate.entity.Station;
import com.railmate.entity.TrainStop;
import com.railmate.repository.StationRepository;
import com.railmate.repository.TrainStopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TrainSearchService {

    private final StationRepository stationRepository;
    private final TrainStopRepository trainStopRepository;

    public TrainSearchService(
            StationRepository stationRepository,
            TrainStopRepository trainStopRepository
    ) {
        this.stationRepository = stationRepository;
        this.trainStopRepository = trainStopRepository;
    }

    @Transactional(readOnly = true)
    public List<TrainSearchResponse> searchTrains(
            String source,
            String destination
    ) {

        // Step 1: Find source and destination stations
        Station sourceStation = resolveStation(source);
        Station destinationStation = resolveStation(destination);

        if (sourceStation.getId().equals(destinationStation.getId())) {
            throw new IllegalArgumentException(
                    "Source and destination stations cannot be the same."
            );
        }

        // Step 2: Find trains that travel from source to destination
        List<TrainStop> sourceStops =
                trainStopRepository.findTrainsBetweenStations(
                        sourceStation.getId(),
                        destinationStation.getId()
                );

        // Step 3: Convert database results into DTOs
        return sourceStops.stream()
                .map(sourceStop -> {

                    Long trainId = sourceStop.getTrain().getId();

                    TrainStop destinationStop =
                            trainStopRepository.findByTrain_IdAndStation_Id(
                                    trainId,
                                    destinationStation.getId()
                            ).orElseThrow(() -> new IllegalStateException(
                                    "Destination stop not found for train: " + trainId
                            ));

                    return new TrainSearchResponse(
                            trainId,
                            sourceStop.getTrain().getTrainNumber(),
                            sourceStop.getTrain().getName(),

                            sourceStation.getCode(),
                            sourceStation.getName(),
                            sourceStop.getArrivalTime(),
                            sourceStop.getDepartureTime(),
                            sourceStop.getDayNumber(),

                            destinationStation.getCode(),
                            destinationStation.getName(),
                            destinationStop.getArrivalTime(),
                            destinationStop.getDepartureTime(),
                            destinationStop.getDayNumber()
                    );
                })
                .toList();
    }

    private Station resolveStation(String input) {

        String value = input.trim();

        // First try an exact station-code match
        return stationRepository.findByCodeIgnoreCase(value)
                .orElseGet(() -> {

                    // Otherwise search by station name
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