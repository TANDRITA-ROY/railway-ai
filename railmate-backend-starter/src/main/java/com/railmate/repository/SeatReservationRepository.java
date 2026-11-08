package com.railmate.repository;

import com.railmate.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SeatReservationRepository
        extends JpaRepository<Passenger, Long> {

    @Query("""
            SELECT passenger.seatNumber
            FROM Passenger passenger
            WHERE passenger.booking.train.id = :trainId
            AND passenger.booking.journeyDate = :journeyDate
            AND passenger.booking.status <> 'CANCELLED'
            AND passenger.seatNumber LIKE CONCAT(:coachNumber, '-%')
            AND passenger.seatNumber IS NOT NULL
            """)
    List<String> findAllocatedSeats(
            @Param("trainId") Long trainId,
            @Param("journeyDate") LocalDate journeyDate,
            @Param("coachNumber") String coachNumber
    );
}