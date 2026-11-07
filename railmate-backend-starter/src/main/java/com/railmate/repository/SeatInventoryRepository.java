package com.railmate.repository;

import com.railmate.entity.SeatInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SeatInventoryRepository
        extends JpaRepository<SeatInventory, Long> {

    @Query("""
            SELECT inventory
            FROM SeatInventory inventory
            JOIN FETCH inventory.train train
            JOIN FETCH inventory.coach coach
            JOIN FETCH inventory.fromStation source
            JOIN FETCH inventory.toStation destination
            WHERE LOWER(train.trainNumber) = LOWER(:trainNumber)
            AND source.id = :sourceStationId
            AND destination.id = :destinationStationId
            AND inventory.journeyDate = :journeyDate
            AND LOWER(coach.coachType) = LOWER(:coachType)
            ORDER BY coach.coachNumber
            """)
    List<SeatInventory> findAvailability(
            @Param("trainNumber") String trainNumber,
            @Param("sourceStationId") Long sourceStationId,
            @Param("destinationStationId") Long destinationStationId,
            @Param("journeyDate") LocalDate journeyDate,
            @Param("coachType") String coachType
    );
}