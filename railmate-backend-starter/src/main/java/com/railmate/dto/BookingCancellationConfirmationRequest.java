package com.railmate.dto;

import jakarta.validation.constraints.NotBlank;

public record BookingCancellationConfirmationRequest(
        @NotBlank String confirmationToken
) {}