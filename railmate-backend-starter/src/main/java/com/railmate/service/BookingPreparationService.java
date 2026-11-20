package com.railmate.service;

import com.railmate.dto.BookingCreationRequest;
import com.railmate.dto.BookingPreparationResponse;
import com.railmate.dto.SeatAvailabilityResponse;
import org.springframework.stereotype.Service;
import com.railmate.exception.ResourceNotFoundException;
import com.railmate.exception.SeatsUnavailableException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingPreparationService {

    private final BookingConfirmationStore confirmationStore;
    private final SeatAvailabilityService seatAvailabilityService;

    public BookingPreparationService(
            BookingConfirmationStore confirmationStore,
            SeatAvailabilityService seatAvailabilityService
    ) {
        this.confirmationStore = confirmationStore;
        this.seatAvailabilityService = seatAvailabilityService;
    }


    public BookingPreparationResponse prepareBooking(BookingCreationRequest request) {

        if (request.journeyDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Journey date cannot be in the past."
            );
        }

        int passengerCount = request.passengers().size();

        List<SeatAvailabilityResponse> availability =
                seatAvailabilityService.checkSeatAvailability(
                        request.trainNumber(),
                        request.source(),
                        request.destination(),
                        request.journeyDate(),
                        request.coachType()
                );

        // Case 1: No matching inventory found
        if (availability.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No seat inventory found for the selected train, route, date and coach class."
            );
        }

        // Case 2: Find an inventory option with enough seats
        SeatAvailabilityResponse selectedOption = availability.stream()
                .filter(option -> option.availableSeats() != null
                        && option.availableSeats() >= passengerCount)
                .findFirst()
                .orElse(null);

        // Case 3: Inventory exists, but insufficient seats
        if (selectedOption == null) {

            int maxAvailableSeats = availability.stream()
                    .map(SeatAvailabilityResponse::availableSeats)
                    .filter(seats -> seats != null)
                    .mapToInt(Integer::intValue)
                    .max()
                    .orElse(0);

            throw new SeatsUnavailableException(
                    "You requested " + passengerCount
                            + " seats, but the maximum available in a matching coach is "
                            + maxAvailableSeats + ". Please reduce the passenger count "
                            + "or select another coach or train."
            );
        }

        // Calculate total fare
        BigDecimal totalFare = selectedOption.fare()
                .multiply(BigDecimal.valueOf(passengerCount));

        // Generate confirmation token only after validation succeeds
        String token = confirmationStore.create(request);

        return new BookingPreparationResponse(
                token,
                request.userId(),
                selectedOption.trainNumber(),
                selectedOption.trainName(),
                selectedOption.sourceStationCode(),
                selectedOption.destinationStationCode(),
                request.journeyDate(),
                request.coachType(),
                passengerCount,
                request.passengers(),
                totalFare,
                "Booking prepared successfully. Please review the details and explicitly confirm."
        );
    }

}
