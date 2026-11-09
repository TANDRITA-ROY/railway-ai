package com.railmate.dto;

import jakarta.validation.constraints.NotBlank;

public record BookingConfirmationRequest(
        @NotBlank String confirmationToken
) {}