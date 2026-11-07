package com.railmate.tool;

import com.railmate.dto.SeatAvailabilityResponse;
import com.railmate.service.SeatAvailabilityService;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class SeatAvailabilityTool {

    private final SeatAvailabilityService seatAvailabilityService;

    public SeatAvailabilityTool(
            SeatAvailabilityService seatAvailabilityService
    ) {
        this.seatAvailabilityService = seatAvailabilityService;
    }

    @Tool("Check available seats and fare for a train journey")
    public List<SeatAvailabilityResponse> checkSeatAvailability(

            @P("Train number, for example 12301")
            String trainNumber,

            @P("Source station code or name")
            String source,

            @P("Destination station code or name")
            String destination,

            @P("Journey date in yyyy-MM-dd format")
            String journeyDate,

            @P("Coach class, for example 3A, SL, or 2A")
            String coachType

    ) {

        LocalDate date = LocalDate.parse(journeyDate);

        return seatAvailabilityService.checkSeatAvailability(
                trainNumber,
                source,
                destination,
                date,
                coachType
        );
    }
}