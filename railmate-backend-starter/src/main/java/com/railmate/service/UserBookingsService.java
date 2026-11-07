package com.railmate.service;

import com.railmate.dto.UserBookingResponse;
import com.railmate.entity.Booking;
import com.railmate.repository.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserBookingsService {

    private final BookingRepository bookingRepository;

    public UserBookingsService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<UserBookingResponse> getUserBookings(Long userId) {

        List<Booking> bookings = bookingRepository.findByUser_Id(userId);

        return bookings.stream()
                .map(booking -> new UserBookingResponse(
                        booking.getId(),
                        booking.getTrain().getTrainNumber(),
                        booking.getTrain().getName(),
                        booking.getFromStation().getCode(),
                        booking.getToStation().getCode(),
                        booking.getJourneyDate(),
                        booking.getStatus(),
                        booking.getTotalFare()
                ))
                .toList();
    }
}