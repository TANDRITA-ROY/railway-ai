package com.railmate.controller;

import com.railmate.dto.SeatAvailabilityResponse;
import com.railmate.service.SeatAvailabilityService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatAvailabilityController {

    private final SeatAvailabilityService seatAvailabilityService;

    public SeatAvailabilityController(
            SeatAvailabilityService seatAvailabilityService
    ) {
        this.seatAvailabilityService = seatAvailabilityService;
    }

    @GetMapping("/availability")
    public ResponseEntity<List<SeatAvailabilityResponse>> checkAvailability(
            @RequestParam String trainNumber,
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate journeyDate,
            @RequestParam String coachType
    ) {

        List<SeatAvailabilityResponse> availability =
                seatAvailabilityService.checkSeatAvailability(
                        trainNumber,
                        source,
                        destination,
                        journeyDate,
                        coachType
                );

        return ResponseEntity.ok(availability);
    }
}