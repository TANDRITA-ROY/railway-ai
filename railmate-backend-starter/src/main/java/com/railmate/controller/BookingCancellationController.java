package com.railmate.controller;

import com.railmate.dto.BookingCancellationResponse;
import com.railmate.dto.BookingCancellationConfirmationRequest;
import com.railmate.service.BookingCancellationConfirmationService;
import com.railmate.service.BookingCancellationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingCancellationController {

    private final BookingCancellationService cancellationService;
    private final BookingCancellationConfirmationService confirmationService;

    public BookingCancellationController(
            BookingCancellationService cancellationService,
            BookingCancellationConfirmationService confirmationService) {
        this.cancellationService = cancellationService;
        this.confirmationService = confirmationService;
    }

    @PostMapping("/{bookingId}/cancellation-request")
    public String requestCancellation(
            @PathVariable Long bookingId,
            @RequestParam Long userId) {

        return cancellationService.requestCancellation(
                bookingId, userId
        );
    }

    @PostMapping("/cancel")
    public BookingCancellationResponse confirmCancellation(
            @Valid @RequestBody BookingCancellationConfirmationRequest request) {

        return confirmationService.confirmCancellation(
                request.confirmationToken()
        );
    }
}