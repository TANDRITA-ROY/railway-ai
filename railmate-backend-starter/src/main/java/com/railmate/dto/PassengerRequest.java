package com.railmate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public record PassengerRequest(

        @NotBlank
        String name,

        @NotNull
        @Min(1)
        @Max(120)
        Integer age,

        @NotBlank
        String gender

) {}