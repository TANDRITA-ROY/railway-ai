package com.railmate.service;

import com.railmate.dto.BookingCreationRequest;
import com.railmate.dto.BookingCreationResponse;
import com.railmate.dto.PassengerDetailsResponse;
import com.railmate.dto.PassengerRequest;
import com.railmate.entity.*;
import com.railmate.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class BookingCreationService {

    private final UserRepository userRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final TicketRepository ticketRepository;
    private final SeatReservationRepository seatReservationRepository;

    public BookingCreationService(
            UserRepository userRepository,
            TrainRepository trainRepository,
            StationRepository stationRepository,
            SeatInventoryRepository seatInventoryRepository,
            BookingRepository bookingRepository,
            PassengerRepository passengerRepository,
            TicketRepository ticketRepository,
            SeatReservationRepository seatReservationRepository
    ) {
        this.userRepository = userRepository;
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.seatInventoryRepository = seatInventoryRepository;
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.ticketRepository = ticketRepository;
        this.seatReservationRepository=seatReservationRepository;
    }

    @Transactional
    public BookingCreationResponse createBooking(BookingCreationRequest request) {

        // 1. Validate journey date
        if (request.journeyDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Journey date cannot be in the past.");
        }

        // 2. Find user
        User user = userRepository.findById(request.userId())
                .orElseThrow(() ->
                        new RuntimeException("User not found."));

        // 3. Find active train
        Train train = trainRepository
                .findByTrainNumberIgnoreCaseAndActiveTrue(request.trainNumber())
                .orElseThrow(() ->
                        new RuntimeException("Train not found or inactive."));

        // 4. Find source station
        Station source = stationRepository
                .findByCodeIgnoreCase(request.source())
                .orElseThrow(() ->
                        new RuntimeException("Source station not found."));

        // 5. Find destination station
        Station destination = stationRepository
                .findByCodeIgnoreCase(request.destination())
                .orElseThrow(() ->
                        new RuntimeException("Destination station not found."));

        // 6. Validate source and destination
        if (source.getId().equals(destination.getId())) {
            throw new RuntimeException(
                    "Source and destination cannot be the same.");
        }

        // 7. Find and lock matching seat inventory
        List<SeatInventory> inventories =
                seatInventoryRepository.findAvailabilityForUpdate(
                        train.getTrainNumber(),
                        source.getId(),
                        destination.getId(),
                        request.journeyDate(),
                        request.coachType()
                );

        int passengerCount = request.passengers().size();

        SeatInventory selectedInventory = inventories.stream()
                .filter(inventory ->
                        inventory.getAvailableSeats() >= passengerCount)
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Not enough seats available for this journey."));

        String coachNumber = selectedInventory.getCoach().getCoachNumber();

        List<String> allocatedSeats =
                seatReservationRepository.findAllocatedSeats(
                        train.getId(),
                        request.journeyDate(),
                        coachNumber
                );

        Set<String> occupiedSeats = new HashSet<>(allocatedSeats);

        // 8. Calculate fare
        BigDecimal totalFare = selectedInventory.getFare()
                .multiply(BigDecimal.valueOf(passengerCount));

        // 9. Create booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setTrain(train);
        booking.setFromStation(source);
        booking.setToStation(destination);
        booking.setJourneyDate(request.journeyDate());
        booking.setStatus("CONFIRMED");
        booking.setTotalFare(totalFare);
        booking.setSeatInventory(selectedInventory);

        Booking savedBooking = bookingRepository.save(booking);

        // 10. Save passengers
        List<Passenger> passengers = new ArrayList<>();

        int seatNumber = 1;

        for (PassengerRequest passengerRequest : request.passengers()) {

            String assignedSeat;

            while (true) {
                if (seatNumber > selectedInventory.getCoach().getTotalSeats()) {
                    throw new RuntimeException(
                            "Not enough individual seats available in coach " + coachNumber
                    );
                }

                assignedSeat = coachNumber + "-" + String.format("%02d", seatNumber);

                if (!occupiedSeats.contains(assignedSeat)) {
                    break;
                }

                seatNumber++;
            }

            Passenger passenger = new Passenger();
            passenger.setBooking(savedBooking);
            passenger.setName(passengerRequest.name());
            passenger.setAge(passengerRequest.age());
            passenger.setGender(passengerRequest.gender());
            passenger.setSeatNumber(assignedSeat);

            passengers.add(passenger);

            occupiedSeats.add(assignedSeat);
            seatNumber++;
        }

        passengerRepository.saveAll(passengers);

        // 11. Update seat inventory
        selectedInventory.setAvailableSeats(
                selectedInventory.getAvailableSeats() - passengerCount
        );

        seatInventoryRepository.save(selectedInventory);

        // 12. Generate ticket
        Ticket ticket = new Ticket();
        ticket.setBooking(booking);
        ticket.setPnr("RM" + UUID.randomUUID()
                .toString().replace("-", "")
                .substring(0, 8).toUpperCase());
        ticket.setIssuedAt(java.time.LocalDateTime.now());
        ticket.setStatus("CONFIRMED");

        ticket = ticketRepository.save(ticket);

        // 13. Prepare passenger response
        List<PassengerDetailsResponse> passengerResponses =
                passengers.stream()
                        .map(passenger -> new PassengerDetailsResponse(
                                passenger.getName(),
                                passenger.getAge(),
                                passenger.getGender(),
                                passenger.getSeatNumber()
                        ))
                        .toList();

        // 14. Return response
        return new BookingCreationResponse(
                booking.getId(),
                ticket.getPnr(),
                booking.getStatus(),
                ticket.getStatus(),
                train.getTrainNumber(),
                train.getName(),
                source.getCode(),
                destination.getCode(),
                request.journeyDate(),
                selectedInventory.getCoach().getCoachNumber(),
                totalFare,
                passengerResponses,
                "Booking created successfully."
        );
    }
}