
package com.railmate.repository;

import com.railmate.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUser_Id(Long userId);

    Optional<Booking> findByIdAndUser_Id(Long bookingId, Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       SELECT b
       FROM Booking b
       WHERE b.id = :bookingId
       AND b.user.id = :userId
       """)
    Optional<Booking> findByIdAndUser_IdForUpdate(
            @Param("bookingId") Long bookingId,
            @Param("userId") Long userId
    );
}
