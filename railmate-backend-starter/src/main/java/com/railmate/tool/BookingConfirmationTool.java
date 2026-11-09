
package com.railmate.tool;

import com.railmate.dto.*;
import com.railmate.service.BookingConfirmationStore;
import com.railmate.service.SeatAvailabilityService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingConfirmationTool {

    private final BookingConfirmationStore confirmationStore;
    private final SeatAvailabilityService seatAvailabilityService;

    public BookingConfirmationTool(
            BookingConfirmationStore confirmationStore,
            SeatAvailabilityService seatAvailabilityService
    ) {
        this.confirmationStore = confirmationStore;
        this.seatAvailabilityService = seatAvailabilityService;
    }


    @Tool("Prepare a railway booking and return a confirmation summary and token. This method DOES NOT create a booking.")
    public BookingPreparationResponse prepareBooking(

            @P("User ID as a number")
            Long userId,

            @P("Train number as a string, for example 12301")
            String trainNumber,

            @P("Source station code as a string, for example HWH")
            String source,

            @P("Destination station code as a string, for example PNBE")
            String destination,

            @P("Journey date as yyyy-MM-dd")
            String journeyDate,

            @P("Coach class as a string, for example 3A")
            String coachType,

            @P("List of passenger names")
            List<String> passengerNames,

            @P("List of passenger ages, in the same order as passenger names")
            List<Integer> passengerAges,

            @P("List of passenger genders, in the same order as passenger names")
            List<String> passengerGenders
    ) {

        if (passengerNames == null || passengerAges == null
                || passengerGenders == null
                || passengerNames.isEmpty()
                || passengerNames.size() != passengerAges.size()
                || passengerNames.size() != passengerGenders.size()) {
            throw new IllegalArgumentException(
                    "Passenger names, ages and genders must have matching non-empty lists."
            );
        }

        LocalDate date = LocalDate.parse(journeyDate);

        List<PassengerRequest> passengers = new ArrayList<>();

        for (int i = 0; i < passengerNames.size(); i++) {
            passengers.add(new PassengerRequest(
                    passengerNames.get(i),
                    passengerAges.get(i),
                    passengerGenders.get(i)
            ));
        }

        BookingCreationRequest request = new BookingCreationRequest(
                userId,
                trainNumber,
                source,
                destination,
                date,
                coachType,
                passengers
        );

        int passengerCount = passengers.size();

        List<SeatAvailabilityResponse> availability =
                seatAvailabilityService.checkSeatAvailability(
                        trainNumber,
                        source,
                        destination,
                        date,
                        coachType
                );

        SeatAvailabilityResponse selectedOption = availability.stream()
                .filter(option ->
                        option.availableSeats() != null
                                && option.availableSeats() >= passengerCount
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Not enough seats available for this booking."
                        )
                );

        BigDecimal totalFare = selectedOption.fare()
                .multiply(BigDecimal.valueOf(passengerCount));

        String token = confirmationStore.create(request);


        return new BookingPreparationResponse(
                token,
                userId,
                selectedOption.trainNumber(),
                selectedOption.trainName(),
                selectedOption.sourceStationCode(),
                selectedOption.destinationStationCode(),
                date,
                coachType,
                passengerCount,
                passengers,
                totalFare,
                "Booking prepared successfully. Please review the details and explicitly confirm."
        );
    }
}
