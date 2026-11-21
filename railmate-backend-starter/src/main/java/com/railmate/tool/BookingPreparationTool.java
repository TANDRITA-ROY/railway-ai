
package com.railmate.tool;

import com.railmate.ai.service.BookingPreparationResultContext;
import com.railmate.dto.*;
import com.railmate.service.BookingPreparationService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class BookingPreparationTool {

    private final BookingPreparationService bookingPreparationService;
    private final BookingPreparationResultContext resultContext;

    public BookingPreparationTool(
            BookingPreparationService bookingPreparationService,
            BookingPreparationResultContext resultContext
    ) {
        this.bookingPreparationService = bookingPreparationService;
        this.resultContext = resultContext;
    }

    @Tool("Prepare a railway booking and return its summary and confirmation token. This does NOT create or confirm a booking.")
    public BookingPreparationResponse prepareBooking(

            @P("User ID as a number")
            Long userId,

            @P("Train number, for example 12301")
            String trainNumber,

            @P("Source station code, for example HWH")
            String source,

            @P("Destination station code, for example PNBE")
            String destination,

            @P("Journey date in yyyy-MM-dd format")
            String journeyDate,

            @P("Coach class, for example 3A")
            String coachType,

            @P("Passenger names")
            List<String> passengerNames,

            @P("Passenger ages in the same order as passenger names")
            List<Integer> passengerAges,

            @P("Passenger genders in the same order as passenger names")
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
                LocalDate.parse(journeyDate),
                coachType,
                passengers
        );

        BookingPreparationResponse response =
                bookingPreparationService.prepareBooking(request);

        // Capture the exact structured response for the controller.
        resultContext.setResult(response);

        return response;
    }
}
