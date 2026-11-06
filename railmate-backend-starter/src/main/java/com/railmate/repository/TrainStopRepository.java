package com.railmate.repository;
import com.railmate.entity.TrainStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainStopRepository extends JpaRepository<TrainStop, Long> {
    @Query("""
            SELECT sourceStop
            FROM TrainStop sourceStop
            JOIN FETCH sourceStop.train train
            WHERE sourceStop.station.id = :sourceStationId
            AND train.active = true
            AND EXISTS (
                SELECT destinationStop.id
                FROM TrainStop destinationStop
                WHERE destinationStop.train.id = train.id
                AND destinationStop.station.id = :destinationStationId
                AND sourceStop.stopNumber < destinationStop.stopNumber
            )
            """)
    List<TrainStop> findTrainsBetweenStations(
            @Param("sourceStationId") Long sourceStationId,
            @Param("destinationStationId") Long destinationStationId
    );

    Optional<TrainStop> findByTrain_IdAndStation_Id(
            Long trainId,
            Long stationId
    );
}
