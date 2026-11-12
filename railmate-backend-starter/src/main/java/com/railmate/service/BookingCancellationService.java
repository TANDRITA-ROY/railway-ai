
package com.railmate.service;

import com.railmate.dto.BookingCancellationResponse;
import com.railmate.entity.Booking;
import com.railmate.entity.Ticket;
import com.railmate.entity.SeatInventory;
import com.railmate.repository.BookingRepository;
import com.railmate.repository.TicketRepository;
import com.railmate.repository.PassengerRepository;
import com.railmate.repository.SeatInventoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingCancellationService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PassengerRepository passengerRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final CancellationConfirmationStore confirmationStore;

    public BookingCancellationService(
            BookingRepository bookingRepository,
            TicketRepository ticketRepository,
            PassengerRepository passengerRepository,
            SeatInventoryRepository seatInventoryRepository,
            CancellationConfirmationStore confirmationStore
    ) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.passengerRepository = passengerRepository;
        this.seatInventoryRepository = seatInventoryRepository;
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
                .findByIdAndUser_IdForUpdate(bookingId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found for this user.")
                );

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("This booking is already cancelled.");
        }

        // Retrieve the inventory associated with this booking
        SeatInventory seatInventory = booking.getSeatInventory();

        // Validate that the booking has an inventory reference
        if (seatInventory == null) {
            throw new RuntimeException(
                    "Seat inventory not found for this booking. Cannot restore seats."
            );
        }

        int passengerCount =
                passengerRepository.findByBooking_Id(bookingId).size();

        if (passengerCount == 0) {
            throw new RuntimeException(
                    "No passengers found for this booking. Cannot restore seats."
            );
        }

        // Get the total capacity of the associated coach
        int totalSeats = seatInventory.getCoach().getTotalSeats();

       // Calculate the restored availability
        int updatedAvailableSeats =
                seatInventory.getAvailableSeats() + passengerCount;

        // Prevent inventory from exceeding coach capacity
        if (updatedAvailableSeats > totalSeats) {
            throw new RuntimeException(
                    "Cannot restore seats. Available inventory would exceed coach capacity."
            );
        }

        // Update inventory
        seatInventory.setAvailableSeats(updatedAvailableSeats);

        Ticket ticket = ticketRepository.findByBooking_Id(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Ticket not found for this booking.")
                );

        booking.setStatus("CANCELLED");
        ticket.setStatus("CANCELLED");

        bookingRepository.save(booking);
        ticketRepository.save(ticket);
        seatInventoryRepository.save(seatInventory);

        return new BookingCancellationResponse(
                booking.getId(),
                ticket.getPnr(),
                booking.getStatus(),
                ticket.getStatus(),
                "Booking cancelled successfully."
        );
    }
}

