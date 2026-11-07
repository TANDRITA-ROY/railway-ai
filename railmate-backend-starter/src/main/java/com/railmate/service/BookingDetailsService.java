package com.railmate.service;

import com.railmate.dto.BookingDetailsResponse;
import com.railmate.dto.PassengerDetailsResponse;
import com.railmate.entity.Booking;
import com.railmate.entity.Passenger;
import com.railmate.entity.Ticket;
import com.railmate.repository.BookingRepository;
import com.railmate.repository.PassengerRepository;
import com.railmate.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingDetailsService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PassengerRepository passengerRepository;

    public BookingDetailsService(
            BookingRepository bookingRepository,
            TicketRepository ticketRepository,
            PassengerRepository passengerRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
    }

    @Transactional(readOnly = true)
    public BookingDetailsResponse getBookingDetails(Long bookingId) {

        // 1. Find the booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found with ID: " + bookingId)
                );

        // 2. Find the ticket associated with this booking
        Ticket ticket = ticketRepository.findByBooking_Id(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found for booking ID: " + bookingId)
                );

        // 3. Find all passengers associated with this booking
        List<PassengerDetailsResponse> passengers =
                passengerRepository.findByBooking_Id(bookingId)
                        .stream()
                        .map(passenger -> new PassengerDetailsResponse(
                                passenger.getName(),
                                passenger.getAge(),
                                passenger.getGender(),
                                passenger.getSeatNumber()
                        ))
                        .toList();

        // 4. Combine everything into one response DTO
        return new BookingDetailsResponse(
                booking.getId(),
                ticket.getPnr(),
                ticket.getStatus(),
                ticket.getIssuedAt(),

                booking.getTrain().getTrainNumber(),
                booking.getTrain().getName(),

                booking.getFromStation().getCode(),
                booking.getFromStation().getName(),

                booking.getToStation().getCode(),
                booking.getToStation().getName(),

                booking.getJourneyDate(),
                booking.getStatus(),
                booking.getTotalFare(),

                passengers
        );
    }
}