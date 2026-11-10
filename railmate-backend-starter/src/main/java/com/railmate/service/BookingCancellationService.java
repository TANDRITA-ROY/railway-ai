
package com.railmate.service;

import com.railmate.dto.BookingCancellationResponse;
import com.railmate.entity.Booking;
import com.railmate.entity.Ticket;
import com.railmate.repository.BookingRepository;
import com.railmate.repository.TicketRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingCancellationService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final CancellationConfirmationStore confirmationStore;

    public BookingCancellationService(
            BookingRepository bookingRepository,
            TicketRepository ticketRepository,
            CancellationConfirmationStore confirmationStore
    ) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.confirmationStore = confirmationStore;
    }

    @Transactional(readOnly = true)
    public String requestCancellation(Long bookingId, Long userId) {

        Booking booking = bookingRepository
                .findByIdAndUser_Id(bookingId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found for this user.")
                );

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("This booking is already cancelled.");
        }

        return confirmationStore.create(bookingId, userId);
    }

    @Transactional
    public BookingCancellationResponse executeCancellation(
            Long bookingId,
            Long userId
    ) {

        Booking booking = bookingRepository
                .findByIdAndUser_Id(bookingId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found for this user.")
                );

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("This booking is already cancelled.");
        }

        Ticket ticket = ticketRepository.findByBooking_Id(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found for this booking.")
                );

        booking.setStatus("CANCELLED");
        ticket.setStatus("CANCELLED");

        bookingRepository.save(booking);
        ticketRepository.save(ticket);

        return new BookingCancellationResponse(
                booking.getId(),
                ticket.getPnr(),
                booking.getStatus(),
                ticket.getStatus(),
                "Booking cancelled successfully."
        );
    }
}
