package com.railmate.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record BookingCreationRequest(

        @NotNull
        Long userId,

        @NotBlank
        String trainNumber,

        @NotBlank
        String source,

        @NotBlank
        String destination,

        @NotNull
        LocalDate journeyDate,

        @NotBlank
        String coachType,

        @NotEmpty
        List<@Valid PassengerRequest> passengers

) {}